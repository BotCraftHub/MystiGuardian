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
package io.github.yusufsdiscordbot.mystiguardian.api;

import static io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI.objectMapper;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.TokensResponse;
import io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI;
import io.github.yusufsdiscordbot.mystiguardian.api.util.SecurityUtils;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import spark.Spark;

public class OAuthAPI {

    private static String clientId;
    private static String clientSecret;
    private static String redirectUri;
    private static DiscordRestAPI discordRestAPI;

    public static void handleAuth() {
        Spark.port(8080);

        handleAuthRequest();
        handleLoginRequest();
        handelGetGuilds();
    }

    private static DiscordRestAPI getDiscordRestAPI() {
        return new DiscordRestAPI(null, clientId, clientSecret, redirectUri);
    }

    private static void handleAuthRequest() {
        val discordSource = MystiGuardianUtils.jConfig.get("discord-auth");

        if (discordSource == null) {
            MystiGuardianUtils.discordAuthLogger.error("No discord source found in config");
            throw new RuntimeException("No discord source found in config");
        }

        logger.info("Successfully loaded discord source from config");

        clientId = discordSource.get("clientId").asText();
        clientSecret = discordSource.get("clientSecret").asText();
        redirectUri = discordSource.get("redirectUri").asText();

        Spark.get("/auth/discord", (req, res) -> {
            // Construct the Discord OAuth2 Authorization URL
            String authorizationUrl = "https://discord.com/api/oauth2/authorize" + "?client_id="
                    + clientId + "&redirect_uri="
                    + redirectUri + "&response_type=code"
                    + "&scope=identify%20guilds";

            res.redirect(authorizationUrl);
            return null;
        });
    }

    private static void handleLoginRequest() {
        Spark.post("/login", (req, res) -> {
            // Handle the callback after the user authenticates
            String code = req.queryParams("code");

            if (code == null) {
                res.status(400);
                return "No code provided";
            }

            logger.info("Received request from " + req.ip() + " with code " + code);

            try {
                discordRestAPI = getDiscordRestAPI();
            } catch (Exception e) {
                MystiGuardianUtils.discordAuthLogger.error("Failed to get discord rest api", e);
                res.status(500);
                return "Failed to get discord rest api";
            }

            long requestTime = System.currentTimeMillis();
            TokensResponse tokens = discordRestAPI.getTokens(code);
            String accessToken = tokens.getAccessToken();
            String refreshToken = tokens.getRefreshToken();
            long expiresAt = requestTime / 1000 + tokens.getExpiresIn();

            val authUser = new OAuthUser(accessToken, discordRestAPI, refreshToken, expiresAt);

            ObjectNode responseBody = objectMapper.createObjectNode();
            responseBody.put("encryptedUserId", authUser.getEncryptedUserId());
            responseBody.put("expiresAt", expiresAt);

            // Set headers and status
            res.type("application/json");
            res.header("Access-Control-Allow-Origin", "*"); // Allow cross-origin requests
            res.status(200);

            // Convert JSON object to string and return
            return responseBody.toString();
        });
    }

    private static void handelGetGuilds() {
        Spark.get("/guilds", (req, res) -> {
            val encryptedUserId = req.headers("encryptedUserId");

            val authUser = getAuthUser(encryptedUserId);

            DiscordRestAPI discordApi = authUser.getDiscordRestAPI();

            if (discordApi == null) {
                res.status(500);
                return "Discord rest api is null";
            }

            try {
                return discordRestAPI.getGuilds().toJson();
            } catch (Exception e) {
                MystiGuardianUtils.discordAuthLogger.error("Failed to get guilds", e);
                res.status(500);
                return "Failed to get guilds";
            }
        });
    }

    private static OAuthUser getAuthUser(String encryptedUserId) throws Exception {
        val decipherUserId = SecurityUtils.decipherUserId(encryptedUserId);

        if (MystiGuardianUtils.isLong(decipherUserId)) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to decipher user id");
            throw new RuntimeException("Failed to decipher user id");
        }

        val databaseUser = MystiGuardianDatabaseHandler.AuthHandler.getAuthRecord(Long.parseLong(decipherUserId));

        if (databaseUser == null) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to get database user");
            throw new RuntimeException("Failed to get database user");
        }

        return databaseUser;
    }
}
