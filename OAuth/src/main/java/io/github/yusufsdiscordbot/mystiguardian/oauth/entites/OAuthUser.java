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
package io.github.yusufsdiscordbot.mystiguardian.oauth.entites;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.impl.OAuthUserImpl;
import java.util.Optional;

/** Represents Discord user information. */
public interface OAuthUser {

    /**
     * Gets the user's id.
     *
     * @return The user's id.
     */
    Long getId();

    /**
     * Gets the users id as a string.
     *
     * @return The user's id as a string.
     */
    default String getIdAsString() {
        return String.valueOf(getId());
    }

    /**
     * Gets the user's username.
     *
     * @return The user's username.
     */
    String getUsername();

    /**
     * Gets the user's Discord-tag.
     *
     * @return The user's Discord-tag.
     */
    String getDiscriminator();

    /**
     * Gets the user's display name, if set. For bots, this is the application name.
     *
     * @return The user's display name.
     */
    Optional<String> getGlobalName();

    /**
     * Gets the user's avatar hash.
     *
     * @return The user's avatar hash.
     */
    Optional<String> getAvatar();

    /**
     * Checks if the user belongs to an OAuth2 application.
     *
     * @return True if the user belongs to an OAuth2 application, false otherwise.
     */
    Optional<Boolean> isBot();

    /**
     * Checks if the user is an Official Discord System user (part of the urgent message system).
     *
     * @return True if the user is a Discord system user, false otherwise.
     */
    Optional<Boolean> isSystem();

    /**
     * Checks if the user has two-factor authentication enabled on their account.
     *
     * @return True if the user has two-factor authentication enabled, false otherwise.
     */
    Optional<Boolean> isMfaEnabled();

    /**
     * Gets the user's banner hash.
     *
     * @return The user's banner hash.
     */
    Optional<String> getBanner();

    /**
     * Gets the user's banner color encoded as an integer representation of a hexadecimal color code.
     *
     * @return The user's banner color.
     */
    Optional<Integer> getAccentColor();

    /**
     * Gets the user's chosen language option.
     *
     * @return The user's chosen language option.
     */
    Optional<String> getLocale();

    /**
     * Checks if the email on this account has been verified.
     *
     * @return True if the email has been verified, false otherwise.
     */
    Optional<Boolean> isVerified();

    /**
     * Gets the flags on a user's account.
     *
     * @return The flags on a user's account.
     */
    Optional<Integer> getFlags();

    /**
     * Gets the type of Nitro subscription on a user's account.
     *
     * @return The type of Nitro subscription.
     */
    Optional<Integer> getPremiumType();

    /**
     * Gets the public flags on a user's account.
     *
     * @return The public flags on a user's account.
     */
    Optional<Integer> getPublicFlags();

    /**
     * Gets the user's avatar decoration hash.
     *
     * @return The user's avatar decoration hash.
     */
    Optional<String> getAvatarDecoration();

    /**
     * Gets the json of the user.
     *
     * @return The json of the user.
     */
    String getJson();

    /**
     * Gets the user object from the json.
     *
     * @return The user object from the json.
     */
    static OAuthUser fromJson(String json) {
        return new OAuthUserImpl(new ObjectMapper().valueToTree(json));
    }
}
