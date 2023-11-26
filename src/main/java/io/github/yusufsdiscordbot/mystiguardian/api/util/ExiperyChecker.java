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
package io.github.yusufsdiscordbot.mystiguardian.api.util;

import io.github.yusufsdiscordbot.mystiguardian.api.entities.OAuthUser;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.val;

public class ExiperyChecker {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void check() {

        final Runnable checker = () -> {
            MystiGuardianUtils.logger.info("Checking for expired users...");

            val databaseUsers = MystiGuardianDatabaseHandler.AuthHandler.getAllAuthRecords();

            databaseUsers.forEach(((s, authRecords) -> {
                authRecords.forEach(authRecord -> {
                    val oAuthUser =
                            (OAuthUser) MystiGuardianDatabaseHandler.AuthHandler.getAuthRecord(authRecord.getId());

                    if (oAuthUser == null) {
                        MystiGuardianUtils.logger.info(
                                "User " + Arrays.toString(s) + " is null. Removing from database.");
                        MystiGuardianDatabaseHandler.AuthHandler.deleteAuthRecord(authRecord.getId());
                        return;
                    }

                    val userId = oAuthUser.getUser().getId();
                    val expiresAt = oAuthUser.getExpiresAt();

                    if (expiresAt < System.currentTimeMillis()) {
                        MystiGuardianDatabaseHandler.AuthHandler.deleteAuthRecord(userId);
                        MystiGuardianUtils.logger.info("Removed user " + userId + " from database.");
                    }
                });
            }));
        };

        scheduler.scheduleAtFixedRate(checker, 0, 1, TimeUnit.MINUTES);
    }
}
