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

import com.auth0.jwt.JWT;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.TokensResponse;
import io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import spark.Spark;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI.objectMapper;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

public class OAuthAPI {

    private static String clientId;
    private static String clientSecret;
    private static String redirectUri;
    private static OAuthUser authUser;
    private static DiscordRestAPI discordRestAPI;

    public static void handleAuth() {
        Spark.port(8080);

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
            String authorizationUrl = "https://discord.com/api/oauth2/authorize" +
                    "?client_id=" + clientId +
                    "&redirect_uri=" + redirectUri +
                    "&response_type=code" +
                    "&scope=identify%20guilds";

            res.redirect(authorizationUrl);
            return null;
        });

        Spark.post("/login", (req, res) -> {
            // Handle the callback after user authenticates
            String code = req.queryParams("code");

            if (code == null) {
                res.status(400);
                return "No code provided";
            }

            logger.info("Received request from " + req.ip() + " with code " + code);

            discordRestAPI = getDiscordRestAPI();

            long requestTime = System.currentTimeMillis();
            TokensResponse tokens = discordRestAPI.getTokens(code);
            String accessToken = tokens.getAccessToken();
            String refreshToken = tokens.getRefreshToken();
            long expiresAt = requestTime / 1000 + tokens.getExpiresIn();

            authUser = new OAuthUser(accessToken, discordRestAPI, refreshToken, expiresAt);

            /*
            val jwt = JWT.create()
                    .withExpiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 7))
                    .withIssuer("MystiGuardian")
                    .withSubject(authUser.getUser().getIdAsString())
                    .sign(MystiGuardianUtils.algorithm);
             */

            res.status(200);

            val body = objectMapper.createObjectNode();
            body.put("encryptedUserId", authUser.getEncryptedUserId());
            body.put("expiresAt", expiresAt);

            res.type("application/json");
            res.body(body.toString());

            return res;
        });
    }


    private static DiscordRestAPI getDiscordRestAPI() {
        return new DiscordRestAPI(null, clientId, clientSecret, redirectUri);
    }

    private void logUrl(HttpServletRequest request) {
        //we need get the current port and the current host
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();

        // Construct the URL
        String url = scheme + "://" + host;
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url += ":" + port;
        }

        logger.info("Current bot server url: " + url);
    }
}
