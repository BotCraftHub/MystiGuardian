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
package io.github.yusufsdiscordbot.mystiguardian.commands.admin.file;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@Slf4j
@SlashEventBus
public class DeleteFileCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        event.deferReply().queue();

        OptionMapping nameOption = event.getOption("name");
        if (nameOption == null) {
            replyUtils.sendError("Missing file name!");
            return;
        }

        String fileName = nameOption.getAsString();

        if (event.getGuild() == null) {
            replyUtils.sendError("This command can only be used in a server!");
            return;
        }

        String guildId = event.getGuild().getId();

        // Check if user has permission (either uploader or admin)
        var file = MystiGuardianDatabaseHandler.StoredFiles.getFile(guildId, fileName);

        if (file == null) {
            val embed =
                    new EmbedBuilder()
                            .setTitle("❌ File Not Found")
                            .setDescription(String.format("No file found with the name `%s`.", fileName))
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setFooter(
                                    "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        // Check if user is the uploader or has manage messages permission
        boolean canDelete =
                file.getUploadedBy().equals(event.getUser().getId())
                        || (event.getMember() != null
                                && event.getMember().hasPermission(Permission.MESSAGE_MANAGE));

        if (!canDelete) {
            val embed =
                    new EmbedBuilder()
                            .setTitle("❌ Permission Denied")
                            .setDescription(
                                    "You can only delete files that you uploaded, unless you have the Manage Messages permission.")
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setFooter(
                                    "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        try {
            boolean deleted = MystiGuardianDatabaseHandler.StoredFiles.deleteFile(guildId, fileName);

            if (deleted) {
                val embed =
                        new EmbedBuilder()
                                .setTitle("✅ File Deleted")
                                .setDescription(String.format("File `%s` has been successfully deleted.", fileName))
                                .setColor(MystiGuardianUtils.getBotColor())
                                .setFooter(
                                        "Deleted by " + event.getUser().getName(), event.getUser().getAvatarUrl());

                event.getHook().sendMessageEmbeds(embed.build()).queue();

                logger.info(
                        "File '{}' deleted by {} in guild {}", fileName, event.getUser().getId(), guildId);
            } else {
                val embed =
                        new EmbedBuilder()
                                .setTitle("❌ Deletion Failed")
                                .setDescription("The file could not be deleted. It may have already been removed.")
                                .setColor(MystiGuardianUtils.getBotColor())
                                .setFooter(
                                        "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

                event.getHook().sendMessageEmbeds(embed.build()).queue();
            }

        } catch (Exception e) {
            logger.error("Error deleting file: {}", e.getMessage(), e);
            val embed =
                    new EmbedBuilder()
                            .setTitle("❌ Error")
                            .setDescription("An error occurred while deleting the file. Please try again later.")
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setFooter(
                                    "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "deletefile";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Delete a stored file (you must be the uploader or have Manage Messages permission)";
    }

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "name", "The name of the file to delete", true));
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
