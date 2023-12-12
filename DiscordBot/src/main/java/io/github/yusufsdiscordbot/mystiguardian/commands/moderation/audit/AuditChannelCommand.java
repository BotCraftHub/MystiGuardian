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
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.EnumSet;
import java.util.List;
import lombok.val;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class AuditChannelCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        val channel = event.getOptionByName("channel")
                .flatMap(SlashCommandInteractionOption::getChannelValue)
                .orElse(null);

        if (channel == null) {
            replyUtils.sendError("You must specify a channel");
            return;
        }

        val server = event.getServer().orElse(null);

        if (server == null) {
            replyUtils.sendError("You must be in a server to use this command");
            return;
        }

        val auditChannel = server.getTextChannelById(channel.getId()).orElse(null);

        if (auditChannel == null) {
            replyUtils.sendError("The channel you specified is not in this server");
            return;
        }

        val sucess = MystiGuardianDatabaseHandler.AuditChannel.setAuditChannelRecord(
                server.getIdAsString(), auditChannel.getIdAsString());

        if (sucess) {
            replyUtils.sendSuccess("Successfully set the audit log channel to " + auditChannel.getMentionTag());
        } else {
            replyUtils.sendError("The audit channel has already been set to "
                    + server.getChannelById(MystiGuardianDatabaseHandler.AuditChannel.getAuditChannelRecord(
                                    server.getIdAsString()))
                            .map(channel1 -> channel1.asServerTextChannel()
                                    .map(ServerTextChannel::getMentionTag)
                                    .orElse("null"))
                            .orElse("null"));
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
    public List<SlashCommandOption> getOptions() {
        return List.of(SlashCommandOption.createChannelOption(
                "channel",
                "The channel to set as the audit log channel",
                true,
                List.of(ChannelType.SERVER_TEXT_CHANNEL)));
    }

    @Override
    public EnumSet<PermissionType> getRequiredPermissions() {
        return EnumSet.of(PermissionType.MANAGE_SERVER);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
