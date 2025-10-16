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
package io.github.yusufsdiscordbot.mystiguardian.api;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import io.github.yusufsdiscordbot.mystiguardian.api.job.FindAnApprenticeshipJob;
import io.github.yusufsdiscordbot.mystiguardian.api.job.HigherinJob;
import io.github.yusufsdiscordbot.mystiguardian.api.job.Job;
import io.github.yusufsdiscordbot.mystiguardian.api.job.JobSource;
import io.github.yusufsdiscordbot.mystiguardian.config.DAConfig;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class JobSpreadsheetManager {
    private static final String LOG_PREFIX = "JobSpreadsheetManager";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final String HEADER_RANGE_NUMBER = "!A1:J1";
    private final Sheets sheetsService;
    private final String spreadsheetId;

    public JobSpreadsheetManager(@NotNull Sheets sheetsService, @NotNull String spreadsheetId) {
        this.sheetsService = Objects.requireNonNull(sheetsService, "sheetsService cannot be null");
        this.spreadsheetId = Objects.requireNonNull(spreadsheetId, "spreadsheetId cannot be null");

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
        return "Jobs " + getAcademicYear();
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
                createJobsSheet(currentSheetName);
            }

            ensureHeaders(currentSheetName);
        } catch (Exception e) {
            logger.error("{}: Initialization failed: {}", LOG_PREFIX, e.getMessage());
            throw new IOException("Failed to initialize sheet", e);
        }
    }

    private void createJobsSheet(String sheetName) throws IOException {
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

    private <T extends Job> List<T> filterNewJobs(List<T> scrapedJobs) throws IOException {
        List<String> existingIds = getExistingJobIds();
        return scrapedJobs.stream()
                .filter(job -> !existingIds.contains(job.getId()))
                .collect(Collectors.toList());
    }

    private <T extends Job> void saveJobs(List<T> jobs, JobSource source) throws IOException {
        if (jobs == null || jobs.isEmpty()) {
            return;
        }

        String currentSheetName = getCurrentSheetName();
        ensureJobsSheetExists(currentSheetName);
        List<List<Object>> values = convertJobsToRows(jobs, source);

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

    public List<String> getExistingJobIds() throws IOException {
        try {
            String currentSheetName = getCurrentSheetName();
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean hasJobsSheet =
                    spreadsheet.getSheets().stream()
                            .anyMatch(s -> s.getProperties().getTitle().equals(currentSheetName));

            if (!hasJobsSheet) {
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
            throw new IOException("Failed to get existing job IDs", e);
        }
    }

    private void ensureJobsSheetExists(String sheetName) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        boolean hasJobsSheet =
                spreadsheet.getSheets().stream()
                        .anyMatch(s -> s.getProperties().getTitle().equals(sheetName));

        if (!hasJobsSheet) {
            createJobsSheet(sheetName);
        }
    }

    private <T extends Job> List<List<Object>> convertJobsToRows(List<T> jobs, JobSource source) {
        return jobs.stream()
                .filter(job -> job != null && job.getId() != null)
                .map(
                        job -> {
                            if (job instanceof HigherinJob higherinJob) {
                                // RateMyApprenticeship job format
                                return Arrays.<Object>asList(
                                        higherinJob.getId(),
                                        higherinJob.getTitle(),
                                        higherinJob.getCompanyName(),
                                        higherinJob.getLocation(),
                                        String.join(", ", higherinJob.getCategories()),
                                        higherinJob.getSalary(),
                                        higherinJob.getOpeningDate() != null
                                                ? higherinJob.getOpeningDate().toString()
                                                : "",
                                        higherinJob.getClosingDate() != null
                                                ? higherinJob.getClosingDate().toString()
                                                : "",
                                        higherinJob.getUrl(),
                                        source.getCode());
                            } else if (job instanceof FindAnApprenticeshipJob govJob) {
                                // GOV.UK job format - no categories, has createdAtDate instead of openingDate
                                return Arrays.<Object>asList(
                                        govJob.getId(),
                                        govJob.getTitle(),
                                        govJob.getCompanyName(),
                                        govJob.getLocation(),
                                        "", // No categories for GOV.UK jobs
                                        govJob.getSalary(),
                                        govJob.getCreatedAtDate() != null ? govJob.getCreatedAtDate().toString() : "",
                                        govJob.getClosingDate() != null ? govJob.getClosingDate().toString() : "",
                                        govJob.getUrl(),
                                        source.getCode());
                            } else {
                                // Fallback for any other job type
                                return Arrays.<Object>asList(
                                        job.getId(),
                                        job.getTitle(),
                                        job.getCompanyName(),
                                        job.getLocation(),
                                        "",
                                        job.getSalary(),
                                        "",
                                        job.getClosingDate() != null ? job.getClosingDate().toString() : "",
                                        job.getUrl(),
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

    public void scheduleProcessNewJobs(JDA jda) {
        logger.info("{}: Scheduling job processing", LOG_PREFIX);
        Objects.requireNonNull(jda, "JDA instance cannot be null");

        MystiGuardianUtils.getScheduler()
                .scheduleAtFixedRate(() -> processNewJobsSafely(jda), 0, 1, TimeUnit.HOURS);
    }

    private void processNewJobsSafely(JDA jda) {
        MystiGuardianUtils.runInVirtualThread(
                () -> {
                    try {
                        processAndSaveNewJobs(jda);
                    } catch (Exception e) {
                        logger.error(
                                "{}: HigherinJob processing failed: {}",
                                LOG_PREFIX,
                                e.getMessage() + e.fillInStackTrace());
                    }
                });
    }

    private void processAndSaveNewJobs(JDA jda) {
        List<TextChannel> textChannels = getTextChannels(jda);
        ApprenticeshipScraper scraper = new ApprenticeshipScraper();

        // Process RMA jobs first
        processRateMyApprenticeshipJobs(scraper, textChannels);

        // Then process GOV jobs
        processFindAnApprenticeshipJobs(scraper, textChannels);
    }

    private void processRateMyApprenticeshipJobs(
            ApprenticeshipScraper scraper, List<TextChannel> textChannels) {
        try {
            List<HigherinJob> scrapedRmaJobs = scraper.scrapeRateMyApprenticeshipJobs();
            List<HigherinJob> newRmaJobs = filterNewJobs(scrapedRmaJobs);
            if (!newRmaJobs.isEmpty()) {
                saveJobs(newRmaJobs, JobSource.RATE_MY_APPRENTICESHIP);
                sendToDiscord(newRmaJobs, textChannels);
            }
        } catch (Exception e) {
            logger.error("Failed to process RMA jobs: {}", e.getMessage());
        }
    }

    private void processFindAnApprenticeshipJobs(
            ApprenticeshipScraper scraper, List<TextChannel> textChannels) {
        try {
            List<FindAnApprenticeshipJob> scrapedGovJobs = scraper.scrapeFindAnApprenticeshipJobs();
            List<FindAnApprenticeshipJob> newGovJobs = filterNewJobs(scrapedGovJobs);
            if (!newGovJobs.isEmpty()) {
                saveJobs(newGovJobs, JobSource.GOV_UK);
                sendToDiscord(newGovJobs, textChannels);
            }
        } catch (Exception e) {
            logger.error("Failed to process GOV.UK jobs: {}", e.getMessage());
        }
    }

    private <T extends Job> void sendToDiscord(
            @NotNull List<T> newJobs, List<TextChannel> textChannels) {
        final int BATCH_SIZE = 10;
        final int DELAY_MS = 1000;

        // Build the ping message once
        String pingMessage = buildPingMessage();

        for (TextChannel textChannel : textChannels) {
            if (textChannel == null) {
                logger.warn("Skipping null text channel");
                continue;
            }

            for (int i = 0; i < newJobs.size(); i += BATCH_SIZE) {
                List<MessageEmbed> batchEmbeds =
                        newJobs.stream()
                                .skip(i)
                                .limit(BATCH_SIZE)
                                .map(Job::getEmbed)
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

                if (i + BATCH_SIZE < newJobs.size()) {
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
        List<String> rolesToPing = MystiGuardianUtils.getMainConfig().rolesToPing();
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
        DAConfig config = MystiGuardianUtils.getDAConfig();

        for (DAConfig.GuildChannelConfig guildChannel : config.guildChannels()) {
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
     * Retrieves all jobs from the current year's spreadsheet for web viewing
     *
     * @return List of maps containing job data
     * @throws IOException if there's an error reading from the spreadsheet
     */
    public List<Map<String, Object>> getAllJobsForWeb() throws IOException {
        List<Map<String, Object>> jobsList = new ArrayList<>();

        try {
            String currentSheetName = getCurrentSheetName();
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean hasJobsSheet =
                    spreadsheet.getSheets().stream()
                            .anyMatch(s -> s.getProperties().getTitle().equals(currentSheetName));

            if (!hasJobsSheet) {
                logger.warn("No jobs sheet found for current year");
                return jobsList;
            }

            // Fetch all data from the sheet
            String dataRange = String.format("%s!A2:J", currentSheetName); // Skip header row
            ValueRange response =
                    sheetsService.spreadsheets().values().get(spreadsheetId, dataRange).execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                logger.info("No jobs found in spreadsheet");
                return jobsList;
            }

            // Parse each row into a job map
            for (List<Object> row : values) {
                if (row.isEmpty()) continue;

                Map<String, Object> job = new HashMap<>();

                // Map columns to job fields
                job.put("id", getStringValue(row, 0));
                job.put("title", getStringValue(row, 1));
                job.put("companyName", getStringValue(row, 2));
                job.put("location", getStringValue(row, 3));

                // Parse categories
                String categoriesStr = getStringValue(row, 4);
                if (!categoriesStr.isEmpty()) {
                    job.put("categories", Arrays.asList(categoriesStr.split(",\\s*")));
                } else {
                    job.put("categories", Collections.emptyList());
                }

                job.put("salary", getStringValue(row, 5));
                job.put("openingDate", getStringValue(row, 6));
                job.put("createdAtDate", getStringValue(row, 6)); // For GOV.UK jobs
                job.put("closingDate", getStringValue(row, 7));
                job.put("url", getStringValue(row, 8));
                job.put("source", getStringValue(row, 9));

                // Only add jobs that have an ID and aren't expired
                if (!job.get("id").toString().isEmpty()) {
                    String closingDateStr = job.get("closingDate").toString();
                    if (!closingDateStr.isEmpty()) {
                        try {
                            LocalDate closingDate = LocalDate.parse(closingDateStr);
                            // Only include jobs that haven't closed yet
                            if (closingDate.isAfter(LocalDate.now()) || closingDate.isEqual(LocalDate.now())) {
                                jobsList.add(job);
                            }
                        } catch (Exception e) {
                            // If date parsing fails, include the job anyway
                            jobsList.add(job);
                        }
                    } else {
                        // No closing date, include it
                        jobsList.add(job);
                    }
                }
            }

            logger.info("Retrieved {} active jobs for web view", jobsList.size());

        } catch (Exception e) {
            logger.error("Failed to fetch jobs for web: {}", e.getMessage());
            throw new IOException("Failed to fetch jobs from spreadsheet", e);
        }

        return jobsList;
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
