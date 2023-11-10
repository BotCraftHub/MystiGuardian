package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import io.github.yusufsdiscordbot.mystiguardian.database.HandleDataBaseTables;
import org.jetbrains.annotations.NotNull;
import org.jooq.CreateTableElementListStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.impl.DSL;

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
        this.create.constraint(DSL.constraint(STR. "pk_\{ name }" ).primaryKey(key));
        return this;
    }

    @Override
    public DatabaseTableBuilder addUniqueConstraint(String... columns) {
        this.create.constraint(DSL.constraint(STR. "uk_\{ name }" ).unique(columns));
        return this;
    }

    @Override
    public void execute() {
        for (Map.Entry<String, DataType<?>> entry : values.entrySet()) {
            // Ignore the returned object since it's not needed in this context
            this.create.column(entry.getKey(), entry.getValue());
        }

        this.create.execute();

        HandleDataBaseTables.tables.add(name);
        HandleDataBaseTables.tablesColumns.put(name, values);
    }


}
