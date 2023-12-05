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
package io.github.yusufsdiscordbot.mystiguardian.event.listener;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.ModerationActionTriggerEventHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.jetbrains.annotations.NotNull;

public class ModerationActionTriggerEventListener implements ModerationActionTriggerEventHandler {
    @Override
    public void onModerationActionTriggerEvent(ModerationActionTriggerEvent event) {
        val systemChannel = event.getApi()
                .getServerById(event.getServerId())
                .flatMap(k -> k.getChannelById(
                        MystiGuardianDatabaseHandler.AuditChannel.getAuditChannelRecord(k.getIdAsString())))
                .flatMap(Channel::asServerTextChannel)
                .orElse(null);

        if (systemChannel == null) {
            return;
        }

        val admin = event.getApi().getUserById(event.getAdminId()).join();

        if (event.getModerationActionId() != null) {
            assert event.getReason() != null;
            assert event.getUserId() != null;

            val user = event.getApi().getUserById(event.getUserId()).join();

            val embedBuilder =
                    getEmbedBuilder(event, user, admin, event.getModerationActionId(), event.getSoftBanAmountOfDays());

            systemChannel.sendMessage(embedBuilder);

            val userEmbedBuilder = getUserEmbedBuilder(
                    event, admin, event.getModerationActionId(), event.getSoftBanAmountOfDays(), event.getServerId());

            user.openPrivateChannel().join().sendMessage(userEmbedBuilder);
        }

        if (event.getModerationTypes() == MystiGuardianUtils.ModerationTypes.DELETE_MESSAGES) {
            val embedBuilder = getMessageDeletedEmbed(event, admin, event.getAmountOfMessagesDeleted());
            systemChannel.sendMessage(embedBuilder);
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

        embedBuilder.setTitle(MystiGuardianUtils.formatString(
                "%s was %sed",
                user.getDiscriminatedName(),
                event.getModerationTypes().getName().toLowerCase()));
        embedBuilder.setFooter(MystiGuardianUtils.formatString(
                "User id: %s | %s id: %d | Admin id: %s",
                user.getIdAsString(),
                event.getModerationTypes().getName().toLowerCase(),
                moderationActionId,
                admin.getIdAsString()));
        similarFields(embedBuilder, event, admin);

        if (softBanAmountOfDays != null) {
            embedBuilder.addField("Ban Duration", softBanAmountOfDays + " days");
        }

        return embedBuilder;
    }

    @NotNull
    private static EmbedBuilder getMessageDeletedEmbed(
            @NotNull ModerationActionTriggerEvent event, @NotNull User admin, Integer amountOfMessagesDeleted) {
        val embedBuilder = new EmbedBuilder();

        embedBuilder.setTitle(MystiGuardianUtils.formatString("%d messages were deleted", amountOfMessagesDeleted));
        embedBuilder.setFooter(MystiGuardianUtils.formatString("Admin id: %s", admin.getIdAsString()));

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
        embedBuilder.setTitle(MystiGuardianUtils.formatString(
                "You were %sed", event.getModerationTypes().getName().toLowerCase()));
        embedBuilder.setFooter(MystiGuardianUtils.formatString(
                "%s id: %d | server id: %s | admin id: %s",
                event.getModerationTypes().getName().toLowerCase(),
                moderationActionId,
                serverId,
                admin.getIdAsString()));
        similarFields(embedBuilder, event, admin);

        if (softBanAmountOfDays != null) {
            embedBuilder.addField("Ban Duration", softBanAmountOfDays + " days");
        }

        return embedBuilder;
    }

    private static void similarFields(
            @NotNull EmbedBuilder embedBuilder, @NotNull ModerationActionTriggerEvent event, @NotNull User admin) {

        if (event.getReason() != null) {
            embedBuilder.addField("Reason", event.getReason());
        }

        embedBuilder.setTimestampToNow();
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        embedBuilder.setAuthor(event.getApi().getYourself());
    }
}
