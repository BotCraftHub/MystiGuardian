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
import io.github.yusufsdiscordbot.mystiguardian.api.JobSpreadsheetManager;
import io.github.yusufsdiscordbot.mystiguardian.commands.moderation.util.UnbanCheckThread;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.event.EventDispatcher;
import io.github.yusufsdiscordbot.mystiguardian.event.events.DiscordEvents;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.events.NewDAEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.listener.ModerationActionTriggerEventListener;
import io.github.yusufsdiscordbot.mystiguardian.event.listener.NewDAEventListener;
import io.github.yusufsdiscordbot.mystiguardian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.jooq.DSLContext;

@Slf4j
public class MystiGuardianConfig {
    @Getter private static final String version = System.getProperty("version");
    @Getter private static MystiGuardianDatabase database;
    @Getter private static DSLContext context;
    @Getter private static final EventDispatcher eventDispatcher = new EventDispatcher();
    @Getter private JDA jda;
    public static Instant startTime = Instant.ofEpochSecond(0L);
    public static Future<?> mainThread;
    public static boolean reloading = false;
    private SlashCommandsHandler slashCommandsHandler;
    private UnbanCheckThread unbanCheckThread;
    @Getter private static MystiGuardianConfig instance;
    @Getter private static JobSpreadsheetManager jobSpreadsheetManager;

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

        logger.info("Logged in as {}", jda.getSelfUser().getAsMention());

        if (reloading) {
            notifyOwner();
            reloading = false;
        }

        eventDispatcher.registerEventHandler(
                ModerationActionTriggerEvent.class, new ModerationActionTriggerEventListener());

        eventDispatcher.registerEventHandler(NewDAEvent.class, new NewDAEventListener());

        jda.addEventListener(new DiscordEvents(slashCommandsHandler));
    }

    private void notifyOwner() {
        Objects.requireNonNull(jda.getUserById(getMainConfig().ownerId()))
                .openPrivateChannel()
                .flatMap(channel -> channel.sendMessage("Reloaded successfully"))
                .queue();
    }

    public void handleRegistrations() {
        try {
            this.slashCommandsHandler = new AutoSlashAdder(jda);
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

        this.unbanCheckThread = new UnbanCheckThread(jda);

        if (unbanCheckThread.isRunning()) {
            logger.info("Stopping unban check thread...");
            unbanCheckThread.stop();
        }

        logger.info("Starting unban check thread...");
        unbanCheckThread.start();

        // Initialize web service for apprenticeship viewer
        try {
            var webServiceConfig = MystiGuardianUtils.getMainConfig().webService();
            if (webServiceConfig != null) {
                logger.info(
                        "Starting apprenticeship web service on port {} with base URL: {}",
                        webServiceConfig.port(),
                        webServiceConfig.baseUrl());
                io.github.yusufsdiscordbot.mystiguardian.web.ApprenticeshipWebService.initialize(
                        webServiceConfig.port(), webServiceConfig.baseUrl());
            } else {
                logger.warn(
                        "Web service configuration not found in config.json. Apprenticeship viewer will not be available.");
            }
        } catch (Exception e) {
            logger.error("Failed to start apprenticeship web service", e);
        }

        try {
            logger.info("Checking for DAs");

            jobSpreadsheetManager =
                    new JobSpreadsheetManager(
                            MystiGuardianUtils.getDAConfig().sheetsService(),
                            MystiGuardianUtils.getDAConfig().spreadsheetId());

            jobSpreadsheetManager.scheduleProcessNewJobs(jda);
        } catch (Exception e) {
            logger.error("Failed to check for DAS", e);
        }

        MystiGuardianUtils.clearGithubAIModel();
    }

    public void setAPI(JDA jda) {
        this.jda = jda;
    }
}
