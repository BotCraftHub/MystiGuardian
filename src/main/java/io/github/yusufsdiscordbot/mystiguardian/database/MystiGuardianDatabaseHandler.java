package io.github.yusufsdiscordbot.mystiguardian.database;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfWarnsRecord;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.ReloadAuditRecord;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.WarnsRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Result;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static io.github.yusufsdiscordbot.mystiguardian.utils.DatabaseUtils.deleteRecord;
import static io.github.yusufsdiscordbot.mystiguardian.utils.DatabaseUtils.updateRecord;
import static io.github.yusufsdiscordbot.mystigurdian.db.Tables.*;

public class MystiGuardianDatabaseHandler {
    private static UUID uniqueId = UUID.randomUUID();


    public static class ReloadAudit {
        public static void setReloadAuditRecord(String userId, String reason) {
            MystiGuardian.getContext().insertInto(RELOAD_AUDIT, RELOAD_AUDIT.ID, RELOAD_AUDIT.USER_ID, RELOAD_AUDIT.REASON, RELOAD_AUDIT.TIME)
                    .values(uniqueId.getLeastSignificantBits() + uniqueId.getMostSignificantBits(), userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        @NotNull
        public static Result<ReloadAuditRecord> getReloadAuditRecords() {
            return MystiGuardian.getContext().selectFrom(RELOAD_AUDIT).fetch();
        }

    }

    public static class Warns {
        public static long setWarnsRecord(String guildId, String userId, String reason) {
            //return the id
            return Objects.requireNonNull(MystiGuardian.getContext().insertInto(WARNS, WARNS.ID, WARNS.GUILD_ID, WARNS.USER_ID, WARNS.REASON, WARNS.TIME)
                            .values(uniqueId.getLeastSignificantBits() + uniqueId.getMostSignificantBits(), guildId, userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                            .returning(WARNS.ID)
                            .fetchOne())
                    .getId();
        }

        public static Result<WarnsRecord> getWarnsRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(WARNS).where(WARNS.GUILD_ID.eq(guildId)).and(WARNS.USER_ID.eq(userId)).fetch();
        }

        public static @Nullable WarnsRecord getWarnRecordById(String guildId, Long id) {
            return MystiGuardian.getContext()
                    .selectFrom(WARNS)
                    .where(WARNS.GUILD_ID.eq(guildId)).and(WARNS.ID.eq(id)).fetchOne();
        }
    }

    public static class AmountOfWarns {
        public static void updateAmountOfWarns(String guildId, String userId) {
            // Get the current amount of warns
            updateRecord(MystiGuardian.getContext(), AMOUNT_OF_WARNS, AMOUNT_OF_WARNS.AMOUNT_OF_WARNS_, guildId, userId);
        }

        @NotNull
        public static Result<AmountOfWarnsRecord> getAmountOfWarnsRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_WARNS).where(AMOUNT_OF_WARNS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_WARNS.USER_ID.eq(userId)).fetch();
        }
    }

    public static class TimeOut {
        public static void setTimeOutRecord(String guildId, String userId, String reason, OffsetDateTime duration) {
            MystiGuardian.getContext().insertInto(TIME_OUT, TIME_OUT.ID, TIME_OUT.GUILD_ID, TIME_OUT.USER_ID, TIME_OUT.REASON, TIME_OUT.DURATION, TIME_OUT.TIME)
                    .values(uniqueId.getLeastSignificantBits() + uniqueId.getMostSignificantBits(), guildId, userId, reason, duration, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        public static void deleteTimeOutRecord(String guildId, String userId) {
            MystiGuardian.getContext().deleteFrom(TIME_OUT).where(TIME_OUT.GUILD_ID.eq(guildId)).and(TIME_OUT.USER_ID.eq(userId)).execute();
        }

        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.TimeOutRecord> getTimeOutRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(TIME_OUT).where(TIME_OUT.GUILD_ID.eq(guildId)).and(TIME_OUT.USER_ID.eq(userId)).fetch();
        }
    }

    public static class AmountOfTimeOuts {
        public static void updateAmountOfTimeOuts(String guildId, String userId) {
            //get the current amount of time outs
            updateRecord(MystiGuardian.getContext(), AMOUNT_OF_TIME_OUTS, AMOUNT_OF_TIME_OUTS.AMOUNT_OF_TIME_OUTS_, guildId, userId);
        }

        public static void deleteAmountOfTimeOutsRecord(String guildId, String userId) {
            deleteRecord(MystiGuardian.getContext(), AMOUNT_OF_TIME_OUTS, guildId, userId);
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfTimeOutsRecord> getAmountOfTimeOutsRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_TIME_OUTS).where(AMOUNT_OF_TIME_OUTS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_TIME_OUTS.USER_ID.eq(userId)).fetch();
        }
    }

    public static class Kick {
        public static void setKickRecord(String guildId, String userId, String reason) {
            MystiGuardian.getContext().insertInto(KICK, KICK.ID, KICK.GUILD_ID, KICK.USER_ID, KICK.REASON, KICK.TIME)
                    .values(uniqueId.getLeastSignificantBits() + uniqueId.getMostSignificantBits(), guildId, userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        public static void deleteKickRecord(String guildId, String userId) {
            MystiGuardian.getContext().deleteFrom(KICK).where(KICK.GUILD_ID.eq(guildId)).and(KICK.USER_ID.eq(userId)).execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.KickRecord> getKickRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(KICK).where(KICK.GUILD_ID.eq(guildId)).and(KICK.USER_ID.eq(userId)).fetch();
        }
    }

    public static class AmountOfKicks {
        public static void updateAmountOfKicks(String guildId, String userId) {
            //get the current amount of kicks
            updateRecord(MystiGuardian.getContext(), AMOUNT_OF_KICKS, AMOUNT_OF_KICKS.AMOUNT_OF_KICKS_, guildId, userId);
        }

        public static void deleteAmountOfKicksRecord(String guildId, String userId) {
            deleteRecord(MystiGuardian.getContext(), AMOUNT_OF_KICKS, guildId, userId);
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfKicksRecord> getAmountOfKicksRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_KICKS).where(AMOUNT_OF_KICKS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_KICKS.USER_ID.eq(userId)).fetch();
        }
    }

    public static class Ban {
        public static void setBanRecord(String guildId, String userId, String reason) {
            MystiGuardian.getContext().insertInto(BAN, BAN.ID, BAN.GUILD_ID, BAN.USER_ID, BAN.REASON, BAN.TIME)
                    .values(uniqueId.getLeastSignificantBits() + uniqueId.getMostSignificantBits(), guildId, userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        public static void deleteBanRecord(String guildId, String userId) {
            MystiGuardian.getContext().deleteFrom(BAN).where(BAN.GUILD_ID.eq(guildId)).and(BAN.USER_ID.eq(userId)).execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.BanRecord> getBanRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(BAN).where(BAN.GUILD_ID.eq(guildId)).and(BAN.USER_ID.eq(userId)).fetch();
        }
    }

    public static class AmountOfBans {
        public static void updateAmountOfBans(String guildId, String userId) {
            //get the current amount of bans
            updateRecord(MystiGuardian.getContext(), AMOUNT_OF_BANS, AMOUNT_OF_BANS.AMOUNT_OF_BANS_, guildId, userId);
        }

        public static void deleteAmountOfBansRecord(String guildId, String userId) {
            deleteRecord(MystiGuardian.getContext(), AMOUNT_OF_BANS, guildId, userId);
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfBansRecord> getAmountOfBansRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_BANS).where(AMOUNT_OF_BANS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_BANS.USER_ID.eq(userId)).fetch();
        }
    }
}
