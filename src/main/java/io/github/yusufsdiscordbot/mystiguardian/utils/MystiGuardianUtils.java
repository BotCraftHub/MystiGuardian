package io.github.yusufsdiscordbot.mystiguardian.utils;

import io.github.realyusufismail.jconfig.JConfig;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MystiGuardianUtils {
    public static Logger logger = LoggerFactory.getLogger(MystiGuardian.class);
    public static Logger databaseLogger = LoggerFactory.getLogger("database");

    @Getter
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    public static JConfig jConfig = JConfig.build();

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

    @Getter
    public static enum CloseCodes {
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
}
