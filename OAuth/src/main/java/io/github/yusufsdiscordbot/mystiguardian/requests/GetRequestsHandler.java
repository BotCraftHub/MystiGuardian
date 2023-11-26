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

import io.github.yusufsdiscordbot.mystiguardian.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.endpoints.GetEndpoints;
import lombok.val;
import spark.Spark;

public class GetRequestsHandler {

    public GetRequestsHandler() {
        handleDiscordAuthRequest();
    }

    private void handleDiscordAuthRequest() {
        Spark.get(GetEndpoints.DISCORD_AUTH.getEndpoint(), (request, response) -> {
            val authorizationUrl = "https://discord.com/api/oauth2/authorize" + "?client_id="
                    + OAuth.getClientId() + "&redirect_uri="
                    + OAuth.getRedirectUri() + "&response_type=code"
                    + "&scope=identify%20guilds";

            response.redirect(authorizationUrl);
            return null;
        });
    }
}
