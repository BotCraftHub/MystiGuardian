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

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.endpoints.GetEndpoints;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import spark.Spark;

import static spark.Spark.options;

public class GetRequestsHandler {
    private static final String JWT_PREFIX = "jwt ";

    public GetRequestsHandler() {
        handleGetBotGuildsRequest();
        ping();
    }

    private void handleGetBotGuildsRequest() {

        Spark.options("/guilds", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            return "";
        });

        Spark.get("/guilds", (request, response) -> {
            val jwt = request.headers("Bearer");

            MystiGuardianUtils.discordAuthLogger.info("JWT: " + request.headers("Bearer"));

            val decodedJWT = OAuth.getAuthUtils().validateJwt(jwt.substring(JWT_PREFIX.length()));

            if (decodedJWT == null) {
                response.status(401);
                return "Invalid JWT provided";
            }

            val userId = decodedJWT.getClaim("user_id").asString();
            val id = decodedJWT.getClaim("id").asLong();

            val accessToken = MystiGuardianDatabaseHandler.OAuth.getAccessToken(id, userId);

            if (accessToken == null) {
                response.status(401);
                return "Access token not found";
            }

            val guilds = OAuth.getDiscordRestAPI().getGuilds(accessToken);

            if (guilds == null) {
                response.status(401);
                return "Failed to get guilds";
            }

            MystiGuardianUtils.discordAuthLogger.info("Guilds: " + guilds);

            response.type("application/json");
            response.status(200);

            return guilds;
        });
    }


    private void ping() {
        Spark.get("/ping", (request, response) -> {
            response.status(200);
            return "Pong!";
        });
    }
}
