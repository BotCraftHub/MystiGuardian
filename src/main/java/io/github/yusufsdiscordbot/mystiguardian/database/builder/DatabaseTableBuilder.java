package io.github.yusufsdiscordbot.mystiguardian.database.builder;

public interface DatabaseTableBuilder {

    /**
     * Adds a column to the table.
     */
    DatabaseTableBuilder addColumn(DatabaseColumnBuilderRecord column);

    /**
     * Adds a primary key to the table.
     *
     * @param key The key.
     * @return The table.
     */
    DatabaseTableBuilder addPrimaryKey(String key);

    /**
     * Adds a unique constraint to the table.
     *
     * @param columns The columns.
     * @return The table.
     */
    DatabaseTableBuilder addUniqueConstraint(String... columns);

    /**
     * Executes the query.
     */
    void execute();
}
