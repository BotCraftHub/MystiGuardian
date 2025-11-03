/*
 * Copyright 2025 RealYusufIsmail.
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
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import spark.Spark;

/**
 * Handles GET requests for database-related operations via the OAuth web service.
 *
 * <p>This handler manages database queries for:
 *
 * <ul>
 *   <li>Audit channel configuration
 *   <li>Guild-specific settings
 * </ul>
 *
 * <p>All requests require valid JWT authentication.
 */
public class DatabaseGetRequests {

    /** Constructs a new DatabaseGetRequests handler and initializes routes. */
    public DatabaseGetRequests() {
        getAuditChannel();
    }

    /** Registers the endpoint for retrieving audit channel information for a guild. */
    public void getAuditChannel() {
        Spark.get(
                GetEndpoints.GET_AUDIT_CHANNEL.getEndpoint(),
                (request, response) -> {
                    val decodedJWT =
                            OAuth.getAuthUtils()
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

                    val channel = MystiGuardian.getMystiGuardian().getJda().getTextChannelById(channelId);

                    if (channel == null) {
                        response.status(404);
                        return "Channel not found or is not a valid text channel";
                    }

                    val jsonBuilder = MystiGuardianUtils.objectMapper.createObjectNode();

                    jsonBuilder.put("id", channelId);
                    jsonBuilder.put("name", channel.getName());
                    jsonBuilder.put("type", channel.getType().getId());

                    response.status(200);
                    response.type("application/json");

                    return jsonBuilder.toString();
                });
    }
}
