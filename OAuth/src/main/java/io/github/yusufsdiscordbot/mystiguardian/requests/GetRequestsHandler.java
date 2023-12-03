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

import com.auth0.jwt.interfaces.DecodedJWT;
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
        Spark.get("/guilds", (request, response) -> {
            request.headers().forEach((value) -> MystiGuardianUtils.discordAuthLogger.info("Header: " + value));

            val jwt = request.headers("Authorization");

            val cookieHeader = request.headers("Cookie");

            String jwtFromCookie = null;
            if (jwt == null || !jwt.startsWith(JWT_PREFIX)) {
                if (cookieHeader != null) {
                    MystiGuardianUtils.discordAuthLogger.info("Cookie header: " + cookieHeader);
                    val cookies = cookieHeader.split(";");
                    for (String cookie : cookies) {
                        MystiGuardianUtils.discordAuthLogger.info("Cookie: " + cookie);
                        if (cookie.contains("jwt=")) {
                            jwtFromCookie = cookie.substring(cookie.indexOf("jwt=") + 4);
                            MystiGuardianUtils.discordAuthLogger.info("JWT from cookie: " + jwtFromCookie);
                            break;
                        }
                    }
                }
            }

            DecodedJWT decodedJWT = null;

            boolean isJwtFromCookie = false;
            boolean isJwtFromHeader = false;

            if (jwtFromCookie != null) {
                decodedJWT = OAuth.getAuthUtils().validateJwt(jwtFromCookie);
                isJwtFromCookie = true;

                MystiGuardianUtils.discordAuthLogger.info("JWT from cookie: " + jwtFromCookie);
            } else if (jwt != null) {
                decodedJWT = OAuth.getAuthUtils().validateJwt(jwt.substring(JWT_PREFIX.length()));
                isJwtFromHeader = true;

                MystiGuardianUtils.discordAuthLogger.info("JWT from header: " + jwt);
            }


            if (!isJwtFromCookie && !isJwtFromHeader) {
                response.status(401);
                MystiGuardianUtils.discordAuthLogger.info("JWT not found");
                return "JWT not found";
            }

            // TODO : Add method to get these values from the JWT
            val userId = decodedJWT.getClaim("userId").asLong();
            val id = decodedJWT.getClaim("id").asLong();

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
