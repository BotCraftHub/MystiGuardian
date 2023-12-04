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

public interface OAuthGuild {

    /**
     * Gets the guild's id.
     *
     * @return The guild's id.
     */
    Long getId();

    /**
     * Gets the guild's id as a string.
     *
     * @return The guild's id as a string.
     */
    default String getIdAsString() {
        return String.valueOf(getId());
    }

    /**
     * Gets the guild's name.
     *
     * @return The guild's name.
     */
    String getName();

    /**
     * Gets the guild's icon hash.
     *
     * @return The guild's icon hash.
     */
    String getIcon();

    /**
     * Gets the permissions the user has in the guild.
     *
     * @return The permissions the user has in the guild.
     */
    Long getPermissions();

    /**
     * Whether the bot is in this guild.
     *
     * @return Whether the bot is in this guild.
     */
    boolean isBotInGuild();

    /**
     * Gets the guild json.
     *
     * @return The guild json.
     */
    JsonNode getJson();
}
