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
package io.github.yusufsdiscordbot.mystiguardian.entites.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.yusufsdiscordbot.mystiguardian.entites.OAuthJWt;

public class OAuthJWtImpl implements OAuthJWt {

    private final long userId;
    private final long databaseId;
    private final long expirationTime;

    public OAuthJWtImpl(DecodedJWT decodedJWT) {

        this.userId = decodedJWT.getClaim("user_id").asLong();
        this.databaseId = decodedJWT.getClaim("database_id").asLong();
        this.expirationTime = decodedJWT.getClaim("expiration_time").asLong();
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getDatabaseId() {
        return databaseId;
    }

    @Override
    public long getExpirationTime() {
        return expirationTime;
    }
}
