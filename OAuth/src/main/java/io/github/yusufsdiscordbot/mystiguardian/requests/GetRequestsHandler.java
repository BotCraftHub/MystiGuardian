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
package io.github.yusufsdiscordbot.mystiguardian.requests;

import static io.github.yusufsdiscordbot.mystiguardian.utils.EntityManager.getGuildsThatUserCanManage;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.endpoints.GetEndpoints;
import io.github.yusufsdiscordbot.mystiguardian.http.DiscordRestAPI;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.channel.ChannelType;
import spark.Spark;

public class GetRequestsHandler {

    public GetRequestsHandler() {
        handleGetBotGuildsRequest();
        ping();
        getChannels();
    }

    private void handleGetBotGuildsRequest() {
        Spark.get(GetEndpoints.GET_GUILDS.getEndpoint(), (request, response) -> {
            val jwt = request.headers("Authorization");

            val decodedJWT = OAuth.getAuthUtils().validateJwt(jwt, response).orElse(null);

            if (decodedJWT == null) {
                return "JWT not found";
            }

            val userId = decodedJWT.getUserId();
            val id = decodedJWT.getDatabaseId();

            val accessToken = MystiGuardianDatabaseHandler.OAuth.getAccessToken(id, String.valueOf(userId));

            if (accessToken == null) {
                response.status(408);
                MystiGuardianUtils.discordAuthLogger.info("Access token not found");
                return "Access token not found";
            }

            val guilds = OAuth.getDiscordRestAPI().getGuilds(accessToken);

            if (guilds == null) {
                response.status(409);
                MystiGuardianUtils.discordAuthLogger.error("Failed to get guilds");
                return "Failed to get guilds";
            }

            val guildsThatUserCanManage = getGuildsThatUserCanManage(guilds);

            response.type("application/json");
            response.status(200);

            return guildsThatUserCanManage;
        });
    }

    private void ping() {
        Spark.get(GetEndpoints.PING.getEndpoint(), (request, response) -> {
            response.status(200);
            return "Pong!";
        });
    }

    private void getChannels() {
        Spark.get(GetEndpoints.GET_CHANNELS.getEndpoint(), (request, response) -> {
            val decodedJWT = OAuth.getAuthUtils()
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

            val channels = MystiGuardian.getMystiGuardian().getApi().getServerChannels().stream()
                    .filter(channel -> channel.asServerChannel()
                            .map(serverChannel ->
                                    serverChannel.getServer().getIdAsString().equals(guildId))
                            .orElse(false))
                    .toList();

            if (channels.isEmpty()) {
                return "No channels found";
            }

            val json = DiscordRestAPI.objectMapper.createArrayNode();

            val textChannels = channels.stream()
                    .filter(channel -> channel.getType() == ChannelType.SERVER_TEXT_CHANNEL)
                    .toList();

            channels.forEach(channel -> {
                val object = DiscordRestAPI.objectMapper.createObjectNode();

                object.put("id", channel.getIdAsString());
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
