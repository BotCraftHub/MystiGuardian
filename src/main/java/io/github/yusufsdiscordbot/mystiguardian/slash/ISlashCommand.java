package io.github.yusufsdiscordbot.mystiguardian.slash;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public interface ISlashCommand {

    default void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
    }

    default void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
    }

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
