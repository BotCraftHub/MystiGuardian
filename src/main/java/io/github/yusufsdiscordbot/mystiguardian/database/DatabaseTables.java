package io.github.yusufsdiscordbot.mystiguardian.database;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

public class DatabaseTables {
    private final DSLContext context;


    public DatabaseTables(DSLContext context) {
        this.context = context;

        handleReloadAuditTable();
        handleWarnsTable();
        handleAmountOfWarnsTable();
        handleTimeOutTable();
        handleAmountOfTimeOutsTable();
        handleKickTable();
        handleAmountOfKicksTable();
        handleBanTable();
        handleAmountOfBansTable();
    }

    private void handleReloadAuditTable() {
        MystiGuardianUtils.createTable(context, "reload_audit")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "reason").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time").isNullable(false).build())
                .execute();
    }

    private void handleWarnsTable() {
        MystiGuardianUtils.createTable(context, "warns")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "reason").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time").isNullable(false).build())
                .execute();
    }

    private void handleAmountOfWarnsTable() {
        MystiGuardianUtils.createTable(context, "amount_of_warns")
                .addPrimaryKey("id")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_warns").isNullable(false).build())
                .execute();
    }

    private void handleTimeOutTable() {
        MystiGuardianUtils.createTable(context, "time_out")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "reason").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "duration").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time").isNullable(false).build())
                .execute();
    }

    private void handleAmountOfTimeOutsTable() {
        MystiGuardianUtils.createTable(context, "amount_of_time_outs")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_time_outs").isNullable(false).build())
                .execute();
    }

    private void handleKickTable() {
        MystiGuardianUtils.createTable(context, "kick")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "reason").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time").isNullable(false).build())
                .execute();
    }

    private void handleAmountOfKicksTable() {
        MystiGuardianUtils.createTable(context, "amount_of_kicks")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_kicks").isNullable(false).build())
                .execute();
    }

    private void handleBanTable() {
        MystiGuardianUtils.createTable(context, "ban")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "reason").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time").isNullable(false).build())
                .execute();
    }

    private void handleAmountOfBansTable() {
        MystiGuardianUtils.createTable(context, "amount_of_bans")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "guild_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 256), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_bans").isNullable(false).build())
                .execute();
    }
}
