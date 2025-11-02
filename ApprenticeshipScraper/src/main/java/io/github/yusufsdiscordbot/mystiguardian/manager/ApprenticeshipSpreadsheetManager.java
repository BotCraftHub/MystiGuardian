/*
 * Copyright 2024 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.yusufsdiscordbot.mystiguardian.manager;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.Apprenticeship;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.ApprenticeshipSource;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.FindAnApprenticeship;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.HigherinApprenticeship;
import io.github.yusufsdiscordbot.mystiguardian.ApprenticeshipScraper;
import io.github.yusufsdiscordbot.mystiguardian.config.DAConfig;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages apprenticeship data in Google Sheets and posts new apprenticeships to Discord.
 *
 * <p>This class:
 * <ul>
 *   <li>Tracks apprenticeships in a Google Spreadsheet</li>
 *   <li>Detects and processes new apprenticeships</li>
 *   <li>Posts announcements to configured Discord channels</li>
 *   <li>Handles rate limiting and retry logic for API calls</li>
 * </ul>
 *
 * <p>The spreadsheet structure includes columns for:
 * ID, Title, Company, Location, Categories, Salary, Opening Date, Closing Date, URL, Source
 *
 * @see io.github.yusufsdiscordbot.mystiguardian.config.DAConfig
 * @see io.github.yusufsdiscordbot.mystiguardian.ApprenticeshipScraper
 */
@Slf4j
public class ApprenticeshipSpreadsheetManager {
    private static final String LOG_PREFIX = "ApprenticeshipSpreadsheetManager";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final String HEADER_RANGE_NUMBER = "!A1:J1";
    private final Sheets sheetsService;
    private final String spreadsheetId;
    private final ScheduledExecutorService scheduler;
    private final DAConfig daConfig;
    @Nullable private final List<String> rolesToPing;

    /**
     * Constructs an ApprenticeshipSpreadsheetManager.
     *
     * @param sheetsService the Google Sheets API service instance
     * @param spreadsheetId the ID of the Google Spreadsheet to use
     * @param scheduler the executor service for scheduling periodic tasks
     * @param daConfig the Digital Apprenticeship configuration
     * @param rolesToPing optional list of Discord role IDs to ping when posting apprenticeships
     * @throws NullPointerException if any required parameter is null
     */
    public ApprenticeshipSpreadsheetManager(
            @NotNull Sheets sheetsService,
            @NotNull String spreadsheetId,
            @NotNull ScheduledExecutorService scheduler,
            @NotNull DAConfig daConfig,
            @Nullable List<String> rolesToPing) {
        this.sheetsService = Objects.requireNonNull(sheetsService, "sheetsService cannot be null");
        this.spreadsheetId = Objects.requireNonNull(spreadsheetId, "spreadsheetId cannot be null");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler cannot be null");
        this.daConfig = Objects.requireNonNull(daConfig, "daConfig cannot be null");
        this.rolesToPing = rolesToPing;

        logger.info("{}: Initializing with spreadsheet ID: {}", LOG_PREFIX, spreadsheetId);
        try {
            initializeSheet();
        } catch (IOException e) {
            logger.error("{}: Failed to initialize: {}", LOG_PREFIX, e.getMessage());
            throw new RuntimeException("Failed to initialize spreadsheet", e);
        }
    }

    /**
     * Get the current academic/recruitment year. From September onwards, returns the next calendar
     * year. E.g., September 2025 onwards returns 2026.
     */
    private int getAcademicYear() {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();

        // If we're in September or later, use next year
        if (now.getMonthValue() >= Month.SEPTEMBER.getValue()) {
            return currentYear + 1;
        }

        return currentYear;
    }

    /** Get the sheet name for the current academic year. */
    private String getCurrentSheetName() {
        return "Apprenticeships " + getAcademicYear();
    }

    private void initializeSheet() throws IOException {
        logger.debug("{}: Starting sheet initialization", LOG_PREFIX);
        try {
            String currentSheetName = getCurrentSheetName();
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean hasJobsSheet =
                    spreadsheet.getSheets().stream()
                            .anyMatch(s -> s.getProperties().getTitle().equals(currentSheetName));

            if (!hasJobsSheet) {
                createApprenticeshipsSheet(currentSheetName);
            }

            ensureHeaders(currentSheetName);
        } catch (Exception e) {
            logger.error("{}: Initialization failed: {}", LOG_PREFIX, e.getMessage());
            throw new IOException("Failed to initialize sheet", e);
        }
    }

    private void createApprenticeshipsSheet(String sheetName) throws IOException {
        BatchUpdateSpreadsheetRequest request =
                new BatchUpdateSpreadsheetRequest()
                        .setRequests(
                                Collections.singletonList(
                                        new Request()
                                                .setAddSheet(
                                                        new AddSheetRequest()
                                                                .setProperties(new SheetProperties().setTitle(sheetName)))));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, request).execute();
        logger.info("{}: Created new sheet: {}", LOG_PREFIX, sheetName);
    }

    private void ensureHeaders(String sheetName) throws IOException {
        String headerRange = sheetName + HEADER_RANGE_NUMBER;
        ValueRange headerResponse =
                sheetsService.spreadsheets().values().get(spreadsheetId, headerRange).execute();

        if (headerResponse.getValues() == null || headerResponse.getValues().isEmpty()) {
            ValueRange headers =
                    new ValueRange().setValues(Collections.singletonList(Arrays.asList(Columns.HEADERS)));

            sheetsService
                    .spreadsheets()
                    .values()
                    .update(spreadsheetId, headerRange, headers)
                    .setValueInputOption("RAW")
                    .execute();
            logger.info("{}: Added headers to sheet: {}", LOG_PREFIX, sheetName);
        }
    }

    private <T extends Apprenticeship> List<T> filterNewApprenticeships(
            List<T> scrapedApprenticeships) throws IOException {
        List<String> existingIds = getExistingApprenticeshipIds();
        return scrapedApprenticeships.stream()
                .filter(apprenticeship -> !existingIds.contains(apprenticeship.getId()))
                .collect(Collectors.toList());
    }

    private <T extends Apprenticeship> void saveApprenticeships(
            List<T> apprenticeships, ApprenticeshipSource source) throws IOException {
        if (apprenticeships == null || apprenticeships.isEmpty()) {
            return;
        }

        String currentSheetName = getCurrentSheetName();
        ensureApprenticeshipsSheetExists(currentSheetName);
        List<List<Object>> values = convertApprenticeshipsToRows(apprenticeships, source);

        if (!values.isEmpty()) {
            String range =
                    String.format("%s!A%d", currentSheetName, getNextAvailableRow(currentSheetName));
            ValueRange body = new ValueRange().setValues(values);

            executeWithRetry(
                    () ->
                            sheetsService
                                    .spreadsheets()
                                    .values()
                                    .append(spreadsheetId, range, body)
                                    .setValueInputOption("RAW")
                                    .setInsertDataOption("INSERT_ROWS")
                                    .execute());
        }
    }

    /**
     * Retrieves all existing apprenticeship IDs from the spreadsheet.
     *
     * <p>This method reads the ID column (column A) from the current month's sheet
     * and returns a list of all apprenticeship IDs already tracked.
     *
     * @return list of existing apprenticeship IDs (empty list if no sheet exists)
     * @throws IOException if an error occurs while reading from the spreadsheet
     */
    public List<String> getExistingApprenticeshipIds() throws IOException {
        try {
            String currentSheetName = getCurrentSheetName();
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean hasApprenticeshipsSheet =
                    spreadsheet.getSheets().stream()
                            .anyMatch(s -> s.getProperties().getTitle().equals(currentSheetName));

            if (!hasApprenticeshipsSheet) {
                return new ArrayList<>();
            }

            String idColumnRange = String.format("%s!A2:A", currentSheetName);
            ValueRange response =
                    sheetsService.spreadsheets().values().get(spreadsheetId, idColumnRange).execute();

            List<String> ids = new ArrayList<>();
            if (response.getValues() != null) {
                response
                        .getValues()
                        .forEach(
                                row -> {
                                    if (!row.isEmpty()) {
                                        String id = row.getFirst().toString().trim();
                                        if (!id.isEmpty()) {
                                            ids.add(id);
                                        }
                                    }
                                });
            }
            return ids;
        } catch (Exception e) {
            logger.error("{}: Failed to get existing IDs: {}", LOG_PREFIX, e.getMessage());
            throw new IOException("Failed to get existing apprenticeship IDs", e);
        }
    }

    private void ensureApprenticeshipsSheetExists(String sheetName) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        boolean hasApprenticeshipsSheet =
                spreadsheet.getSheets().stream()
                        .anyMatch(s -> s.getProperties().getTitle().equals(sheetName));

        if (!hasApprenticeshipsSheet) {
            createApprenticeshipsSheet(sheetName);
        }
    }

    private <T extends Apprenticeship> List<List<Object>> convertApprenticeshipsToRows(
            List<T> apprenticeships, ApprenticeshipSource source) {
        return apprenticeships.stream()
                .filter(apprenticeship -> apprenticeship != null && apprenticeship.getId() != null)
                .map(
                        apprenticeship -> {
                            if (apprenticeship instanceof HigherinApprenticeship higherinApprenticeship) {
                                // RateMyApprenticeship apprenticeship format
                                return Arrays.<Object>asList(
                                        higherinApprenticeship.getId(),
                                        higherinApprenticeship.getTitle(),
                                        higherinApprenticeship.getCompanyName(),
                                        higherinApprenticeship.getLocation(),
                                        String.join(", ", higherinApprenticeship.getCategories()),
                                        higherinApprenticeship.getSalary(),
                                        higherinApprenticeship.getOpeningDate() != null
                                                ? higherinApprenticeship.getOpeningDate().toString()
                                                : "",
                                        higherinApprenticeship.getClosingDate() != null
                                                ? higherinApprenticeship.getClosingDate().toString()
                                                : "",
                                        higherinApprenticeship.getUrl(),
                                        source.getCode());
                            } else if (apprenticeship instanceof FindAnApprenticeship govApprenticeship) {
                                // GOV.UK apprenticeship format - no categories, has createdAtDate instead of
                                // openingDate
                                return Arrays.<Object>asList(
                                        govApprenticeship.getId(),
                                        govApprenticeship.getTitle(),
                                        govApprenticeship.getCompanyName(),
                                        govApprenticeship.getLocation(),
                                        "", // No categories for GOV.UK apprenticeships
                                        govApprenticeship.getSalary(),
                                        govApprenticeship.getCreatedAtDate() != null
                                                ? govApprenticeship.getCreatedAtDate().toString()
                                                : "",
                                        govApprenticeship.getClosingDate() != null
                                                ? govApprenticeship.getClosingDate().toString()
                                                : "",
                                        govApprenticeship.getUrl(),
                                        source.getCode());
                            } else {
                                // Fallback for any other apprenticeship type
                                return Arrays.<Object>asList(
                                        apprenticeship.getId(),
                                        apprenticeship.getTitle(),
                                        apprenticeship.getCompanyName(),
                                        apprenticeship.getLocation(),
                                        "",
                                        apprenticeship.getSalary(),
                                        "",
                                        apprenticeship.getClosingDate() != null
                                                ? apprenticeship.getClosingDate().toString()
                                                : "",
                                        apprenticeship.getUrl(),
                                        source.getCode());
                            }
                        })
                .collect(Collectors.toList());
    }

    private void executeWithRetry(IOOperation operation) throws IOException {
        IOException lastException = null;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                operation.execute();
                return;
            } catch (IOException e) {
                lastException = e;
                if (attempt < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry", ie);
                    }
                }
            }
        }
        throw lastException;
    }

    private int getNextAvailableRow(String sheetName) throws IOException {
        String rangeA1 = String.format("%s!A:A", sheetName);
        ValueRange response =
                sheetsService.spreadsheets().values().get(spreadsheetId, rangeA1).execute();

        return (response.getValues() == null) ? 1 : response.getValues().size() + 1;
    }

    /**
     * Schedules periodic apprenticeship processing and posting to Discord.
     *
     * <p>This method sets up a scheduled task that:
     * <ul>
     *   <li>Scrapes new apprenticeships from configured sources</li>
     *   <li>Compares them with existing apprenticeships in the spreadsheet</li>
     *   <li>Posts new apprenticeships to Discord channels</li>
     *   <li>Updates the spreadsheet with new entries</li>
     * </ul>
     *
     * <p>The task runs on a fixed schedule defined by the scheduler.
     *
     * @param jda the JDA instance for posting to Discord
     * @throws NullPointerException if jda is null
     */
    public void scheduleProcessNewApprenticeships(JDA jda) {
        logger.info("{}: Scheduling apprenticeship processing", LOG_PREFIX);
        Objects.requireNonNull(jda, "JDA instance cannot be null");

        scheduler.scheduleAtFixedRate(() -> processNewApprenticeshipsSafely(jda), 0, 1, TimeUnit.HOURS);
    }

    private void processNewApprenticeshipsSafely(JDA jda) {
        // Use virtual threads if available (Java 21+), otherwise use regular thread pool
        try {
            Thread.ofVirtual()
                    .start(
                            () -> {
                                try {
                                    processAndSaveNewApprenticeships(jda);
                                } catch (Exception e) {
                                    logger.error(
                                            "{}: Apprenticeship processing failed via virtual threads: {}",
                                            LOG_PREFIX,
                                            e.getMessage() + e.fillInStackTrace());
                                }
                            });
        } catch (UnsupportedOperationException e) {
            // Fallback for older Java versions
            new Thread(
                            () -> {
                                try {
                                    processAndSaveNewApprenticeships(jda);
                                } catch (Exception ex) {
                                    logger.error(
                                            "{}: Apprenticeship processing failed via normal threads: {}",
                                            LOG_PREFIX,
                                            ex.getMessage() + ex.fillInStackTrace());
                                }
                            })
                    .start();
        }
    }

    private void processAndSaveNewApprenticeships(JDA jda) {
        List<TextChannel> textChannels = getTextChannels(jda);
        ApprenticeshipScraper scraper = new ApprenticeshipScraper();

        // Process RMA apprenticeships first
        processRateMyApprenticeships(scraper, textChannels);

        // Then process GOV apprenticeships
        processFindAnApprenticeships(scraper, textChannels);
    }

    private void processRateMyApprenticeships(
            ApprenticeshipScraper scraper, List<TextChannel> textChannels) {
        try {
            List<HigherinApprenticeship> scrapedRmaApprenticeships =
                    scraper.scrapeRateMyApprenticeshipJobs();
            List<HigherinApprenticeship> newRmaApprenticeships =
                    filterNewApprenticeships(scrapedRmaApprenticeships);
            if (!newRmaApprenticeships.isEmpty()) {
                saveApprenticeships(newRmaApprenticeships, ApprenticeshipSource.RATE_MY_APPRENTICESHIP);
                sendToDiscord(newRmaApprenticeships, textChannels);
            }
        } catch (Exception e) {
            logger.error("Failed to process RMA apprenticeships: {}", e.getMessage());
        }
    }

    private void processFindAnApprenticeships(
            ApprenticeshipScraper scraper, List<TextChannel> textChannels) {
        try {
            List<FindAnApprenticeship> scrapedGovApprenticeships =
                    scraper.scrapeFindAnApprenticeshipJobs();
            List<FindAnApprenticeship> newGovApprenticeships =
                    filterNewApprenticeships(scrapedGovApprenticeships);
            if (!newGovApprenticeships.isEmpty()) {
                saveApprenticeships(newGovApprenticeships, ApprenticeshipSource.GOV_UK);
                sendToDiscord(newGovApprenticeships, textChannels);
            }
        } catch (Exception e) {
            logger.error("Failed to process GOV.UK apprenticeships: {}", e.getMessage());
        }
    }

    private <T extends Apprenticeship> void sendToDiscord(
            @NotNull List<T> newApprenticeships, List<TextChannel> textChannels) {
        final int BATCH_SIZE = 10;
        final int DELAY_MS = 1000;

        // Build the ping message once
        String pingMessage = buildPingMessage();

        for (TextChannel textChannel : textChannels) {
            if (textChannel == null) {
                logger.warn("Skipping null text channel");
                continue;
            }

            for (int i = 0; i < newApprenticeships.size(); i += BATCH_SIZE) {
                List<MessageEmbed> batchEmbeds =
                        newApprenticeships.stream()
                                .skip(i)
                                .limit(BATCH_SIZE)
                                .map(Apprenticeship::getEmbed)
                                .collect(Collectors.toList());

                // Send with ping message as content if there are people/roles to ping
                if (pingMessage != null && !pingMessage.isEmpty()) {
                    textChannel
                            .sendMessage(pingMessage)
                            .setEmbeds(batchEmbeds)
                            .queue(
                                    success ->
                                            logger.debug(
                                                    "Successfully sent batch of {} jobs to channel {} in guild {}",
                                                    batchEmbeds.size(),
                                                    textChannel.getId(),
                                                    textChannel.getGuild().getId()),
                                    error ->
                                            logger.error(
                                                    "Failed to send batch to channel {} in guild {}: {}",
                                                    textChannel.getId(),
                                                    textChannel.getGuild().getId(),
                                                    error.getMessage()));
                } else {
                    // No pings configured, send embeds only
                    textChannel
                            .sendMessageEmbeds(batchEmbeds)
                            .queue(
                                    success ->
                                            logger.debug(
                                                    "Successfully sent batch of {} jobs to channel {} in guild {}",
                                                    batchEmbeds.size(),
                                                    textChannel.getId(),
                                                    textChannel.getGuild().getId()),
                                    error ->
                                            logger.error(
                                                    "Failed to send batch to channel {} in guild {}: {}",
                                                    textChannel.getId(),
                                                    textChannel.getGuild().getId(),
                                                    error.getMessage()));
                }

                if (i + BATCH_SIZE < newApprenticeships.size()) {
                    try {
                        Thread.sleep(DELAY_MS);
                    } catch (InterruptedException e) {
                        logger.error("Sleep interrupted while sending batches: {}", e.getMessage());
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // Add delay between channels to avoid rate limiting
            if (textChannels.indexOf(textChannel) < textChannels.size() - 1) {
                try {
                    Thread.sleep(DELAY_MS);
                } catch (InterruptedException e) {
                    logger.error("Sleep interrupted between channels: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private String buildPingMessage() {
        StringBuilder pings = new StringBuilder();

        // Add a friendly message before pings
        pings.append("📢 **Hey there!** New opportunities just dropped! ");

        // Add role pings
        if (rolesToPing != null && !rolesToPing.isEmpty()) {
            for (String roleId : rolesToPing) {
                if (roleId != null && !roleId.isEmpty()) {
                    pings.append("<@&").append(roleId).append("> ");
                }
            }
        }

        return pings.toString().trim();
    }

    private List<TextChannel> getTextChannels(JDA jda) {
        List<TextChannel> channels = new ArrayList<>();

        for (DAConfig.GuildChannelConfig guildChannel : daConfig.guildChannels()) {
            try {
                var guild = jda.getGuildById(guildChannel.guildId());
                if (guild == null) {
                    logger.warn("Guild with ID {} not found", guildChannel.guildId());
                    continue;
                }

                var channel = guild.getTextChannelById(guildChannel.channelId());
                if (channel == null) {
                    logger.warn(
                            "Channel with ID {} not found in guild {}",
                            guildChannel.channelId(),
                            guildChannel.guildId());
                    continue;
                }

                channels.add(channel);
                logger.debug(
                        "Added channel {} from guild {} to notification list",
                        guildChannel.channelId(),
                        guildChannel.guildId());
            } catch (Exception e) {
                logger.error(
                        "Failed to get channel {} from guild {}: {}",
                        guildChannel.channelId(),
                        guildChannel.guildId(),
                        e.getMessage());
            }
        }

        if (channels.isEmpty()) {
            logger.error("No valid text channels found in configuration!");
        }

        return channels;
    }

    @FunctionalInterface
    private interface IOOperation {
        void execute() throws IOException;
    }

    /**
     * Retrieves all apprenticeships from the current year's spreadsheet for web viewing
     *
     * @return List of maps containing apprenticeship data
     * @throws IOException if there's an error reading from the spreadsheet
     */
    public List<Map<String, Object>> getAllJobsForWeb() throws IOException {
        List<Map<String, Object>> apprenticeshipsList = new ArrayList<>();

        try {
            String currentSheetName = getCurrentSheetName();
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean hasApprenticeshipSheet =
                    spreadsheet.getSheets().stream()
                            .anyMatch(s -> s.getProperties().getTitle().equals(currentSheetName));

            if (!hasApprenticeshipSheet) {
                logger.warn("No apprenticeships sheet found for current year");
                return apprenticeshipsList;
            }

            // Fetch all data from the sheet
            String dataRange = String.format("%s!A2:J", currentSheetName); // Skip header row
            ValueRange response =
                    sheetsService.spreadsheets().values().get(spreadsheetId, dataRange).execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                logger.info("No apprenticeships found in spreadsheet");
                return apprenticeshipsList;
            }

            // Parse each row into an apprenticeship map
            for (List<Object> row : values) {
                if (row.isEmpty()) continue;

                Map<String, Object> apprenticeship = new HashMap<>();

                // Map columns to apprenticeship fields
                apprenticeship.put("id", getStringValue(row, 0));
                apprenticeship.put("title", getStringValue(row, 1));
                apprenticeship.put("companyName", getStringValue(row, 2));
                apprenticeship.put("location", getStringValue(row, 3));

                // Parse categories
                String categoriesStr = getStringValue(row, 4);
                if (!categoriesStr.isEmpty()) {
                    apprenticeship.put("categories", Arrays.asList(categoriesStr.split(",\\s*")));
                } else {
                    apprenticeship.put("categories", Collections.emptyList());
                }

                apprenticeship.put("salary", getStringValue(row, 5));
                apprenticeship.put("openingDate", getStringValue(row, 6));
                apprenticeship.put("createdAtDate", getStringValue(row, 6)); // For GOV.UK apprenticeships
                apprenticeship.put("closingDate", getStringValue(row, 7));
                apprenticeship.put("url", getStringValue(row, 8));
                apprenticeship.put("source", getStringValue(row, 9));

                // Only add apprenticeships that have an ID and aren't expired
                if (!apprenticeship.get("id").toString().isEmpty()) {
                    String closingDateStr = apprenticeship.get("closingDate").toString();
                    if (!closingDateStr.isEmpty()) {
                        try {
                            LocalDate closingDate = LocalDate.parse(closingDateStr);
                            // Only include apprenticeships that haven't closed yet
                            if (closingDate.isAfter(LocalDate.now()) || closingDate.isEqual(LocalDate.now())) {
                                apprenticeshipsList.add(apprenticeship);
                            }
                        } catch (Exception e) {
                            // If date parsing fails, include the apprenticeship anyway
                            apprenticeshipsList.add(apprenticeship);
                        }
                    } else {
                        // No closing date, include it
                        apprenticeshipsList.add(apprenticeship);
                    }
                }
            }

            logger.info("Retrieved {} active apprenticeships for web view", apprenticeshipsList.size());

        } catch (Exception e) {
            logger.error("Failed to fetch apprenticeships for web: {}", e.getMessage());
            throw new IOException("Failed to fetch apprenticeships from spreadsheet", e);
        }

        return apprenticeshipsList;
    }

    private String getStringValue(List<Object> row, int index) {
        if (index < row.size() && row.get(index) != null) {
            return row.get(index).toString().trim();
        }
        return "";
    }

    private static final class Columns {
        static final String[] HEADERS = {
            "ID",
            "Title",
            "Company",
            "Location",
            "Categories",
            "Salary",
            "Opening Date",
            "Closing Date",
            "URL",
            "Source"
        };
    }
}
