package io.github.yusufsdiscordbot.mystiguardian;

import io.github.realyusufismail.jconfig.util.JConfigUtils;
import io.github.yusufsdiscordbot.mystiguardian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.time.Instant;

public class MystiGuardian {
    public static Instant startTime;

    void main() {
        val token = JConfigUtils.getString("token");

        if (token == null) {
            MystiGuardianUtils.logger.error("Token is null, exiting...");
            return;
        }

        val api = new DiscordApiBuilder().setToken(token).login()
                .join();

        MystiGuardianUtils.logger.info(STR."Logged in as \{api.getYourself().getDiscriminatedName()}");
        startTime = Instant.now();

        api.updateActivity(ActivityType.LISTENING, "to your commands");
        SlashCommandsHandler handler;

        try {
            handler = new AutoSlashAdder(api);
        } catch (Exception e) {
            MystiGuardianUtils.logger.error("Failed to load slash commands", e);
            return;
        }

        api.addSlashCommandCreateListener(handler::onSlashCommandCreateEvent);
    }
}
