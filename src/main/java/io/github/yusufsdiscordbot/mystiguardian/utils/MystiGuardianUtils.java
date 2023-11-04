package io.github.yusufsdiscordbot.mystiguardian.utils;

import io.github.realyusufismail.jconfig.JConfig;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseColumnBuilder;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseColumnBuilderImpl;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseTableBuilder;
import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseTableBuilderImpl;
import lombok.Getter;
import lombok.val;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MystiGuardianUtils {
    public static Logger logger = LoggerFactory.getLogger(MystiGuardian.class);
    public static Logger databaseLogger = LoggerFactory.getLogger("database");
    public static JConfig jConfig = JConfig.build();
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
    @Contract(" -> new")
    public static Color getRandomColor() {
        return new Color((int) (Math.random() * 0x1000000));
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
                    org.javacord.api.entity.message.component.Button.primary("prev_" + currentIndex + "_" + pageName.name, "Previous Page"),
                    Button.primary("next_" + currentIndex + "_" + pageName.name, "Next Page"),
                    Button.primary("delete", "Delete")
            );

        } else {
            //add another _userId to the end of the string
            return ActionRow.of(
                    org.javacord.api.entity.message.component.Button.primary("prev_" + currentIndex + "_" + pageName.name + "_" + userId, "Previous Page"),
                    Button.primary("next_" + currentIndex + "_" + pageName.name + "_" + userId, "Next Page"),
                    Button.primary("delete", "Delete")
            );
        }
    }

    public static ActionRow getPageActionRow(int currentIndex, PageNames pageName) {
        return getPageActionRow(currentIndex, pageName, null);
    }

    public static boolean isLong(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false; // Handle null or empty strings as invalid
        }

        try {
            long value = Long.parseLong(id);
            return true;
        } catch (NumberFormatException e) {
            return false; // Parsing failed
        }
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
        RELOAD_AUDIT("reloadaudit"),
        WARN_AUDIT("warnaudit"),
        KICK_AUDIT("kickaudit"),
        BAN_AUDIT("banaudit"),
        TIME_OUT_AUDIT("timeoutaudit"),
        AMOUNT_AUDIT("amountaudit");

        private final String name;

        PageNames(String name) {
            this.name = name;
        }
    }

    public enum ModerationTypes {
        WARN("warn"),
        KICK("kick"),
        BAN("ban"),
        TIME_OUT("timeout");

        private final String name;

        ModerationTypes(String name) {
            this.name = name;
        }

    }
}
