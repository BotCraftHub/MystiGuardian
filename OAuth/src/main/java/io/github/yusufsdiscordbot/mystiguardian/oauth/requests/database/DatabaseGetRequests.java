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
package io.github.yusufsdiscordbot.mystiguardian.oauth.requests.database;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.oauth.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.oauth.endpoints.GetEndpoints;
import io.github.yusufsdiscordbot.mystiguardian.oauth.http.DiscordRestAPI;
import lombok.val;
import spark.Spark;

public class DatabaseGetRequests {

    public DatabaseGetRequests() {
        getAuditChannel();
    }

    public void getAuditChannel() {
        Spark.get(GetEndpoints.GET_AUDIT_CHANNEL.getEndpoint(), (request, response) -> {
            val decodedJWT = OAuth.getAuthUtils()
                    .validateJwt(request.headers("Authorization"), response)
                    .orElse(null);

            if (decodedJWT == null) {
                return "JWT not found";
            }

            val guildId = request.queryParams("guildId");

            if (guildId == null) {
                response.status(400);
                return "Guild ID not found";
            }

            val channelId = MystiGuardianDatabaseHandler.AuditChannel.getAuditChannelRecord(guildId);

            if (channelId == null) {
                response.status(200);
                response.type("application/json");
                return "{}";
            }

            val channel = MystiGuardian.getMystiGuardian().getApi().getChannelById(channelId);

            if (channel.isEmpty()) {
                response.status(404);
                return "Channel not found";
            }

            val textChannel = channel.get().asServerTextChannel().orElse(null);

            if (textChannel == null) {
                response.status(404);
                return "Channel could not be casted to a text channel";
            }

            val jsonBuilder = DiscordRestAPI.objectMapper.createObjectNode();

            jsonBuilder.put("id", channelId);
            jsonBuilder.put("name", textChannel.getName());
            jsonBuilder.put("type", textChannel.getType().getId());

            response.status(200);
            response.type("application/json");

            return jsonBuilder.toString();
        });
    }
}
