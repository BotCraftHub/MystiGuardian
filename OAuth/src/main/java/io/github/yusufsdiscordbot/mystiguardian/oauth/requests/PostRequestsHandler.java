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
            val redirectUri = request.queryParams("redirect_uri");

            if (code == null) {
                response.status(400);
                response.body("Missing code");
                MystiGuardianUtils.logger.error("Missing code");
                return response;
            }

            try {
                TokensResponse tokensResponse = OAuth.getDiscordRestAPI().getToken(code, redirectUri);

                if (tokensResponse == null) {
                    MystiGuardianUtils.logger.error("Failed to get tokens");
                    response.status(400);
                    response.body("Failed to get tokens");
                    return response;
                }

                val user = OAuth.getDiscordRestAPI().getUser(tokensResponse.getAccessToken());

                if (user == null) {
                    response.status(400);
                    response.body("Failed to get user");
                    MystiGuardianUtils.logger.error("Failed to get user");
                    return response;
                }

                val requestTime = System.currentTimeMillis();
                val refreshToken = tokensResponse.getRefreshToken();
                long expiresAt = requestTime / 1000 + tokensResponse.getExpiresIn();

                val id = MystiGuardianDatabaseHandler.OAuth.setOAuthRecord(
                        tokensResponse.getAccessToken(), refreshToken, user.getJson(), user.getIdAsString(), expiresAt);

                val jwt = OAuth.getAuthUtils().generateJwt(user.getId(), expiresAt, id);

                if (jwt == null) {
                    response.status(500);
                    response.body("Failed to generate JWT");
                    MystiGuardianUtils.logger.error("Failed to generate JWT");
                    return response;
                }

                val json = MystiGuardianUtils.objectMapper.createObjectNode();
                json.put("jwt", jwt);
                json.put("expiresAt", expiresAt);

                response.status(200);
                response.type("application/json");
                return json.toString();
            } catch (Exception e) {
                MystiGuardianUtils.logger.error("Failed to get tokens", e);
                response.status(500);
                response.body("Failed to get tokens");
                return response;
            }
        });
    }
}
