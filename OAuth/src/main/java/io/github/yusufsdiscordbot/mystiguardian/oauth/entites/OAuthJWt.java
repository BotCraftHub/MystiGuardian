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

/**
 * Represents a decoded and validated JWT (JSON Web Token) used for OAuth authentication.
 *
 * <p>This interface provides access to claims stored in the JWT token.
 */
public interface OAuthJWt {

    /**
     * Gets the user id of the user
     *
     * @return the user id
     */
    long getUserId();

    /**
     * Gets the database id for the user
     *
     * @return the database id
     */
    long getDatabaseId();

    /**
     * Gets the expiration time of the token
     *
     * @return the expiration time
     */
    long getExpirationTime();
}
