package io.github.yusufsdiscordbot.mystiguardian.slash;

import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

import java.security.Permission;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public interface ISlashCommand {

    void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event);

    @NotNull
    String getName();

    @NotNull
    String getDescription();

    default List<SlashCommandOption> getOptions() {
        return Collections.emptyList();
    }

    default EnumSet<PermissionType> getRequiredPermissions() {
        return null;
    }

    default boolean isGlobal() {
        return true;
    }

    default boolean isOwnerOnly() {
        return false;
    }
}
