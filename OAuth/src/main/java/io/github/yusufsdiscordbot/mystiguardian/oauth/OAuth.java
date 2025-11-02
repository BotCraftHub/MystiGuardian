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
package io.github.yusufsdiscordbot.mystiguardian.oauth;

import io.github.yusufsdiscordbot.mystiguardian.oauth.http.DiscordRestAPI;
import io.github.yusufsdiscordbot.mystiguardian.oauth.requests.MainRequestsHandler;
import io.github.yusufsdiscordbot.mystiguardian.oauth.utils.JWTUtils;
import io.github.yusufsdiscordbot.mystiguardian.oauth.utils.PortUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import spark.Spark;

/**
 * Main OAuth service for Discord authentication and web API.
 *
 * <p>This class initializes and runs the OAuth2 web service that provides:
 *
 * <ul>
 *   <li>Discord OAuth2 authentication flow
 *   <li>JWT token generation and validation
 *   <li>REST API endpoints for web dashboard integration
 *   <li>User and guild information retrieval
 * </ul>
 *
 * <p>The service runs on Spark Java framework and automatically finds an available port in the
 * range 25590-25600. It handles:
 *
 * <ul>
 *   <li>OAuth callback processing
 *   <li>Token exchange with Discord
 *   <li>Secure JWT generation for session management
 *   <li>CORS configuration for web dashboard
 * </ul>
 *
 * <p><b>Configuration:</b> OAuth credentials are loaded from config.json under the "discordAuth"
 * section:
 *
 * <pre>
 * {
 *   "discordAuth": {
 *     "clientId": "your-client-id",
 *     "clientSecret": "your-client-secret"
 *   }
 * }
 * </pre>
 *
 * <p><b>Security:</b> JWT tokens are used for securing API endpoints. The secret is automatically
 * generated or loaded from configuration.
 *
 * @see JWTUtils
 * @see DiscordRestAPI
 * @see MainRequestsHandler
 */
@Slf4j
public class OAuth {

    /** JWT utility for token generation and validation. */
    @Getter private static JWTUtils authUtils;

    /** Discord application client ID from config. */
    @Getter private static String clientId;

    /** Discord application client secret from config. */
    @Getter private static String clientSecret;

    /** Discord REST API client for OAuth token exchange and user data retrieval. */
    @Getter private static DiscordRestAPI discordRestAPI;

    /**
     * Initializes and starts the OAuth web service.
     *
     * <p>This method:
     *
     * <ul>
     *   <li>Initializes JWT utilities
     *   <li>Loads OAuth credentials from configuration
     *   <li>Creates Discord REST API client
     *   <li>Finds an available port (25590-25600)
     *   <li>Starts Spark web server
     *   <li>Registers all HTTP request handlers
     * </ul>
     *
     * <p>The service runs on a separate thread and continues running until the bot is shut down.
     *
     * @throws IOException if configuration cannot be loaded or port cannot be found
     */
    public static void runOAuth() throws IOException {
        authUtils = new JWTUtils();

        clientId = MystiGuardianUtils.getDiscordAuthConfig().clientId();
        clientSecret = MystiGuardianUtils.getDiscordAuthConfig().clientSecret();

        discordRestAPI = new DiscordRestAPI(clientId, clientSecret);

        int port = PortUtils.findOpenPort(25590, 25600);
        Spark.port(port);

        try {
            new MainRequestsHandler();
        } catch (Exception e) {
            logger.error("Failed to register requests", e);
        }
    }
}
