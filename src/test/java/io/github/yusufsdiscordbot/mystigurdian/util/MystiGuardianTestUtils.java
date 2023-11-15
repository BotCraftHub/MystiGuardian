/*
 * Copyright 2023 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystigurdian.util;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.MalformedURLException;
import java.net.URI;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.IconImpl;
import org.javacord.core.entity.message.embed.EmbedBuilderDelegateImpl;
import org.javacord.core.interaction.SlashCommandInteractionOptionImpl;

public class MystiGuardianTestUtils {
    private static Long mockDiscordId = 123456789L;

    public static ObjectNode embedToJson(EmbedBuilder embed) {
        return ((EmbedBuilderDelegateImpl) embed.getDelegate()).toJsonNode();
    }

    public static String getEmbedDescription(EmbedBuilder embed) {
        return embedToJson(embed).get("description").asText();
    }

    public static void setCommonVariables(DiscordApi api, User user, SlashCommandInteraction event)
            throws MalformedURLException {
        when(user.getAvatar())
                .thenReturn(new IconImpl(
                        api,
                        URI.create(
                                        "https://cdn.discordapp.com/avatars/422708001976221697/f41bc30da291dbb710d67cf216fa8de2.webp?size=1024&width=0&height=512")
                                .toURL()));
        when(event.getUser()).thenReturn(user); // This line is already present
        when(user.getName()).thenReturn("TestUser");
        when(event.getUser().getId()).thenReturn(mockDiscordId);
        when(event.getApi()).thenReturn(api);
    }

    public static <T> SlashCommandInteractionOption getOptionByName(DiscordApiImpl api, String name, T value) {
        if (!(value instanceof String
                || value instanceof Integer
                || value instanceof Boolean
                || value instanceof Long
                || value instanceof Double)) {
            throw new IllegalArgumentException("Value must be a String, Integer, Boolean, Long or Double");
        }

        val jsonNode = new ObjectMapper().createObjectNode();

        jsonNode.put("name", name);

        switch (value.getClass().getSimpleName()) {
            case "String":
                jsonNode.put("value", (String) value);
                jsonNode.put("type", SlashCommandOptionType.STRING.getValue());
                break;
            case "Integer":
                jsonNode.put("value", (Integer) value);
                jsonNode.put("type", SlashCommandOptionType.LONG.getValue());
                break;
            case "Boolean":
                jsonNode.put("value", (Boolean) value);
                jsonNode.put("type", SlashCommandOptionType.BOOLEAN.getValue());
                break;
            case "Long":
                jsonNode.put("value", (Long) value);
                jsonNode.put("type", SlashCommandOptionType.LONG.getValue());
                break;
            case "Double":
                jsonNode.put("value", (Double) value);
                jsonNode.put("type", SlashCommandOptionType.DECIMAL.getValue());
                break;
        }

        return new SlashCommandInteractionOptionImpl(api, jsonNode, null, null);
    }
}
