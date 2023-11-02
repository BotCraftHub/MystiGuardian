package io.github.yusufsdiscordbot.mystiguardian.database;

import lombok.Getter;
import lombok.val;
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
        new DatabaseTables(create);
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
                    databaseLogger.info("Table " + tableName + " is not in the list of tables, dropping it");
                    // Create the table here
                    create.dropTable(tableName).execute();
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
                    val columns = tablesColumns.get(tableName);

                    columns.forEach((columnName, dataType) -> {
                        if (!columnNames.contains(columnName)) {
                            databaseLogger.info("Column " + columnName + " is not in the list of columns, adding it");
                            // Create the table here
                            create.alterTable(tableName)
                                    .addColumn(columnName, dataType)
                                    .execute();
                        }
                    });

                    //TODO: this is not working
                    // SQL [alter table "reload_audit" drop "amount_of_bans"]; ERROR: column "amount_of_bans" of relation "reload_audit" does not exist
                    for (String columnName : columnNames) {
                        if (!columns.containsKey(columnName)) {
                            databaseLogger.info("Column " + columnName + " is not in the list of columns, dropping it");
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

    public static void addTablesToDatabase(Connection connection) throws SQLException {
        DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
        handleTables(create);
    }
}





