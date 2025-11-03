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
package io.github.yusufsdiscordbot.mystiguardian.oauth.requests;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.oauth.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.oauth.endpoints.PutEndpoints;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import spark.Spark;

/**
 * Handles PUT requests for the OAuth web service.
 *
 * <p>This handler manages update operations for configuration settings like audit channels.
 */
@Slf4j
public class PutRequestsHandler {

    /** Constructs a new PutRequestsHandler and initializes all PUT endpoints. */
    public PutRequestsHandler() {
        handlePutAuditChannelRequest();
    }

    private void handlePutAuditChannelRequest() {
        Spark.put(
                PutEndpoints.AUDIT_CHANNEL.getEndpoint(),
                (request, response) -> {
                    val jwt = request.headers("Authorization");

                    val decodedJWT = OAuth.getAuthUtils().validateJwt(jwt, response).orElse(null);

                    if (decodedJWT == null) {
                        return "AUTHORIZATION NOT FOUND";
                    }

                    val guildId = request.queryParams("guildId");
                    val channelId = request.queryParams("channelId");

                    if (guildId == null || channelId == null) {
                        response.status(400);
                        logger.error("Guild ID or channel ID is null");
                        return "Guild ID or channel ID is null";
                    }

                    MystiGuardianDatabaseHandler.AuditChannel.updateAuditChannelRecord(guildId, channelId);

                    response.status(200);
                    response.type("application/json");

                    val jsonBuilder = MystiGuardianUtils.objectMapper.createObjectNode();

                    jsonBuilder.put("id", channelId);
                    jsonBuilder.put("guildId", guildId);

                    return jsonBuilder.toString();
                });
    }
}
