package io.github.yusufsdiscordbot.mystiguardian.database.builder;

public interface DatabaseTableBuilder {

    /**
     * Adds a column to the table.
     */
    DatabaseTableBuilder addColumn(DatabaseColumnBuilderRecord column);

    /**
     * Executes the query.
     */
    void execute();
}
