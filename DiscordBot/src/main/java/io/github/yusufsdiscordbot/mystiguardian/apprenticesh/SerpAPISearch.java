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
package io.github.yusufsdiscordbot.mystiguardian.apprenticesh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

public class SerpAPISearch {
    private static final int TOTAL_CREDITS = 2500;
    private static final LocalDate START_DATE = LocalDate.of(2024, 9, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 5, 31);

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private int remainingCredits;

    public SerpAPISearch() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.remainingCredits = TOTAL_CREDITS;
    }

    private boolean isWithinSearchPeriod() {
        LocalDate currentDate = LocalDate.now();
        return !currentDate.isBefore(START_DATE) && !currentDate.isAfter(END_DATE);
    }

    private long calculateDaysRemaining() {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(START_DATE)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(currentDate, END_DATE);
    }

    private boolean canPerformSearch() {
        if (!isWithinSearchPeriod()) {
            return false;
        }

        long daysRemaining = calculateDaysRemaining();
        if (daysRemaining <= 0 || remainingCredits <= 0) {
            return false;
        }

        // Calculate the maximum number of searches we can afford per day
        int maxSearchesPerDay = (int) (remainingCredits / daysRemaining);
        return maxSearchesPerDay > 0;
    }

    @NotNull
    private String search(String query) throws IOException {
        if (!canPerformSearch()) {
            throw new IOException("Search limit exceeded or insufficient credits.");
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url =
                String.format(
                        "https://serpapi.com/search.json?q=%s&location=United+Kingdom&hl=en&gl=uk&api_key=%s",
                        encodedQuery, MystiGuardianUtils.getSerpAPIConfig().apiKey());

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Reduce credits after a successful search
            remainingCredits--;
            return response.body().string();
        }
    }

    @NotNull
    private EmbedBuilder parseAndReturnEmbed(String jsonResponse) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode results = rootNode.path("organic_results");
        val embed = new EmbedBuilder();
        embed.setTitle("Search Results");
        embed.setDescription("Here are the search results for your query");

        for (JsonNode result : results) {
            String title = result.path("title").asText();
            String link = result.path("link").asText();
            String snippet = result.path("snippet").asText();

            embed.addField(title, snippet + "\n" + link);
        }

        return embed;
    }

    public void searchAndSendResponse(DiscordApi api) {
        MystiGuardianUtils.runInVirtualThread(
                () -> {
                    try {
                        String jsonResponse = search(MystiGuardianUtils.getSerpAPIConfig().query());
                        EmbedBuilder embed = parseAndReturnEmbed(jsonResponse);
                        val chanel =
                                api.getServerById(MystiGuardianUtils.getSerpAPIConfig().guildId())
                                        .flatMap(
                                                server ->
                                                        server.getTextChannelById(
                                                                MystiGuardianUtils.getSerpAPIConfig().channelId()));

                        chanel.ifPresent(
                                channel -> {
                                    channel.sendMessage(embed);
                                });

                    } catch (IOException e) {
                        api.getOwner()
                                .ifPresent(
                                        owner -> {
                                            try {
                                                owner
                                                        .get()
                                                        .openPrivateChannel()
                                                        .thenAccept(
                                                                channel -> {
                                                                    channel.sendMessage(
                                                                            "Failed to perform search: " + e.getMessage());
                                                                });
                                            } catch (InterruptedException | ExecutionException ex) {
                                                // Ignore
                                            }
                                        });
                    }
                });
    }
}
