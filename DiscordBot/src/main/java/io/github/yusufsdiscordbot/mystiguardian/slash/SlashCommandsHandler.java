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
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.jetbrains.annotations.NotNull;

public class SlashCommandsHandler {
    private final Map<String, ISlashCommand> slashCommands = new HashMap<>();
    private final List<SlashCommandBuilder> registeredSlashCommands = new ArrayList<>();
    private final DiscordApi api;

    public SlashCommandsHandler(DiscordApi api) {
        this.api = api;
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
            val slash = SlashCommand.with(
                            slashCommand.getName(), slashCommand.getDescription(), slashCommand.getOptions())
                    .setEnabledInDms(false);

            if (slashCommand.getRequiredPermissions() != null) {
                slash.setDefaultEnabledForPermissions(slashCommand.getRequiredPermissions());
            }

            registeredSlashCommands.add(slash);
        } else {
            val slash =
                    SlashCommand.with(slashCommand.getName(), slashCommand.getDescription(), slashCommand.getOptions());

            if (slashCommand.getRequiredPermissions() != null) {
                slash.setDefaultEnabledForPermissions(slashCommand.getRequiredPermissions());
            }

            registeredSlashCommands.add(slash);
        }
    }

    protected void registerSlashCommands(@NotNull List<ISlashCommand> slashCommands) {
        slashCommands.forEach(this::addSlashCommand);
    }

    protected void sendSlash() {
        registeredSlashCommands.forEach(
                slashCommandBuilder -> slashCommandBuilder.createGlobal(api).join());

        deleteOutdatedSlashCommands();
    }

    private void deleteOutdatedSlashCommands() {
        val slashCommands = api.getGlobalSlashCommands().join();

        slashCommands.forEach(slashCommand -> {
            // check if the name is in the list
            if (!this.slashCommands.containsKey(slashCommand.getName())) {
                logger.info(
                        MystiGuardianUtils.formatString("Deleting outdated slash command %s", slashCommand.getName()));

                slashCommand.delete().join();
            }
        });
    }

    public void onSlashCommandCreateEvent(@NotNull SlashCommandCreateEvent event) {
        val name = event.getSlashCommandInteraction().getCommandName();

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

            if (!event.getSlashCommandInteraction().getUser().getIdAsString().equals(ownerId.asText())) {
                event.getSlashCommandInteraction()
                        .createImmediateResponder()
                        .setContent("You are not the owner of this bot, you cannot use this command")
                        .respond();
                return;
            }
        }

        slashCommand.onSlashCommandInteractionEvent(
                event.getSlashCommandInteraction(),
                new MystiGuardianUtils.ReplyUtils(
                        event.getSlashCommandInteraction().createImmediateResponder()),
                new PermChecker(event.getSlashCommandInteraction()));
    }
}
