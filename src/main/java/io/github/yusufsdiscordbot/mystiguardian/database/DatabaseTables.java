package io.github.yusufsdiscordbot.mystiguardian.database;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.jooq.DSLContext;
import org.jooq.impl.SQLDataType;

public class DatabaseTables {
    private final DSLContext context;


    public DatabaseTables(DSLContext context) {
        this.context = context;

        handleReloadAuditTable();
    }

    private void handleReloadAuditTable() {
        MystiGuardianUtils.createTable(context, "reload_audit")
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.BIGINT, "id").isAutoIncrement(true).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 2147483647), "user_id").isNullable(false).build())
                .addColumn(MystiGuardianUtils.createColumn(SQLDataType.VARCHAR( 2147483647), "reason").isNullable(false).build());
    }
}
