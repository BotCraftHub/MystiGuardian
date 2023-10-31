package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.util.HashMap;
import java.util.Map;

public class DatabaseColumnBuilderImpl implements DatabaseColumnBuilder {
    private final DataType<?> type;
    private final String name;

    public DatabaseColumnBuilderImpl(DataType<?> type, String name) {
        this.type = type;
        this.name = name;
    }


    @Override
    public DatabaseColumnBuilder isNullable(boolean nullable) {
       this.type.nullable(nullable);
       return this;
    }

    @Override
    public DatabaseColumnBuilder isAutoIncrement(boolean autoIncrement) {
        this.type.identity(autoIncrement);
        return this;
    }

    @Override
    public DatabaseColumnBuilderRecord build() {
        return new DatabaseColumnBuilderRecord(name, type);
    }
}
