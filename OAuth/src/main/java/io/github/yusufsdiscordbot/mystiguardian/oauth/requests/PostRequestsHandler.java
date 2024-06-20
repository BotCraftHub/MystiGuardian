/*
 * Copyright 2024 RealYusufIsmail.
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

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.oauth.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.oauth.endpoints.PostEndpoints;
import io.github.yusufsdiscordbot.mystiguardian.oauth.response.TokensResponse;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import spark.Spark;

public class PostRequestsHandler {

    public PostRequestsHandler() {
        handlePostLoginRequest();
    }

    private void handlePostLoginRequest() {
        Spark.post(PostEndpoints.LOGIN.getEndpoint(), (request, response) -> {
            val code = request.queryParams("code");

            if (code == null) {
                response.status(400);
                return "Missing code";
            }

            TokensResponse tokensResponse = OAuth.getDiscordRestAPI().getToken(code);

            val user = OAuth.getDiscordRestAPI().getUser(tokensResponse.getAccessToken());

            val requestTime = System.currentTimeMillis();
            val refreshToken = tokensResponse.getRefreshToken();
            long expiresAt = requestTime / 1000 + tokensResponse.getExpiresIn();

            val id = MystiGuardianDatabaseHandler.OAuth.setOAuthRecord(
                    tokensResponse.getAccessToken(), refreshToken, user.getJson(), user.getIdAsString(), expiresAt);

            val jwt = OAuth.getAuthUtils().generateJwt(user.getId(), expiresAt, id);

            val json = MystiGuardianUtils.objectMapper.createObjectNode();
            json.put("jwt", jwt);
            json.put("expiresAt", expiresAt);

            response.status(200);
            response.type("application/json");

            return json.toString();
        });
    }
}
