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
package io.github.yusufsdiscordbot.mystiguardian.oauth.entites.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.OAuthGuild;
import lombok.val;

public class OAuthGuildImpl implements OAuthGuild {
    private final JsonNode json;

    private final Long id;
    private final String name;
    private final String icon;
    private final Long permissions;
    private final boolean botInGuild;

    public OAuthGuildImpl(JsonNode json) {
        this.json = json;
        this.id = json.get("id").asLong();
        this.name = json.get("name").asText();
        this.icon = json.has("icon") ? json.get("icon").asText() : null;
        this.permissions = json.get("permissions").asLong();

        val mutualGuilds = MystiGuardian.getMystiGuardian().getApi().getSelfUser().getMutualGuilds();
        this.botInGuild = mutualGuilds.stream().anyMatch(guild -> guild.getIdLong() == id);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public Long getPermissions() {
        return permissions;
    }

    @Override
    public boolean isBotInGuild() {
        return botInGuild;
    }

    @Override
    public JsonNode getJson() {
        val objectNode = (com.fasterxml.jackson.databind.node.ObjectNode) json;
        objectNode.put("bot_in_guild", botInGuild);
        return objectNode;
    }
}
