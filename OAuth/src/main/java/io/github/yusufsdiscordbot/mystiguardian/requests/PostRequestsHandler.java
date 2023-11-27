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
import io.github.yusufsdiscordbot.mystiguardian.endpoints.PostRequests;
import io.github.yusufsdiscordbot.mystiguardian.response.TokensResponse;
import lombok.val;
import spark.Spark;

public class PostRequestsHandler {

    private void handlePostLoginRequest() {
        Spark.post(PostRequests.LOGIN.getEndpoint(), (request, response) -> {
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

            val jwt = OAuth.getAuthUtils().generateJwt(user.getId(), expiresAt);

            return null;
        });
    }
}
