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
package io.github.yusufsdiscordbot.mystiguardian;

import io.github.realyusufismail.jconfig.classes.JConfigException;
import io.github.yusufsdiscordbot.mystiguardian.http.DiscordRestAPI;
import io.github.yusufsdiscordbot.mystiguardian.requests.MainRequestsHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.AuthUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import lombok.Getter;
import lombok.val;
import spark.Spark;

public class OAuth {

    @Getter
    private static AuthUtils authUtils;

    @Getter
    private static String clientId;

    @Getter
    private static String clientSecret;

    @Getter
    private static String redirectUri;

    @Getter
    private static DiscordRestAPI discordRestAPI;

    public static void runOAuth() throws IOException {
        authUtils = new AuthUtils();

        val discordSource = MystiGuardianUtils.jConfig.get("discord-auth");

        if (discordSource == null) {
            throw new JConfigException("Missing discord auth config");
        }

        val preClientId = discordSource.get("clientId");
        val preClientSecret = discordSource.get("clientSecret");
        val preRedirectUri = discordSource.get("redirectUri");

        if (preClientId == null || preClientSecret == null || preRedirectUri == null) {
            throw new JConfigException("Missing discord auth config");
        }

        clientId = preClientId.asText();
        clientSecret = preClientSecret.asText();
        redirectUri = preRedirectUri.asText();

        discordRestAPI = new DiscordRestAPI(clientId, clientSecret, redirectUri);

        Spark.port(8080);

        try {
            new MainRequestsHandler();
        } catch (Exception e) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to register requests", e);
        }
    }
}
