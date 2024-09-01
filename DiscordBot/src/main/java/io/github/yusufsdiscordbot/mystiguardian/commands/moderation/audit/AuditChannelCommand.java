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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.EnumSet;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class AuditChannelCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        OptionMapping channelOption = event.getOption("channel");
        if (channelOption == null) {
            replyUtils.sendError("You must specify a channel");
            return;
        }

        TextChannel auditChannel = channelOption.getAsChannel().asTextChannel();
        Guild guild = event.getGuild();

        if (guild == null) {
            replyUtils.sendError("You must be in a server to use this command");
            return;
        }

        boolean success =
                MystiGuardianDatabaseHandler.AuditChannel.setAuditChannelRecord(
                        guild.getId(), auditChannel.getId());

        if (success) {
            replyUtils.sendSuccess(
                    "Successfully set the audit log channel to " + auditChannel.getAsMention());
        } else {
            String existingChannelId =
                    MystiGuardianDatabaseHandler.AuditChannel.getAuditChannelRecord(guild.getId());
            TextChannel existingChannel = guild.getTextChannelById(existingChannelId);

            replyUtils.sendError(
                    "The audit channel has already been set to "
                            + (existingChannel != null ? existingChannel.getAsMention() : "null"));
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "audit-channel";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Sets the audit log channel for the server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(
                                OptionType.CHANNEL, "channel", "The channel to set as the audit log channel", true)
                        .setChannelTypes(EnumSet.of(ChannelType.TEXT)));
    }

    @Override
    public EnumSet<Permission> getRequiredPermissions() {
        return EnumSet.of(Permission.MANAGE_SERVER);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
