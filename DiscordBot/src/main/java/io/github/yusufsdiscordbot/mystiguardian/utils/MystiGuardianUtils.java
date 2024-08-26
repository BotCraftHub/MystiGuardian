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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.realyusufismail.jconfig.JConfig;
import io.github.yusufsdiscordbot.mystiguardian.config.*;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseColumnBuilder;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseColumnBuilderImpl;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseTableBuilder;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseTableBuilderImpl;
import io.github.yusufsdiscordbot.mystiguardian.github.GithubAIModel;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.val;
import net.fellbaum.jemoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class MystiGuardianUtils {
    public static Logger logger = LoggerFactory.getLogger(MystiGuardianUtils.class);
    public static Logger databaseLogger = LoggerFactory.getLogger("database");
    public static Logger discordAuthLogger = LoggerFactory.getLogger("discordAuth");
    public static Logger youtubeLogger = LoggerFactory.getLogger("youtube");
    public static final OkHttpClient client = new OkHttpClient();
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static JConfig jConfig;
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final CentralProcessor processor = systemInfo.getHardware().getProcessor();
    private static final Map<Long, GithubAIModel> githubAIModel = new HashMap<>();

    @Getter
    private static final ExecutorService virtualThreadPerTaskExecutor =
            Executors.newVirtualThreadPerTaskExecutor();

    public static String formatUptimeDuration(@NotNull Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (days > 0) {
            return String.format(
                    "%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d minutes, %d seconds", minutes, seconds);
        } else {
            return String.format("%d seconds", seconds);
        }
    }

    @NotNull
    public static String formatOffsetDateTime(@NotNull OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static Color getBotColor() {
        return new Color(148, 87, 235);
    }

    public static ZoneOffset getZoneOffset() {
        return ZoneOffset.UTC;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static DatabaseTableBuilder createTable(DSLContext create, String tableName) {
        return new DatabaseTableBuilderImpl(create, tableName);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DatabaseColumnBuilder createColumn(DataType<?> type, String name) {
        return new DatabaseColumnBuilderImpl(type, name);
    }

    public static ActionRow getPageActionRow(
            int currentIndex, PageNames pageName, @Nullable String userId) {
        if (userId != null) {
            return ActionRow.of(
                    Button.primary(
                            formatString("prev_%d_%s_%s", currentIndex, pageName.name(), userId),
                            "Previous Page"),
                    Button.primary(
                            formatString("next_%d_%s_%s", currentIndex, pageName.name(), userId), "Next Page"),
                    getDeleteButton());

        } else {
            // add another _userId to the end of the string
            return ActionRow.of(
                    Button.primary(
                            formatString("prev_%d_%s", currentIndex, pageName.name()), "Previous Page"),
                    Button.primary(formatString("next_%d_%s", currentIndex, pageName.name()), "Next Page"),
                    getDeleteButton());
        }
    }

    public static Button getDeleteButton() {
        return Button.danger(
                "delete", "Delete", getDiscordEmoji("negative_squared_cross_mark").getUnicode());
    }

    public static ActionRow getPageActionRow(int currentIndex, PageNames pageName) {
        return getPageActionRow(currentIndex, pageName, null);
    }

    public static Emoji getDiscordEmoji(String emojiName) {
        return EmojiManager.getByDiscordAlias(emojiName)
                .orElseThrow(() -> new IllegalArgumentException("Emoji not found"));
    }

    public static boolean isLong(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false; // Handle null or empty strings as invalid
        }

        long l;

        try {
            l = Long.parseLong(id);
        } catch (NumberFormatException e) {
            l = -1;
        }

        return l > 0;
    }

    public static Long getRandomId() {
        val uuid = UUID.randomUUID();
        val mathRandom = Math.random() * 100000000000000000L;
        val randomId =
                uuid.getLeastSignificantBits() + uuid.getMostSignificantBits() + (long) mathRandom;
        return Math.abs(randomId);
    }

    @Nullable
    public static String formatString(String template, Object... args) {
        try {
            return String.format(template, args);
        } catch (IllegalFormatException e) {
            logger.error("An error occurred while formatting the string: {}", e.getMessage());
            return null;
        }
    }

    @NotNull
    public static String getMemoryUsage() {
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        long usedHeapMemory = heapMemoryUsage.getUsed() / (1024 * 1024); // in megabytes
        long maxHeapMemory = heapMemoryUsage.getMax() / (1024 * 1024); // in megabytes
        long usedNonHeapMemory = nonHeapMemoryUsage.getUsed() / (1024 * 1024); // in megabytes
        long maxNonHeapMemory = nonHeapMemoryUsage.getMax(); // in bytes

        String maxNonHeapMemoryStr =
                (maxNonHeapMemory == -1) ? "undefined" : (maxNonHeapMemory / (1024 * 1024)) + "MB";

        return String.format(
                "\nHeap Memory: %dMB/%dMB\nNon-Heap Memory: %dMB/%s",
                usedHeapMemory, maxHeapMemory, usedNonHeapMemory, maxNonHeapMemoryStr);
    }

    public static double getCpuUsage(long delay) {
        long start = System.nanoTime();
        long[] oldTicks = processor.getSystemCpuLoadTicks();
        long toWait = delay - (System.nanoTime() - start) / 1000000L;
        if (toWait > 0L) {
            try {
                Thread.sleep(toWait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return processor.getSystemCpuLoadBetweenTicks(oldTicks);
    }

    public static String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    public static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }

    @NotNull
    public static GithubAIModel getGithubAIModel(long id) {
        if (!githubAIModel.containsKey(id)) {
            return new GithubAIModel(
                    "meta-llama-3-8b-instruct",
                    "You are a java developer, existing on discord. You aim to help others with their problems and make their day better.");
        }

        return getGithubAIModel(id);
    }

    public static synchronized void clearGithubAIModel() {
        virtualThreadPerTaskExecutor.submit(
                () -> {
                    try {
                        TimeUnit.HOURS.sleep(24);
                        githubAIModel.clear();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Interrupted while clearing github AI model", e);
                    }
                });
    }

    public static void runInVirtualThread(Runnable task) {
        virtualThreadPerTaskExecutor.submit(task);
    }

    public static void shutdownExecutorService() {
        virtualThreadPerTaskExecutor.shutdown();
        try {
            // Wait for the executor service to terminate gracefully
            if (!virtualThreadPerTaskExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                virtualThreadPerTaskExecutor.shutdownNow(); // Force shutdown
                // Wait for the executor service to terminate after forcing shutdown
                if (!virtualThreadPerTaskExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    logger.error("Executor service did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            virtualThreadPerTaskExecutor.shutdownNow(); // Force shutdown on interruption
            Thread.currentThread().interrupt(); // Restore interrupted status
            logger.error("Interrupted during shutdown of executor service", ie);
        }
    }

    @NotNull
    public static SerpAPIConfig getSerpAPIConfig() {
        val serpAPI = getRequiredConfigObject("serpAPI");

        return new SerpAPIConfig(
                getRequiredStringValue(serpAPI, "apiKey"),
                getRequiredLongValue(serpAPI, "guildId"),
                getRequiredLongValue(serpAPI, "discordChannelId"),
                getRequiredStringValue(serpAPI, "query"));
    }

    @NotNull
    public static MainConfig getMainConfig() {
        return new MainConfig(
                getRequiredStringValue("token"),
                getRequiredStringValue("ownerId"),
                getRequiredStringValue("githubToken"));
    }

    @NotNull
    public static DiscordAuthConfig getDiscordAuthConfig() {
        val discordAuth = getRequiredConfigObject("discord-auth");

        return new DiscordAuthConfig(
                getRequiredStringValue(discordAuth, "clientId"),
                getRequiredStringValue(discordAuth, "clientSecret"));
    }

    @NotNull
    public static YoutubeConfig getYoutubeConfig() {
        val youtube = getRequiredConfigObject("youtube");

        return new YoutubeConfig(
                getRequiredStringValue(youtube, "apiKey"),
                getRequiredStringValue(youtube, "channelId"),
                getRequiredStringValue(youtube, "discordChannelId"),
                getRequiredStringValue(youtube, "guildId"));
    }

    @NotNull
    public static DataSourceConfig getDataSourceConfig() {
        val dataSource = getRequiredConfigObject("dataSource");

        return new DataSourceConfig(
                getRequiredStringValue(dataSource, "user"),
                getRequiredStringValue(dataSource, "password"),
                getRequiredStringValue(dataSource, "driver"),
                getRequiredStringValue(dataSource, "port"),
                getRequiredStringValue(dataSource, "name"),
                getRequiredStringValue(dataSource, "host"),
                getRequiredStringValue(dataSource, "url"));
    }

    @NotNull
    public static TripAdvisorConfig getTripAdvisorConfig() {
        val tripAdvisor = getRequiredConfigObject("tripAdvisor");

        return new TripAdvisorConfig(getRequiredStringValue(tripAdvisor, "apiKey"));
    }

    private static String getRequiredStringValue(String key) {
        val value = jConfig.get(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " not found in config");
        }
        return value.asText();
    }

    @NotNull
    private static JsonNode getRequiredConfigObject(String key) {
        val value = jConfig.get(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " config not found in config");
        }
        return value;
    }

    private static String getRequiredStringValue(@NotNull JsonNode config, String key) {
        val value = config.get(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " not found in " + config);
        }
        return value.asText();
    }

    private static long getRequiredLongValue(@NotNull JsonNode config, String key) {
        JsonNode value = config.get(key);

        if (value == null) {
            throw new IllegalArgumentException(key + " not found in " + config);
        }

        long valueAsLong;

        if (value.isTextual()) {
            try {
                valueAsLong = Long.parseLong(value.asText());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(key + " is not a valid long value");
            }
        } else {
            valueAsLong = value.asLong();
        }

        return valueAsLong;
    }

    public static String handleAPIError(@NotNull Response response) {
        int statusCode = response.code();

        String errorMessage =
                switch (statusCode) {
                    case 400 -> "Bad Request: The server could not understand the request due to invalid syntax.";
                    case 401 -> "Unauthorized: Authentication is required or has failed.";
                    case 403 -> "Forbidden: The server understood the request, but refuses to authorize it.";
                    case 404 -> "Not Found: The requested resource could not be found.";
                    case 500 -> "Internal Server Error: The server encountered an error and could not complete the request.";
                    case 502 -> "Bad Gateway: The server was acting as a gateway or proxy and received an invalid response from the upstream server.";
                    case 503 -> "Service Unavailable: The server is not ready to handle the request, possibly due to maintenance or overload.";
                    case 504 -> "Gateway Timeout: The server was acting as a gateway or proxy and did not receive a timely response from the upstream server.";
                    default -> "Unexpected Error: Received HTTP status code " + statusCode;
                };

        try {
            String responseBody = response.body().string();
            errorMessage += " | Response body: " + responseBody;
        } catch (IOException e) {
            errorMessage += " | Error reading response body: " + e.getMessage();
        }

        return errorMessage;
    }

    @Getter
    public enum CloseCodes {
        OWNER_REQUESTED(4000, "Owner requested shutdown"),
        SHUTDOWN(4001, "Shutdown command received"),
        RESTART(4002, "Restart command received"),
        RELOAD(4004, "Reload command received");

        private final int code;

        private final String reason;

        CloseCodes(int code, String reason) {
            this.code = code;
            this.reason = reason;
        }
    }

    public enum PageNames {
        RELOAD_AUDIT(),
        WARN_AUDIT(),
        KICK_AUDIT(),
        BAN_AUDIT(),
        TIME_OUT_AUDIT(),
        AMOUNT_AUDIT();

        PageNames() {}
    }

    @Getter
    public enum ModerationTypes {
        WARN("warn"),
        KICK("kick"),
        BAN("ban"),
        TIME_OUT("timeout"),
        DELETE_MESSAGES("delete_messages"),
        SOFT_BAN("soft ban");

        private final String name;

        ModerationTypes(String name) {
            this.name = name;
        }
    }

    public static class ReplyUtils {
        private final InteractionImmediateResponseBuilder builder;
        private final ActionRow[] coreActionRows = new ActionRow[] {ActionRow.of(getDeleteButton())};

        public ReplyUtils(InteractionImmediateResponseBuilder builder) {
            this.builder = builder;
        }

        public void sendError(String message) {
            builder
                    .setContent(formatString("Error: %s", message))
                    .setFlags(MessageFlag.EPHEMERAL, MessageFlag.URGENT)
                    .respond();
        }

        public void sendSuccess(String message) {
            builder
                    .setContent(formatString("Success: %s", message))
                    .setFlags(MessageFlag.EPHEMERAL, MessageFlag.URGENT)
                    .respond();
        }

        public void sendInfo(String message) {
            builder
                    .setContent(formatString("Info: %s", message))
                    .setFlags(MessageFlag.SUPPRESS_NOTIFICATIONS)
                    .addComponents(coreActionRows)
                    .respond();
        }

        public void sendEmbed(EmbedBuilder embedBuilder) {
            builder.addEmbed(embedBuilder).addComponents(coreActionRows).respond();
        }

        public EmbedBuilder getDefaultEmbed() {
            return new EmbedBuilder().setColor(getBotColor());
        }
    }
}
