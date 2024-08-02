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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import static org.jooq.impl.DSL.field;

import java.util.List;
import java.util.Objects;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;

public class DatabaseUtils {
    public static void updateRecord(
            DSLContext context, Table<?> table, Field<Integer> field, String guildId, String userId) {
        Field<String> guildIdField = field(guildId).cast(String.class);
        Field<String> userIdField = field(userId).cast(String.class);

        // Cursor returned more than one result
        List<Integer> currentValues =
                context
                        .select(field)
                        .from(table)
                        .where(
                                Objects.requireNonNull(table.field("guild_id", String.class))
                                        .eq(guildIdField)
                                        .and(
                                                Objects.requireNonNull(table.field("user_id", String.class))
                                                        .eq(userIdField)))
                        .fetch(field);

        // get the highest value
        Integer currentValue;
        if (currentValues.isEmpty()) {
            currentValue = null;
        } else {
            currentValue = currentValues.stream().max(Integer::compareTo).orElse(null);
        }

        Integer newValue = (currentValue == null || currentValue == 0) ? 1 : currentValue + 1;

        context
                .insertInto(
                        table,
                        table.field("guild_id", String.class),
                        table.field("user_id", String.class),
                        field,
                        table.field("id", Long.class))
                .values(guildId, userId, newValue, MystiGuardianUtils.getRandomId())
                .onConflict(table.field("guild_id"), table.field("user_id"), table.field("id"))
                .doUpdate()
                .set(field, newValue)
                .execute();
    }

    public static void deleteRecord(
            DSLContext context, Table<?> table, String guildId, String userId) {
        Field<String> guildIdField = field(guildId).cast(String.class);
        Field<String> userIdField = field(userId).cast(String.class);

        context
                .deleteFrom(table)
                .where(
                        Objects.requireNonNull(table.field("guild_id", String.class))
                                .eq(guildIdField)
                                .and(Objects.requireNonNull(table.field("user_id", String.class)).eq(userIdField)))
                .execute();
    }
}
