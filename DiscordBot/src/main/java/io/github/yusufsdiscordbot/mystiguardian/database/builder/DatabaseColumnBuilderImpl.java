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

import lombok.extern.slf4j.Slf4j;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

@Slf4j
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
