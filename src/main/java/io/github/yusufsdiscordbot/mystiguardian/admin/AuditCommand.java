package io.github.yusufsdiscordbot.mystiguardian.admin;

import io.github.yusufsdiscordbot.mystiguardian.admin.audit.ReloadAuditCommand;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("unused")
public class AuditCommand implements ISlashCommand {
    private final static String RELOAD_AUDIT_COMMAND_NAME = "reload-audit";
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        if (event.getOptionByName(RELOAD_AUDIT_COMMAND_NAME).isPresent()) {
            new ReloadAuditCommand().onSlashCommandInteractionEvent(event);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "audit";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get information about the bot's audit logs for thing such as bans, kicks, and reloads.";
    }

    @Override
    public EnumSet<PermissionType> getRequiredPermissions() {
        return EnumSet.of(PermissionType.ADMINISTRATOR);
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createSubcommand(RELOAD_AUDIT_COMMAND_NAME, "Get information about the bot's reload audit logs.")
        );
    }
}
