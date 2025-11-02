/*
 * Copyright 2025 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class SoftBanCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        val user =
                Objects.requireNonNull(event.getOption("user", OptionMapping::getAsUser), "User is null");
        val reason =
                Objects.requireNonNull(
                        event.getOption("reason", OptionMapping::getAsString), "reason is null");
        val duration =
                Objects.requireNonNull(
                        event.getOption("duration", OptionMapping::getAsLong), "duration is null");

        val guild = event.getGuild();
        if (guild == null) {
            replyUtils.sendError("This command can only be used in servers");
            return;
        }

        val member = guild.getMember(user);
        if (member != null) {
            if (!permChecker.canInteract(member)) {
                replyUtils.sendError("You cannot soft ban this user as they have a higher role than you");
                return;
            }

            if (!permChecker.canBotInteract(member)) {
                replyUtils.sendError("I cannot soft ban this user as they have a higher role than me");
                return;
            }
        }

        guild
                .ban(user, 0, TimeUnit.SECONDS)
                .reason(reason)
                .queue(
                        success -> {
                            val id =
                                    MystiGuardianDatabaseHandler.SoftBan.setSoftBanRecord(
                                            guild.getId(), user.getId(), reason, duration.intValue());

                            MystiGuardianDatabaseHandler.AmountOfBans.updateAmountOfBans(
                                    guild.getId(), user.getId());

                            replyUtils.sendSuccess(
                                    "Banned user " + user.getAsTag() + " for " + duration + " days");

                            MystiGuardianConfig.getEventDispatcher()
                                    .dispatchEvent(
                                            new ModerationActionTriggerEvent(
                                                            MystiGuardianUtils.ModerationTypes.SOFT_BAN,
                                                            event.getJDA(),
                                                            guild.getId(),
                                                            event.getUser().getId())
                                                    .setModerationActionId(id)
                                                    .setUserId(user.getId())
                                                    .setReason(reason)
                                                    .setSoftBanAmountOfDays(duration.intValue()));
                        },
                        failure ->
                                replyUtils.sendError(
                                        "Failed to ban user "
                                                + user.getAsTag()
                                                + " as a result of "
                                                + failure.getMessage()));
    }

    @NotNull
    @Override
    public String getName() {
        return "softban";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "bans a user for a short period of time";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to ban", true),
                new OptionData(OptionType.STRING, "reason", "The reason for the ban", true),
                new OptionData(OptionType.INTEGER, "duration", "The duration of the ban in days", true)
                        .setRequiredRange(0, 360));
    }

    @Override
    public EnumSet<Permission> getRequiredPermissions() {
        return EnumSet.of(Permission.BAN_MEMBERS);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
