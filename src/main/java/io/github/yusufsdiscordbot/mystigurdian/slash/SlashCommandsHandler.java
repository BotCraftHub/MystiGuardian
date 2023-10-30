package io.github.yusufsdiscordbot.mystigurdian.slash;

import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.yusufsdiscordbot.mystigurdian.utils.MystiGurdianUtils.logger;

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
           logger.warn(STR."Slash command \{slashCommand.getName()} already exists");
           return;
       }

       slashCommands.put(slashCommand.getName(), slashCommand);

       if (!slashCommand.isGlobal()) {
           registeredSlashCommands.add(
               SlashCommand.with(slashCommand.getName(), slashCommand.getDescription(), slashCommand.getOptions())
                       .setEnabledInDms(false)
           );
       } else {
           registeredSlashCommands.add(
               SlashCommand.with(slashCommand.getName(), slashCommand.getDescription(), slashCommand.getOptions())
           );
       }
    }

    protected void registerSlashCommands(List<ISlashCommand> slashCommands) {
        slashCommands.forEach(this::addSlashCommand);
    }

    protected void sendSlash() {
        registeredSlashCommands.forEach(slashCommandBuilder -> slashCommandBuilder.createGlobal(api).join());
    }

    public void onSlashCommandCreateEvent(SlashCommandCreateEvent event) {
        val name = event.getSlashCommandInteraction().getCommandName();

        if (!slashCommands.containsKey(name)) {
            logger.warn(STR."Slash command \{name} does not exist");
            return;
        }

        val slashCommand = slashCommands.get(name);

        slashCommand.onSlashCommandInteractionEvent(event.getSlashCommandInteraction());
    }
}
