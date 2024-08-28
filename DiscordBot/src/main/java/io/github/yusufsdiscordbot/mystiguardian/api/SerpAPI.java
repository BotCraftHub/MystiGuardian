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
import io.github.yusufsdiscordbot.mystiguardian.utils.ResultStorage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                throw new IOException("Search failed");
            }

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
                Map.of("q", query, "location", "United Kingdom"),
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
            val title = result.path("title").asText("No title available");
            val link = result.path("link").asText("");

            if (!filteredOutLink(link)) {
                // TODO: Later on check if there any new urls and send them to the channel

                val snippet = result.path("snippet").asText("No description available");

                String fieldContent = String.format("(%s)\n%s", link, snippet);

                fieldContent =
                        fieldContent.length() > 1024 ? fieldContent.substring(0, 1021) + "..." : fieldContent;

                embed.addField(title, fieldContent, false);
            }
        }
        return embed;
    }

    private boolean filteredOutLink(String link) {
        try {
            URI uri = new URI(link);
            String host = uri.getHost();

            // List of domains to filter out
            String[] filteredDomains = {"reddit.com", "facebook.com", "thestudentroom.co.uk"};

            // Check if the host is in the list of filtered domains
            for (String domain : filteredDomains) {
                if (host != null && host.contains(domain)) {
                    return true;
                }
            }
        } catch (URISyntaxException e) {
            MystiGuardianUtils.logger.error("Failed to parse URI", e);
        }
        return false;
    }

    public void scheduleSearchAndSendResponse(JDA api) {
        MystiGuardianUtils.getScheduler()
                .scheduleAtFixedRate(
                        () -> searchAndSendResponse(api),
                        0, // initial delay
                        12, // period
                        TimeUnit.HOURS);
    }

    public void searchAndSendResponse(JDA api) {
        MystiGuardianUtils.runInVirtualThread(
                () -> {
                    try {
                        MystiGuardianUtils.logger.info("Running SERP API search...");
                        String query = MystiGuardianUtils.getSerpAPIConfig().query();
                        JsonNode result = search(query);

                        if (result.isEmpty() || result.path("organic_results").isEmpty()) {
                            MystiGuardianUtils.logger.info("No results found for query: {}", query);
                            return;
                        }
                        try {
                            ResultStorage.storeResults(query, result);
                        } catch (IOException e) {
                            MystiGuardianUtils.logger.error("Failed to store search results", e);
                        }

                        EmbedBuilder embed = parseJsonToEmbed(result);
                        sendEmbedToChannel(api, embed);
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

    private void sendEmbedToChannel(@NotNull JDA api, EmbedBuilder embed) {
        api.getGuildById(MystiGuardianUtils.getSerpAPIConfig().guildId())
                .getTextChannelById(MystiGuardianUtils.getSerpAPIConfig().channelId())
                .sendMessageEmbeds(embed.build())
                .queue();
    }

    private void handleSearchFailure(@NotNull JDA api, Throwable ex) {
        api.getGuildById(MystiGuardianUtils.getSerpAPIConfig().guildId())
                .getTextChannelById(MystiGuardianUtils.getSerpAPIConfig().channelId())
                .sendMessage("Failed to perform search: " + ex.getMessage() + ex)
                .queue();
    }

    private void sendNothingFoundMessage(@NotNull JDA api) {
        api.getGuildById(MystiGuardianUtils.getSerpAPIConfig().guildId())
                .getTextChannelById(MystiGuardianUtils.getSerpAPIConfig().channelId())
                .sendMessage("No new relevant results found.")
                .queue();
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
