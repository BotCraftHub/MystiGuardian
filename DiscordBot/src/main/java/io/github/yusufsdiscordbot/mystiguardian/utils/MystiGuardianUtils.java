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

import io.github.realyusufismail.jconfig.JConfig;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseColumnBuilder;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseColumnBuilderImpl;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseTableBuilder;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseTableBuilderImpl;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.IllegalFormatException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.val;
import net.fellbaum.jemoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;
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
    public static Logger logger = LoggerFactory.getLogger(MystiGuardianConfig.class);
    public static Logger databaseLogger = LoggerFactory.getLogger("database");
    public static Logger discordAuthLogger = LoggerFactory.getLogger("discordAuth");
    public static JConfig jConfig;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final SystemInfo systemInfo = new SystemInfo();
    private static final CentralProcessor processor = systemInfo.getHardware().getProcessor();

    @Getter
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public static String formatUptimeDuration(@NotNull Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (days > 0) {
            return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d minutes, %d seconds", minutes, seconds);
        } else {
            return String.format("%d seconds", seconds);
        }
    }

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

    public static ActionRow getPageActionRow(int currentIndex, PageNames pageName, @Nullable String userId) {
        if (userId != null) {
            return ActionRow.of(
                    Button.primary(
                            formatString("prev_%d_%s_%s", currentIndex, pageName.name(), userId), "Previous Page"),
                    Button.primary(formatString("next_%d_%s_%s", currentIndex, pageName.name(), userId), "Next Page"),
                    getDeleteButton());

        } else {
            // add another _userId to the end of the string
            return ActionRow.of(
                    Button.primary(formatString("prev_%d_%s", currentIndex, pageName.name()), "Previous Page"),
                    Button.primary(formatString("next_%d_%s", currentIndex, pageName.name()), "Next Page"),
                    getDeleteButton());
        }
    }

    public static Button getDeleteButton() {
        return Button.danger(
                "delete",
                "Delete",
                getDiscordEmoji("negative_squared_cross_mark").getUnicode());
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
        val randomId = uuid.getLeastSignificantBits() + uuid.getMostSignificantBits() + (long) mathRandom;
        return Math.abs(randomId);
    }

    public static String formatString(String template, Object... args) {
        try {
            return String.format(template, args);
        } catch (IllegalFormatException e) {
            logger.error("An error occurred while formatting the string: " + e.getMessage());
            return null;
        }
    }

    public static String getMemoryUsage() {
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        long usedHeapMemory = heapMemoryUsage.getUsed() / (1024 * 1024); // in megabytes
        long maxHeapMemory = heapMemoryUsage.getMax() / (1024 * 1024); // in megabytes
        long usedNonHeapMemory = nonHeapMemoryUsage.getUsed() / (1024 * 1024); // in megabytes
        long maxNonHeapMemory = nonHeapMemoryUsage.getMax() / (1024 * 1024); // in megabytes

        return String.format(
                "Heap Memory: %dMB/%dMB\nNon-Heap Memory: %dMB/%dMB",
                usedHeapMemory, maxHeapMemory, usedNonHeapMemory, maxNonHeapMemory);
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
            builder.setContent(formatString("Error: %s", message))
                    .setFlags(MessageFlag.EPHEMERAL, MessageFlag.URGENT)
                    .respond();
        }

        public void sendSuccess(String message) {
            builder.setContent(formatString("Success: %s", message))
                    .setFlags(MessageFlag.EPHEMERAL, MessageFlag.URGENT)
                    .respond();
        }

        public void sendInfo(String message) {
            builder.setContent(formatString("Info: %s", message))
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
