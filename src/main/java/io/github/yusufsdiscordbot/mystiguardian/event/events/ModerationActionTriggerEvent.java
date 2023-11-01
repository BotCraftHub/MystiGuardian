package io.github.yusufsdiscordbot.mystiguardian.event.events;

import io.github.yusufsdiscordbot.mystiguardian.event.generic.GenericSubscribeEvent;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.javacord.api.DiscordApi;

public record ModerationActionTriggerEvent(MystiGuardianUtils.ModerationTypes moderationTypes, DiscordApi api, String serverId, String userId, String reason,
                                           String admin) implements GenericSubscribeEvent {
}
