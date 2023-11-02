package io.github.yusufsdiscordbot.mystiguardian.event.listener;

import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.ModerationActionTriggerEventHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.jConfig;

public class ModerationActionTriggerEventListener implements ModerationActionTriggerEventHandler {
    @NotNull
    private static EmbedBuilder getEmbedBuilder(ModerationActionTriggerEvent event, User user, User admin, long warnId) {
        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(STR."\{user.getDiscriminatedName()} was \{event.moderationTypes().name().toLowerCase()}ed");
        embedBuilder.addField("Reason", event.reason());
        embedBuilder.addField("Admin", admin.getDiscriminatedName());
        embedBuilder.setFooter(STR."User id: \{user.getIdAsString()} | Warn id: \{warnId}");
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.api().getYourself());
        return embedBuilder;
    }

    @Override
    public void onModerationActionTriggerEvent(ModerationActionTriggerEvent event) {
        // Placeholder for now, replace with database stuff later
        val systemChannelId = Objects.requireNonNull(jConfig.get("systemChannelId"), "System channel id can not be null").asText();
        val systemChannel = event.api().getServerById(event.serverId())
                .flatMap(serverById -> serverById.getChannelById(systemChannelId))
                .flatMap(Channel::asServerTextChannel)
                .orElse(null);

        if (systemChannel == null) {
            return;
        }

        val user = event.api().getUserById(event.userId())
                .join();
        val admin = event.api().getUserById(event.admin())
                .join();

        final var embedBuilder = getEmbedBuilder(event, user, admin, event.warnId());

        systemChannel.sendMessage(embedBuilder);
    }
}
