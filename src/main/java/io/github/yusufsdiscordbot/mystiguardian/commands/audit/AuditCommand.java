package io.github.yusufsdiscordbot.mystiguardian.commands.audit;

import io.github.yusufsdiscordbot.mystiguardian.audit.type.*;
import io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.*;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("unused")
public class AuditCommand implements ISlashCommand {
    private static final String RELOAD_AUDIT_OPTION_NAME = "reload-audit";
    public static final String WARN_AUDIT_OPTION_NAME = "warn-audit";
    public static final String KICK_AUDIT_OPTION_NAME = "kick-audit";
    public static final String BAN_AUDIT_OPTION_NAME = "ban-audit";
    public static final String TIME_OUT_AUDIT_OPTION_NAME = "time-out-audit";
    public static final String AMOUNT_AUDIT_OPTION_NAME = "amount-audit";

    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        if (event.getOptionByName(RELOAD_AUDIT_OPTION_NAME).isPresent()) {
            new ReloadAuditCommand().onSlashCommandInteractionEvent(event);
        } else if (event.getOptionByName(WARN_AUDIT_OPTION_NAME).isPresent()) {
            new WarnAuditCommand().onSlashCommandInteractionEvent(event);
        } else if (event.getOptionByName(KICK_AUDIT_OPTION_NAME).isPresent()) {
            new KickAuditCommand().onSlashCommandInteractionEvent(event);
        } else if (event.getOptionByName(BAN_AUDIT_OPTION_NAME).isPresent()) {
            new BanAuditCommand().onSlashCommandInteractionEvent(event);
        } else if (event.getOptionByName(TIME_OUT_AUDIT_OPTION_NAME).isPresent()) {
            new TimeOutAuditCommand().onSlashCommandInteractionEvent(event);
        } else if (event.getOptionByName(AMOUNT_AUDIT_OPTION_NAME).isPresent()) {
            new AmountAuditCommand().onSlashCommandInteractionEvent(event);
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
    public boolean isGlobal() {
        return false;
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createSubcommand(RELOAD_AUDIT_OPTION_NAME, "Get information about the bot's reload audit logs."),
                SlashCommandOption.createSubcommand(WARN_AUDIT_OPTION_NAME, "Get information about the bot's warn audit logs.", List.of(
                        SlashCommandOption.createUserOption("user", "The user to get warn audit logs for.", true)
                )),
                SlashCommandOption.createSubcommand(KICK_AUDIT_OPTION_NAME, "Get information about the bot's kick audit logs.", List.of(
                        SlashCommandOption.createUserOption("user", "The user to get kick audit logs for.", true)
                )),
                SlashCommandOption.createSubcommand(BAN_AUDIT_OPTION_NAME, "Get information about the bot's ban audit logs.", List.of(
                        SlashCommandOption.createUserOption("user", "The user to get ban audit logs for.", true)
                )),
                SlashCommandOption.createSubcommand(TIME_OUT_AUDIT_OPTION_NAME, "Get information about the bot's time out audit logs.", List.of(
                        SlashCommandOption.createUserOption("user", "The user to get time out audit logs for.", true)
                )),
                SlashCommandOption.createSubcommand(AMOUNT_AUDIT_OPTION_NAME, "Get information about the bot's amount audit logs for a certain moderartion.", List.of(
                        SlashCommandOption.createUserOption("user", "The user to get amount audit logs for.", true),
                        new SlashCommandOptionBuilder().setType(SlashCommandOptionType.STRING).setName("moderation-type").setDescription("The moderation type to get amount audit logs for.")
                                .setRequired(true).addChoice("warn", "warn").addChoice("kick", "kick")
                                .addChoice("ban", "ban").addChoice("time-out", "time-out").build()
                ))
        );
    }
}
