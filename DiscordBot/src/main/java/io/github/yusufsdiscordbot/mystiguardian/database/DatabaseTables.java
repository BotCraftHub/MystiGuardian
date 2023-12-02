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

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.databaseLogger;

import io.github.yusufsdiscordbot.mystiguardian.database.builder.DatabaseTableBuilder;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

@SuppressWarnings("unused")
public class DatabaseTables {
    private final DSLContext context;

    public DatabaseTables(DSLContext context) {
        this.context = context;

        List<DatabaseTableBuilder> databaseTableBuilders = new ArrayList<>();

        Method[] methods = DatabaseTables.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("handle") && method.getParameterCount() == 0) {
                try {
                    DatabaseTableBuilder tableBuilder = (DatabaseTableBuilder) method.invoke(this);
                    databaseTableBuilders.add(tableBuilder);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    databaseLogger.error("Error while invoking database table builder", e);
                }
            }
        }

        databaseTableBuilders.forEach(DatabaseTableBuilder::execute);
    }

    private DatabaseTableBuilder handleReloadAuditTable() {
        return MystiGuardianUtils.createTable(context, "reload_audit")
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
                .addPrimaryKey("id"); // Specify the primary key column
    }

    private DatabaseTableBuilder handleWarnsTable() {
        return MystiGuardianUtils.createTable(context, "warns")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleAmountOfWarnsTable() {
        return MystiGuardianUtils.createTable(context, "amount_of_warns")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleTimeOutTable() {
        return MystiGuardianUtils.createTable(context, "time_out")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleAmountOfTimeOutsTable() {
        return MystiGuardianUtils.createTable(context, "amount_of_time_outs")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleKickTable() {
        return MystiGuardianUtils.createTable(context, "kick")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleAmountOfKicksTable() {
        return MystiGuardianUtils.createTable(context, "amount_of_kicks")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleBanTable() {
        return MystiGuardianUtils.createTable(context, "ban")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleAmountOfBansTable() {
        return MystiGuardianUtils.createTable(context, "amount_of_bans")
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
                .addUniqueConstraint("guild_id", "user_id", "id");
    }

    private DatabaseTableBuilder handleSoftBanTable() {
        return MystiGuardianUtils.createTable(context, "soft_ban")
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
                        .build());
    }

    private DatabaseTableBuilder handleOAuthTable() {
        return MystiGuardianUtils.createTable(context, "oauth")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id")
                        .isAutoIncrement(true)
                        .build())
                .addPrimaryKey("id") // Specify the primary key column
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "user_id")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "access_token")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(256), "refresh_token")
                        .isNullable(false)
                        .build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR(1000), "user_json")
                        .isNullable(false)
                        .build());
    }
}
