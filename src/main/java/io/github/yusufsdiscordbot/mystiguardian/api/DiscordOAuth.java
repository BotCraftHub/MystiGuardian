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

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import okhttp3.OkHttpClient;
import spark.Spark;

public class DiscordOAuth {
    private static final OkHttpClient client = new OkHttpClient();

    public static void handleAuth() {
        Spark.port(8080);

        val discordSource = MystiGuardianUtils.jConfig.get("discord-auth");

        if (discordSource == null) {
            MystiGuardianUtils.discordAuthLogger.error("No discord source found in config");
            throw new RuntimeException("No discord source found in config");
        }

        Spark.get("/auth/discord", (req, res) -> {
            val clientId = discordSource.get("clientId").asText();
            val redirectUri = discordSource.get("redirectUri").asText();

            // res.redirect(authUrl);

            return "https://discord.com/api/oauth2/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri
                    + "&response_type=code&scope=identify%20guilds";
        });

        Spark.get("/callback", (req, res) -> {
            return "ok";
        });
    }
}
