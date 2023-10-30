package io.github.yusufsdiscordbot.mystigurdian.slash;

import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface ISlashCommand {

    void onSlashCommandInteractionEvent(SlashCommandInteraction event);

    @NotNull
    String getName();

    @NotNull
    String getDescription();

    default List<SlashCommandOption> getOptions() {
        return Collections.emptyList();
    }

    default boolean isGlobal() {
        return true;
    }
}
