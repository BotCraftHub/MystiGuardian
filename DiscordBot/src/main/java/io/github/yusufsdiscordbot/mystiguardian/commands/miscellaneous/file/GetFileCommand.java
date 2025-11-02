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
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@Slf4j
@SlashEventBus
public class GetFileCommand implements ISlashCommand {

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


            replyUtils.sendError("This command can only be used in a server!");
            var file = MystiGuardianDatabaseHandler.StoredFiles.getFile(guildId, fileName);
        }

        String guildId = event.getGuild().getId();

        try {
            StoredFilesRecord file = MystiGuardianDatabaseHandler.StoredFiles.getFile(guildId, fileName);

            if (file == null) {
                val embed = new EmbedBuilder()
                        .setTitle("❌ File Not Found")
                        .setDescription(String.format("No file found with the name `%s`. Use `/listfiles` to see all available files.", fileName))
                        .setColor(MystiGuardianUtils.getBotColor())
                        .setFooter("Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

                event.getHook().sendMessageEmbeds(embed.build()).queue();
                return;
            }

            val embed = new EmbedBuilder()
                    .setTitle("📄 " + fileName)
                    .addField("File Type", file.getFileType(), true)
                    .addField("Uploaded By", String.format("<@%s>", file.getUploadedBy()), true)
                    .addField("Uploaded At",
                        file.getUploadedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        true);

            if (file.getDescription() != null && !file.getDescription().isEmpty()) {
                embed.addField("Description", file.getDescription(), false);
            }

            embed.addField("Download", String.format("[Click here to download](%s)", file.getFileUrl()), false)
                    .setColor(MystiGuardianUtils.getBotColor())
                    .setFooter("Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();

            logger.info("File '{}' retrieved by {} in guild {}", fileName, event.getUser().getId(), guildId);

        } catch (Exception e) {
            logger.error("Error retrieving file: {}", e.getMessage(), e);
            val embed = new EmbedBuilder()
                    .setTitle("❌ Error")
                    .setDescription("An error occurred while retrieving the file. Please try again later.")
                    .setColor(MystiGuardianUtils.getBotColor())
                    .setFooter("Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "getfile";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Retrieve a stored file by its name";
    }

    @Override
    public @NotNull List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "name", "The name of the file to retrieve", true)
        );
    }
}

