package io.github.yusufsdiscordbot.mystiguardian.database;

import org.jooq.DSLContext;

public class DatabaseTables {
    private final DSLContext create;


    public DatabaseTables(DSLContext create) {
        this.create = create;
    }
}
