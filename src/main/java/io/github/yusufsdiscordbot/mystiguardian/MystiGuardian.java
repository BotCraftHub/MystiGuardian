package io.github.yusufsdiscordbot.mystiguardian;

import io.github.realyusufismail.jconfig.util.JConfigUtils;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.sql.SQLException;
import java.time.Instant;
import java.util.concurrent.Future;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.*;

public class MystiGuardian {
    public static Instant startTime;
    private SlashCommandsHandler handler;
    public static Future<?> mainThread;
    private Long reloadChannelId;
    private MystiGuardianDatabase database;

    @SuppressWarnings("unused")
    public MystiGuardian() {}

    public MystiGuardian(Long reloadChannelId) {
        this.reloadChannelId = reloadChannelId;
    }

    public void main() {
        mainThread = getExecutorService().submit(this::run);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");

            mainThread.cancel(true);
            try {
                database.getDs().getConnection().close();
            } catch (SQLException e) {
                logger.error("Failed to close database connection", e);
            }

            logger.info("Shutdown complete");
        }));
    }

   public void run() {
        val token = JConfigUtils.getString("token");

        if (token == null) {
            logger.error("Token is null, exiting...");
            return;
        }

        val api = new DiscordApiBuilder().setToken(token).login()
                .join();

        logger.info(STR."Logged in as \{api.getYourself().getName()}");
        startTime = Instant.now();

        api.updateActivity(ActivityType.LISTENING, "to your commands");

        handleRegistrations(api);

        api.addSlashCommandCreateListener(handler::onSlashCommandCreateEvent);

        if (reloadChannelId != null) {
            api.getTextChannelById(reloadChannelId).ifPresent(channel -> channel
                    .sendMessage("Reloaded!"));
        }
    }


    private void handleRegistrations(DiscordApi api) {
        try {
            this.handler = new AutoSlashAdder(api);
        } catch (Exception e) {
            logger.error("Failed to load slash commands", e);
            return;
        }

        try {
             database = new MystiGuardianDatabase();
        } catch (Exception e) {
            logger.error("Failed to load database", e);
            return;
        }
    }
}
