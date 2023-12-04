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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import lombok.val;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DeleteMessagesCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        val amount = event.getOptionByName("amount")
                .orElseThrow()
                .getLongValue()
                .orElseGet(() -> {
                    replyUtils.sendError("Invalid amount");
                    return 0L;
                });

        val channelOption = event.getOptionByName("channel").orElse(null);

        TextChannel channel;

        if (channelOption == null) {
            channel = event.getChannel().orElseGet(() -> {
                replyUtils.sendError("Not in a channel");
                return null;
            });
        } else {
            channel = Objects.requireNonNull(channelOption.getChannelValue().orElseGet(() -> {
                        replyUtils.sendError("Error getting channel");
                        return null;
                    }))
                    .asTextableRegularServerChannel()
                    .orElseGet(() -> {
                        replyUtils.sendError("Channel is not a text channel");
                        return null;
                    });
        }

        if (channel == null) {
            return;
        }

        val server = event.getServer().orElse(null);

        if (server == null) {
            replyUtils.sendError("This command can only be used in servers");
            return;
        }

        channel.getMessages(amount.intValue()).thenAccept(messages -> {
            channel.bulkDelete(messages)
                    .thenAccept(deletedMessages -> {
                        replyUtils.sendSuccess("Successfully deleted " + amount.intValue() + " messages");

                        MystiGuardianConfig.getEventDispatcher()
                                .dispatchEvent(new ModerationActionTriggerEvent(
                                                MystiGuardianUtils.ModerationTypes.DELETE_MESSAGES,
                                                event.getApi(),
                                                server.getIdAsString(),
                                                event.getUser().getIdAsString())
                                        .setAmountOfMessagesDeleted(amount.intValue()));
                    })
                    .exceptionally(throwable -> {
                        replyUtils.sendError("Failed to delete messages " + throwable.getMessage());
                        return null;
                    });
        });
    }

    @NotNull
    @Override
    public String getName() {
        return "delete-messages";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Delete messages from a channel";
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public EnumSet<PermissionType> getRequiredPermissions() {
        return EnumSet.of(PermissionType.MANAGE_MESSAGES);
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createLongOption("amount", "The amount of messages to delete", true, 2, 100),
                SlashCommandOption.createChannelOption(
                        "channel",
                        "The channel to delete messages from",
                        false,
                        List.of(ChannelType.getTextChannelTypes())));
    }
}
