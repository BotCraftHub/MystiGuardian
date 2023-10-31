package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.util.Map;

public interface DatabaseColumnBuilder {


    /**
     * Whether the column is nullable.
     *
     * @param nullable Whether the column is nullable.
     */
    DatabaseColumnBuilder isNullable(boolean nullable);

    /**
     * Whether the column is auto increment.
     *
     * @param autoIncrement Whether the column is auto increment.
     */
    DatabaseColumnBuilder isAutoIncrement(boolean autoIncrement);

    /**
     * Builds the table.
     *
     * @return The table.
     */
    DatabaseColumnBuilderRecord build();
}
