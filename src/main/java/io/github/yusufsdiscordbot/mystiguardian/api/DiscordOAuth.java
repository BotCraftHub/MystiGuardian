package io.github.yusufsdiscordbot.mystiguardian.api;

import io.github.yusufsdiscordbot.mystiguardian.api.endpoints.Endpoints;
import io.github.yusufsdiscordbot.mystiguardian.api.reuqestis.GetRequests;
import io.github.yusufsdiscordbot.mystiguardian.api.reuqestis.PostRequests;

public interface DiscordOAuth {

    /**
     * Listens to GET requests.
     *
     * @param endpoint The endpoint to listen to.
     * @return The [GetRequests] instance.
     */
    GetRequests listentGetRequests(Endpoints endpoint);

    /**
     * Listens to POST requests.
     *
     * @param endpoint The endpoint to listen to.
     * @return The [PostRequests] instance.
     */
    PostRequests listenPostRequests(Endpoints endpoint);
}
