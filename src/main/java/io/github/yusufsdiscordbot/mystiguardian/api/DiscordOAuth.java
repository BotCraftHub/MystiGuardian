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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import spark.Spark;

import java.io.IOException;

public class DiscordOAuth {
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void handleAuth() {
        Spark.port(8080);

        val discordSource = MystiGuardianUtils.jConfig.get("discord-auth");

        if (discordSource == null) {
            MystiGuardianUtils.discordAuthLogger.error("No discord source found in config");
            throw new RuntimeException("No discord source found in config");
        }

        Spark.get("/auth/discord", (req, res) -> {
            val clientId = discordSource.get("clientId").asText();
            val clientSecret = discordSource.get("clientSecret").asText();
            val redirectUri = discordSource.get("redirectUri").asText();

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

            val clientId = discordSource.get("clientId").asText();
            val clientSecret = discordSource.get("clientSecret").asText();
            val redirectUri = discordSource.get("redirectUri").asText();

            // Step 3: Exchange the code for an access token
            RequestBody formBody = new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("grant_type", "authorization_code")
                    .add("code", code)
                    .add("redirect_uri", redirectUri)
                    .build();

            Request request = new Request.Builder()
                    .url("https://discord.com/api/oauth2/token")
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    // Handle the error
                    return "Failed to get the session. Redirecting to login page.";
                }

                // Parse the response to obtain the access token
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                String accessToken = jsonNode.get("access_token").asText();

                // TODO: Handle the access token as needed

                MystiGuardianUtils.discordAuthLogger.info("Authentication successful!");



                return "Authentication successful!";
            } catch (IOException e) {
                MystiGuardianUtils.discordAuthLogger.error("Error during callback handling", e);
                return "Failed to get the session. Redirecting to login page.";
            }
        });
    }
}
