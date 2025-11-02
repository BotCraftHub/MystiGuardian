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
package io.github.yusufsdiscordbot.mystiguardian.event.listener;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.ModerationActionTriggerEventHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.Instant;
import java.util.Optional;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

public class ModerationActionTriggerEventListener implements ModerationActionTriggerEventHandler {

    @Override
    public void onModerationActionTriggerEvent(ModerationActionTriggerEvent event) {
        val systemChannel = getSystemChannel(event);
        if (systemChannel.isEmpty()) {
            return;
        }

        val admin = Optional.ofNullable(event.getJda().getUserById(event.getAdminId()));

        if (event.getModerationActionId() != null) {
            handleModerationAction(event, systemChannel.get(), admin);
        }

        if (event.getModerationTypes() == MystiGuardianUtils.ModerationTypes.DELETE_MESSAGES) {
            handleDeletedMessages(event, systemChannel.get(), admin);
        }
    }

    private Optional<TextChannel> getSystemChannel(ModerationActionTriggerEvent event) {
        return Optional.ofNullable(event.getJda().getGuildById(event.getServerId()))
                .flatMap(
                        guild -> {
                            String channelId =
                                    MystiGuardianDatabaseHandler.AuditChannel.getAuditChannelRecord(
                                            event.getServerId());

                            if (channelId == null) {
                                return Optional.empty();
                            }

                            return Optional.ofNullable(guild.getChannelById(TextChannel.class, channelId));
                        });
    }

    private void handleModerationAction(
            ModerationActionTriggerEvent event, TextChannel systemChannel, Optional<User> admin) {
        val reason =
                Optional.ofNullable(event.getReason())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException("Reason cannot be null for a moderation action."));
        val userId =
                Optional.ofNullable(event.getUserId())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "User ID cannot be null for a moderation action."));

        val userFuture = Optional.ofNullable(event.getJda().getUserById(userId));
        if (userFuture.isEmpty()) {
            event
                    .getJda()
                    .retrieveUserById(userId)
                    .queue(
                            user -> {
                                EmbedBuilder embedBuilder = createModerationActionEmbed(event, user, admin, reason);
                                systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();

                                EmbedBuilder userEmbedBuilder = createUserNotificationEmbed(event, admin, reason);
                                user.openPrivateChannel()
                                        .queue(
                                                privateChannel ->
                                                        privateChannel.sendMessageEmbeds(userEmbedBuilder.build()).queue());
                            },
                            throwable -> {
                                systemChannel
                                        .sendMessage(
                                                "Failed to retrieve user for moderation action: " + throwable.getMessage())
                                        .queue();
                            });
            return;
        }

        val user = userFuture.get();
        EmbedBuilder embedBuilder = createModerationActionEmbed(event, user, admin, reason);
        systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();

        EmbedBuilder userEmbedBuilder = createUserNotificationEmbed(event, admin, reason);
        user.openPrivateChannel()
                .queue(
                        privateChannel -> privateChannel.sendMessageEmbeds(userEmbedBuilder.build()).queue());
    }

    private void handleDeletedMessages(
            ModerationActionTriggerEvent event, TextChannel systemChannel, Optional<User> admin) {
        EmbedBuilder embedBuilder = createMessageDeletedEmbed(event, admin);
        systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    @NotNull
    private EmbedBuilder createModerationActionEmbed(
            @NotNull ModerationActionTriggerEvent event,
            @NotNull User user,
            Optional<User> admin,
            String reason) {
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(
                                MystiGuardianUtils.formatString(
                                        "%s was %sed",
                                        user.getName(), event.getModerationTypes().getName().toLowerCase()))
                        .setFooter(
                                MystiGuardianUtils.formatString(
                                        "User id: %s | %s id: %d | Admin id: %s",
                                        user.getId(),
                                        event.getModerationTypes().getName().toLowerCase(),
                                        event.getModerationActionId(),
                                        admin.map(User::getId).orElse("Unknown")))
                        .setTimestamp(Instant.now())
                        .setColor(MystiGuardianUtils.getBotColor())
                        .setAuthor(event.getJda().getSelfUser().getName());

        admin.ifPresent(
                value ->
                        embedBuilder.setFooter(
                                MystiGuardianUtils.formatString(
                                        "User id: %s | %s id: %d | Admin id: %s",
                                        user.getId(),
                                        event.getModerationTypes().getName().toLowerCase(),
                                        event.getModerationActionId(),
                                        value.getId())));

        addReasonField(embedBuilder, reason);
        addSoftBanField(embedBuilder, event);

        return embedBuilder;
    }

    @NotNull
    private EmbedBuilder createMessageDeletedEmbed(
            @NotNull ModerationActionTriggerEvent event, Optional<User> admin) {
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(
                                MystiGuardianUtils.formatString(
                                        "%d messages were deleted", event.getAmountOfMessagesDeleted()))
                        .setTimestamp(Instant.now())
                        .setColor(MystiGuardianUtils.getBotColor())
                        .setAuthor(event.getJda().getSelfUser().getName());

        admin.ifPresent(
                value ->
                        embedBuilder.setFooter(MystiGuardianUtils.formatString("Admin id: %s", value.getId())));
        return embedBuilder;
    }

    @NotNull
    private EmbedBuilder createUserNotificationEmbed(
            @NotNull ModerationActionTriggerEvent event, Optional<User> admin, String reason) {
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(
                                MystiGuardianUtils.formatString(
                                        "You were %sed", event.getModerationTypes().getName().toLowerCase()))
                        .setTimestamp(Instant.now())
                        .setColor(MystiGuardianUtils.getBotColor())
                        .setAuthor(event.getJda().getSelfUser().getName());

        admin.ifPresent(
                value ->
                        embedBuilder.setFooter(
                                MystiGuardianUtils.formatString(
                                        "%s id: %d | server id: %s | admin id: %s",
                                        event.getModerationTypes().getName().toLowerCase(),
                                        event.getModerationActionId(),
                                        event.getServerId(),
                                        value.getId())));

        addReasonField(embedBuilder, reason);
        addSoftBanField(embedBuilder, event);

        return embedBuilder;
    }

    private void addReasonField(@NotNull EmbedBuilder embedBuilder, String reason) {
        embedBuilder.addField("Reason", reason, false);
    }

    private void addSoftBanField(
            @NotNull EmbedBuilder embedBuilder, @NotNull ModerationActionTriggerEvent event) {
        Optional.ofNullable(event.getSoftBanAmountOfDays())
                .ifPresent(days -> embedBuilder.addField("Ban Duration", days + " days", false));
    }
}
