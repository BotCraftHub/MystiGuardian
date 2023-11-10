package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import org.jooq.DataType;

public record DatabaseColumnBuilderRecord(String name, DataType<?> dataType) {
}
