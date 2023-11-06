package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.util.HashMap;
import java.util.Map;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

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

        if (this.type == SQLDataType.BIGINT) {
            this.type.identity(autoIncrement);
        } else {
            logger.error("The type of the column is not BIGINT, therefore it cannot be auto incremented");
        }

        return this;
    }

    @Override
    public DatabaseColumnBuilderRecord build() {
        return new DatabaseColumnBuilderRecord(name, type);
    }
}
