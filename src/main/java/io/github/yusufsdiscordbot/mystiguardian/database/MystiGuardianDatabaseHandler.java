package io.github.yusufsdiscordbot.mystiguardian.database;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.AmountOfWarns;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfWarnsRecord;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.ReloadAuditRecord;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.WarnsRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.Result;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static io.github.yusufsdiscordbot.mystigurdian.db.Tables.*;

public class MystiGuardianDatabaseHandler {

    public static class ReloadAudit {
        public static void setReloadAuditRecord(String userId, String reason) {
            MystiGuardian.getContext().insertInto(RELOAD_AUDIT, RELOAD_AUDIT.USER_ID, RELOAD_AUDIT.REASON, RELOAD_AUDIT.TIME)
                    .values(userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        @NotNull
        public static Result<ReloadAuditRecord> getReloadAuditRecords() {
            return MystiGuardian.getContext().selectFrom(RELOAD_AUDIT).fetch();
        }

    }

    public static class Warns {
        public static void setWarnsRecord(String guildId, String userId, String reason) {
            MystiGuardian.getContext().insertInto(WARNS, WARNS.GUILD_ID, WARNS.USER_ID, WARNS.REASON, WARNS.TIME)
                    .values(guildId, userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
                    .execute();
        }

        public static Result<WarnsRecord> getWarnsRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(WARNS).where(WARNS.GUILD_ID.eq(guildId)).and(WARNS.USER_ID.eq(userId)).fetch();
        }
    }

    public static class AmountOfWarns {
        public static void updateAmountOfWarns(String guildId, String userId) {
            //get the current amount of warns
            Integer currentAmountOfWarns = MystiGuardian.getContext().selectFrom(AMOUNT_OF_WARNS).where(AMOUNT_OF_WARNS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_WARNS.USER_ID.eq(userId))
                    .fetchOne(io.github.yusufsdiscordbot.mystigurdian.db.tables.AmountOfWarns.AMOUNT_OF_WARNS.AMOUNT_OF_WARNS_);

            //if the current amount of warns is 0 or null, set it to 1, else add 1 to the current amount of warns
            Integer newAmountOfWarns = currentAmountOfWarns == null || currentAmountOfWarns == 0 ? 1 : currentAmountOfWarns + 1;

            MystiGuardian.getContext().insertInto(AMOUNT_OF_WARNS, AMOUNT_OF_WARNS.GUILD_ID, AMOUNT_OF_WARNS.USER_ID, AMOUNT_OF_WARNS.AMOUNT_OF_WARNS_)
                    .values(guildId, userId, newAmountOfWarns)
                    .onDuplicateKeyUpdate()
                    .set(AMOUNT_OF_WARNS.AMOUNT_OF_WARNS_, newAmountOfWarns)
                    .execute();
        }

        @NotNull
        public static Result<AmountOfWarnsRecord> getAmountOfWarnsRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_WARNS).where(AMOUNT_OF_WARNS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_WARNS.USER_ID.eq(userId)).fetch();
        }
    }

    public static class TimeOut {
        public static void setTimeOutRecord(String guildId, String userId, String reason, OffsetDateTime duration) {
            MystiGuardian.getContext().insertInto(TIME_OUT, TIME_OUT.GUILD_ID, TIME_OUT.USER_ID, TIME_OUT.REASON, TIME_OUT.DURATION, TIME_OUT.TIME)
                    .values(guildId, userId, reason, duration, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
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
            Integer currentAmountOfTimeOuts = MystiGuardian.getContext().selectFrom(AMOUNT_OF_TIME_OUTS).where(AMOUNT_OF_TIME_OUTS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_TIME_OUTS.USER_ID.eq(userId))
                    .fetchOne(io.github.yusufsdiscordbot.mystigurdian.db.tables.AmountOfTimeOuts.AMOUNT_OF_TIME_OUTS.AMOUNT_OF_TIME_OUTS_);

            //if the current amount of time outs is 0 or null, set it to 1, else add 1 to the current amount of time outs
            Integer newAmountOfTimeOuts = currentAmountOfTimeOuts == null || currentAmountOfTimeOuts == 0 ? 1 : currentAmountOfTimeOuts + 1;

            MystiGuardian.getContext().insertInto(AMOUNT_OF_TIME_OUTS, AMOUNT_OF_TIME_OUTS.GUILD_ID, AMOUNT_OF_TIME_OUTS.USER_ID, AMOUNT_OF_TIME_OUTS.AMOUNT_OF_TIME_OUTS_)
                    .values(guildId, userId, newAmountOfTimeOuts)
                    .onDuplicateKeyUpdate()
                    .set(AMOUNT_OF_TIME_OUTS.AMOUNT_OF_TIME_OUTS_, newAmountOfTimeOuts)
                    .execute();
        }

        public static void deleteAmountOfTimeOutsRecord(String guildId, String userId) {
            MystiGuardian.getContext().deleteFrom(AMOUNT_OF_TIME_OUTS).where(AMOUNT_OF_TIME_OUTS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_TIME_OUTS.USER_ID.eq(userId)).execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfTimeOutsRecord> getAmountOfTimeOutsRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_TIME_OUTS).where(AMOUNT_OF_TIME_OUTS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_TIME_OUTS.USER_ID.eq(userId)).fetch();
        }
    }

    public static class Kick {
        public static void setKickRecord(String guildId, String userId, String reason) {
            MystiGuardian.getContext().insertInto(KICK, KICK.GUILD_ID, KICK.USER_ID, KICK.REASON, KICK.TIME)
                    .values(guildId, userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
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
            Integer currentAmountOfKicks = MystiGuardian.getContext().selectFrom(AMOUNT_OF_KICKS).where(AMOUNT_OF_KICKS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_KICKS.USER_ID.eq(userId))
                    .fetchOne(io.github.yusufsdiscordbot.mystigurdian.db.tables.AmountOfKicks.AMOUNT_OF_KICKS.AMOUNT_OF_KICKS_);

            //if the current amount of kicks is 0 or null, set it to 1, else add 1 to the current amount of kicks
            Integer newAmountOfKicks = currentAmountOfKicks == null || currentAmountOfKicks == 0 ? 1 : currentAmountOfKicks + 1;

            MystiGuardian.getContext().insertInto(AMOUNT_OF_KICKS, AMOUNT_OF_KICKS.GUILD_ID, AMOUNT_OF_KICKS.USER_ID, AMOUNT_OF_KICKS.AMOUNT_OF_KICKS_)
                    .values(guildId, userId, newAmountOfKicks)
                    .onDuplicateKeyUpdate()
                    .set(AMOUNT_OF_KICKS.AMOUNT_OF_KICKS_, newAmountOfKicks)
                    .execute();
        }

        public static void deleteAmountOfKicksRecord(String guildId, String userId) {
            MystiGuardian.getContext().deleteFrom(AMOUNT_OF_KICKS).where(AMOUNT_OF_KICKS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_KICKS.USER_ID.eq(userId)).execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfKicksRecord> getAmountOfKicksRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_KICKS).where(AMOUNT_OF_KICKS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_KICKS.USER_ID.eq(userId)).fetch();
        }
    }

    public static class Ban {
        public static void setBanRecord(String guildId, String userId, String reason) {
            MystiGuardian.getContext().insertInto(BAN, BAN.GUILD_ID, BAN.USER_ID, BAN.REASON, BAN.TIME)
                    .values(guildId, userId, reason, OffsetDateTime.of(LocalDateTime.now(), MystiGuardianUtils.getZoneOffset()))
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
            Integer currentAmountOfBans = MystiGuardian.getContext().selectFrom(AMOUNT_OF_BANS).where(AMOUNT_OF_BANS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_BANS.USER_ID.eq(userId))
                    .fetchOne(io.github.yusufsdiscordbot.mystigurdian.db.tables.AmountOfBans.AMOUNT_OF_BANS.AMOUNT_OF_BANS_);

            //if the current amount of bans is 0 or null, set it to 1, else add 1 to the current amount of bans
            Integer newAmountOfBans = currentAmountOfBans == null || currentAmountOfBans == 0 ? 1 : currentAmountOfBans + 1;

            MystiGuardian.getContext().insertInto(AMOUNT_OF_BANS, AMOUNT_OF_BANS.GUILD_ID, AMOUNT_OF_BANS.USER_ID, AMOUNT_OF_BANS.AMOUNT_OF_BANS_)
                    .values(guildId, userId, newAmountOfBans)
                    .onDuplicateKeyUpdate()
                    .set(AMOUNT_OF_BANS.AMOUNT_OF_BANS_, newAmountOfBans)
                    .execute();
        }

        public static void deleteAmountOfBansRecord(String guildId, String userId) {
            MystiGuardian.getContext().deleteFrom(AMOUNT_OF_BANS).where(AMOUNT_OF_BANS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_BANS.USER_ID.eq(userId)).execute();
        }

        @NotNull
        public static Result<io.github.yusufsdiscordbot.mystigurdian.db.tables.records.AmountOfBansRecord> getAmountOfBansRecords(String guildId, String userId) {
            return MystiGuardian.getContext().selectFrom(AMOUNT_OF_BANS).where(AMOUNT_OF_BANS.GUILD_ID.eq(guildId)).and(AMOUNT_OF_BANS.USER_ID.eq(userId)).fetch();
        }
    }
}
