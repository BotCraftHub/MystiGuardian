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
package io.github.yusufsdiscordbot.mystiguardian.entites;

import com.fasterxml.jackson.databind.JsonNode;

public class OAuthGuildImpl implements OAuthGuild {
    private final JsonNode json;

    private final Long id;
    private final String name;
    private final String icon;
    private final Long permissions;

    public OAuthGuildImpl(JsonNode json) {
        this.json = json;
        this.id = json.get("id").asLong();
        this.name = json.get("name").asText();
        this.icon = json.has("icon") ? json.get("icon").asText() : null;
        this.permissions = json.get("permissions").asLong();
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
    public JsonNode getJson() {
        return json;
    }
}
