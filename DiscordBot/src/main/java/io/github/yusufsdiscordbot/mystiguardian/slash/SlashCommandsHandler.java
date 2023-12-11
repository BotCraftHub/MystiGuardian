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
package io.github.yusufsdiscordbot.mystiguardian.slash;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.jConfig;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public class SlashCommandsHandler {
    private final Map<String, ISlashCommand> slashCommands = new HashMap<>();
    private final List<SlashCommandData> registeredSlashCommands = new ArrayList<SlashCommandData>();
    private final JDA jda;

    public SlashCommandsHandler(JDA jda) {
        this.jda = jda;
    }

    private void addSlashCommand(ISlashCommand slashCommand) {
        if (slashCommand.getName().isBlank()) {
            throw new IllegalArgumentException("Slash command name cannot be blank");
        }

        if (slashCommands.containsKey(slashCommand.getName())) {
            logger.warn(MystiGuardianUtils.formatString("Slash command %s already exists", slashCommand.getName()));
            return;
        }
        slashCommands.put(slashCommand.getName(), slashCommand);

        if (!slashCommand.isGlobal()) {
            val slash = Commands.slash(
                            slashCommand.getName(), slashCommand.getDescription())
                    .addOptions(slashCommand.getOptions())
                    .setGuildOnly(true);

            if (slashCommand.getRequiredPermissions() != null) {
                slash.setDefaultPermissions(slashCommand.getRequiredPermissions());
            }

            registeredSlashCommands.add(slash);
        } else {
            SlashCommandData slash =
                    Commands.slash(slashCommand.getName(), slashCommand.getDescription())
                            .addOptions(slashCommand.getOptions());

            if (slashCommand.getRequiredPermissions() != null) {
                slash.setDefaultPermissions(slashCommand.getRequiredPermissions());
            }

            registeredSlashCommands.add(slash);
        }
    }

    protected void registerSlashCommands(@NotNull List<ISlashCommand> slashCommands) {
        slashCommands.forEach(this::addSlashCommand);
    }

    protected void sendSlash() {
        registeredSlashCommands.forEach(
                slashCommandBuilder -> jda.upsertCommand(slashCommandBuilder).queue());
    }

    public void onSlashCommandCreateEvent(@NotNull SlashCommandInteractionEvent event) {
        val name = event.getName();

        if (!slashCommands.containsKey(name)) {
            logger.warn(MystiGuardianUtils.formatString("Slash command %s does not exist", name));
            return;
        }

        val slashCommand = slashCommands.get(name);

        if (slashCommand.isOwnerOnly()) {
            val ownerId = jConfig.get("owner-id");

            if (ownerId == null) {
                logger.error("Owner id is null, exiting...");
                return;
            }

            if (!event.getUser().getId().equals(ownerId.asText())) {
                event.reply("You are not the owner of this bot").setEphemeral(true).queue();
                return;
            }
        }

        slashCommand.onSlashCommandInteractionEvent(
                event,
                new MystiGuardianUtils.ReplyUtils(
                        event));
    }
}
