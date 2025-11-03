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
package io.github.yusufsdiscordbot.mystiguardian.oauth.entites.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.OAuthUser;
import java.util.Optional;

/**
 * Implementation of {@link OAuthUser} for Discord users retrieved via OAuth.
 *
 * <p>This class parses user data from JSON and provides access to all available user fields.
 */
public class OAuthUserImpl implements OAuthUser {
    private final Long id;
    private final String username;
    private final String discriminator;
    private final String globalName;
    private final String avatar;
    private final Boolean bot;
    private final Boolean system;
    private final Boolean mfaEnabled;
    private final String banner;
    private final Integer accentColor;
    private final String locale;
    private final Boolean verified;
    private final Integer flags;
    private final Integer premiumType;
    private final Integer publicFlags;
    private final String avatarDecoration;
    private final JsonNode json;

    /**
     * Constructs a new OAuthUserImpl from JSON data.
     *
     * @param json the JSON node containing user data from Discord API
     */
    public OAuthUserImpl(JsonNode json) {
        this.json = json;
        this.id = json.get("id").asLong();
        this.username = json.get("username").asText();
        this.discriminator = json.get("discriminator").asText();
        this.globalName = json.has("global_name") ? json.get("global_name").asText() : null;
        this.avatar = json.has("avatar") ? json.get("avatar").asText() : null;
        this.bot = json.has("bot") ? json.get("bot").asBoolean() : null;
        this.system = json.has("system") ? json.get("system").asBoolean() : null;
        this.mfaEnabled = json.has("mfa_enabled") ? json.get("mfa_enabled").asBoolean() : null;
        this.banner = json.has("banner") ? json.get("banner").asText() : null;
        this.accentColor = json.has("accent_color") ? json.get("accent_color").asInt() : null;
        this.locale = json.has("locale") ? json.get("locale").asText() : null;
        this.verified = json.has("verified") ? json.get("verified").asBoolean() : null;
        this.flags = json.has("flags") ? json.get("flags").asInt() : null;
        this.premiumType = json.has("premium_type") ? json.get("premium_type").asInt() : null;
        this.publicFlags = json.has("public_flags") ? json.get("public_flags").asInt() : null;
        this.avatarDecoration =
                json.has("avatar_decoration") ? json.get("avatar_decoration").asText() : null;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getDiscriminator() {
        return discriminator;
    }

    @Override
    public Optional<String> getGlobalName() {
        return Optional.ofNullable(globalName);
    }

    @Override
    public Optional<String> getAvatar() {
        return Optional.ofNullable(avatar);
    }

    @Override
    public Optional<Boolean> isBot() {
        return Optional.ofNullable(bot);
    }

    @Override
    public Optional<Boolean> isSystem() {
        return Optional.ofNullable(system);
    }

    @Override
    public Optional<Boolean> isMfaEnabled() {
        return Optional.ofNullable(mfaEnabled);
    }

    @Override
    public Optional<String> getBanner() {
        return Optional.ofNullable(banner);
    }

    @Override
    public Optional<Integer> getAccentColor() {
        return Optional.ofNullable(accentColor);
    }

    @Override
    public Optional<String> getLocale() {
        return Optional.ofNullable(locale);
    }

    @Override
    public Optional<Boolean> isVerified() {
        return Optional.ofNullable(verified);
    }

    @Override
    public Optional<Integer> getFlags() {
        return Optional.ofNullable(flags);
    }

    @Override
    public Optional<Integer> getPremiumType() {
        return Optional.ofNullable(premiumType);
    }

    @Override
    public Optional<Integer> getPublicFlags() {
        return Optional.ofNullable(publicFlags);
    }

    @Override
    public Optional<String> getAvatarDecoration() {
        return Optional.ofNullable(avatarDecoration);
    }

    @Override
    public String getJson() {
        return json.toString();
    }
}
