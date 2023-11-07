package io.github.yusufsdiscordbot.mystigurdian.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.val;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.message.embed.EmbedBuilderDelegateImpl;
import org.javacord.core.interaction.SlashCommandInteractionOptionImpl;
import org.slf4j.Logger;

public class MystiGuardianTestUtils {
    public static Logger logger = org.slf4j.LoggerFactory.getLogger(MystiGuardianTestUtils.class);

    public static ObjectNode embedToJson(EmbedBuilder embed) {
        return ((EmbedBuilderDelegateImpl) embed.getDelegate()).toJsonNode();
    }

    public static <T> SlashCommandInteractionOption getOptionByName(DiscordApiImpl api, String name, T value) {
        if (!(value instanceof String || value instanceof Integer || value instanceof Boolean || value instanceof Long || value instanceof Double)) {
            throw new IllegalArgumentException("Value must be a String, Integer, Boolean, Long or Double");
        }

        val jsonNode = new ObjectMapper().createObjectNode();

        jsonNode.put("name", name);

        switch (value.getClass().getSimpleName()) {
            case "String":
                jsonNode.put("value", (String) value);
                break;
            case "Integer":
                jsonNode.put("value", (Integer) value);
                break;
            case "Boolean":
                jsonNode.put("value", (Boolean) value);
                break;
            case "Long":
                jsonNode.put("value", (Long) value);
                break;
            case "Double":
                jsonNode.put("value", (Double) value);
                break;
        }

        return new SlashCommandInteractionOptionImpl(api, jsonNode, null, null);
    }
}
