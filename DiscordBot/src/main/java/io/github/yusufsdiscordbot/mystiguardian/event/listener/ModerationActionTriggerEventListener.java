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
package io.github.yusufsdiscordbot.mystiguardian.event.listener;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.ModerationActionTriggerEventHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.Instant;
import java.util.Objects;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

public class ModerationActionTriggerEventListener implements ModerationActionTriggerEventHandler {
    @Override
    public void onModerationActionTriggerEvent(ModerationActionTriggerEvent event) {
        val systemChannel =
                Objects.requireNonNull(event.getApi().getGuildById(event.getServerId()))
                        .getChannelById(
                                TextChannel.class,
                                Objects.requireNonNull(
                                        MystiGuardianDatabaseHandler.AuditChannel.getAuditChannelRecord(
                                                event.getServerId()),
                                        "Channel is null"));

        if (systemChannel == null) {
            return;
        }

        val admin = event.getApi().getUserById(event.getAdminId());

        if (event.getModerationActionId() != null) {
            assert event.getReason() != null;
            assert event.getUserId() != null;

            val user = event.getApi().getUserById(event.getUserId());

            val embedBuilder =
                    getEmbedBuilder(
                            event, user, admin, event.getModerationActionId(), event.getSoftBanAmountOfDays());

            systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();

            val userEmbedBuilder =
                    getUserEmbedBuilder(
                            event,
                            admin,
                            event.getModerationActionId(),
                            event.getSoftBanAmountOfDays(),
                            event.getServerId());

            user.openPrivateChannel().complete().sendMessageEmbeds(userEmbedBuilder.build()).queue();
        }

        if (event.getModerationTypes() == MystiGuardianUtils.ModerationTypes.DELETE_MESSAGES) {
            val embedBuilder = getMessageDeletedEmbed(event, admin, event.getAmountOfMessagesDeleted());
            systemChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    @NotNull
    private static EmbedBuilder getEmbedBuilder(
            @NotNull ModerationActionTriggerEvent event,
            @NotNull User user,
            User admin,
            long moderationActionId,
            Integer softBanAmountOfDays) {
        val embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(
                MystiGuardianUtils.formatString(
                        "%s was %sed", user.getName(), event.getModerationTypes().getName().toLowerCase()));
        embedBuilder.setFooter(
                MystiGuardianUtils.formatString(
                        "User id: %s | %s id: %d | Admin id: %s",
                        user.getId(),
                        event.getModerationTypes().getName().toLowerCase(),
                        moderationActionId,
                        admin.getId()));
        similarFields(embedBuilder, event, admin);

        if (softBanAmountOfDays != null) {
            embedBuilder.addField("Ban Duration", softBanAmountOfDays + " days", false);
        }

        return embedBuilder;
    }

    @NotNull
    private static EmbedBuilder getMessageDeletedEmbed(
            @NotNull ModerationActionTriggerEvent event,
            @NotNull User admin,
            Integer amountOfMessagesDeleted) {
        val embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(
                MystiGuardianUtils.formatString("%d messages were deleted", amountOfMessagesDeleted));
        embedBuilder.setFooter(MystiGuardianUtils.formatString("Admin id: %s", admin.getId()));

        similarFields(embedBuilder, event, admin);
        return embedBuilder;
    }

    @NotNull
    private static EmbedBuilder getUserEmbedBuilder(
            @NotNull ModerationActionTriggerEvent event,
            User admin,
            long moderationActionId,
            Integer softBanAmountOfDays,
            String serverId) {
        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(
                MystiGuardianUtils.formatString(
                        "You were %sed", event.getModerationTypes().getName().toLowerCase()));
        embedBuilder.setFooter(
                MystiGuardianUtils.formatString(
                        "%s id: %d | server id: %s | admin id: %s",
                        event.getModerationTypes().getName().toLowerCase(),
                        moderationActionId,
                        serverId,
                        admin.getId()));
        similarFields(embedBuilder, event, admin);

        if (softBanAmountOfDays != null) {
            embedBuilder.addField("Ban Duration", softBanAmountOfDays + " days", false);
        }

        return embedBuilder;
    }

    private static void similarFields(
            @NotNull EmbedBuilder embedBuilder,
            @NotNull ModerationActionTriggerEvent event,
            @NotNull User admin) {

        if (event.getReason() != null) {
            embedBuilder.addField("Reason", event.getReason(), false);
        }

        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.getApi().getSelfUser().getName());
    }
}
