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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.api.serp.GoogleSearch;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.ResultFilter;
import io.github.yusufsdiscordbot.mystiguardian.utils.ResultStorage;
import io.github.yusufsdiscordbot.mystiguardian.utils.SourceFilter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

public class SerpAPI {
    private static final int TOTAL_CREDITS_PER_MONTH = 100;
    private static final LocalDate START_DATE = LocalDate.of(2024, 8, 20);
    private static final LocalDate END_DATE = LocalDate.of(2025, 5, 31);
    private static final int MAX_SEARCHES_PER_DAY = 2;
    private static final String CREDITS_FILE_PATH = "remaining_credits.json";

    private final ObjectMapper objectMapper;
    private int remainingCreditsPerMonth;
    private int searchesToday;
    private LocalDate lastSearchDate;

    public SerpAPI() {
        this.objectMapper = new ObjectMapper();
        loadRemainingCredits();
        this.searchesToday = 0;
        this.lastSearchDate = LocalDate.now();
    }

    private boolean isWithinSearchPeriod() {
        val currentDate = LocalDate.now();
        return !currentDate.isBefore(START_DATE) && !currentDate.isAfter(END_DATE);
    }

    private int calculateDaysRemainingInMonth() {
        val currentDate = LocalDate.now();
        val endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        return (int) ChronoUnit.DAYS.between(currentDate, endOfMonth) + 1;
    }

    private boolean canPerformSearch() {
        val currentDate = LocalDate.now();
        if (!currentDate.equals(lastSearchDate)) {
            searchesToday = 0;
            lastSearchDate = currentDate;
        }
        if (!isWithinSearchPeriod()) {
            return false;
        }
        int daysRemainingInMonth = calculateDaysRemainingInMonth();
        if (daysRemainingInMonth <= 0
                || remainingCreditsPerMonth <= 0
                || searchesToday >= MAX_SEARCHES_PER_DAY) {
            return false;
        }
        int maxSearchesPerDay =
                (int) Math.ceil((double) remainingCreditsPerMonth / daysRemainingInMonth);
        return maxSearchesPerDay > 0;
    }

    @NotNull
    private JsonNode search(String query) throws IOException {
        if (!canPerformSearch()) {
            throw new IOException("Search limit exceeded or insufficient credits.");
        }

        try {
            val googleSearch = getGoogleSearch(query);

            val jsonResponse = googleSearch.getJson();

            synchronized (this) {
                remainingCreditsPerMonth--;
                searchesToday++;
                saveRemainingCredits();
            }

            return jsonResponse;
        } catch (Exception e) {
            MystiGuardianUtils.logger.error("Search failed", e);
            throw new RuntimeException("Search failed", e);
        }
    }

    private GoogleSearch getGoogleSearch(String query) {
        return new GoogleSearch(
                Map.of("q", query, "location", "United Kingdom", "hl", "en", "gl", "uk"),
                MystiGuardianUtils.getSerpAPIConfig().apiKey());
    }

    @NotNull
    private EmbedBuilder parseAndReturnEmbed(String jsonResponse) throws JsonProcessingException {
        val rootNode = objectMapper.readTree(jsonResponse);
        val results = rootNode.path("organic_results");
        val embed = new EmbedBuilder();

        embed.setTitle("Search Results");
        embed.setDescription("Here are the search results for your query");

        for (val result : results) {
            val title = result.path("title").asText();
            val link = result.path("link").asText();
            val snippet = result.path("snippet").asText();
            val displayed_link = result.path("displayed_link").asText();

            embed.addField(
                    title,
                    String.format("[%s](%s)\n%s", displayed_link, link, snippet)
                            .substring(0, Math.min(1024, snippet.length())),
                    false);
        }

        return embed;
    }

    public void searchAndSendResponse(DiscordApi api) {
        MystiGuardianUtils.runInVirtualThread(
                () -> {
                    try {
                        val result = search(MystiGuardianUtils.getSerpAPIConfig().query());

                        ResultStorage.storeResults(MystiGuardianUtils.getSerpAPIConfig().query(), result);

                        val newLinks =
                                ResultFilter.getNewResults(
                                        MystiGuardianUtils.getSerpAPIConfig().query(), result, objectMapper);

                        // If there are no new links, there's no need to continue
                        if (newLinks.isEmpty()) {
                            MystiGuardianUtils.logger.info("No new results to display.");
                            return;
                        }

                        // Filter out results from excluded sources
                        val excludedSources = Set.of("reddit.com", "facebook.com");
                        val filteredResults =
                                SourceFilter.filterBySource(result, excludedSources, objectMapper);

                        // Further filter the results to only include the new links
                        val newFilteredResults =
                                ResultFilter.filterResultsByLinks(filteredResults, newLinks, objectMapper);

                        if (!newFilteredResults.isEmpty()) {
                            EmbedBuilder embed = parseJsonToEmbed(newFilteredResults);
                            sendEmbedToChannel(api, embed);
                        } else {
                            MystiGuardianUtils.logger.info("No new relevant results after filtering.");
                        }
                    } catch (IOException e) {
                        MystiGuardianUtils.logger.error("Failed to perform search", e);
                        handleSearchFailure(api, e);
                    }
                });
    }

    @NotNull
    private EmbedBuilder parseJsonToEmbed(@NotNull JsonNode jsonNode) {
        try {
            return parseAndReturnEmbed(jsonNode.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    private void sendEmbedToChannel(@NotNull DiscordApi api, EmbedBuilder embed) {
        api.getServerById(MystiGuardianUtils.getSerpAPIConfig().guildId())
                .flatMap(
                        server -> server.getTextChannelById(MystiGuardianUtils.getSerpAPIConfig().channelId()))
                .ifPresent(channel -> channel.sendMessage(embed));
    }

    private void handleSearchFailure(@NotNull DiscordApi api, Throwable ex) {
        api.getServerById(MystiGuardianUtils.getSerpAPIConfig().guildId())
                .flatMap(
                        server -> server.getTextChannelById(MystiGuardianUtils.getSerpAPIConfig().channelId()))
                .ifPresent(
                        channel -> channel.sendMessage("Failed to perform search: " + ex.getMessage() + ex));
    }

    private void saveRemainingCredits() {
        val rootNode = objectMapper.createObjectNode();
        rootNode.put("remainingCreditsPerMonth", remainingCreditsPerMonth);
        rootNode.put("lastUpdated", LocalDate.now().toString());

        try {
            objectMapper.writeValue(new File(CREDITS_FILE_PATH), rootNode);
        } catch (IOException e) {
            MystiGuardianUtils.logger.error("Failed to save remaining credits", e);
        }
    }

    private void loadRemainingCredits() {
        val creditsFile = new File(CREDITS_FILE_PATH);

        if (creditsFile.exists()) {
            try {
                JsonNode rootNode = objectMapper.readTree(creditsFile);
                this.lastSearchDate = LocalDate.parse(rootNode.path("lastUpdated").asText());

                if (lastSearchDate.getMonth() != LocalDate.now().getMonth()) {
                    // Reset credits for the new month
                    this.remainingCreditsPerMonth = TOTAL_CREDITS_PER_MONTH;
                } else {
                    this.remainingCreditsPerMonth =
                            rootNode.path("remainingCreditsPerMonth").asInt(TOTAL_CREDITS_PER_MONTH);
                }
            } catch (IOException e) {
                MystiGuardianUtils.logger.error("Failed to load remaining credits", e);
            }
        } else {
            this.remainingCreditsPerMonth = TOTAL_CREDITS_PER_MONTH;
            this.lastSearchDate = LocalDate.now();
        }
    }
}
