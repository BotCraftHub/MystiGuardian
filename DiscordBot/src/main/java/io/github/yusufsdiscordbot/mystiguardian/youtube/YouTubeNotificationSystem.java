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
package io.github.yusufsdiscordbot.mystiguardian.youtube;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;

public class YouTubeNotificationSystem {
    private final String apikey;
    private final String youtubeChannelId;
    private final TextChannel discordChannel;
    private final Set<String> notifiedVideos = new HashSet<>();
    private final Set<String> notifiedPremieres = new HashSet<>();
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private final int maxRetries = 5;
    private volatile boolean running = true;

    public YouTubeNotificationSystem(DiscordApi api) {
        this.apikey = MystiGuardianUtils.getYoutubeConfig().apiKey();
        this.youtubeChannelId = MystiGuardianUtils.getYoutubeConfig().channelId();
        val discordChannelId = MystiGuardianUtils.getYoutubeConfig().discordChannelId();
        val guildId = MystiGuardianUtils.getYoutubeConfig().guildId();

        this.discordChannel =
                api.getServerById(guildId)
                        .flatMap(server -> server.getTextChannelById(discordChannelId))
                        .orElseThrow(() -> new IllegalArgumentException("Discord channel not found"));

        MystiGuardianUtils.getVirtualThreadPerTaskExecutor().submit(this::runNotificationLoop);
    }

    private void runNotificationLoop() {
        while (running) {
            try {
                // Run the check in a virtual thread
                MystiGuardianUtils.runInVirtualThread(
                        () -> {
                            try {
                                checkForNewVideosOrPremieres();
                                retryCount.set(0);
                            } catch (IOException e) {
                                handleException(e);
                            }
                        });

                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                handleException(e);
                running = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }

    public void checkForNewVideosOrPremieres() throws IOException {
        // Manually specify the date you want to use (20/6/2024)
        Instant startOfSpecifiedDate =
                ZonedDateTime.of(2024, 6, 20, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();

        String urlString =
                String.format(
                        "https://www.googleapis.com/youtube/v3/search?key=%s&channelId=%s&part=snippet,id&order=date&publishedAfter=%s&maxResults=1",
                        apikey, youtubeChannelId, startOfSpecifiedDate.toString());

        Request request = new Request.Builder().url(urlString).build();

        try (Response response = MystiGuardianUtils.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleErrorResponse(response);
                return;
            }

            String responseBody = response.body().string();
            JsonNode rootNode = MystiGuardianUtils.objectMapper.readTree(responseBody);
            JsonNode items = rootNode.path("items");

            if (items.isArray() && !items.isEmpty()) {
                JsonNode latestItem = items.get(0);
                String itemId = latestItem.path("id").path("videoId").asText();
                String itemType = latestItem.path("id").path("kind").asText();
                String title = latestItem.path("snippet").path("title").asText();
                String publishDateStr = latestItem.path("snippet").path("publishedAt").asText();
                Instant publishDate = Instant.parse(publishDateStr);
                String itemUrl = "https://www.youtube.com/watch?v=" + itemId;

                if ("youtube#video".equals(itemType)) {
                    // Handle regular videos
                    if (isNewVideo(itemId) && isWithinLastThreeDays(publishDate)) {
                        notifiedVideos.add(itemId);

                        new MessageBuilder()
                                .append("New video uploaded: ")
                                .append(title)
                                .append("\n")
                                .append(itemUrl)
                                .send(discordChannel);
                    }
                } else if ("youtube#liveBroadcast".equals(itemType)) {
                    // Handle live broadcasts (Premieres)
                    String liveBroadcastStatus =
                            latestItem.path("snippet").path("liveBroadcastContent").asText();

                    if ("upcoming".equals(liveBroadcastStatus) && isNewPremiere(itemId)) {
                        notifiedPremieres.add(itemId);
                        new MessageBuilder()
                                .append("Upcoming Premiere: ")
                                .append(title)
                                .append("\n")
                                .append(itemUrl)
                                .send(discordChannel);
                    }
                }
            }
        }
    }

    private void handleErrorResponse(Response response) throws IOException {
        int responseCode = response.code();
        String responseBody = response.body().string();

        if (responseCode == 403) {
            JsonNode rootNode = MystiGuardianUtils.objectMapper.readTree(responseBody);
            String reason = rootNode.path("error").path("errors").get(0).path("reason").asText();

            if ("quotaExceeded".equals(reason)) {
                MystiGuardianUtils.youtubeLogger.error("YouTube API quota exceeded. Pausing requests.");

                // Exponential backoff
                int retries = retryCount.incrementAndGet();
                if (retries <= maxRetries) {
                    long backoffTime = (long) Math.pow(2, retries) * 1000; // Exponential backoff
                    MystiGuardianUtils.youtubeLogger.info("{} milliseconds.", "Retrying in " + backoffTime);

                    try {
                        TimeUnit.MILLISECONDS.sleep(backoffTime);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    MystiGuardianUtils.youtubeLogger.error("Max retries exceeded. Shutting down.");
                    retryCount.set(0);
                    try {
                        TimeUnit.HOURS.sleep(24);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        } else {
            MystiGuardianUtils.youtubeLogger.error(
                    "{}{}",
                    "Error while checking for new videos or premieres, Response code: "
                            + responseCode
                            + ", Response body: ",
                    responseBody);
        }
    }

    private void handleException(Exception e) {
        MystiGuardianUtils.youtubeLogger.error("Error checking for new videos or premieres", e);
    }

    private boolean isNewVideo(String videoId) {
        return !notifiedVideos.contains(videoId);
    }

    private boolean isNewPremiere(String premiereId) {
        return !notifiedPremieres.contains(premiereId);
    }

    private boolean isWithinLastThreeDays(Instant publishDate) {
        Instant now = Instant.now();
        return ChronoUnit.DAYS.between(publishDate, now) <= 3;
    }
}
