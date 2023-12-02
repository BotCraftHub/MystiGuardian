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

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class HandleDataBaseTables {
    public static List<String> tables = new ArrayList<>();
    public static Map<String, Map<String, DataType<?>>> tablesColumns = new HashMap<>();

    @Getter
    private static DSLContext context;

    private static void handleTables(DSLContext create) {

        databaseLogger.info("Creating tables");

        context = create;
        new DatabaseTables(create);
        try {
            checkTables(create);
        } catch (SQLException e) {
            databaseLogger.error("Error while checking tables", e);
        }
    }

    private static void checkTables(DSLContext create) throws SQLException {
        java.sql.ResultSet rs = create.select()
                .from("information_schema.tables")
                .where("table_schema = 'public'")
                .fetch()
                .intoResultSet();

        try {
            List<String> tableNames = new ArrayList<>();
            while (rs.next()) {
                tableNames.add(rs.getString("table_name"));
            }
            for (String tableName : tables) {
                if (!tableNames.contains(tableName)) {
                    databaseLogger.info(MystiGuardianUtils.formatString(
                            "Table %s is not in the list of tables, dropping it", tableName));
                    databaseLogger.info(MystiGuardianUtils.formatString(
                            "Table %s is not in the list of tables, dropping it", tableName));
                    // Create the table here
                    create.dropTable(tableName).execute();
                }
            }
        } finally {
            rs.close();
        }

        // Now check for any changes in the columns

        rs = create.select()
                .from("information_schema.columns")
                .where("table_schema = 'public'")
                .fetch()
                .intoResultSet();

        try {
            List<String> columnNames = new ArrayList<>();

            while (rs.next()) {
                columnNames.add(rs.getString("column_name"));
            }

            for (String tableName : tables) {
                if (tablesColumns.containsKey(tableName)) {
                    val columns = tablesColumns.get(tableName);

                    columns.forEach((columnName, dataType) -> {
                        if (!columnNames.contains(columnName)) {
                            databaseLogger.info(MystiGuardianUtils.formatString(
                                    "Column %s is not in the list of columns, adding it", columnName));
                            // Create the table here
                            create.alterTable(tableName)
                                    .addColumn(columnName, dataType)
                                    .execute();
                        }
                    });

                    for (String columnName : columnNames) {
                        if (!columns.containsKey(columnName) && columnExistsInTable(tableName, columnName, create)) {
                            databaseLogger.info(MystiGuardianUtils.formatString(
                                    "Column %s is not in the list of columns, dropping it", columnName));
                            // Create the table here
                            create.alterTable(tableName).dropColumn(columnName).execute();
                        }
                    }
                }
            }
        } finally {
            rs.close();
        }
    }

    private static boolean columnExistsInTable(String tableName, String columnName, DSLContext create) {
        return create.fetchExists(create.selectOne()
                .from("information_schema.columns")
                .where("table_name = ? AND column_name = ?", tableName, columnName));
    }

    public static void addTablesToDatabase(Connection connection) throws SQLException {
        DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
        handleTables(create);
    }
}
