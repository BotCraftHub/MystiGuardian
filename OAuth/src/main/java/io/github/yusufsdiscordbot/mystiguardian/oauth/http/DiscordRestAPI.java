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
 * Client for interacting with the Discord REST API via OAuth2.
 *
 * <p>This class handles OAuth2 token exchanges and provides methods to:
 * <ul>
 *   <li>Exchange authorization codes for access tokens
 *   <li>Refresh expired access tokens
 *   <li>Retrieve authenticated user information
 *   <li>Fetch user's guild list
 * </ul>
 */
@Slf4j
    /**
     * Exchanges an authorization code for access and refresh tokens.
     *
     * @param code the authorization code from Discord OAuth2 flow
     * @param redirectUri the redirect URI used in the OAuth2 flow
     * @return a TokensResponse containing access token, refresh token, and related data
     */
public class DiscordRestAPI {
    private static final String BASE_URI = "https://discord.com/api/v10";

    private final String clientId;
    private final String clientSecret;

    /**
     * Constructs a new DiscordRestAPI client.
     *
     * @param clientId the Discord application client ID
    /**
     * Refreshes an expired access token using a refresh token.
     *
     * @param refreshToken the refresh token obtained from a previous token exchange
     * @param redirectUri the redirect URI used in the OAuth2 flow
     * @return a new TokensResponse with a fresh access token
     */
     * @param clientSecret the Discord application client secret
     */
    public DiscordRestAPI(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

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

    public TokensResponse getNewToken(String refreshToken, String redirectUri) {
        val requestBody =
                new FormBody.Builder()
                        .add("client_id", clientId)
                        .add("client_secret", clientSecret)
    /**
     * Retrieves the authenticated user's information from Discord.
     *
     * @param accessToken the valid OAuth2 access token
     * @return an OAuthUser object containing user information
     */
                        .add("grant_type", "refresh_token")
                        .add("refresh_token", refreshToken)
                        .add("redirect_uri", redirectUri)
                        .add("scope", "identify guilds")
                        .build();

        return getTokenResponse(requestBody);
    }

    /**
     * Retrieves the list of guilds the authenticated user is a member of.
     *
     * @param accessToken the valid OAuth2 access token
     * @return a JSON string containing the user's guilds
     */
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
