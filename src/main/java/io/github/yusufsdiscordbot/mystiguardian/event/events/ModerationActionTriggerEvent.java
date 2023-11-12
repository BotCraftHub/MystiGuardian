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
    @Nullable
    private Long moderationActionId = null;
    @Nullable
    private String userId = null;
    @Nullable
    private Integer amountOfMessagesDeleted = null;
    @Nullable
    private String reason = null;

    public ModerationActionTriggerEvent(MystiGuardianUtils.ModerationTypes moderationTypes, DiscordApi api,
                                        String serverId, String adminId) {
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
}
