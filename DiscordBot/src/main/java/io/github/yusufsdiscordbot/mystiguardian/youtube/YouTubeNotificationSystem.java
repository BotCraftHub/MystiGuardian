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
import io.github.realyusufismail.jconfig.JConfig;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.val;
import okhttp3.Request;
import okhttp3.Response;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageSet;
import org.jetbrains.annotations.NotNull;

public class YouTubeNotificationSystem {
    private final String apikey;
    private final String youtubeChannelId;
    private final TextChannel discordChannel;

    public YouTubeNotificationSystem(DiscordApi api, @NotNull JConfig jConfig) {
        val youtube = jConfig.get("youtube");
        if (youtube == null) {
            throw new IllegalArgumentException("YouTube configuration is missing");
        }

        this.apikey = youtube.get("apikey").asText();
        this.youtubeChannelId = youtube.get("channelId").asText();
        val discordChannelId = youtube.get("discordChannelId").asText();
        val guildId = youtube.get("guildId").asText();

        this.discordChannel = api.getServerById(guildId)
                .flatMap(server -> server.getTextChannelById(discordChannelId))
                .orElseThrow(() -> new IllegalArgumentException("Discord channel not found"));

        new Thread(() -> {
            while (true) {
                try {
                    checkForNewVideos();
                    Thread.sleep(60000); // Check every minute
                } catch (InterruptedException | IOException e) {
                    MystiGuardianUtils.youtubeLogger.error("Error checking for new videos", e);
                }
            }
        })
                .start();
    }

    public void checkForNewVideos() throws IOException {
        // Manually specify the date you want to use (20/6/2024)
        Instant startOfSpecifiedDate =
                ZonedDateTime.of(2024, 6, 20, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();

        String urlString = String.format(
                "https://www.googleapis.com/youtube/v3/search?key=%s&channelId=%s&part=snippet,id&order=date&publishedAfter=%s&maxResults=1",
                apikey, youtubeChannelId, startOfSpecifiedDate.toString());

        Request request = new Request.Builder().url(urlString).build();

        try (Response response = MystiGuardianUtils.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonNode rootNode = MystiGuardianUtils.objectMapper.readTree(responseBody);
            JsonNode items = rootNode.path("items");

            if (items.isArray() && !items.isEmpty()) {
                JsonNode latestVideo = items.get(0);
                String videoId = latestVideo.path("id").path("videoId").asText();
                String title = latestVideo.path("snippet").path("title").asText();
                String publishDateStr = latestVideo.path("snippet").path("publishedAt").asText();
                Instant publishDate = Instant.parse(publishDateStr);
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                if (isNewVideo(title) && isWithinLastThreeDays(publishDate)) {
                    new MessageBuilder()
                            .append("New video uploaded: ")
                            .append(title)
                            .append("\n")
                            .append(videoUrl)
                            .send(discordChannel);
                }
            }
        }
    }

    private boolean isNewVideo(String latestTitle) {
        MessageSet messages = discordChannel.getMessages(10).join();
        Optional<Message> lastMessage = messages.stream().findFirst();

        if (lastMessage.isPresent()) {
            String lastMessageContent = lastMessage.get().getContent();
            return !lastMessageContent.contains(latestTitle);
        } else {
            return true; // If there are no messages, consider it as a new video
        }
    }

    private boolean isWithinLastThreeDays(Instant publishDate) {
        Instant now = Instant.now();
        return ChronoUnit.DAYS.between(publishDate, now) <= 3;
    }
}