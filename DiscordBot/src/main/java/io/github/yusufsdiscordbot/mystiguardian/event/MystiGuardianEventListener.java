package io.github.yusufsdiscordbot.mystiguardian.event;

import io.github.yusufsdiscordbot.mystiguardian.button.ButtonClickHandler;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MystiGuardianEventListener extends ListenerAdapter {
    private final SlashCommandsHandler slashCommandsHandler;

    public MystiGuardianEventListener(SlashCommandsHandler slashCommandsHandler) {
        this.slashCommandsHandler = slashCommandsHandler;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        slashCommandsHandler.onSlashCommandCreateEvent(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        new ButtonClickHandler(event);
    }
}