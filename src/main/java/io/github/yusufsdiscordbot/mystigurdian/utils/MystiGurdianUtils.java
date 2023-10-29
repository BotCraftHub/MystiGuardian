package io.github.yusufsdiscordbot.mystigurdian.utils;

import io.github.yusufsdiscordbot.mystigurdian.MystiGurdian;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Duration;

public class MystiGurdianUtils {
    public static Logger logger = LoggerFactory.getLogger(MystiGurdian.class);

    public static String formatUptimeDuration(Duration duration) {
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

    public static Color getRandomColor() {
        return new Color((int) (Math.random() * 0x1000000));
    }

    public static Color getBotColor() {
        return new Color(148, 87, 235);
    }
}
