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
import io.github.yusufsdiscordbot.mystiguardian.api.job.Job;
import io.github.yusufsdiscordbot.mystiguardian.api.job.JobSource;
import io.github.yusufsdiscordbot.mystiguardian.api.job.RateMyApprenticeshipJob;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
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
    private static final String SHEET_NAME = "Jobs";
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

    private void initializeSheet() throws IOException {
        logger.debug("{}: Starting sheet initialization", LOG_PREFIX);
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean hasJobsSheet =
                    spreadsheet.getSheets().stream()
                            .anyMatch(s -> s.getProperties().getTitle().equals(Columns.SHEET_NAME));

            if (!hasJobsSheet) {
                createJobsSheet();
            }

            ensureHeaders();
        } catch (Exception e) {
            logger.error("{}: Initialization failed: {}", LOG_PREFIX, e.getMessage());
            throw new IOException("Failed to initialize sheet", e);
        }
    }

    private void createJobsSheet() throws IOException {
        BatchUpdateSpreadsheetRequest request =
                new BatchUpdateSpreadsheetRequest()
                        .setRequests(
                                Collections.singletonList(
                                        new Request()
                                                .setAddSheet(
                                                        new AddSheetRequest()
                                                                .setProperties(
                                                                        new SheetProperties().setTitle(Columns.SHEET_NAME)))));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, request).execute();
        logger.info("{}: Created new Jobs sheet", LOG_PREFIX);
    }

    private void ensureHeaders() throws IOException {
        String headerRange = Columns.SHEET_NAME + HEADER_RANGE_NUMBER;
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
            logger.info("{}: Added headers to sheet", LOG_PREFIX);
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

        ensureJobsSheetExists();
        List<List<Object>> values = convertJobsToRows(jobs, source);

        if (!values.isEmpty()) {
            String range = String.format("%s!A%d", SHEET_NAME, getNextAvailableRow());
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
            Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
            boolean hasJobsSheet =
                    spreadsheet.getSheets().stream()
                            .anyMatch(s -> s.getProperties().getTitle().equals(SHEET_NAME));

            if (!hasJobsSheet) {
                return new ArrayList<>();
            }

            String idColumnRange = String.format("%s!A2:A", SHEET_NAME);
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

    private void ensureJobsSheetExists() throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        boolean hasJobsSheet =
                spreadsheet.getSheets().stream()
                        .anyMatch(s -> s.getProperties().getTitle().equals(SHEET_NAME));

        if (!hasJobsSheet) {
            createJobsSheet();
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
                                        job instanceof RateMyApprenticeshipJob
                                                ? String.join(", ", ((RateMyApprenticeshipJob) job).getCategories())
                                                : "",
                                        job.getSalary(),
                                        job instanceof RateMyApprenticeshipJob
                                                ? ((RateMyApprenticeshipJob) job).getOpeningDate()
                                                : "",
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

    private int getNextAvailableRow() throws IOException {
        String rangeA1 = String.format("%s!A:A", SHEET_NAME);
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
                                "{}: RateMyApprenticeshipJob processing failed: {}",
                                LOG_PREFIX,
                                e.getMessage() + e.fillInStackTrace());
                    }
                });
    }

    private void processAndSaveNewJobs(JDA jda) {
        TextChannel textChannel = getTextChannel(jda);
        ApprenticeshipScraper scraper = new ApprenticeshipScraper();

        // Process RMA jobs first
        processRateMyApprenticeshipJobs(scraper, textChannel);

        // Then process GOV jobs
        processFindAnApprenticeshipJobs(scraper, textChannel);
    }

    private void processRateMyApprenticeshipJobs(
            ApprenticeshipScraper scraper, TextChannel textChannel) {
        try {
            List<RateMyApprenticeshipJob> scrapedRmaJobs = scraper.scrapeRateMyApprenticeshipJobs();
            List<RateMyApprenticeshipJob> newRmaJobs = filterNewJobs(scrapedRmaJobs);
            if (!newRmaJobs.isEmpty()) {
                saveJobs(newRmaJobs, JobSource.RATE_MY_APPRENTICESHIP);
                sendToDiscord(newRmaJobs, textChannel);
            }
        } catch (Exception e) {
            logger.error("Failed to process RMA jobs: {}", e.getMessage());
        }
    }

    private void processFindAnApprenticeshipJobs(
            ApprenticeshipScraper scraper, TextChannel textChannel) {
        try {
            List<FindAnApprenticeshipJob> scrapedGovJobs = scraper.scrapeFindAnApprenticeshipJobs();
            List<FindAnApprenticeshipJob> newGovJobs = filterNewJobs(scrapedGovJobs);
            if (!newGovJobs.isEmpty()) {
                saveJobs(newGovJobs, JobSource.GOV_UK);
                sendToDiscord(newGovJobs, textChannel);
            }
        } catch (Exception e) {
            logger.error("Failed to process GOV.UK jobs: {}", e.getMessage());
        }
    }

    private <T extends Job> void sendToDiscord(@NotNull List<T> newJobs, TextChannel textChannel) {
        final int BATCH_SIZE = 10;
        final int DELAY_MS = 1000;

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
                            success -> logger.debug("Successfully sent batch of {} jobs", batchEmbeds.size()),
                            error -> logger.error("Failed to send batch: {}", error.getMessage()));

            if (i + BATCH_SIZE < newJobs.size()) {
                try {
                    Thread.sleep(DELAY_MS);
                } catch (InterruptedException e) {
                    logger.error("Sleep interrupted while sending batches: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private TextChannel getTextChannel(JDA jda) {
        return Objects.requireNonNull(
                Objects.requireNonNull(
                                jda.getGuildById(MystiGuardianUtils.getDAConfig().guildId()), "Guild id is null")
                        .getTextChannelById(MystiGuardianUtils.getDAConfig().channelId()),
                "Channel is null");
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
