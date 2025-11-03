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
package io.github.yusufsdiscordbot.mystiguardian.oauth.http;

import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.OAuthUser;
import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.impl.OAuthUserImpl;
import io.github.yusufsdiscordbot.mystiguardian.oauth.response.TokensResponse;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.FormBody;
import org.jetbrains.annotations.NotNull;

/**
 * Client for interacting with Discord's REST API.
 *
 * <p>Handles OAuth2 token exchange, user information retrieval, and guild queries.
 */
@Slf4j
public class DiscordRestAPI {
    private static final String BASE_URI = "https://discord.com/api/v10";

    private final String clientId;
    private final String clientSecret;

    /**
     * Constructs a new DiscordRestAPI client.
     *
     * @param clientId the Discord application client ID
     * @param clientSecret the Discord application client secret
     */
    public DiscordRestAPI(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Exchanges an authorization code for OAuth2 tokens.
     *
     * @param code the authorization code from Discord OAuth flow
     * @param redirectUri the redirect URI used in the OAuth flow
     * @return the token response containing access and refresh tokens
     * @throws RuntimeException if the token exchange fails
     */
    public TokensResponse getToken(String code, String redirectUri) {
        try {
            val requestBody =
                    new FormBody.Builder()
                            .add("client_id", clientId)
                            .add("client_secret", clientSecret)
                            .add("grant_type", "authorization_code")
                            .add("code", code)
                            .add("redirect_uri", redirectUri)
                            .add("scope", "identify guilds")
                            .build();
            return getTokenResponse(requestBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Refreshes OAuth2 tokens using a refresh token.
     *
     * @param refreshToken the refresh token to exchange
     * @param redirectUri the redirect URI used in the OAuth flow
     * @return the new token response with refreshed tokens
     */
    public TokensResponse getNewToken(String refreshToken, String redirectUri) {
        val requestBody =
                new FormBody.Builder()
                        .add("client_id", clientId)
                        .add("client_secret", clientSecret)
                        .add("grant_type", "refresh_token")
                        .add("refresh_token", refreshToken)
                        .add("redirect_uri", redirectUri)
                        .add("scope", "identify guilds")
                        .build();

        return getTokenResponse(requestBody);
    }

    @NotNull
    private TokensResponse getTokenResponse(FormBody requestBody) {
        val request =
                new okhttp3.Request.Builder().url(BASE_URI + "/oauth2/token").post(requestBody).build();

        try (val response = MystiGuardianUtils.client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException(MystiGuardianUtils.handleAPIError(response));
            }

            // Read the response body once and store it
            String responseBodyString = response.body().string();

            // Parse the JSON response
            val json = MystiGuardianUtils.objectMapper.readTree(responseBodyString);
            return new TokensResponse(json);
        } catch (Exception e) {
            logger.error("Failed to get token", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the authenticated user's information.
     *
     * @param accessToken the OAuth2 access token
     * @return the user information
     * @throws RuntimeException if the request fails
     */
    public OAuthUser getUser(String accessToken) {
        val request =
                new okhttp3.Request.Builder()
                        .url(BASE_URI + "/users/@me")
                        .header("Authorization", "Bearer " + accessToken)
                        .build();

        try (val response = MystiGuardianUtils.client.newCall(request).execute()) {
            val json = MystiGuardianUtils.objectMapper.readTree(response.body().string());
            return new OAuthUserImpl(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the guilds (servers) the authenticated user is a member of.
     *
     * @param accessToken the OAuth2 access token
     * @return JSON string containing the user's guilds
     * @throws RuntimeException if the request fails
     */
    public String getGuilds(String accessToken) {
        val request =
                new okhttp3.Request.Builder()
                        .url(BASE_URI + "/users/@me/guilds")
                        .header("Authorization", "Bearer " + accessToken)
                        .build();

        try (val response = MystiGuardianUtils.client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
