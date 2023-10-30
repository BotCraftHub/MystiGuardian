package io.github.yusufsdiscordbot.mystigurdian;

import io.github.realyusufismail.jconfig.util.JConfigUtils;
import io.github.yusufsdiscordbot.mystigurdian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystigurdian.slash.SlashCommandsHandler;
import io.github.yusufsdiscordbot.mystigurdian.utils.MystiGurdianUtils;
import lombok.val;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.time.Duration;
import java.time.Instant;

public class MystiGurdian {
    public static Instant startTime;

    void main() {
        val token = JConfigUtils.getString("token");

        if (token == null) {
            MystiGurdianUtils.logger.error("Token is null, exiting...");
            return;
        }

        val api = new DiscordApiBuilder().setToken(token).login()
                .join();

        MystiGurdianUtils.logger.info(STR."Logged in as \{api.getYourself().getDiscriminatedName()}");
        startTime = Instant.now();

        api.updateActivity(ActivityType.LISTENING, "to your commands");
        SlashCommandsHandler handler;

        try {
            handler = new AutoSlashAdder(api);
        } catch (Exception e) {
            MystiGurdianUtils.logger.error("Failed to load slash commands", e);
            return;
        }

        api.addSlashCommandCreateListener(handler::onSlashCommandCreateEvent);
    }
}
