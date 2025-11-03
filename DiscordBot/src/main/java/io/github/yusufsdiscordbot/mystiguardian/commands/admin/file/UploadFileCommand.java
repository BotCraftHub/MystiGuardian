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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@Slf4j
@SlashEventBus
public class UploadFileCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        event.deferReply().queue();

        OptionMapping nameOption = event.getOption("name");
        OptionMapping fileOption = event.getOption("file");
        OptionMapping descOption = event.getOption("description");

        if (nameOption == null || fileOption == null) {
            replyUtils.sendError("Missing required options!");
            return;
        }

        String fileName = nameOption.getAsString();
        Message.Attachment attachment = fileOption.getAsAttachment();
        String description = descOption != null ? descOption.getAsString() : null;

        if (event.getGuild() == null) {
            replyUtils.sendError("This command can only be used in a server!");
            return;
        }

        String guildId = event.getGuild().getId();

        // Check if file with this name already exists
        if (MystiGuardianDatabaseHandler.StoredFiles.fileExists(guildId, fileName)) {
            val embed =
                    new EmbedBuilder()
                            .setTitle("❌ File Already Exists")
                            .setDescription(
                                    String.format(
                                            "A file with the name `%s` already exists in this server. Please use a different name or delete the existing file first.",
                                            fileName))
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setFooter(
                                    "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        // Validate file size (8MB Discord limit for normal uploads)
        if (attachment.getSize() > 8 * 1024 * 1024) {
            val embed =
                    new EmbedBuilder()
                            .setTitle("❌ File Too Large")
                            .setDescription("The file is too large! Maximum file size is 8MB.")
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setFooter(
                                    "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
            return;
        }

        try {
            // The Discord CDN URL is permanent and can be stored directly
            String fileUrl = attachment.getUrl();
            String fileType = attachment.getFileExtension();
            if (fileType == null) {
                fileType = "unknown";
            }

            // Store in database
            MystiGuardianDatabaseHandler.StoredFiles.storeFile(
                    guildId, fileName, fileType, description, fileUrl, event.getUser().getId());

            val embed =
                    new EmbedBuilder()
                            .setTitle("✅ File Uploaded Successfully")
                            .setDescription(String.format("File `%s` has been uploaded and stored.", fileName))
                            .addField("File Name", fileName, true)
                            .addField("File Type", fileType, true)
                            .addField("File Size", String.format("%.2f KB", attachment.getSize() / 1024.0), true);

            if (description != null) {
                embed.addField("Description", description, false);
            }

            embed
                    .setColor(MystiGuardianUtils.getBotColor())
                    .setFooter("Uploaded by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();

            logger.info(
                    "File '{}' uploaded by {} in guild {}", fileName, event.getUser().getId(), guildId);

        } catch (Exception e) {
            logger.error("Error uploading file: {}", e.getMessage(), e);
            val embed =
                    new EmbedBuilder()
                            .setTitle("❌ Upload Failed")
                            .setDescription("An error occurred while uploading the file. Please try again later.")
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setFooter(
                                    "Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "uploadfile";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Upload and store a file (like tips, documents, etc.) for later retrieval";
    }

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "name", "A unique name to identify this file", true),
                new OptionData(OptionType.ATTACHMENT, "file", "The file to upload", true),
                new OptionData(
                        OptionType.STRING, "description", "Optional description of the file", false));
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
