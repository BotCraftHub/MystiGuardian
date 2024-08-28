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

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

public class SlashCommandsHandler {
    private final Map<String, ISlashCommand> slashCommands = new HashMap<>();
    private final List<SlashCommandData> registeredSlashCommands = new ArrayList<>();
    private final JDA api;

    public SlashCommandsHandler(JDA api) {
        this.api = api;
    }

    private void addSlashCommand(ISlashCommand slashCommand) {
        if (slashCommand.getName().isBlank()) {
            throw new IllegalArgumentException("Slash command name cannot be blank");
        }

        if (slashCommands.containsKey(slashCommand.getName())) {
            logger.warn(
                    MystiGuardianUtils.formatString(
                            "Slash command %s already exists", slashCommand.getName()));
            return;
        }
        slashCommands.put(slashCommand.getName(), slashCommand);

        if (!slashCommand.isGlobal()) {

            val slash =
                    new CommandDataImpl(slashCommand.getName(), slashCommand.getDescription())
                            .setGuildOnly(true)
                            .addOptions(slashCommand.getOptions())
                            .addSubcommands(slashCommand.getSubcommands());

            if (slashCommand.getRequiredPermissions() != null) {
                slash.setDefaultPermissions(
                        DefaultMemberPermissions.enabledFor(slashCommand.getRequiredPermissions()));
            }

            registeredSlashCommands.add(slash);
        } else {
            val slash =
                    new CommandDataImpl(slashCommand.getName(), slashCommand.getDescription())
                            .addOptions(slashCommand.getOptions())
                            .addSubcommands(slashCommand.getSubcommands());

            if (slashCommand.getRequiredPermissions() != null) {
                slash.setDefaultPermissions(
                        DefaultMemberPermissions.enabledFor(slashCommand.getRequiredPermissions()));
            }

            registeredSlashCommands.add(slash);
        }
    }

    protected void registerSlashCommands(@NotNull List<ISlashCommand> slashCommands) {
        slashCommands.forEach(this::addSlashCommand);
    }

    protected void sendSlash() {
        api.updateCommands().addCommands(registeredSlashCommands).queue();

        deleteOutdatedSlashCommands();
    }

    private void deleteOutdatedSlashCommands() {
        val slashCommands =
                api.retrieveCommands().complete().stream()
                        .filter(command -> command.getType().equals(Command.Type.SLASH))
                        .toList();

        slashCommands.forEach(
                slashCommand -> {
                    if (!this.slashCommands.containsKey(slashCommand.getName())) {
                        logger.info(
                                MystiGuardianUtils.formatString(
                                        "Deleting outdated slash command %s", slashCommand.getName()));

                        slashCommand.delete().queue();
                    }
                });
    }

    public void onSlashCommandCreateEvent(@NotNull SlashCommandInteractionEvent event) {
        val name = event.getName();

        if (!slashCommands.containsKey(name)) {
            logger.warn(MystiGuardianUtils.formatString("Slash command %s does not exist", name));
            return;
        }

        val slashCommand = slashCommands.get(name);

        if (slashCommand.isOwnerOnly()) {
            if (!event.getUser().getId().equals(MystiGuardianUtils.getMainConfig().ownerId())) {
                event.reply("You are not the owner of this bot, you cannot use this command").queue();
                return;
            }
        }

        slashCommand.onSlashCommandInteractionEvent(
                event, new MystiGuardianUtils.ReplyUtils(event), new PermChecker(event));
    }
}
