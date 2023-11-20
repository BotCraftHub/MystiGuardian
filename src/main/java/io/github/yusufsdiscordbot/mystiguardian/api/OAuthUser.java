package io.github.yusufsdiscordbot.mystiguardian.api;

import io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI;
import lombok.Getter;
import org.javacord.api.entity.user.User;

@Getter
public class OAuthUser {
    private final User user;

    public OAuthUser(String accessToken, DiscordRestAPI discordRestAPI) throws IllegalArgumentException {
        discordRestAPI.setAccessToken(accessToken);
        this.user = discordRestAPI.getUser();
    }
}