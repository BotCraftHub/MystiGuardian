package io.github.yusufsdiscordbot.mystigurdian.slash;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandsHandler {
    private final List<SlashCommandBuilder> registeredSlashCommands = new ArrayList<>();
    private final DiscordApi api;

    public SlashCommandsHandler(DiscordApi api) {
        this.api = api;
    }

    private void addSlashCommand(ISlashCommand slashCommand) {
       if (slashCommand.getName().isBlank()) {
           throw new IllegalArgumentException("Slash command name cannot be blank");
       }

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
}
