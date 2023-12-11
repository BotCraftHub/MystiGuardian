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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation.util;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.OffsetTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.val;
import net.dv8tion.jda.api.JDA;

public class UnbanCheckThread {
    private final JDA jda;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public UnbanCheckThread(JDA jda) {
        this.jda = jda;
    }

    public void start() {
        final Runnable checker = () -> {
            // Here, you can add the logic to check if anyone needs to be unbanned.
            MystiGuardianUtils.logger.info("Checking for unbans...");
            val servers = jda.getGuilds();

            servers.forEach(server -> {
                val bans = MystiGuardianDatabaseHandler.SoftBan.getSoftBanRecords(server.getId());
                for (val ban : bans) {
                    val userId = ban.getUserId();
                    val user = server.getMemberById(userId);

                    val timeOfBan = ban.getTime();
                    val days = ban.getDays();
                    val currentTime = OffsetTime.now();

                    val timeOfUnban = timeOfBan.plusDays(days);

                    if (currentTime.isAfter(timeOfUnban.toOffsetTime())) {
                        MystiGuardianDatabaseHandler.SoftBan.deleteSoftBanRecord(server.getId(), userId);

                        if (user != null) {
                            server.unban(user).queue(unbanned -> {
                                MystiGuardianUtils.logger.info(
                                        "Unbanned user " + userId + " from server " + server.getId());
                            });


                            val auditLogChannel = MystiGuardianDatabaseHandler.AuditChannel
                                    .getAuditChannelRecord(server.getId());

                            if (auditLogChannel != null) {
                                Objects.requireNonNull(server.getTextChannelById(auditLogChannel)).sendMessage("User " + userId + " has been unbanned from server "
                                            + server.getId() + " automatically.")
                                    .queue();
                            }
                        } else {
                            MystiGuardianUtils.logger.info(
                                    "User " + userId + " is not in server " + server.getId() + " anymore.");
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
