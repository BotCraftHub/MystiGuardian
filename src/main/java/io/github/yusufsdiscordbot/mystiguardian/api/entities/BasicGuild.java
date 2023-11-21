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
package io.github.yusufsdiscordbot.mystiguardian.api.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import static io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI.objectMapper;

public class BasicGuild {
    private final JsonNode guild;

    public BasicGuild(JsonNode guild) {
        this.guild = guild;
    }

    public String getId() {
        return guild.get("id").asText();
    }

    public String getName() {
        return guild.get("name").asText();
    }

    public String getIcon() {
        return guild.get("icon").asText();
    }

    public String toJson() {
        val objectNode = objectMapper.createObjectNode();
        objectNode.put("id", getId());
        objectNode.put("name", getName());
        objectNode.put("icon", getIcon());
        return objectNode.toString();
    }
}
