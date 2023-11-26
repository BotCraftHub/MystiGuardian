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
package io.github.yusufsdiscordbot.mystiguardian.entites;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class OAuthUserImpl implements OAuthUser {
    private final Long id;
    private final String username;
    private final String discriminator;
    private final String globalName;
    private final String avatar;
    private final boolean bot;
    private final boolean system;
    private final boolean mfaEnabled;
    private final String banner;
    private final Integer accentColor;
    private final String locale;
    private final Boolean verified;
    private final Integer flags;
    private final Integer premiumType;
    private final Integer publicFlags;
    private final String avatarDecoration;
    private final JsonNode json;

    public OAuthUserImpl(JsonNode json) {
        this.json = json;
        this.id = json.get("id").asLong();
        this.username = json.get("username").asText();
        this.discriminator = json.get("discriminator").asText();
        this.globalName = json.has("global_name") ? json.get("global_name").asText() : null;
        this.avatar = json.has("avatar") ? json.get("avatar").asText() : null;
        this.bot = json.get("bot").asBoolean();
        this.system = json.get("system").asBoolean();
        this.mfaEnabled = json.get("mfa_enabled").asBoolean();
        this.banner = json.has("banner") ? json.get("banner").asText() : null;
        this.accentColor = json.has("accent_color") ? json.get("accent_color").asInt() : null;
        this.locale = json.get("locale").asText();
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
    public boolean isBot() {
        return bot;
    }

    @Override
    public boolean isSystem() {
        return system;
    }

    @Override
    public boolean isMfaEnabled() {
        return mfaEnabled;
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
    public String getLocale() {
        return locale;
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
