package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import io.github.yusufsdiscordbot.mystiguardian.database.HandleDataBaseTables;
import org.jetbrains.annotations.NotNull;
import org.jooq.CreateTableElementListStep;
import org.jooq.DSLContext;
import org.jooq.DataType;

import java.util.Map;

public class DatabaseTableBuilderImpl implements DatabaseTableBuilder {
    private final DSLContext context;
    private final String name;
    private final CreateTableElementListStep create;
    private Map<String, DataType<?>> values;

    public DatabaseTableBuilderImpl(DSLContext context, String name) {
        this.context = context;
        this.name = name;

        create = this.context.createTableIfNotExists(name);
    }


    @Override
    public DatabaseTableBuilder addColumn(@NotNull DatabaseColumnBuilderRecord column) {
        column.values().forEach(create::column);
        this.values = column.values();
        return this;
    }

    @Override
    public void execute() {
        create.execute();

        HandleDataBaseTables.tables.add(name);
        HandleDataBaseTables.tablesColumns.put(name, values);
    }


}
