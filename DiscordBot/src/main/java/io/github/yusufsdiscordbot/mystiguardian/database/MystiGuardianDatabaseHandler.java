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
package io.github.yusufsdiscordbot.mystiguardian.database;

import static io.github.yusufsdiscordbot.mystiguardian.db.Tables.*;
import static io.github.yusufsdiscordbot.mystiguardian.utils.DatabaseUtils.deleteRecord;
import static io.github.yusufsdiscordbot.mystiguardian.utils.DatabaseUtils.updateRecord;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.db.tables.records.*;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Result;

// TODO: Cleanup is required. Implement auth delete and update methods
public class MystiGuardianDatabaseHandler {

    public static class ReloadAudit {
        public static void setReloadAuditRecord(String userId, String reason) {
            MystiGuardianConfig.getContext()
                    .insertInto(
                            RELOAD_AUDIT,
                            RELOAD_AUDIT.ID,
                            RELOAD_AUDIT.USER_ID,
                            RELOAD_AUDIT.REASON,
                            RELOAD_AUDIT.TIME)
                    .values(
                            MystiGuardianUtils.getRandomId(),
                            userId,
                            reason,
                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        @NotNull
        public static Result<ReloadAuditRecord> getReloadAuditRecords() {
            return MystiGuardianConfig.getContext().selectFrom(RELOAD_AUDIT).fetch();
        }
    }

    public static class Warns {
        public static long setWarnsRecord(String guildId, String userId, String reason) {
            // return the id
            return Objects.requireNonNull(
                            MystiGuardianConfig.getContext()
                                    .insertInto(
                                            WARNS, WARNS.ID, WARNS.GUILD_ID, WARNS.USER_ID, WARNS.REASON, WARNS.TIME)
                                    .values(
                                            MystiGuardianUtils.getRandomId(),
                                            guildId,
                                            userId,
                                            reason,
                                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                                    .returning(WARNS.ID)
                                    .fetchOne())
                    .getId();
        }

        public static Result<WarnsRecord> getWarnsRecords(String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(WARNS)
                    .where(WARNS.GUILD_ID.eq(guildId))
                    .and(WARNS.USER_ID.eq(userId))
                    .fetch();
        }

        public static @Nullable WarnsRecord getWarnRecordById(String guildId, Long id) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(WARNS)
                    .where(WARNS.GUILD_ID.eq(guildId))
                    .and(WARNS.ID.eq(id))
                    .fetchOne();
        }
    }

    public static class AmountOfWarns {
        public static void updateAmountOfWarns(String guildId, String userId) {
            // Get the current amount of warns
            updateRecord(
                    MystiGuardianConfig.getContext(),
                    AMOUNT_OF_WARNS,
                    AMOUNT_OF_WARNS.AMOUNT_OF_WARNS_,
                    guildId,
                    userId);
        }

        @NotNull
        public static Result<AmountOfWarnsRecord> getAmountOfWarnsRecords(
                String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(AMOUNT_OF_WARNS)
                    .where(AMOUNT_OF_WARNS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_WARNS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class TimeOut {
        public static void setTimeOutRecord(
                String guildId, String userId, String reason, OffsetDateTime duration) {
            MystiGuardianConfig.getContext()
                    .insertInto(
                            TIME_OUT,
                            TIME_OUT.ID,
                            TIME_OUT.GUILD_ID,
                            TIME_OUT.USER_ID,
                            TIME_OUT.REASON,
                            TIME_OUT.DURATION,
                            TIME_OUT.TIME)
                    .values(
                            MystiGuardianUtils.getRandomId(),
                            guildId,
                            userId,
                            reason,
                            duration,
                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        public static void deleteTimeOutRecord(String guildId, String userId) {
            MystiGuardianConfig.getContext()
                    .deleteFrom(TIME_OUT)
                    .where(TIME_OUT.GUILD_ID.eq(guildId))
                    .and(TIME_OUT.USER_ID.eq(userId))
                    .execute();
        }

        public static Result<TimeOutRecord> getTimeOutRecords(String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(TIME_OUT)
                    .where(TIME_OUT.GUILD_ID.eq(guildId))
                    .and(TIME_OUT.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class AmountOfTimeOuts {
        public static void updateAmountOfTimeOuts(String guildId, String userId) {
            // get the current amount of time outs
            updateRecord(
                    MystiGuardianConfig.getContext(),
                    AMOUNT_OF_TIME_OUTS,
                    AMOUNT_OF_TIME_OUTS.AMOUNT_OF_TIME_OUTS_,
                    guildId,
                    userId);
        }

        public static void deleteAmountOfTimeOutsRecord(String guildId, String userId) {
            deleteRecord(MystiGuardianConfig.getContext(), AMOUNT_OF_TIME_OUTS, guildId, userId);
        }

        @NotNull
        public static Result<AmountOfTimeOutsRecord> getAmountOfTimeOutsRecords(
                String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(AMOUNT_OF_TIME_OUTS)
                    .where(AMOUNT_OF_TIME_OUTS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_TIME_OUTS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class Kick {
        public static Long setKickRecord(String guildId, String userId, String reason) {
            return Objects.requireNonNull(
                            MystiGuardianConfig.getContext()
                                    .insertInto(KICK, KICK.ID, KICK.GUILD_ID, KICK.USER_ID, KICK.REASON, KICK.TIME)
                                    .values(
                                            MystiGuardianUtils.getRandomId(),
                                            guildId,
                                            userId,
                                            reason,
                                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                                    .returning(KICK.ID)
                                    .fetchOne())
                    .getId();
        }

        public static void deleteKickRecord(String guildId, String userId) {
            MystiGuardianConfig.getContext()
                    .deleteFrom(KICK)
                    .where(KICK.GUILD_ID.eq(guildId))
                    .and(KICK.USER_ID.eq(userId))
                    .execute();
        }

        @NotNull
        public static Result<KickRecord> getKickRecords(String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(KICK)
                    .where(KICK.GUILD_ID.eq(guildId))
                    .and(KICK.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class AmountOfKicks {
        public static void updateAmountOfKicks(String guildId, String userId) {
            // get the current amount of kicks
            updateRecord(
                    MystiGuardianConfig.getContext(),
                    AMOUNT_OF_KICKS,
                    AMOUNT_OF_KICKS.AMOUNT_OF_KICKS_,
                    guildId,
                    userId);
        }

        public static void deleteAmountOfKicksRecord(String guildId, String userId) {
            deleteRecord(MystiGuardianConfig.getContext(), AMOUNT_OF_KICKS, guildId, userId);
        }

        @NotNull
        public static Result<AmountOfKicksRecord> getAmountOfKicksRecords(
                String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(AMOUNT_OF_KICKS)
                    .where(AMOUNT_OF_KICKS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_KICKS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class Ban {
        public static Long setBanRecord(String guildId, String userId, String reason) {
            return Objects.requireNonNull(
                            MystiGuardianConfig.getContext()
                                    .insertInto(BAN, BAN.ID, BAN.GUILD_ID, BAN.USER_ID, BAN.REASON, BAN.TIME)
                                    .values(
                                            MystiGuardianUtils.getRandomId(),
                                            guildId,
                                            userId,
                                            reason,
                                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                                    .returning(BAN.ID)
                                    .fetchOne())
                    .getId();
        }

        public static void deleteBanRecord(String guildId, String userId) {
            MystiGuardianConfig.getContext()
                    .deleteFrom(BAN)
                    .where(BAN.GUILD_ID.eq(guildId))
                    .and(BAN.USER_ID.eq(userId))
                    .execute();
        }

        @NotNull
        public static Result<BanRecord> getBanRecords(String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(BAN)
                    .where(BAN.GUILD_ID.eq(guildId))
                    .and(BAN.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class AmountOfBans {
        public static void updateAmountOfBans(String guildId, String userId) {
            // get the current amount of bans
            updateRecord(
                    MystiGuardianConfig.getContext(),
                    AMOUNT_OF_BANS,
                    AMOUNT_OF_BANS.AMOUNT_OF_BANS_,
                    guildId,
                    userId);
        }

        public static void deleteAmountOfBansRecord(String guildId, String userId) {
            deleteRecord(MystiGuardianConfig.getContext(), AMOUNT_OF_BANS, guildId, userId);
        }

        @NotNull
        public static Result<AmountOfBansRecord> getAmountOfBansRecords(String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(AMOUNT_OF_BANS)
                    .where(AMOUNT_OF_BANS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_BANS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class SoftBan {
        public static long setSoftBanRecord(
                String guildId, String userId, String reason, Integer days) {
            return Objects.requireNonNull(
                            MystiGuardianConfig.getContext()
                                    .insertInto(
                                            SOFT_BAN,
                                            SOFT_BAN.ID,
                                            SOFT_BAN.GUILD_ID,
                                            SOFT_BAN.USER_ID,
                                            SOFT_BAN.REASON,
                                            SOFT_BAN.TIME,
                                            SOFT_BAN.DAYS)
                                    .values(
                                            MystiGuardianUtils.getRandomId(),
                                            guildId,
                                            userId,
                                            reason,
                                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()),
                                            days)
                                    .returning()
                                    .fetchOne())
                    .getId();
        }

        public static void deleteSoftBanRecord(String guildId, String userId) {
            MystiGuardianConfig.getContext()
                    .deleteFrom(SOFT_BAN)
                    .where(SOFT_BAN.GUILD_ID.eq(guildId))
                    .and(SOFT_BAN.USER_ID.eq(userId))
                    .execute();
        }

        @NotNull
        public static Result<SoftBanRecord> getSoftBanRecords(String guildId, String userId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(SOFT_BAN)
                    .where(SOFT_BAN.GUILD_ID.eq(guildId))
                    .and(SOFT_BAN.USER_ID.eq(userId))
                    .fetch();
        }

        public static @NotNull Result<SoftBanRecord> getSoftBanRecords(String guildId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(SOFT_BAN)
                    .where(SOFT_BAN.GUILD_ID.eq(guildId))
                    .fetch();
        }
    }

    public static class OAuth {
        public static Long setOAuthRecord(
                String accessToken, String refreshToken, String userJson, String userId, long expiresAt) {
            val id = getId();

            MystiGuardianConfig.getContext()
                    .insertInto(
                            OAUTH,
                            OAUTH.ID,
                            OAUTH.ACCESS_TOKEN,
                            OAUTH.REFRESH_TOKEN,
                            OAUTH.USER_JSON,
                            OAUTH.USER_ID,
                            OAUTH.EXPIRES_IN)
                    .values(id, accessToken, refreshToken, userJson, userId, String.valueOf(expiresAt))
                    .execute();

            return id;
        }

        public static void deleteOAuthRecord(String userId) {
            MystiGuardianConfig.getContext().deleteFrom(OAUTH).where(OAUTH.USER_ID.eq(userId)).execute();
        }

        public static void updateOAuthRecord(
                String accessToken, String refreshToken, String userJson, String userId) {
            MystiGuardianConfig.getContext()
                    .update(OAUTH)
                    .set(OAUTH.ACCESS_TOKEN, accessToken)
                    .set(OAUTH.REFRESH_TOKEN, refreshToken)
                    .set(OAUTH.USER_JSON, userJson)
                    .where(OAUTH.USER_ID.eq(userId))
                    .execute();
        }

        public static String getAccessToken(long id, String userId) {
            return MystiGuardianConfig.getContext()
                    .select(OAUTH.ACCESS_TOKEN)
                    .from(OAUTH)
                    .where(OAUTH.ID.eq(id))
                    .and(OAUTH.USER_ID.eq(userId))
                    .fetch()
                    .get(0)
                    .value1();
        }

        public static Long getId() {
            List<Long> existingIds =
                    MystiGuardianConfig.getContext().select(OAUTH.ID).from(OAUTH).fetch().getValues(OAUTH.ID);

            // Find the maximum ID from the list
            Optional<Long> maxId = existingIds.stream().max(Long::compare);

            // If the list is empty, start with 1, otherwise add 1 to the max ID
            return maxId.map(id -> id + 1).orElse(1L);
        }
    }

    public static class AuditChannel {
        public static boolean setAuditChannelRecord(String guildId, String channelId) {
            if (getAuditChannelRecord(guildId) != null) {
                MystiGuardianConfig.getContext()
                        .update(AUDIT_CHANNEL)
                        .set(AUDIT_CHANNEL.CHANNEL_ID, channelId)
                        .where(AUDIT_CHANNEL.GUILD_ID.eq(guildId))
                        .execute();
                return false;
            }

            MystiGuardianConfig.getContext()
                    .insertInto(AUDIT_CHANNEL, AUDIT_CHANNEL.GUILD_ID, AUDIT_CHANNEL.CHANNEL_ID)
                    .values(guildId, channelId)
                    .execute();

            return true;
        }

        public static void deleteAuditChannelRecord(String guildId) {
            MystiGuardianConfig.getContext()
                    .deleteFrom(AUDIT_CHANNEL)
                    .where(AUDIT_CHANNEL.GUILD_ID.eq(guildId))
                    .execute();
        }

        @Nullable
        public static String getAuditChannelRecord(String guildId) {
            val channel =
                    MystiGuardianConfig.getContext()
                            .select(AUDIT_CHANNEL.CHANNEL_ID)
                            .from(AUDIT_CHANNEL)
                            .where(AUDIT_CHANNEL.GUILD_ID.eq(guildId))
                            .fetch();

            if (channel.isEmpty()) {
                return null;
            } else {
                return channel.get(0).value1();
            }
        }

        public static void updateAuditChannelRecord(String guildId, String channelId) {
            if (getAuditChannelRecord(guildId) == null) {
                MystiGuardianConfig.getContext()
                        .insertInto(AUDIT_CHANNEL, AUDIT_CHANNEL.GUILD_ID, AUDIT_CHANNEL.CHANNEL_ID)
                        .values(guildId, channelId)
                        .execute();
            } else {
                MystiGuardianConfig.getContext()
                        .update(AUDIT_CHANNEL)
                        .set(AUDIT_CHANNEL.CHANNEL_ID, channelId)
                        .where(AUDIT_CHANNEL.GUILD_ID.eq(guildId))
                        .execute();
            }
        }
    }

    public static class StoredFiles {
        /**
         * Store a new file record in the database
         *
         * @param guildId The guild ID where the file was uploaded
         * @param fileName The name/identifier for the file
         * @param fileType The file type/extension
         * @param description Optional description of the file
         * @param fileUrl The URL where the file is stored (Discord CDN)
         * @param uploadedBy The user ID who uploaded the file
         * @return The ID of the created record
         */
        public static long storeFile(
                String guildId,
                String fileName,
                String fileType,
                @Nullable String description,
                String fileUrl,
                String uploadedBy) {
            return Objects.requireNonNull(
                            MystiGuardianConfig.getContext()
                                    .insertInto(
                                            STORED_FILES,
                                            STORED_FILES.ID,
                                            STORED_FILES.GUILD_ID,
                                            STORED_FILES.FILE_NAME,
                                            STORED_FILES.FILE_TYPE,
                                            STORED_FILES.DESCRIPTION,
                                            STORED_FILES.FILE_URL,
                                            STORED_FILES.UPLOADED_BY,
                                            STORED_FILES.UPLOADED_AT)
                                    .values(
                                            MystiGuardianUtils.getRandomId(),
                                            guildId,
                                            fileName,
                                            fileType,
                                            description,
                                            fileUrl,
                                            uploadedBy,
                                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                                    .returning(STORED_FILES.ID)
                                    .fetchOne())
                    .getId();
        }

        /**
         * Get a specific file by name and guild ID
         *
         * @param guildId The guild ID
         * @param fileName The file name
         * @return The file record, or null if not found
         */
        @Nullable
        public static StoredFilesRecord getFile(String guildId, String fileName) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(STORED_FILES)
                    .where(STORED_FILES.GUILD_ID.eq(guildId))
                    .and(STORED_FILES.FILE_NAME.eq(fileName))
                    .fetchOne();
        }

        /**
         * Get all files for a specific guild
         *
         * @param guildId The guild ID
         * @return List of all file records for the guild
         */
        @NotNull
        public static Result<StoredFilesRecord> getAllFiles(String guildId) {
            return MystiGuardianConfig.getContext()
                    .selectFrom(STORED_FILES)
                    .where(STORED_FILES.GUILD_ID.eq(guildId))
                    .orderBy(STORED_FILES.UPLOADED_AT.desc())
                    .fetch();
        }

        /**
         * Delete a file record
         *
         * @param guildId The guild ID
         * @param fileName The file name
         * @return true if file was deleted, false if not found
         */
        public static boolean deleteFile(String guildId, String fileName) {
            int deleted =
                    MystiGuardianConfig.getContext()
                            .deleteFrom(STORED_FILES)
                            .where(STORED_FILES.GUILD_ID.eq(guildId))
                            .and(STORED_FILES.FILE_NAME.eq(fileName))
                            .execute();
            return deleted > 0;
        }

        /**
         * Update the description of a file
         *
         * @param guildId The guild ID
         * @param fileName The file name
         * @param newDescription The new description
         * @return true if updated, false if not found
         */
        public static boolean updateDescription(
                String guildId, String fileName, String newDescription) {
            int updated =
                    MystiGuardianConfig.getContext()
                            .update(STORED_FILES)
                            .set(STORED_FILES.DESCRIPTION, newDescription)
                            .where(STORED_FILES.GUILD_ID.eq(guildId))
                            .and(STORED_FILES.FILE_NAME.eq(fileName))
                            .execute();
            return updated > 0;
        }

        /**
         * Check if a file with the given name exists in the guild
         *
         * @param guildId The guild ID
         * @param fileName The file name
         * @return true if file exists, false otherwise
         */
        public static boolean fileExists(String guildId, String fileName) {
            return MystiGuardianConfig.getContext()
                            .selectCount()
                            .from(STORED_FILES)
                            .where(STORED_FILES.GUILD_ID.eq(guildId))
                            .and(STORED_FILES.FILE_NAME.eq(fileName))
                            .fetchOne(0, int.class)
                    > 0;
        }
    }
}
