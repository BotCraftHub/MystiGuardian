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
package io.github.yusufsdiscordbot.mystiguardian.oauth.endpoints;

import lombok.Getter;

/**
 * Enum representing available GET endpoints in the OAuth web service.
 */
@Getter
public enum GetEndpoints {
    /** Ping endpoint for health checks. */
    PING("/ping"),
    /** Endpoint to retrieve user's guilds. */
    GET_GUILDS("/guilds"),
    /** Endpoint to retrieve audit channel configuration. */
    GET_AUDIT_CHANNEL("/audit-channel"),
    /** Endpoint to retrieve channels from a guild. */
    GET_CHANNELS("/channels");

    private final String endpoint;

    GetEndpoints(String endpoint) {
        this.endpoint = endpoint;
    }
}
