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
package io.github.yusufsdiscordbot.mystiguardian.requests;

import static io.github.yusufsdiscordbot.mystiguardian.utils.EntityManager.getGuildsThatUserCanManage;

import io.github.yusufsdiscordbot.mystiguardian.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.endpoints.GetEndpoints;
import io.github.yusufsdiscordbot.mystiguardian.entites.OAuthJWt;
import io.github.yusufsdiscordbot.mystiguardian.utils.CorsFilter;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import spark.Spark;

public class GetRequestsHandler {
    private static final String JWT_PREFIX = "jwt ";

    public GetRequestsHandler() {
        // needed for cors
        Spark.before(CorsFilter::applyCorsHeaders);
        handleGetBotGuildsRequest();
        ping();
    }

    private void handleGetBotGuildsRequest() {
        Spark.get(GetEndpoints.GET_GUILDS.getEndpoint(), (request, response) -> {
            val jwt = request.headers("Authorization");

            if (jwt == null || !jwt.startsWith(JWT_PREFIX)) {
                response.status(401);
                MystiGuardianUtils.discordAuthLogger.info("JWT not found");
                return "JWT not found";
            }

            OAuthJWt decodedJWT = OAuth.getAuthUtils().validateJwt(jwt.substring(JWT_PREFIX.length()));

            val userId = decodedJWT.getUserId();
            val id = decodedJWT.getDatabaseId();

            val accessToken = MystiGuardianDatabaseHandler.OAuth.getAccessToken(id, String.valueOf(userId));

            if (accessToken == null) {
                response.status(408);
                MystiGuardianUtils.discordAuthLogger.info("Access token not found");
                return "Access token not found";
            }

            val guilds = OAuth.getDiscordRestAPI().getGuilds(accessToken);

            if (guilds == null) {
                response.status(409);
                MystiGuardianUtils.discordAuthLogger.error("Failed to get guilds");
                return "Failed to get guilds";
            }

            val guildsThatUserCanManage = getGuildsThatUserCanManage(guilds);

            response.type("application/json");
            response.status(200);

            return guildsThatUserCanManage;
        });
    }

    private void ping() {
        Spark.get(GetEndpoints.PING.getEndpoint(), (request, response) -> {
            response.status(200);
            return "Pong!";
        });
    }
}
