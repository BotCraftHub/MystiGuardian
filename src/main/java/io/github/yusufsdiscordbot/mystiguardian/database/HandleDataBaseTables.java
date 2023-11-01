package io.github.yusufsdiscordbot.mystiguardian.database;

import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.databaseLogger;

public class HandleDataBaseTables {
    public static List<String> tables = new ArrayList<>();
    public static Map<String, Map<String, DataType<?>>> tablesColumns = new HashMap<>();
    @Getter
    private static DSLContext context;


    private static void handleTables(DSLContext create) {
        context = create;

        try {
            checkTables(create);
        } catch (SQLException e) {
            databaseLogger.error("Error while checking tables", e);
        }
    }

    private static void checkTables(DSLContext create) throws SQLException {
        java.sql.ResultSet rs = create.select().from("information_schema.tables")
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
                    databaseLogger.info("Table " + tableName + " is not in the list of tables, creating it");
                    // Create the table here
                    create.createTable(tableName).execute();
                }
            }
        } finally {
            rs.close();
        }

        // Now check for any changes in the columns

        rs = create.select().from("information_schema.columns")
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
                    Map<String, DataType<?>> columns = tablesColumns.get(tableName);
                    for (Map.Entry<String, DataType<?>> column : columns.entrySet()) {
                        if (!columnNames.contains(column.getKey())) {
                            // Add the column
                            databaseLogger.info("Column " + column.getKey() + " is not in the list of columns, adding it");
                            create.alterTable(tableName)
                                    .addColumn(column.getKey(), column.getValue())
                                    .execute();
                        }
                    }
                    for (String column : columnNames) {
                        if (!columns.containsKey(column)) {
                            // Drop the column
                            databaseLogger.info("Column " + column + " is not in the list of columns, dropping it");
                            create.alterTable(tableName)
                                    .dropColumn(column)
                                    .execute();
                        }
                    }
                }
            }
        } finally {
            rs.close();
        }
    }

    public static void addTablesToDatabase(Connection connection) throws SQLException {
        DSLContext create = DSL.using(connection, SQLDialect.DEFAULT);
        handleTables(create);
    }
}





