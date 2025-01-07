/*
 * Copyright 2024 RealYusufIsmail.
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

@Slf4j
public class DiscordRestAPI {
    private static final String BASE_URI = "https://discord.com/api/v10";

    private final String clientId;
    private final String clientSecret;

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
