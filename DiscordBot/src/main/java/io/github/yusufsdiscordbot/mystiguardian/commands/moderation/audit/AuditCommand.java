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
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
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
        void onSlashCommandInteractionEvent(SlashCommandInteractionEvent event);
    }

    @FunctionalInterface
    interface ParameterizedAuditCommand {
        void onSlashCommandInteractionEvent(
                SlashCommandInteractionEvent event,
                MystiGuardianUtils.ReplyUtils replyUtils,
                PermChecker permChecker);
    }

    private static final Map<String, IAuditCommand> commandMap =
            Map.of(
                    WARN_AUDIT_OPTION_NAME,
                    event -> new WarnAuditCommand().onSlashCommandInteractionEvent(event),
                    KICK_AUDIT_OPTION_NAME,
                    event -> new KickAuditCommand().onSlashCommandInteractionEvent(event),
                    BAN_AUDIT_OPTION_NAME,
                    event -> new BanAuditCommand().onSlashCommandInteractionEvent(event),
                    TIME_OUT_AUDIT_OPTION_NAME,
                    event -> new TimeOutAuditCommand().onSlashCommandInteractionEvent(event),
                    RELOAD_AUDIT_OPTION_NAME,
                    event -> new ReloadAuditCommand().onSlashCommandInteractionEvent(event),
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
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        commandMap.entrySet().stream()
                .filter(
                        entry -> {
                            event.getOptionsByName(entry.getKey());
                            return true;
                        })
                .findFirst()
                .ifPresent(entry -> entry.getValue().onSlashCommandInteractionEvent(event));

        parameterizedCommandMap.entrySet().stream()
                .filter(
                        entry -> {
                            event.getOptionsByName(entry.getKey());
                            return true;
                        })
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
        return "Get information about the bot's audit logs for things such as bans, kicks, and reloads.";
    }

    @Override
    public EnumSet<Permission> getRequiredPermissions() {
        return EnumSet.of(Permission.VIEW_AUDIT_LOGS);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public List<SubcommandData> getSubcommands() {
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
                        TIME_OUT_AUDIT_OPTION_NAME, "Get information about the bot's timeout audit logs."),
                createAmountAuditSubcommand(),
                createWarnByIdAuditSubcommand(),
                createUserAuditSubcommand(
                        SOFT_BAN_AUDIT_OPTION_NAME, "Get information about the bot's soft ban audit logs."));
    }

    private SubcommandData createSimpleSubcommand(String name, String description) {
        return new SubcommandData(name, description);
    }

    private SubcommandData createUserAuditSubcommand(String name, String description) {
        return new SubcommandData(name, description)
                .addOption(OptionType.USER, "user", "The user to get audit logs for.", true);
    }

    private SubcommandData createAmountAuditSubcommand() {
        return new SubcommandData(
                        AMOUNT_AUDIT_OPTION_NAME,
                        "Get information about the bot's amount audit logs for a certain moderation.")
                .addOption(OptionType.USER, "user", "The user to get amount audit logs for.", true)
                .addOptions(
                        new OptionData(
                                        OptionType.STRING,
                                        "moderation-type",
                                        "The moderation type to get amount audit logs for.",
                                        true)
                                .addChoices(
                                        new Command.Choice(
                                                MystiGuardianUtils.ModerationTypes.WARN.name(),
                                                MystiGuardianUtils.ModerationTypes.WARN.name()),
                                        new Command.Choice(
                                                MystiGuardianUtils.ModerationTypes.KICK.name(),
                                                MystiGuardianUtils.ModerationTypes.KICK.name()),
                                        new Command.Choice(
                                                MystiGuardianUtils.ModerationTypes.BAN.name(),
                                                MystiGuardianUtils.ModerationTypes.BAN.name()),
                                        new Command.Choice(
                                                MystiGuardianUtils.ModerationTypes.TIME_OUT.name(),
                                                MystiGuardianUtils.ModerationTypes.TIME_OUT.name())));
    }

    private SubcommandData createWarnByIdAuditSubcommand() {
        return new SubcommandData(
                        WARN_BY_ID_AUDIT_OPTION_NAME,
                        "Get information about the bot's warn audit logs by warn ID.")
                .addOption(OptionType.STRING, "warn-id", "The warn ID to get warn audit logs for.", true);
    }
}
