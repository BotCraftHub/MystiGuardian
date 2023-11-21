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
package io.github.yusufsdiscordbot.mystiguardian.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.BasicGuild;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.MystiUserImpl;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.TokensResponse;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.io.Serializable;

import lombok.val;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

public class DiscordRestAPI implements Serializable {
    private String accessToken = null;

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String[] scope = {"identify", "guilds"};
    public static final String BASE_URI = "https://discord.com/api/v10";
    private static final OkHttpClient client = new OkHttpClient();
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public DiscordRestAPI(@Nullable String accessToken, String clientId, String clientSecret, String redirectUri) {
        this.accessToken = accessToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

        if (accessToken != null) {
            client.newBuilder().addInterceptor(chain -> {
                Headers headers = getHeaders();
                return chain.proceed(
                        chain.request().newBuilder().headers(headers).build());
            });
        }
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public MystiUserImpl getUser() {
        assert accessToken != null;
        // get the user

        val request = new okhttp3.Request.Builder()
                .headers(getHeaders())
                .url(BASE_URI + "/users/@me")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            val responseBody = response.body();

            val body = objectMapper.readTree(responseBody.string());

            return new MystiUserImpl(body);
        } catch (IOException e) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to get user", e);
            return null;
        }
    }

    public TokensResponse getTokens(String code) throws IOException {
        val requestBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .add("scope", String.join(" ", scope))
                .build();

        val request = new okhttp3.Request.Builder()
                .url(BASE_URI + "/oauth2/token")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            val responseBody = response.body();

            val body = objectMapper.readTree(responseBody.string());

            return new TokensResponse(body);
        } catch (IOException e) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to get tokens", e);
            return null;
        }
    }

    private Headers getHeaders() {
        return new Headers.Builder()
                .add("Content-Type", "application/json")
                .add("Authorization", "Bearer " + accessToken)
                .add("User-Agent", "DiscordBot (" + "https://github.com/BotCraftHub/MystiGuardian" + ", 1.0)")
                .add("accept", "application/json")
                .build();
    }

    public BasicGuild getGuilds() {
        assert accessToken != null;
        // get the user

        val request = new okhttp3.Request.Builder()
                .headers(getHeaders())
                .url(BASE_URI + "/users/@me/guilds")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            val responseBody = response.body();

            val body = objectMapper.readTree(responseBody.string());

            return new BasicGuild(body);
        } catch (IOException e) {
            MystiGuardianUtils.discordAuthLogger.error("Failed to get guilds", e);
            return null;
        }
    }
}
