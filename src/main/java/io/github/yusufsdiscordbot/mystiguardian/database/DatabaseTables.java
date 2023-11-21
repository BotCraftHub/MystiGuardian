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
        handleSoftBanTable();
        handleAuthTable();
    }

    private void handleReloadAuditTable() {
        MystiGuardianUtils.createTable(context, "reload_audit")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "reason")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .execute();
    }

    private void handleWarnsTable() {
        MystiGuardianUtils.createTable(context, "warns")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "reason")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleAmountOfWarnsTable() {
        MystiGuardianUtils.createTable(context, "amount_of_warns")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_warns")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleTimeOutTable() {
        MystiGuardianUtils.createTable(context, "time_out")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "reason")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "duration")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleAmountOfTimeOutsTable() {
        MystiGuardianUtils.createTable(context, "amount_of_time_outs")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_time_outs")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleKickTable() {
        MystiGuardianUtils.createTable(context, "kick")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "reason")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleAmountOfKicksTable() {
        MystiGuardianUtils.createTable(context, "amount_of_kicks")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_kicks")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleBanTable() {
        MystiGuardianUtils.createTable(context, "ban")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "reason")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleAmountOfBansTable() {
        MystiGuardianUtils.createTable(context, "amount_of_bans")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "amount_of_bans")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addUniqueConstraint("guild_id", "user_id", "id")
                .execute();
    }

    private void handleSoftBanTable() {
        MystiGuardianUtils.createTable(context, "soft_ban")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "guild_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "reason")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.INTEGER, "days")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.TIMESTAMP, "time")
                        .isNullable(false)
                        .build())
                .execute();
    }

    private void handleAuthTable() {
        // so we have accessToken, refreshToken, expiresAt, userId
        MystiGuardianUtils.createTable(context, "auth")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build()) // Specify the primary key column
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "access_token")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "refresh_token")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "expires_at")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addPrimaryKey("id")
                .execute();
    }
}
