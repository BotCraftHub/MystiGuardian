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
                        job ->
                                Arrays.<Object>asList(
                                        job.getId(),
                                        job.getTitle(),
                                        job.getCompanyName(),
                                        job.getLocation(),
                                        job instanceof HigherinJob
                                                ? String.join(", ", ((HigherinJob) job).getCategories())
                                                : "",
                                        job.getSalary(),
                                        job instanceof HigherinJob ? ((HigherinJob) job).getOpeningDate() : "",
                                        job.getClosingDate() != null ? job.getClosingDate().toString() : "",
                                        job.getUrl(),
                                        source.getCode()))
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

    /**
     * Sync spreadsheet jobs with Discord channels. Posts any jobs from the spreadsheet that are not
     * found in the Discord channel history. Useful for initial setup or recovery after message
     * deletion.
     */
    public void syncSpreadsheetToDiscord(JDA jda) {
        logger.info("{}: Starting sync of spreadsheet to Discord channels", LOG_PREFIX);
        List<TextChannel> textChannels = getTextChannels(jda);

        try {
            // Get all jobs from the current year's spreadsheet
            List<Map<String, String>> allJobs = getAllJobsFromSpreadsheet();

            if (allJobs.isEmpty()) {
                logger.info("{}: No jobs found in spreadsheet to sync", LOG_PREFIX);
                return;
            }

            logger.info("{}: Found {} jobs in spreadsheet", LOG_PREFIX, allJobs.size());

            for (TextChannel channel : textChannels) {
                try {
                    syncChannelWithSpreadsheet(channel, allJobs);
                } catch (Exception e) {
                    logger.error(
                            "{}: Failed to sync channel {} in guild {}: {}",
                            LOG_PREFIX,
                            channel.getId(),
                            channel.getGuild().getId(),
                            e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("{}: Failed to sync spreadsheet: {}", LOG_PREFIX, e.getMessage());
        }
    }

    private void syncChannelWithSpreadsheet(TextChannel channel, List<Map<String, String>> allJobs) {
        logger.info(
                "{}: Syncing channel {} in guild {}",
                LOG_PREFIX,
                channel.getId(),
                channel.getGuild().getId());

        // Get job IDs that already exist in Discord channel
        Set<String> postedJobIds = getPostedJobIdsFromChannel(channel);
        logger.info("{}: Found {} jobs already posted in channel", LOG_PREFIX, postedJobIds.size());

        // Filter jobs that haven't been posted yet
        List<Map<String, String>> missingJobs =
                allJobs.stream()
                        .filter(job -> !postedJobIds.contains(job.get("id")))
                        .collect(Collectors.toList());

        if (missingJobs.isEmpty()) {
            logger.info("{}: Channel is already in sync, no missing jobs", LOG_PREFIX);
            return;
        }

        logger.info("{}: Found {} missing jobs to post to channel", LOG_PREFIX, missingJobs.size());

        // Convert missing jobs to Job objects and send them
        List<Job> jobsToPost = new ArrayList<>();
        for (Map<String, String> jobData : missingJobs) {
            try {
                Job job = reconstructJobFromSpreadsheet(jobData);
                if (job != null) {
                    jobsToPost.add(job);
                }
            } catch (Exception e) {
                logger.error(
                        "{}: Failed to reconstruct job {}: {}", LOG_PREFIX, jobData.get("id"), e.getMessage());
            }
        }

        if (!jobsToPost.isEmpty()) {
            logger.info("{}: Posting {} missing jobs to channel", LOG_PREFIX, jobsToPost.size());
            sendToDiscord(jobsToPost, Collections.singletonList(channel));
        }
    }

    private Set<String> getPostedJobIdsFromChannel(TextChannel channel) {
        Set<String> jobIds = new HashSet<>();

        try {
            // Retrieve message history (last 1000 messages should cover most cases)
            channel
                    .getIterableHistory()
                    .takeAsync(1000)
                    .thenAccept(
                            messages -> {
                                for (var message : messages) {
                                    // Extract job URLs from embeds
                                    for (MessageEmbed embed : message.getEmbeds()) {
                                        for (MessageEmbed.Field field : embed.getFields()) {
                                            if ("Apply Here".equals(field.getName()) && field.getValue() != null) {
                                                String url = field.getValue();
                                                String jobId = extractJobIdFromUrl(url);
                                                if (jobId != null) {
                                                    jobIds.add(jobId);
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                    .join(); // Wait for completion
        } catch (Exception e) {
            logger.error(
                    "{}: Failed to retrieve message history from channel: {}", LOG_PREFIX, e.getMessage());
        }

        return jobIds;
    }

    private String extractJobIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Extract ID from HigherIn URL: https://higherin.com/jobs/35439/...
        if (url.contains("higherin.com/jobs/")) {
            String[] parts = url.split("/jobs/");
            if (parts.length > 1) {
                String[] idParts = parts[1].split("/");
                if (idParts.length > 0) {
                    return idParts[0];
                }
            }
        }

        // Extract ID from GOV.UK URL:
        // https://www.findapprenticeship.service.gov.uk/apprenticeships/VAC1234567890
        if (url.contains("findapprenticeship.service.gov.uk/apprenticeships/")) {
            String[] parts = url.split("/apprenticeships/");
            if (parts.length > 1) {
                return parts[1].split("/")[0];
            }
        }

        return null;
    }

    private List<Map<String, String>> getAllJobsFromSpreadsheet() throws IOException {
        List<Map<String, String>> jobs = new ArrayList<>();
        String currentSheetName = getCurrentSheetName();

        try {
            // Get all data from the sheet
            String dataRange = String.format("%s!A2:J", currentSheetName);
            ValueRange response =
                    sheetsService.spreadsheets().values().get(spreadsheetId, dataRange).execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return jobs;
            }

            // Convert rows to job maps
            for (List<Object> row : values) {
                if (row.isEmpty()) {
                    continue;
                }

                Map<String, String> job = new HashMap<>();
                job.put("id", row.size() > 0 ? row.get(0).toString() : "");
                job.put("title", row.size() > 1 ? row.get(1).toString() : "");
                job.put("company", row.size() > 2 ? row.get(2).toString() : "");
                job.put("location", row.size() > 3 ? row.get(3).toString() : "");
                job.put("categories", row.size() > 4 ? row.get(4).toString() : "");
                job.put("salary", row.size() > 5 ? row.get(5).toString() : "");
                job.put("openingDate", row.size() > 6 ? row.get(6).toString() : "");
                job.put("closingDate", row.size() > 7 ? row.get(7).toString() : "");
                job.put("url", row.size() > 8 ? row.get(8).toString() : "");
                job.put("source", row.size() > 9 ? row.get(9).toString() : "");

                jobs.add(job);
            }
        } catch (Exception e) {
            logger.error("{}: Failed to get jobs from spreadsheet: {}", LOG_PREFIX, e.getMessage());
            throw new IOException("Failed to retrieve jobs from spreadsheet", e);
        }

        return jobs;
    }

    private Job reconstructJobFromSpreadsheet(Map<String, String> jobData) {
        String source = jobData.get("source");

        if ("RMA".equals(source)) {
            return reconstructHigherinJob(jobData);
        } else if ("GOV".equals(source)) {
            return reconstructGovJob(jobData);
        }

        return null;
    }

    private HigherinJob reconstructHigherinJob(Map<String, String> data) {
        HigherinJob job = new HigherinJob();
        job.setId(data.get("id"));
        job.setTitle(data.get("title"));
        job.setCompanyName(data.get("company"));
        job.setLocation(data.get("location"));
        job.setSalary(data.get("salary"));
        job.setUrl(data.get("url"));

        // Parse categories
        String categoriesStr = data.get("categories");
        if (categoriesStr != null && !categoriesStr.isEmpty()) {
            List<String> categories =
                    Arrays.stream(categoriesStr.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
            job.setCategories(categories);
        }

        // Parse dates
        try {
            String closingDateStr = data.get("closingDate");
            if (closingDateStr != null && !closingDateStr.isEmpty()) {
                job.setClosingDate(LocalDate.parse(closingDateStr));
            }
        } catch (Exception e) {
            logger.warn("{}: Failed to parse closing date for job {}", LOG_PREFIX, data.get("id"));
        }

        return job;
    }

    private FindAnApprenticeshipJob reconstructGovJob(Map<String, String> data) {
        FindAnApprenticeshipJob job = new FindAnApprenticeshipJob();
        job.setId(data.get("id"));
        job.setName(data.get("title"));
        job.setCompanyName(data.get("company"));
        job.setLocation(data.get("location"));
        job.setSalary(data.get("salary"));
        job.setUrl(data.get("url"));

        // Parse dates
        try {
            String closingDateStr = data.get("closingDate");
            if (closingDateStr != null && !closingDateStr.isEmpty()) {
                job.setClosingDate(LocalDate.parse(closingDateStr));
            }

            String createdDateStr = data.get("openingDate");
            if (createdDateStr != null && !createdDateStr.isEmpty()) {
                job.setCreatedAtDate(LocalDate.parse(createdDateStr));
            }
        } catch (Exception e) {
            logger.warn("{}: Failed to parse dates for job {}", LOG_PREFIX, data.get("id"));
        }

        return job;
    }

    @FunctionalInterface
    private interface IOOperation {
        void execute() throws IOException;
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
        static final String SHEET_NAME = "Jobs";
        static final String DEFAULT_SHEET_NAME = "DAs";
        static final String HEADER_RANGE = DEFAULT_SHEET_NAME + HEADER_RANGE_NUMBER;
    }
}
