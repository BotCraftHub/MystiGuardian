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
package io.github.yusufsdiscordbot.mystiguardian.api.entities;

import io.github.yusufsdiscordbot.mystiguardian.api.entities.MystiUserImpl;
import io.github.yusufsdiscordbot.mystiguardian.api.util.DiscordRestAPI;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class OAuthUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // This is a serializable class, so we need this

    private final MystiUserImpl user;
    private final DiscordRestAPI discordRestAPI;
    private final String refreshToken;
    private final long expiresAt;

    public OAuthUser(String accessToken, DiscordRestAPI discordRestAPI, String refreshToken, long expiresAt)
            throws Exception {
        discordRestAPI.setAccessToken(accessToken);
        this.user = discordRestAPI.getUser();
        this.discordRestAPI = discordRestAPI;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;

        MystiGuardianDatabaseHandler.AuthHandler.setAuthRecord(this);
    }
}
