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
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class KickCommand implements ISlashCommand {
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

        val guild = Objects.requireNonNull(event.getGuild(), "Guild is null");

        if (guild.getMembers().contains(user)) {
            val member = guild.getMember(user);

            if (!permChecker.canInteract(member)) {
                replyUtils.sendError("You cannot kick this user as they have a higher role than you");
                return;
            }

            if (!permChecker.canBotInteract(member)) {
                replyUtils.sendError("I cannot kick this user as they have a higher role than me");
                return;
            }
        }

        guild
                .kick(user)
                .reason(reason)
                .queue(
                        kick -> {
                            // Record the kick in the database
                            val kickId =
                                    MystiGuardianDatabaseHandler.Kick.setKickRecord(
                                            guild.getId(), user.getId(), reason);

                            MystiGuardianConfig.getEventDispatcher()
                                    .dispatchEvent(
                                            new ModerationActionTriggerEvent(
                                                            MystiGuardianUtils.ModerationTypes.KICK,
                                                            event.getJDA(),
                                                            guild.getId(),
                                                            event.getUser().getId())
                                                    .setModerationActionId(kickId)
                                                    .setUserId(user.getId())
                                                    .setReason(reason));

                            replyUtils.sendSuccess("Successfully kicked the user");
                        },
                        throwable -> {
                            replyUtils.sendError("Failed to kick user: " + throwable.getMessage());
                        });
    }

    @NotNull
    @Override
    public String getName() {
        return "kick";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Kick a user from the server";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to kick", true),
                new OptionData(OptionType.STRING, "reason", "The reason for the kick", true));
    }

    @Override
    public EnumSet<Permission> getRequiredPermissions() {
        return EnumSet.of(Permission.KICK_MEMBERS);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
