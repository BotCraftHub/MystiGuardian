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
package io.github.yusufsdiscordbot.mystiguardian.database;

import static io.github.yusufsdiscordbot.mystiguardian.utils.DatabaseUtils.deleteRecord;
import static io.github.yusufsdiscordbot.mystiguardian.utils.DatabaseUtils.updateRecord;
import static io.github.yusufsdiscordbot.mystigurdian.db.Tables.*;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.OAuthUser;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.SerializedObjectWrapper;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Result;

public class MystiGuardianDatabaseHandler {

    public static class ReloadAudit {
        public static void setReloadAuditRecord(String userId, String reason) {
            MystiGuardian.getContext()
                    .insertInto(
                            RELOAD_AUDIT, RELOAD_AUDIT.ID, RELOAD_AUDIT.USER_ID, RELOAD_AUDIT.REASON, RELOAD_AUDIT.TIME)
                    .values(
                            MystiGuardianUtils.getRandomId(),
                            userId,
                            reason,
                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        @NotNull
        public static Result<ReloadAuditRecord> getReloadAuditRecords() {
            return MystiGuardian.getContext().selectFrom(RELOAD_AUDIT).fetch();
        }
    }

    public static class Warns {
        public static long setWarnsRecord(String guildId, String userId, String reason) {
            // return the id
            return Objects.requireNonNull(MystiGuardian.getContext()
                            .insertInto(WARNS, WARNS.ID, WARNS.GUILD_ID, WARNS.USER_ID, WARNS.REASON, WARNS.TIME)
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
            return MystiGuardian.getContext()
                    .selectFrom(WARNS)
                    .where(WARNS.GUILD_ID.eq(guildId))
                    .and(WARNS.USER_ID.eq(userId))
                    .fetch();
        }

        public static @Nullable WarnsRecord getWarnRecordById(String guildId, Long id) {
            return MystiGuardian.getContext()
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
                    MystiGuardian.getContext(), AMOUNT_OF_WARNS, AMOUNT_OF_WARNS.AMOUNT_OF_WARNS_, guildId, userId);
        }

        @NotNull
        public static Result<AmountOfWarnsRecord> getAmountOfWarnsRecords(String guildId, String userId) {
            return MystiGuardian.getContext()
                    .selectFrom(AMOUNT_OF_WARNS)
                    .where(AMOUNT_OF_WARNS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_WARNS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class TimeOut {
        public static void setTimeOutRecord(String guildId, String userId, String reason, OffsetDateTime duration) {
            MystiGuardian.getContext()
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
            MystiGuardian.getContext()
                    .deleteFrom(TIME_OUT)
                    .where(TIME_OUT.GUILD_ID.eq(guildId))
                    .and(TIME_OUT.USER_ID.eq(userId))
                    .execute();
        }

        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.TimeOutRecord> getTimeOutRecords(
                String guildId, String userId) {
            return MystiGuardian.getContext()
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
                    MystiGuardian.getContext(),
                    AMOUNT_OF_TIME_OUTS,
                    AMOUNT_OF_TIME_OUTS.AMOUNT_OF_TIME_OUTS_,
                    guildId,
                    userId);
        }

        public static void deleteAmountOfTimeOutsRecord(String guildId, String userId) {
            deleteRecord(MystiGuardian.getContext(), AMOUNT_OF_TIME_OUTS, guildId, userId);
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfTimeOutsRecord>
                getAmountOfTimeOutsRecords(String guildId, String userId) {
            return MystiGuardian.getContext()
                    .selectFrom(AMOUNT_OF_TIME_OUTS)
                    .where(AMOUNT_OF_TIME_OUTS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_TIME_OUTS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class Kick {
        public static void setKickRecord(String guildId, String userId, String reason) {
            MystiGuardian.getContext()
                    .insertInto(KICK, KICK.ID, KICK.GUILD_ID, KICK.USER_ID, KICK.REASON, KICK.TIME)
                    .values(
                            MystiGuardianUtils.getRandomId(),
                            guildId,
                            userId,
                            reason,
                            OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        public static void deleteKickRecord(String guildId, String userId) {
            MystiGuardian.getContext()
                    .deleteFrom(KICK)
                    .where(KICK.GUILD_ID.eq(guildId))
                    .and(KICK.USER_ID.eq(userId))
                    .execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.KickRecord> getKickRecords(
                String guildId, String userId) {
            return MystiGuardian.getContext()
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
                    MystiGuardian.getContext(), AMOUNT_OF_KICKS, AMOUNT_OF_KICKS.AMOUNT_OF_KICKS_, guildId, userId);
        }

        public static void deleteAmountOfKicksRecord(String guildId, String userId) {
            deleteRecord(MystiGuardian.getContext(), AMOUNT_OF_KICKS, guildId, userId);
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfKicksRecord>
                getAmountOfKicksRecords(String guildId, String userId) {
            return MystiGuardian.getContext()
                    .selectFrom(AMOUNT_OF_KICKS)
                    .where(AMOUNT_OF_KICKS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_KICKS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class Ban {
        public static Long setBanRecord(String guildId, String userId, String reason) {
            return Objects.requireNonNull(MystiGuardian.getContext()
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
            MystiGuardian.getContext()
                    .deleteFrom(BAN)
                    .where(BAN.GUILD_ID.eq(guildId))
                    .and(BAN.USER_ID.eq(userId))
                    .execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.BanRecord> getBanRecords(
                String guildId, String userId) {
            return MystiGuardian.getContext()
                    .selectFrom(BAN)
                    .where(BAN.GUILD_ID.eq(guildId))
                    .and(BAN.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class AmountOfBans {
        public static void updateAmountOfBans(String guildId, String userId) {
            // get the current amount of bans
            updateRecord(MystiGuardian.getContext(), AMOUNT_OF_BANS, AMOUNT_OF_BANS.AMOUNT_OF_BANS_, guildId, userId);
        }

        public static void deleteAmountOfBansRecord(String guildId, String userId) {
            deleteRecord(MystiGuardian.getContext(), AMOUNT_OF_BANS, guildId, userId);
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfBansRecord>
                getAmountOfBansRecords(String guildId, String userId) {
            return MystiGuardian.getContext()
                    .selectFrom(AMOUNT_OF_BANS)
                    .where(AMOUNT_OF_BANS.GUILD_ID.eq(guildId))
                    .and(AMOUNT_OF_BANS.USER_ID.eq(userId))
                    .fetch();
        }
    }

    public static class SoftBan {
        public static long setSoftBanRecord(String guildId, String userId, String reason, Integer days) {
            return Objects.requireNonNull(MystiGuardian.getContext()
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
            MystiGuardian.getContext()
                    .deleteFrom(SOFT_BAN)
                    .where(SOFT_BAN.GUILD_ID.eq(guildId))
                    .and(SOFT_BAN.USER_ID.eq(userId))
                    .execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.SoftBanRecord> getSoftBanRecords(
                String guildId, String userId) {
            return MystiGuardian.getContext()
                    .selectFrom(SOFT_BAN)
                    .where(SOFT_BAN.GUILD_ID.eq(guildId))
                    .and(SOFT_BAN.USER_ID.eq(userId))
                    .fetch();
        }

        public static @NotNull Result<SoftBanRecord> getSoftBanRecords(String guildId) {
            return MystiGuardian.getContext()
                    .selectFrom(SOFT_BAN)
                    .where(SOFT_BAN.GUILD_ID.eq(guildId))
                    .fetch();
        }
    }

    public static class AuthHandler {

        public static void setAuthRecord(OAuthUser user) {
            SerializedObjectWrapper serializedAuthClass = serializeWithVersion(user);
            val userId = user.getUser().getId();

            if (getAuthRecord(userId) != null) {
                deleteAuthRecord(userId);
            }

            MystiGuardian.getContext()
                    .insertInto(AUTH, AUTH.ID, AUTH.AUTH_CLASS)
                    .values(user.getUser().getId(), serialize(serializedAuthClass))
                    .execute();
        }

        public static OAuthUser getAuthRecord(Long userId) {
            val context = MystiGuardian.getContext()
                    .selectFrom(AUTH)
                    .where(AUTH.ID.eq(userId))
                    .fetch();

            if (!context.isEmpty()) {
                SerializedObjectWrapper wrapper =
                        deserialize(context.get(0).getAuthClass(), SerializedObjectWrapper.class);
                if (wrapper != null && wrapper.getVersion() == 1) {
                    return deserialize(wrapper.getData(), OAuthUser.class);
                }
            }
            return null;
        }

        public static void deleteAuthRecord(Long userId) {
            MystiGuardian.getContext()
                    .deleteFrom(AUTH)
                    .where(AUTH.ID.eq(userId))
                    .execute();
        }

        public static @NotNull Map<byte[], Result<AuthRecord>> getAllAuthRecords() {
            return MystiGuardian.getContext().selectFrom(AUTH).fetchGroups(AUTH.AUTH_CLASS);
        }

        private static byte[] serialize(Object obj) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(obj);
                return bos.toByteArray();
            } catch (IOException e) {
                MystiGuardianUtils.databaseLogger.error("Error while serializing object", e);
                return null;
            }
        }

        private static SerializedObjectWrapper serializeWithVersion(OAuthUser obj) {
            int version = 1; // Update this when the class changes
            byte[] serializedData = serialize(obj);
            return new SerializedObjectWrapper(version, serializedData);
        }

        private static <T> T deserialize(byte[] bytes, Class<T> clazz) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object obj = ois.readObject();

                if (clazz.isInstance(obj)) {
                    return clazz.cast(obj);
                } else {
                    MystiGuardianUtils.databaseLogger.error("Deserialized object is not of the expected type.");
                    return null;
                }
            } catch (IOException | ClassNotFoundException e) {
                MystiGuardianUtils.databaseLogger.error("Error while deserializing object", e);
                return null;
            }
        }

        private static <T> T deserialize(byte[] bytes) {
            SerializedObjectWrapper wrapper = deserialize(bytes, SerializedObjectWrapper.class);
            if (wrapper != null && wrapper.getVersion() == 1) {
                return deserialize(wrapper.getData());
            }
            return null;
        }
    }
}
