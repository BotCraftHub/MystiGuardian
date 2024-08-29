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
package io.github.yusufsdiscordbot.mystigurdian.util;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.utils.ImageProxy;

public class MystiGuardianTestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectNode embedToJson(EmbedBuilder embed) {
        try {
            JsonNode jsonNode = objectMapper.readTree(embed.build().toData().toString());
            if (jsonNode instanceof ObjectNode) {
                return (ObjectNode) jsonNode;
            } else {
                throw new IllegalArgumentException("The JSON root node is not an ObjectNode");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert embed to JSON", e);
        }
    }

    public static String getEmbedDescription(EmbedBuilder embed) {
        return embedToJson(embed).get("description").asText();
    }

    public static void setCommonVariables(JDA jda, User user, SlashCommandInteraction event) {
        when(user.getAvatar())
                .thenReturn(
                        new ImageProxy(
                                "https://cdn.discordapp.com/avatars/422708001976221697/f41bc30da291dbb710d67cf216fa8de2.webp?size=1024&width=0&height=512"));
        when(event.getUser()).thenReturn(user); // This line is already present
        when(user.getName()).thenReturn("TestUser");
        Long mockDiscordId = 123456789L;
        when(event.getUser().getIdLong()).thenReturn(mockDiscordId);
        when(event.getJDA()).thenReturn(jda);
    }
}
