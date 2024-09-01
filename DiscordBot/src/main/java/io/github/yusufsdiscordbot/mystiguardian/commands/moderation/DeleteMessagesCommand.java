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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class DeleteMessagesCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val amount =
                Objects.requireNonNull(
                        event.getOption("amount", OptionMapping::getAsInt), "Amount is null");

        val channelOption = event.getOption("channel");
        TextChannel channel;

        if (channelOption == null) {
            channel = event.getChannel().asTextChannel();
        } else {
            channel = channelOption.getAsChannel().asTextChannel();
        }

        val guild = event.getGuild();
        if (guild == null) {
            replyUtils.sendError("This command can only be used in servers");
            return;
        }

        channel
                .getHistory()
                .retrievePast(amount)
                .queue(
                        messages -> {
                            channel.purgeMessages(messages);
                            replyUtils.sendSuccess("Successfully deleted " + amount + " messages");

                            MystiGuardianConfig.getEventDispatcher()
                                    .dispatchEvent(
                                            new ModerationActionTriggerEvent(
                                                            MystiGuardianUtils.ModerationTypes.DELETE_MESSAGES,
                                                            event.getJDA(),
                                                            guild.getId(),
                                                            event.getUser().getId())
                                                    .setAmountOfMessagesDeleted(amount));
                        },
                        throwable ->
                                replyUtils.sendError("Failed to delete messages: " + throwable.getMessage()));
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
    public EnumSet<Permission> getRequiredPermissions() {
        return EnumSet.of(Permission.MESSAGE_MANAGE);
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "amount", "The amount of messages to delete", true)
                        .setRequiredRange(2, 100),
                new OptionData(OptionType.CHANNEL, "channel", "The channel to delete messages from", false)
                        .setChannelTypes(ChannelType.TEXT));
    }
}
