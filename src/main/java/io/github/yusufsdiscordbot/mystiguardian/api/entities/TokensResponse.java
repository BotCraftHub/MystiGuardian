package io.github.yusufsdiscordbot.mystiguardian.api.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class TokensResponse {
    private final JsonNode json;

    public TokensResponse(JsonNode json) {
        this.json = json;
    }


    public String getAccessToken() {
        return json.get("access_token").asText();
    }

    public String getTokenType() {
        return json.get("token_type").asText();
    }

    public int getExpiresIn() {
        return json.get("expires_in").asInt();
    }

    public String getRefreshToken() {
        return json.get("refresh_token").asText();
    }

    public String getScope() {
        return json.get("scope").asText();
    }
}
