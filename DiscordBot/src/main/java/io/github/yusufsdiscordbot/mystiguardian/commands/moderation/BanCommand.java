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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class BanCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val user =
                Objects.requireNonNull(event.getOption("user", OptionMapping::getAsUser), "User is null");

        val reason =
                Objects.requireNonNull(
                        event.getOption("reason", OptionMapping::getAsString), "Reason is null");

        val messageDurationDays =
                Optional.ofNullable(event.getOption("message_duration"))
                        .map(OptionMapping::getAsLong)
                        .orElse(0L)
                        .intValue();

        val guild = Objects.requireNonNull(event.getGuild(), "Guild is null");

        if (guild.getMembers().contains(user)) {
            val member = guild.getMember(user);

            if (!permChecker.canInteract(member)) {
                replyUtils.sendError("❌ You cannot ban this user as they have a higher role than you");
                return;
            }

            if (!permChecker.canBotInteract(member)) {
                replyUtils.sendError("❌ I cannot ban this user as they have a higher role than me");
                return;
            }
        }

        // Defer reply to prevent timeout during database operations
        event.deferReply().queue();

        guild
                .ban(user, messageDurationDays, TimeUnit.DAYS)
                .reason(reason)
                .queue(
                        ban -> {
                            // Record the ban in the database
                            val banId =
                                    MystiGuardianDatabaseHandler.Ban.setBanRecord(
                                            guild.getId(), user.getId(), reason);

                            MystiGuardianDatabaseHandler.AmountOfBans.updateAmountOfBans(
                                    guild.getId(), user.getId());

                            MystiGuardianConfig.getEventDispatcher()
                                    .dispatchEvent(
                                            new ModerationActionTriggerEvent(
                                                            MystiGuardianUtils.ModerationTypes.BAN,
                                                            event.getJDA(),
                                                            guild.getId(),
                                                            event.getUser().getId())
                                                    .setModerationActionId(banId)
                                                    .setUserId(user.getId())
                                                    .setReason(reason));

                            event.getHook().sendMessage("✅ Successfully banned **" + user.getAsTag() + "** | Reason: " + reason).queue();
                        },
                        throwable -> {
                            event.getHook().sendMessage("❌ Failed to ban user: " + throwable.getMessage()).queue();
                        });
    }

    @NotNull
    @Override
    public String getName() {
        return "ban";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ban a user from the server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to ban", true),
                new OptionData(OptionType.STRING, "reason", "The reason for the ban", true),
                new OptionData(
                                OptionType.INTEGER,
                                "message_duration",
                                "The amount of days to delete the messages of the user",
                                false)
                        .setRequiredRange(0, 7));
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
