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
import com.google.api.services.sheets.v4.model.ValueRange;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.api.job.Job;
import io.github.yusufsdiscordbot.mystiguardian.event.events.NewDAEvent;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class JobSpreadsheetManager {
    private final Sheets sheetsService;
    private final String spreadsheetId;
    private static final String SHEET_NAME = "Jobs";

    private static final class Columns {
        static final String[] HEADERS = {
            "ID", "Title", "Location", "Category", "Salary", "Opening Date", "Closing Date", "URL"
        };
        static final String ID = "A";
        static final String TITLE = "B";
        static final String LOCATION = "C";
        static final String CATEGORY = "D";
        static final String SALARY = "E";
        static final String OPENING_DATE = "F";
        static final String CLOSING_DATE = "G";
        static final String URL = "H";
        static final String RANGE = SHEET_NAME + "!" + ID + ":" + URL;
        static final String HEADER_RANGE = SHEET_NAME + "!" + ID + "1:" + URL + "1";
    }

    public JobSpreadsheetManager(Sheets sheetsService, String spreadsheetId) {
        if (sheetsService == null || spreadsheetId == null || spreadsheetId.isEmpty()) {
            throw new IllegalArgumentException(
                    "SheetsService and spreadsheetId must not be null or empty");
        }

        this.sheetsService = sheetsService;
        this.spreadsheetId = spreadsheetId;

        try {
            initializeSheet();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize spreadsheet", e);
        }
    }

    private void initializeSheet() throws IOException {
        ValueRange headerResponse =
                sheetsService.spreadsheets().values().get(spreadsheetId, Columns.HEADER_RANGE).execute();

        if (headerResponse.getValues() == null || headerResponse.getValues().isEmpty()) {
            ValueRange headers =
                    new ValueRange().setValues(Collections.singletonList(Arrays.asList(Columns.HEADERS)));

            sheetsService
                    .spreadsheets()
                    .values()
                    .update(spreadsheetId, Columns.HEADER_RANGE, headers)
                    .setValueInputOption("RAW")
                    .execute();
        }
    }

    public List<Job> filterNewJobs(List<Job> scrapedJobs) throws IOException {
        List<Job> newJobs = new ArrayList<>();
        List<String> existingIds = getExistingJobIds();

        for (Job job : scrapedJobs) {
            if (!existingIds.contains(job.getId())) {
                newJobs.add(job);
            }
        }
        return newJobs;
    }

    public List<String> getExistingJobIds() throws IOException {
        String idColumnRange = SHEET_NAME + "!" + Columns.ID + ":" + Columns.ID;

        ValueRange response =
                sheetsService.spreadsheets().values().get(spreadsheetId, idColumnRange).execute();

        List<String> ids = new ArrayList<>();
        if (response.getValues() != null) {
            response
                    .getValues()
                    .forEach(
                            row -> {
                                if (!row.isEmpty()) {
                                    ids.add(row.get(0).toString());
                                }
                            });
        }
        return ids;
    }

    public void saveJobs(List<Job> jobs) throws IOException {
        if (jobs == null || jobs.isEmpty()) {
            return;
        }

        List<List<Object>> values = new ArrayList<>();
        for (Job job : jobs) {
            if (job == null || job.getId() == null) {
                continue;
            }

            List<Object> row =
                    Arrays.asList(
                            job.getId(),
                            job.getTitle(),
                            job.getLocation(),
                            job.getCategory(),
                            job.getSalary(),
                            job.getOpeningDate() != null ? job.getOpeningDate().toString() : "",
                            job.getClosingDate() != null ? job.getClosingDate().toString() : "",
                            job.getUrl());
            values.add(row);
        }

        if (!values.isEmpty()) {
            ValueRange body = new ValueRange().setValues(values);
            sheetsService
                    .spreadsheets()
                    .values()
                    .append(spreadsheetId, Columns.RANGE, body)
                    .setValueInputOption("RAW")
                    .execute();
        }
    }

    private void processNewJobs(JDA jda) {
        val textChannel = getTextChannel(jda);

        MystiGuardianUtils.runInVirtualThread(
                () -> {
                    try {
                        ApprenticeshipScraper scraper = new ApprenticeshipScraper();
                        List<Job> scrapedJobs = scraper.scrapeJobs();

                        List<Job> newJobs = filterNewJobs(scrapedJobs);

                        if (!newJobs.isEmpty()) {
                            saveJobs(newJobs);
                            MystiGuardianConfig.getEventDispatcher()
                                    .dispatchEvent(new NewDAEvent(textChannel, newJobs));
                        }
                    } catch (IOException e) {
                        MystiGuardianUtils.logger.error("Failed to scrape jobs", e);
                    }
                });
    }

    private TextChannel getTextChannel(JDA jda) {
        return Objects.requireNonNull(
                Objects.requireNonNull(jda.getGuildById(MystiGuardianUtils.getDAConfig().guildId()))
                        .getTextChannelById(MystiGuardianUtils.getDAConfig().channelId()));
    }

    public void scheduleProcessNewJobs(JDA jda) {
        MystiGuardianUtils.getScheduler()
                .scheduleAtFixedRate(() -> processNewJobs(jda), 0, 1, TimeUnit.HOURS);
    }
}
