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
package io.github.yusufsdiscordbot.mystiguardian.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.entites.OAuthUser;
import io.github.yusufsdiscordbot.mystiguardian.entites.impl.OAuthUserImpl;
import io.github.yusufsdiscordbot.mystiguardian.response.TokensResponse;
import lombok.val;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

public class DiscordRestAPI {
    private static final String BASE_URI = "https://discord.com/api/v10";
    private static final OkHttpClient client = new OkHttpClient();
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public DiscordRestAPI(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    public TokensResponse getToken(String code) {
        val requestBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .add("scope", "identify guilds")
                .build();

        return getTokenResponse(requestBody);
    }

    public TokensResponse getNewToken(String refreshToken) {
        val requestBody = new FormBody.Builder()
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
        val request = new okhttp3.Request.Builder()
                .url(BASE_URI + "/oauth2/token")
                .post(requestBody)
                .build();

        try (val response = client.newCall(request).execute()) {
            val json = objectMapper.readTree(response.body().string());
            return new TokensResponse(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public OAuthUser getUser(String accessToken) {
        val request = new okhttp3.Request.Builder()
                .url(BASE_URI + "/users/@me")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (val response = client.newCall(request).execute()) {
            val json = objectMapper.readTree(response.body().string());
            return new OAuthUserImpl(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getGuilds(String accessToken) {
        val request = new okhttp3.Request.Builder()
                .url(BASE_URI + "/users/@me/guilds")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (val response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
