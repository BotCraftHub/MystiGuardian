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

import com.zaxxer.hikari.HikariDataSource;
import io.github.yusufsdiscordbot.mystiguardian.api.SerpAPI;
import io.github.yusufsdiscordbot.mystiguardian.button.ButtonClickHandler;
import io.github.yusufsdiscordbot.mystiguardian.commands.moderation.util.UnbanCheckThread;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.event.EventDispatcher;
import io.github.yusufsdiscordbot.mystiguardian.event.events.DiscordEvents;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.listener.ModerationActionTriggerEventListener;
import io.github.yusufsdiscordbot.mystiguardian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.youtube.YouTubeNotificationSystem;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.jooq.DSLContext;

public class MystiGuardianConfig {
    @Getter private static final String version = System.getProperty("version");
    @Getter private static MystiGuardianDatabase database;
    @Getter private static DSLContext context;
    @Getter private static final EventDispatcher eventDispatcher = new EventDispatcher();
    @Getter private DiscordApi api;
    public static Instant startTime = Instant.ofEpochSecond(0L);
    public static Future<?> mainThread;
    public static boolean reloading = false;
    private SlashCommandsHandler slashCommandsHandler;
    private UnbanCheckThread unbanCheckThread;
    @Getter private static MystiGuardianConfig instance;

    @SuppressWarnings("unused")
    public MystiGuardianConfig() {
        instance = this;
    }

    public void handleConfig() {
        if (mainThread != null) {
            mainThread.cancel(true);
        }

        logger.info("Starting bot...");

        mainThread = MystiGuardianUtils.getVirtualThreadPerTaskExecutor().submit(this::run);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void shutdown() {
        logger.info("Shutting down...");

        Optional.ofNullable(database)
                .map(MystiGuardianDatabase::getDs)
                .ifPresent(HikariDataSource::close);

        MystiGuardianUtils.shutdownExecutorService();

        if (mainThread != null) mainThread.cancel(true);
        if (unbanCheckThread != null) unbanCheckThread.stop();

        logger.info("Shutdown complete");
    }

    public void run() {
        startTime = Instant.now();

        logger.info("Logged in as {}", api.getYourself().getDiscriminatedName());

        if (reloading) {
            notifyOwner();
            reloading = false;
        }

        api.updateActivity(ActivityType.LISTENING, "to your commands");

        eventDispatcher.registerEventHandler(
                ModerationActionTriggerEvent.class, new ModerationActionTriggerEventListener());

        api.addSlashCommandCreateListener(slashCommandsHandler::onSlashCommandCreateEvent);
        api.addLostConnectionListener(DiscordEvents::onLostConnectionEvent);
        api.addButtonClickListener(ButtonClickHandler::new);

        new YouTubeNotificationSystem(api);

        MystiGuardianUtils.clearGithubAIModel();

        val serpAPI = new SerpAPI();
        serpAPI.scheduleSearchAndSendResponse(api);
    }

    private void notifyOwner() {
        api.getUserById(MystiGuardianUtils.getMainConfig().ownerId())
                .thenAccept(
                        user ->
                                user.openPrivateChannel()
                                        .thenAccept(channel -> channel.sendMessage("Reloaded successfully")));
    }

    public void handleRegistrations() {
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

        this.unbanCheckThread = new UnbanCheckThread(api);

        if (unbanCheckThread.isRunning()) {
            logger.info("Stopping unban check thread...");
            unbanCheckThread.stop();
        }

        logger.info("Starting unban check thread...");
        unbanCheckThread.start();
    }

    public void setAPI(DiscordApi api) {
        this.api = api;
    }
}
