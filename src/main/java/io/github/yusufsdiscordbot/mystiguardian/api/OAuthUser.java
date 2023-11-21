package io.github.yusufsdiscordbot.mystiguardian.api;

import io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import lombok.Getter;
import org.javacord.api.entity.user.User;

import static io.github.yusufsdiscordbot.mystiguardian.api.util.SecurityUtils.encryptUserId;

@Getter
public class OAuthUser {
    private final User user;
    private final String encryptedUserId;

    public OAuthUser(String accessToken, DiscordRestAPI discordRestAPI, String refreshToken, long expiresAt) throws Exception {
        discordRestAPI.setAccessToken(accessToken);
        this.user = discordRestAPI.getUser();

        encryptedUserId = encryptUserId(user.getIdAsString());

        MystiGuardianDatabaseHandler.AuthHandler.setAuthRecord(encryptedUserId, accessToken, refreshToken, expiresAt);
    }
}