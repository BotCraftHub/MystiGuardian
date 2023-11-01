package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import io.github.yusufsdiscordbot.mystiguardian.database.HandleDataBaseTables;
import org.jetbrains.annotations.NotNull;
import org.jooq.CreateTableElementListStep;
import org.jooq.DSLContext;
import org.jooq.DataType;

import java.util.HashMap;
import java.util.Map;

public class DatabaseTableBuilderImpl implements DatabaseTableBuilder {
    private final String name;
    private final CreateTableElementListStep create;
    private final Map<String, DataType<?>> values = new HashMap<>();

    public DatabaseTableBuilderImpl(DSLContext context, String name) {
        this.name = name;

        this.create = context.createTableIfNotExists(name);
    }


    @Override
    public DatabaseTableBuilder addColumn(@NotNull DatabaseColumnBuilderRecord column) {
        values.put(column.name(), column.dataType());
        return this;
    }

    @Override
    public DatabaseTableBuilder addPrimaryKey(String key) {
        this.create.primaryKey("pk_" + name);
        return this;
    }

    @Override
    public void execute() {
        values.forEach(this.create::column);

        this.create.execute();

        HandleDataBaseTables.tables.add(name);
        HandleDataBaseTables.tablesColumns.put(name, values);
    }


}
