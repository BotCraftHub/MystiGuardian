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
package io.github.yusufsdiscordbot.mystiguardian.oauth.requests;

import static io.github.yusufsdiscordbot.mystiguardian.oauth.utils.EntityManager.getGuildsThatUserCanManage;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.oauth.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.oauth.endpoints.GetEndpoints;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import spark.Spark;

/**
 * Handles GET requests for the OAuth web service.
 *
 * <p>This handler manages various GET endpoints including:
 *
 * <ul>
 *   <li>Guild retrieval
 *   <li>Channel listing
 *   <li>Health check (ping)
 * </ul>
 */
@Slf4j
public class GetRequestsHandler {

    /** Constructs a new GetRequestsHandler and initializes all GET endpoints. */
    public GetRequestsHandler() {
        handleGetBotGuildsRequest();
        ping();
        getChannels();
    }

    private void handleGetBotGuildsRequest() {
        Spark.get(
                GetEndpoints.GET_GUILDS.getEndpoint(),
                (request, response) -> {
                    val jwt = request.headers("Authorization");

                    val decodedJWT = OAuth.getAuthUtils().validateJwt(jwt, response).orElse(null);

                    if (decodedJWT == null) {
                        return "JWT not found";
                    }

                    val userId = decodedJWT.getUserId();
                    val id = decodedJWT.getDatabaseId();

                    val accessToken =
                            MystiGuardianDatabaseHandler.OAuth.getAccessToken(id, String.valueOf(userId));

                    if (accessToken == null) {
                        response.status(408);
                        logger.info("Access token not found");
                        return "Access token not found";
                    }

                    val guilds = OAuth.getDiscordRestAPI().getGuilds(accessToken);

                    if (guilds == null) {
                        response.status(409);
                        logger.error("Failed to get guilds");
                        return "Failed to get guilds";
                    }

                    val guildsThatUserCanManage = getGuildsThatUserCanManage(guilds);

                    response.type("application/json");
                    response.status(200);

                    return guildsThatUserCanManage;
                });
    }

    private void ping() {
        Spark.get(
                GetEndpoints.PING.getEndpoint(),
                (request, response) -> {
                    response.status(200);
                    return "Pong!";
                });
    }

    private void getChannels() {
        Spark.get(
                GetEndpoints.GET_CHANNELS.getEndpoint(),
                (request, response) -> {
                    val decodedJWT =
                            OAuth.getAuthUtils()
                                    .validateJwt(request.headers("Authorization"), response)
                                    .orElse(null);

                    if (decodedJWT == null) {
                        return "JWT not found";
                    }

                    val guildId = request.queryParams("guildId");

                    if (guildId == null) {
                        response.status(400);
                        return "Guild ID not found";
                    }

                    val guild = MystiGuardian.getMystiGuardian().getJda().getGuildById(guildId);

                    if (guild == null) {
                        response.status(404);
                        return "Guild not found";
                    }

                    val channels = guild.getChannelCache().asList();

                    if (channels.isEmpty()) {
                        return "No channels found";
                    }

                    val json = MystiGuardianUtils.objectMapper.createArrayNode();

                    val textChannels =
                            channels.stream().filter(channel -> channel.getType() == ChannelType.TEXT).toList();

                    channels.forEach(
                            channel -> {
                                val object = MystiGuardianUtils.objectMapper.createObjectNode();

                                object.put("id", channel.getId());
                                object.put("name", channel.getName());
                                object.put("type", channel.getType().getId());

                                json.add(object);
                            });

                    response.status(200);
                    response.type("application/json");

                    return json;
                });
    }
}
