package io.github.yusufsdiscordbot.mystiguardian.api.impl;

import io.github.yusufsdiscordbot.mystiguardian.api.DiscordOAuth;
import io.github.yusufsdiscordbot.mystiguardian.api.endpoints.Endpoints;
import io.github.yusufsdiscordbot.mystiguardian.api.reuqestis.GetRequests;
import io.github.yusufsdiscordbot.mystiguardian.api.reuqestis.PostRequests;
import spark.Spark;

public class DiscordOAuthImpl implements DiscordOAuth {
    private final Spark spark;

    public DiscordOAuthImpl(Spark spark) {
        this.spark = spark;
    }

    @Override
    public GetRequests listentGetRequests(Endpoints endpoint) {
        return new GetRequestsImpl(spark, endpoint.getEndpoint());
    }

    @Override
    public PostRequests listenPostRequests(Endpoints endpoint) {
        return null;
    }
}
