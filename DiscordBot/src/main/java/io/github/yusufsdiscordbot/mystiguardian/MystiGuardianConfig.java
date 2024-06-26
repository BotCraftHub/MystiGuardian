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
package io.github.yusufsdiscordbot.mystiguardian;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.*;

import io.github.yusufsdiscordbot.mystiguardian.button.ButtonClickHandler;
import io.github.yusufsdiscordbot.mystiguardian.commands.moderation.util.UnbanCheckThread;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.event.EventDispatcher;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.listener.ModerationActionTriggerEventListener;
import io.github.yusufsdiscordbot.mystiguardian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import io.github.yusufsdiscordbot.mystiguardian.youtube.YouTubeNotificationSystem;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.jooq.DSLContext;

public class MystiGuardianConfig {
    public static Instant startTime = Instant.ofEpochSecond(0L);
    public static Future<?> mainThread;
    public static boolean reloading = false;

    @Getter
    private static MystiGuardianDatabase database;

    @Getter
    private static DSLContext context;

    @Getter
    private static EventDispatcher eventDispatcher = new EventDispatcher();

    private SlashCommandsHandler slashCommandsHandler;
    private UnbanCheckThread unbanCheckThread;

    @Getter
    private DiscordApi api;

    @Getter
    private static final String version = System.getProperty("version");

    @SuppressWarnings("unused")
    public MystiGuardianConfig() {}

    public void handleConfig() {
        if (mainThread != null) {
            mainThread.cancel(true);
        }

        logger.info("Starting bot...");

        mainThread = getExecutorService().submit(this::run);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");

            if (database.getDs() != null) {

                try {
                    Optional.ofNullable(database.getDs().getConnection()).ifPresent(connection -> {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            logger.error("Failed to close database connection", e);
                        }
                    });
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                logger.error("Database is null, not closing connection");
            }

            mainThread.cancel(true);
            unbanCheckThread.stop();
            getExecutorService().shutdown();

            logger.info("Shutdown complete");
        }));
    }

    public void run() {
        startTime = Instant.now();

        logger.info("Logged in as " + api.getYourself().getDiscriminatedName());

        if (reloading) {
            val ownerId = Objects.requireNonNull(jConfig.get("owner-id")).asText();

            if (api.getUserById(ownerId) != null) {
                Optional.ofNullable(api.getUserById(ownerId).join())
                        .ifPresentOrElse(
                                user -> {
                                    user.openPrivateChannel()
                                            .join()
                                            .sendMessage("Reloaded successfully")
                                            .join();
                                },
                                () -> logger.error("Owner is null, not sending message"));
            }

            reloading = false;
        }

        api.updateActivity(ActivityType.LISTENING, "to your commands");

        eventDispatcher.registerEventHandler(
                ModerationActionTriggerEvent.class, new ModerationActionTriggerEventListener());

        api.addSlashCommandCreateListener(slashCommandsHandler::onSlashCommandCreateEvent);
        api.addButtonClickListener(ButtonClickHandler::new);

        new YouTubeNotificationSystem(api, jConfig);
    }

    public void handleRegistrations(DiscordApi api) {
        try {
            this.slashCommandsHandler = new AutoSlashAdder(api);
        } catch (RuntimeException e) {
            logger.error("Failed to load slash commands", e);
            return;
        }

        try {
            database = new MystiGuardianDatabase();
            context = database.getContext();
        } catch (RuntimeException e) {
            logger.error("Failed to load database", e);
            return;
        }

        unbanCheckThread = new UnbanCheckThread(api);

        if (unbanCheckThread.isRunning()) {
            try {
                logger.info("Stopping unban check thread...");
                unbanCheckThread.stop();
            } catch (RuntimeException e) {
                logger.error("Failed to stop unban check thread", e);
            }
        } else {
            try {
                logger.info("Starting unban check thread...");
                unbanCheckThread.start();
            } catch (RuntimeException e) {
                logger.error("Failed to start unban check thread", e);
            }
        }
    }

    public void setAPI(DiscordApi api) {
        this.api = api;
    }
}
