/*
 * Copyright 2023 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation.util;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.OffsetTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.javacord.api.DiscordApi;

public class UnbanCheckThread {
    private final DiscordApi api;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public UnbanCheckThread(DiscordApi api) {
        this.api = api;
    }

    public void start() {
        final Runnable checker = () -> {
            // Here, you can add the logic to check if anyone needs to be unbanned.
            MystiGuardianUtils.logger.info("Checking for unbans...");
            val servers = api.getServers();

            servers.forEach(server -> {
                val bans = MystiGuardianDatabaseHandler.SoftBan.getSoftBanRecords(server.getIdAsString());
                for (val ban : bans) {
                    val userId = ban.getUserId();
                    val user = server.getMemberById(userId).orElse(null);

                    val timeOfBan = ban.getTime();
                    val days = ban.getDays();
                    val currentTime = OffsetTime.now();

                    val timeOfUnban = timeOfBan.plusDays(days);

                    if (currentTime.isAfter(timeOfUnban.toOffsetTime())) {
                        MystiGuardianDatabaseHandler.SoftBan.deleteSoftBanRecord(server.getIdAsString(), userId);

                        if (user != null) {
                            server.unbanUser(user).thenAccept(unbanned -> {
                                MystiGuardianUtils.logger.info(
                                        "Unbanned user " + userId + " from server " + server.getIdAsString());
                            });

                            server.getModeratorsOnlyChannel().ifPresent(channel -> {
                                channel.sendMessage("User " + userId + " has been unbanned from server "
                                        + server.getIdAsString() + " automatically.");
                            });
                        } else {
                            MystiGuardianUtils.logger.info(
                                    "User " + userId + " is not in server " + server.getIdAsString() + " anymore.");
                        }
                    }
                }
            });
        };

        // Schedule the checker to run every hour
        scheduler.scheduleAtFixedRate(checker, 0, 1, TimeUnit.HOURS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    public boolean isRunning() {
        return !scheduler.isShutdown();
    }
}
