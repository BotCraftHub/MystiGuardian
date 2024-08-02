/*
 * Copyright 2024 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.event.events;

import io.github.yusufsdiscordbot.mystiguardian.event.generic.GenericSubscribeEvent;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.Getter;
import org.javacord.api.DiscordApi;
import org.jetbrains.annotations.Nullable;

@Getter
public class ModerationActionTriggerEvent implements GenericSubscribeEvent {

    private final MystiGuardianUtils.ModerationTypes moderationTypes;
    private final DiscordApi api;
    private final String serverId;
    private final String adminId;

    // Nullable
    @Nullable private Long moderationActionId = null;

    @Nullable private String userId = null;

    @Nullable private Integer amountOfMessagesDeleted = null;

    @Nullable private String reason = null;

    @Nullable private Integer softBanAmountOfDays = null;

    public ModerationActionTriggerEvent(
            MystiGuardianUtils.ModerationTypes moderationTypes,
            DiscordApi api,
            String serverId,
            String adminId) {
        this.moderationTypes = moderationTypes;
        this.api = api;
        this.serverId = serverId;
        this.adminId = adminId;
    }

    public ModerationActionTriggerEvent setModerationActionId(long moderationActionId) {
        this.moderationActionId = moderationActionId;
        return this;
    }

    public ModerationActionTriggerEvent setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public ModerationActionTriggerEvent setAmountOfMessagesDeleted(int amountOfMessagesDeleted) {
        this.amountOfMessagesDeleted = amountOfMessagesDeleted;
        return this;
    }

    public ModerationActionTriggerEvent setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public ModerationActionTriggerEvent setSoftBanAmountOfDays(int softBanAmountOfDays) {
        this.softBanAmountOfDays = softBanAmountOfDays;
        return this;
    }
}
