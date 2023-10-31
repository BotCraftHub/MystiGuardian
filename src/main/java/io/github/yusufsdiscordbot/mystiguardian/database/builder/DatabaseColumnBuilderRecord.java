package io.github.yusufsdiscordbot.mystiguardian.database.builder;

import org.jooq.DataType;

import java.util.Map;

public record DatabaseColumnBuilderRecord(Map<String, DataType<?>> values) {}
