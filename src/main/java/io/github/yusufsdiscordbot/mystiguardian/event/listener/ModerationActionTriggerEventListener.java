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

            val embedBuilder = getEmbedBuilder(event, user, admin, event.getModerationActionId());
            systemChannel.sendMessage(embedBuilder)
                    .thenAccept(message -> {
                        user.openPrivateChannel()
                                .thenAccept(privateChannel -> {
                                    val userEmbedBuilder = getUserEmbedBuilder(event, user, admin, event.getModerationActionId());
                                    privateChannel.sendMessage(userEmbedBuilder);
                                });
                    });
        }

        if (event.getModerationTypes() == MystiGuardianUtils.ModerationTypes.DELETE_MESSAGES) {
            val embedBuilder = getMessageDeletedEmbed(event, admin, event.getAmountOfMessagesDeleted());
            systemChannel.sendMessage(embedBuilder);
        }
    }


    @NotNull
    private static EmbedBuilder getEmbedBuilder(@NotNull ModerationActionTriggerEvent event, @NotNull User user, User admin, long moderationActionId) {
        val embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(STR."\{user.getDiscriminatedName()} was \{event.getModerationTypes().getName().toLowerCase()}ed");
        embedBuilder.setFooter(STR."User id: \{user.getIdAsString()} | \{event.getModerationTypes().getName().toLowerCase()} id: \{moderationActionId}");
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.getApi().getYourself());
        similarFields(embedBuilder, event, admin);

        return embedBuilder;
    }

    @NotNull
    private static EmbedBuilder getMessageDeletedEmbed(@NotNull ModerationActionTriggerEvent event, @NotNull User admin, Integer amountOfMessagesDeleted) {
        val embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(STR."\{amountOfMessagesDeleted} messages were deleted");
        embedBuilder.addField("Admin", admin.getDiscriminatedName());
        embedBuilder.setFooter(STR."Admin id: \{admin.getIdAsString()}");
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.getApi().getYourself());
        return embedBuilder;
    }

    @NotNull
    private static EmbedBuilder getUserEmbedBuilder(@NotNull ModerationActionTriggerEvent event, User user, User admin, long moderationActionId) {
        val embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(STR."You were \{event.getModerationTypes().getName().toLowerCase()}ed");
        embedBuilder.setFooter(STR."\{event.getModerationTypes().getName().toLowerCase()} id: \{moderationActionId}");
        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.getApi().getYourself());
        similarFields(embedBuilder, event, admin);

        return embedBuilder;
    }

    private static void similarFields(@NotNull EmbedBuilder embedBuilder, @NotNull ModerationActionTriggerEvent event, @NotNull User admin) {
        embedBuilder.addField("Reason", event.getReason());
    }
}
