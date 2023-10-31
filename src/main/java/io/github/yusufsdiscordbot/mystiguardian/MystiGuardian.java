package io.github.yusufsdiscordbot.mystiguardian;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.*;

public class MystiGuardian {
    public static Instant startTime;
    public static Future<?> mainThread;
    private SlashCommandsHandler handler;
    private Long reloadChannelId;
    private MystiGuardianDatabase database;
    private boolean reloading = false;

    @SuppressWarnings("unused")
    public MystiGuardian() {
    }

    public MystiGuardian(Long reloadChannelId) {
        this.reloadChannelId = reloadChannelId;
        this.reloading = true;
    }

    public void main() {
        mainThread = getExecutorService().submit(this::run);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");

            try {
                if (database != null) {
                    database.getDs().getConnection().close();
                } else {
                    logger.warn("Database is null");
                }
            } catch (SQLException e) {
                logger.error("Failed to close database connection", e);
            }

            mainThread.cancel(true);

            logger.info("Shutdown complete");
        }));
    }

    public void run() {
        val token = Objects.requireNonNull(jConfig.get("token")).asText();

        if (token == null) {
            logger.error("Token is null, exiting...");
            return;
        }

        val api = new DiscordApiBuilder().setToken(token).login()
                .join();

        logger.info(STR."Logged in as \{api.getYourself().getName()}");
        startTime = Instant.now();
        val ownerId = Objects.requireNonNull(jConfig.get("owner-id")).asText();

        if (reloading) {
            if (api.getUserById(ownerId) != null) {
                Optional.ofNullable(api.getUserById(ownerId).join()).ifPresentOrElse(user -> {
                    user.openPrivateChannel().join().sendMessage("Reloaded successfully").join();
                }, () -> api.getChannelById(reloadChannelId).ifPresentOrElse(channel -> channel.asTextChannel().ifPresentOrElse(textChannel -> {
                    textChannel.sendMessage("Reloaded successfully").join();
                }, () -> logger.error("Reload channel is not a text channel")), () -> logger.error("Reload channel does not exist")));
            }
        }

        api.updateActivity(ActivityType.LISTENING, "to your commands");

        handleRegistrations(api);

        api.addSlashCommandCreateListener(handler::onSlashCommandCreateEvent);
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
        }
    }
}
