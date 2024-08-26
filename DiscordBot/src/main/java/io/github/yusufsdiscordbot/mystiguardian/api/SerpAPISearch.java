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
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

public class SerpAPISearch {
    private static final int TOTAL_CREDITS_PER_MONTH = 100;
    private static final LocalDate START_DATE = LocalDate.of(2024, 8, 20);
    private static final LocalDate END_DATE = LocalDate.of(2025, 5, 31);
    private static final int MAX_SEARCHES_PER_DAY = 2;

    private final ObjectMapper objectMapper;
    private int remainingCreditsPerMonth;
    private int searchesToday;
    private LocalDate lastSearchDate;

    public SerpAPISearch() {
        this.objectMapper = new ObjectMapper();
        this.remainingCreditsPerMonth = TOTAL_CREDITS_PER_MONTH;
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
            }

            MystiGuardianUtils.logger.info(
                    "Search completed. Remaining credits: {}", remainingCreditsPerMonth);
            return jsonResponse;
        } catch (Exception e) {
            MystiGuardianUtils.logger.error("Search failed", e);
            throw new RuntimeException("Search failed", e);
        }
    }

    private GoogleSearch getGoogleSearch(String query) {
        MystiGuardianUtils.logger.info("Creating GoogleSearch object for query: {}", query);
        return new GoogleSearch(
                Map.of("q", query, "location", "United Kingdom, UK, England"),
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

            val aboutThisResult = result.path("about_this_result");
            val keywords = aboutThisResult.path("keywords").asText();
            val region = aboutThisResult.path("region").asText();

            embed.addField(
                    title,
                    String.format(
                            "Keywords: %s\nRegion: %s\nLink: %s\nDisplayed Link: %s\nSnippet: %s",
                            keywords, region, link, displayed_link, snippet));
        }

        return embed;
    }

    public void searchAndSendResponse(DiscordApi api) {
        MystiGuardianUtils.runInVirtualThread(
                () -> {
                    try {
                        JsonNode result = search(MystiGuardianUtils.getSerpAPIConfig().query());
                        EmbedBuilder embed = parseJsonToEmbed(result);
                        sendEmbedToChannel(api, embed);
                    } catch (IOException e) {
                        MystiGuardianUtils.logger.error("Failed to perform search", e);
                        handleSearchFailure(api, e);
                    } catch (Exception e) {
                        MystiGuardianUtils.logger.error("Failed to parse JSON", e);
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
                .ifPresent(channel -> channel.sendMessage("Failed to perform search: " + ex.getMessage()));
    }
}
