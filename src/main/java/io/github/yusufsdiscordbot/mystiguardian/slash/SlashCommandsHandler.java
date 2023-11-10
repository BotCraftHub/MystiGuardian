package io.github.yusufsdiscordbot.mystiguardian.slash;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.jConfig;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

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
            logger.warn(STR. "Slash command \{ slashCommand.getName() } already exists" );
            return;
        }

        slashCommands.put(slashCommand.getName(), slashCommand);

        if (!slashCommand.isGlobal()) {
            val slash = SlashCommand.with(slashCommand.getName(), slashCommand.getDescription(), slashCommand.getOptions())
                    .setEnabledInDms(false);

            if (slashCommand.getRequiredPermissions() != null) {
                slash.setDefaultEnabledForPermissions(slashCommand.getRequiredPermissions());
            }

            registeredSlashCommands.add(slash);
        } else {
            val slash = SlashCommand.with(slashCommand.getName(), slashCommand.getDescription(), slashCommand.getOptions());

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
        registeredSlashCommands.forEach(slashCommandBuilder -> slashCommandBuilder.createGlobal(api).join());
    }

    public void onSlashCommandCreateEvent(@NotNull SlashCommandCreateEvent event) {
        val name = event.getSlashCommandInteraction().getCommandName();

        if (!slashCommands.containsKey(name)) {
            logger.warn(STR. "Slash command \{ name } does not exist" );
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
                event.getSlashCommandInteraction().createImmediateResponder().setContent("You are not the owner of this bot, you cannot use this command")
                        .respond();
                return;
            }
        }

        slashCommand.onSlashCommandInteractionEvent(event.getSlashCommandInteraction(), new MystiGuardianUtils.ReplyUtils(event.getSlashCommandInteraction().createImmediateResponder()));
        slashCommand.onSlashCommandInteractionEvent(event.getSlashCommandInteraction());
    }
}
