package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.util.Map;

public interface DatabaseColumnBuilder {

    /**
     * Adds a value to the table.
     *
     * @param name     The name of the column.
     * @param nullable Whether the column is nullable.
     * @return The builder.
     */
    DatabaseColumnBuilder addValue(DataType<?> type, String name, boolean nullable);

    /**
     * Adds a value to the table.
     *
     * @param name     The name of the column.
     * @param nullable Whether the column is nullable.
     * @param autoIncrement Whether the column is auto increment.
     * @return The builder.
     */
    DatabaseColumnBuilder addValue(DataType<?> type, String name, boolean nullable, boolean autoIncrement);

    /**
     * Builds the table.
     *
     * @return The table.
     */
    DatabaseColumnBuilderRecord build();
}
