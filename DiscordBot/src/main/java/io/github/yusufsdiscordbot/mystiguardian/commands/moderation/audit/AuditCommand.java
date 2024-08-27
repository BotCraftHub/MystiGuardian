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
import java.util.Map;
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

    @FunctionalInterface
    interface IAuditCommand {
        void onSlashCommandInteractionEvent(SlashCommandInteraction event);
    }

    @FunctionalInterface
    interface ParameterizedAuditCommand {
        void onSlashCommandInteractionEvent(
                SlashCommandInteraction event,
                MystiGuardianUtils.ReplyUtils replyUtils,
                PermChecker permChecker);
    }

    private static final Map<String, IAuditCommand> commandMap =
            Map.of(
                    RELOAD_AUDIT_OPTION_NAME,
                            event -> new ReloadAuditCommand().onSlashCommandInteractionEvent(event),
                    WARN_AUDIT_OPTION_NAME,
                            event -> new WarnAuditCommand().onSlashCommandInteractionEvent(event),
                    KICK_AUDIT_OPTION_NAME,
                            event -> new KickAuditCommand().onSlashCommandInteractionEvent(event),
                    BAN_AUDIT_OPTION_NAME,
                            event -> new BanAuditCommand().onSlashCommandInteractionEvent(event),
                    TIME_OUT_AUDIT_OPTION_NAME,
                            event -> new TimeOutAuditCommand().onSlashCommandInteractionEvent(event),
                    SOFT_BAN_AUDIT_OPTION_NAME,
                            event -> new SoftBanAuditCommand().onSlashCommandInteractionEvent(event));

    private static final Map<String, ParameterizedAuditCommand> parameterizedCommandMap =
            Map.of(
                    AMOUNT_AUDIT_OPTION_NAME,
                            (event, replyUtils, permChecker) ->
                                    new AmountAuditCommand()
                                            .onSlashCommandInteractionEvent(event, replyUtils, permChecker),
                    WARN_BY_ID_AUDIT_OPTION_NAME,
                            (event, replyUtils, permChecker) ->
                                    new WarnByIdAuditCommand()
                                            .onSlashCommandInteractionEvent(event, replyUtils, permChecker));

    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        commandMap.entrySet().stream()
                .filter(entry -> event.getOptionByName(entry.getKey()).isPresent())
                .findFirst()
                .ifPresent(entry -> entry.getValue().onSlashCommandInteractionEvent(event));

        parameterizedCommandMap.entrySet().stream()
                .filter(entry -> event.getOptionByName(entry.getKey()).isPresent())
                .findFirst()
                .ifPresent(
                        entry ->
                                entry.getValue().onSlashCommandInteractionEvent(event, replyUtils, permChecker));
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
                createSimpleSubcommand(
                        RELOAD_AUDIT_OPTION_NAME, "Get information about the bot's reload audit logs."),
                createUserAuditSubcommand(
                        WARN_AUDIT_OPTION_NAME, "Get information about the bot's warn audit logs."),
                createUserAuditSubcommand(
                        KICK_AUDIT_OPTION_NAME, "Get information about the bot's kick audit logs."),
                createUserAuditSubcommand(
                        BAN_AUDIT_OPTION_NAME, "Get information about the bot's ban audit logs."),
                createUserAuditSubcommand(
                        TIME_OUT_AUDIT_OPTION_NAME, "Get information about the bot's time out audit logs."),
                createAmountAuditSubcommand(),
                createWarnByIdAuditSubcommand(),
                createUserAuditSubcommand(
                        SOFT_BAN_AUDIT_OPTION_NAME, "Get information about the bot's soft ban audit logs."));
    }

    private SlashCommandOption createSimpleSubcommand(String name, String description) {
        return SlashCommandOption.createSubcommand(name, description);
    }

    private SlashCommandOption createUserAuditSubcommand(String name, String description) {
        return SlashCommandOption.createSubcommand(
                name,
                description,
                List.of(
                        SlashCommandOption.createUserOption("user", "The user to get audit logs for.", true)));
    }

    private SlashCommandOption createAmountAuditSubcommand() {
        return SlashCommandOption.createSubcommand(
                AMOUNT_AUDIT_OPTION_NAME,
                "Get information about the bot's amount audit logs for a certain moderation.",
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
                                .build()));
    }

    private SlashCommandOption createWarnByIdAuditSubcommand() {
        return SlashCommandOption.createSubcommand(
                WARN_BY_ID_AUDIT_OPTION_NAME,
                "Get information about the bot's warn audit logs by warn id.",
                List.of(
                        new SlashCommandOptionBuilder()
                                .setType(SlashCommandOptionType.STRING)
                                .setName("warn-id")
                                .setDescription("The warn id to get warn audit logs for.")
                                .setRequired(true)
                                .build()));
    }
}
