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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit;

import io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.type.*;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.EnumSet;
import java.util.List;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class AuditCommand implements ISlashCommand {
    public static final String WARN_AUDIT_OPTION_NAME = "warn-audit";
    public static final String WARN_BY_ID_AUDIT_OPTION_NAME = "warn-by-id-audit";
    public static final String KICK_AUDIT_OPTION_NAME = "kick-audit";
    public static final String BAN_AUDIT_OPTION_NAME = "ban-audit";
    public static final String TIME_OUT_AUDIT_OPTION_NAME = "time-out-audit";
    public static final String AMOUNT_AUDIT_OPTION_NAME = "amount-audit";
    private static final String RELOAD_AUDIT_OPTION_NAME = "reload-audit";
    public static final String SOFT_BAN_AUDIT_OPTION_NAME = "soft-ban-audit";

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
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
            new AmountAuditCommand().onSlashCommandInteractionEvent(event, replyUtils, permChecker);
        } else if (event.getOptionByName(WARN_BY_ID_AUDIT_OPTION_NAME).isPresent()) {
            new WarnByIdAuditCommand().onSlashCommandInteractionEvent(event, replyUtils, permChecker);
        } else if (event.getOptionByName(SOFT_BAN_AUDIT_OPTION_NAME).isPresent()) {
            new SoftBanAuditCommand().onSlashCommandInteractionEvent(event);
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
        return EnumSet.of(PermissionType.VIEW_AUDIT_LOG);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createSubcommand(
                        RELOAD_AUDIT_OPTION_NAME, "Get information about the bot's reload audit logs."),
                SlashCommandOption.createSubcommand(
                        WARN_AUDIT_OPTION_NAME,
                        "Get information about the bot's warn audit logs.",
                        List.of(SlashCommandOption.createUserOption(
                                "user", "The user to get warn audit logs for.", true))),
                SlashCommandOption.createSubcommand(
                        KICK_AUDIT_OPTION_NAME,
                        "Get information about the bot's kick audit logs.",
                        List.of(SlashCommandOption.createUserOption(
                                "user", "The user to get kick audit logs for.", true))),
                SlashCommandOption.createSubcommand(
                        BAN_AUDIT_OPTION_NAME,
                        "Get information about the bot's ban audit logs.",
                        List.of(SlashCommandOption.createUserOption(
                                "user", "The user to get ban audit logs for.", true))),
                SlashCommandOption.createSubcommand(
                        TIME_OUT_AUDIT_OPTION_NAME,
                        "Get information about the bot's time out audit logs.",
                        List.of(SlashCommandOption.createUserOption(
                                "user", "The user to get time out audit logs for.", true))),
                SlashCommandOption.createSubcommand(
                        AMOUNT_AUDIT_OPTION_NAME,
                        "Get information about the bot's amount audit logs for a certain moderartion.",
                        List.of(
                                SlashCommandOption.createUserOption(
                                        "user", "The user to get amount audit logs for.", true),
                                new SlashCommandOptionBuilder()
                                        .setType(SlashCommandOptionType.STRING)
                                        .setName("moderation-type")
                                        .setDescription("The moderation type to get amount audit logs for.")
                                        .setRequired(true)
                                        .addChoice(
                                                MystiGuardianUtils.ModerationTypes.WARN.name(),
                                                MystiGuardianUtils.ModerationTypes.WARN.name())
                                        .addChoice(
                                                MystiGuardianUtils.ModerationTypes.KICK.name(),
                                                MystiGuardianUtils.ModerationTypes.KICK.name())
                                        .addChoice(
                                                MystiGuardianUtils.ModerationTypes.BAN.name(),
                                                MystiGuardianUtils.ModerationTypes.BAN.name())
                                        .addChoice(
                                                MystiGuardianUtils.ModerationTypes.TIME_OUT.name(),
                                                MystiGuardianUtils.ModerationTypes.TIME_OUT.name())
                                        .build())),
                SlashCommandOption.createSubcommand(
                        WARN_BY_ID_AUDIT_OPTION_NAME,
                        "Get information about the bot's warn audit logs by warn id.",
                        List.of(new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.STRING)
                                .setName("warn-id")
                                .setDescription("The warn id to get warn audit logs for.")
                                .setRequired(true)
                                .build())),
                SlashCommandOption.createSubcommand(
                        SOFT_BAN_AUDIT_OPTION_NAME,
                        "Get information about the bot's soft ban audit logs.",
                        List.of(SlashCommandOption.createUserOption(
                                "user", "The user to get soft ban audit logs for.", true))));
    }
}
