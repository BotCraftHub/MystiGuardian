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
package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous.file;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@Slf4j
@SlashEventBus
public class ListFilesCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        event.deferReply().queue();

        if (event.getGuild() == null) {
            replyUtils.sendError("This command can only be used in a server!");
            return;
        }

        String guildId = event.getGuild().getId();

        try {
            var files = MystiGuardianDatabaseHandler.StoredFiles.getAllFiles(guildId);

            if (files.isEmpty()) {
                val embed =
                        new EmbedBuilder()
                                .setTitle("📁 Stored Files")
                                .setDescription(
                                        "No files have been uploaded yet. Use `/uploadfile` to upload files.")
                                .setColor(MystiGuardianUtils.getBotColor())
                                .setFooter(
                                        "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            val embed =
                    new EmbedBuilder()
                            .setTitle("📁 Stored Files")
                            .setDescription(String.format("Total files: %d", files.size()))
                            .setColor(MystiGuardianUtils.getBotColor());

            // Add up to 25 fields (Discord limit)
            int count = 0;
            for (var file : files) {
                if (count >= 25) {
                    embed.addField(
                            "⚠️ More Files Available",
                            "There are more files than can be displayed. Use `/getfile` with a specific name.",
                            false);
                    break;
                }

                StringBuilder fieldValue = new StringBuilder();
                fieldValue.append(String.format("**Type:** %s\n", file.getFileType()));
                fieldValue.append(
                        String.format(
                                "**Uploaded:** %s\n",
                                file.getUploadedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

                if (file.getDescription() != null && !file.getDescription().isEmpty()) {
                    String desc = file.getDescription();
                    if (desc.length() > 100) {
                        desc = desc.substring(0, 97) + "...";
                    }
                    fieldValue.append(String.format("**Description:** %s\n", desc));
                }

                fieldValue.append(String.format("Use `/getfile name:%s` to retrieve", file.getFileName()));

                embed.addField("📄 " + file.getFileName(), fieldValue.toString(), false);
                count++;
            }

            embed.setFooter("Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();

            logger.info("File list retrieved by {} in guild {}", event.getUser().getId(), guildId);

        } catch (Exception e) {
            logger.error("Error listing files: {}", e.getMessage(), e);
            val embed =
                    new EmbedBuilder()
                            .setTitle("❌ Error")
                            .setDescription(
                                    "An error occurred while retrieving the file list. Please try again later.")
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setFooter(
                                    "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "listfiles";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "List all stored files in this server";
    }
}
