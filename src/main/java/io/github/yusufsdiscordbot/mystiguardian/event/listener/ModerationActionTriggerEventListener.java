package io.github.yusufsdiscordbot.mystiguardian.event.listener;

import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.ModerationActionTriggerEventHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public class ModerationActionTriggerEventListener implements ModerationActionTriggerEventHandler {
    @NotNull
    private static EmbedBuilder getEmbedBuilder(ModerationActionTriggerEvent event, User user, User admin, long moderationActionId) {
        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(STR. "\{ user.getDiscriminatedName() } was \{ event.getModerationTypes().getName().toLowerCase() }ed" );
        embedBuilder.addField("Reason", event.getReason());
        embedBuilder.addField("Admin", admin.getDiscriminatedName());
        embedBuilder.setFooter(STR. "User id: \{ user.getIdAsString() } | \{ event.getModerationTypes().getName().toLowerCase() } id: \{ moderationActionId }");
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.getApi().getYourself());
        return embedBuilder;
    }

    private static EmbedBuilder getMessageDeletedEmbed(ModerationActionTriggerEvent event, User admin, Integer amountOfMessagesDeleted) {
        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(STR. "\{ amountOfMessagesDeleted } messages were deleted" );
        embedBuilder.addField("Admin", admin.getDiscriminatedName());
        embedBuilder.setFooter(STR. "Admin id: \{ admin.getIdAsString() }");
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.getApi().getYourself());
        return embedBuilder;
    }

    @Override
    public void onModerationActionTriggerEvent(ModerationActionTriggerEvent event) {
        val systemChannel = event.getApi().getServerById(event.getServerId())
                .flatMap(Server::getModeratorsOnlyChannel)
                .flatMap(Channel::asServerTextChannel)
                .orElse(null);

        if (systemChannel == null) {
            return;
        }

        val admin = event.getApi().getUserById(event.getAdminId())
                .join();

        if (event.getModerationActionId() != null) {
            assert event.getReason() != null;
            assert event.getUserId() != null;

            val user = event.getApi().getUserById(event.getUserId())
                    .join();

            final var embedBuilder = getEmbedBuilder(event, user, admin, event.getModerationActionId());
            systemChannel.sendMessage(embedBuilder);
        }

        if (event.getModerationTypes() == MystiGuardianUtils.ModerationTypes.DELETE_MESSAGES) {
            val embedBuilder = getMessageDeletedEmbed(event, admin, event.getAmountOfMessagesDeleted());
            systemChannel.sendMessage(embedBuilder);
        }
    }
}
