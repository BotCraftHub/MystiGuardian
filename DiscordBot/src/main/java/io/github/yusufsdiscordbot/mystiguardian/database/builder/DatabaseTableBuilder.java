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
