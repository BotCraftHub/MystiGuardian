package io.github.yusufsdiscordbot.mystiguardian.api.endpoints;

import lombok.Getter;
import lombok.val;

public enum Endpoints {
    LOGIN("/login"),
    GET_GUILDS("/guilds"),
    GET_USER("/users/@me"),
    GET_GUILDS_THAT_BOT_IS_IN("/bot/me/guilds");


    @Getter
    private final String endpoint;

    Endpoints(String endpoint) {
        this.endpoint = endpoint;
    }
}
