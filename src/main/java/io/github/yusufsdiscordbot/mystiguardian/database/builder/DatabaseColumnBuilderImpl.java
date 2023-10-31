package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.util.Map;

public class DatabaseColumnBuilderImpl implements DatabaseColumnBuilder {
    private final Map<String, DataType<?>> values;

    public DatabaseColumnBuilderImpl(Map<String, DataType<?>> values) {
        this.values = values;
    }


    @Override
    public DatabaseColumnBuilder addValue(DataType<?> type, String name, boolean nullable) {
        this.values.put(name, type.nullable(nullable));
        return this;
    }

    @Override
    public DatabaseColumnBuilder addValue(DataType<?> type, String name, boolean nullable, boolean autoIncrement) {
        this.values.put(name, type.nullable(nullable).identity(autoIncrement));
        return this;
    }

    @Override
    public DatabaseColumnBuilderRecord build() {
        return new DatabaseColumnBuilderRecord(values);
    }
}
