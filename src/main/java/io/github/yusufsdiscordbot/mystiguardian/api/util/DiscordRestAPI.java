package io.github.yusufsdiscordbot.mystiguardian.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.MystiUserImpl;
import io.github.yusufsdiscordbot.mystiguardian.api.entities.TokensResponse;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import okhttp3.*;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

public class DiscordRestAPI {
    private String accessToken = null;

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String[] scope = {"identify", "guilds"};
    public static final String BASE_URI = "https://discord.com/api/v10";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public DiscordRestAPI(@Nullable String accessToken, String clientId, String clientSecret, String redirectUri) {
        this.accessToken = accessToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

       if (accessToken != null) {
           client.newBuilder().addInterceptor(chain -> {
               Headers headers = getHeaders();
               return chain.proceed(chain.request().newBuilder().headers(headers).build());
           });
       }
    }

    public DiscordRestAPI setAccessToken(String accessToken) {
        this.accessToken = accessToken;

        client.newBuilder().addInterceptor(chain -> {
            Headers headers = getHeaders();
            return chain.proceed(chain.request().newBuilder().headers(headers).build());
        });

        return this;
    }

    public User getUser() {
        assert accessToken != null;
        // get the user

        val request = new okhttp3.Request.Builder()
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

    public TokensResponse getTokens(String code) throws IOException
    {
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
}
