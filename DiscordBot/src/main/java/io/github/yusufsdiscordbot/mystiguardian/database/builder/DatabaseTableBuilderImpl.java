/*
 * Copyright 2024 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.formatString;

import io.github.yusufsdiscordbot.mystiguardian.database.HandleDataBaseTables;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jooq.CreateTableElementListStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.impl.DSL;

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
        this.create.constraint(DSL.constraint(formatString("pk_%s", name)).primaryKey(key));
        return this;
    }

    @Override
    public DatabaseTableBuilder addUniqueConstraint(String... columns) {
        this.create.constraint(DSL.constraint(formatString("uk_%s", name)).unique(columns));
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
