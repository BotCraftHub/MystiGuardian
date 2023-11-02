package io.github.yusufsdiscordbot.mystiguardian.utils;

import lombok.val;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.jooq.impl.DSL.field;

public class DatabaseUtils {
    public static void updateRecord(DSLContext context, Table<?> table, Field<Integer> field, String guildId, String userId) {
        Field<String> guildIdField = field(guildId).cast(String.class);
        Field<String> userIdField = field(userId).cast(String.class);

        //Cursor returned more than one result
        List<Integer> currentValues = context
                .select(field)
                .from(table)
                .where(Objects.requireNonNull(table.field("guild_id", String.class)).eq(guildIdField).and(Objects.requireNonNull(table.field("user_id", String.class)).eq(userIdField)))
                .fetch(field);

        //get the highest value
        Integer currentValue;
        if (currentValues.isEmpty()) {
            currentValue = null;
        } else {
            currentValue = currentValues.stream().max(Integer::compareTo).orElse(null);
        }

        Integer newValue = (currentValue == null || currentValue == 0) ? 1 : currentValue + 1;

        val uuid = UUID.randomUUID();
        context
                .insertInto(table, table.field("guild_id", String.class), table.field("user_id", String.class), field, table.field("id", Long.class))
                .values(guildId, userId, newValue, uuid.getLeastSignificantBits() + uuid.getMostSignificantBits())
                .onConflict(table.field("guild_id"), table.field("user_id"), table.field("id"))
                .doUpdate()
                .set(field, newValue)
                .execute();
    }

    public static void deleteRecord(DSLContext context, Table<?> table, String guildId, String userId) {
        Field<String> guildIdField = field(guildId).cast(String.class);
        Field<String> userIdField = field(userId).cast(String.class);

        context
                .deleteFrom(table)
                .where(Objects.requireNonNull(table.field("guild_id", String.class)).eq(guildIdField).and(Objects.requireNonNull(table.field("user_id", String.class)).eq(userIdField)))
                .execute();
    }
}
