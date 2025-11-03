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
package io.github.yusufsdiscordbot.mystiguardian.oauth.response;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents the response from Discord's OAuth2 token exchange.
 *
 * <p>This class parses and provides access to OAuth token data including access tokens,
 * refresh tokens, and token metadata.
 *
 * @param json the JSON node containing token data from Discord API
 */
public record TokensResponse(JsonNode json) {
    /**
     * Constructs a new TokensResponse from JSON data.
     *
     * @param json the JSON node containing token data from Discord API
     */
    public TokensResponse {
    }

    /**
     * Gets the OAuth2 access token.
     *
     * @return the access token string
     */
    public String getAccessToken() {
        return json.get("access_token").asText();
    }

    /**
     * Gets the token type (typically "Bearer").
     *
     * @return the token type
     */
    public String getTokenType() {
        return json.get("token_type").asText();
    }

    /**
     * Gets the token expiration time in seconds.
     *
     * @return the number of seconds until the token expires
     */
    public int getExpiresIn() {
        return json.get("expires_in").asInt();
    }

    /**
     * Gets the refresh token for obtaining new access tokens.
     *
     * @return the refresh token string
     */
    public String getRefreshToken() {
        return json.get("refresh_token").asText();
    }

    /**
     * Gets the OAuth2 scope granted.
     *
     * @return the space-separated scope string
     */
    public String getScope() {
        return json.get("scope").asText();
    }
}
