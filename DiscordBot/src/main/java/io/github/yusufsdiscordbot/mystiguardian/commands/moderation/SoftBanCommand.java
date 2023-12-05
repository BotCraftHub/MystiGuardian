/*
 * Copyright 2023 RealYusufIsmail.
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

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.permChecker;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import lombok.val;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SoftBanCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        val user = event.getOptionByName("user")
                .orElseThrow(() -> new IllegalArgumentException("User is not present"))
                .getUserValue()
                .orElseThrow(() -> new IllegalArgumentException("User is not present"));

        val reason = event.getOptionByName("reason")
                .orElseThrow(() -> new IllegalArgumentException("Reason is not present"))
                .getStringValue()
                .orElseThrow(() -> new IllegalArgumentException("Reason is not present"));

        val durationAsLong = event.getOptionByName("duration")
                .orElseThrow(() -> new IllegalArgumentException("Duration is not present"))
                .getLongValue()
                .orElseThrow(() -> new IllegalArgumentException("Duration is not present"));

        val server = event.getServer().orElseThrow(() -> new IllegalArgumentException("Server is not present"));

        val canCommandRun = permChecker(event.getApi().getYourself(), event.getUser(), user, server, replyUtils);

        if (!canCommandRun) {
            replyUtils.sendError(
                    "You cannot soft ban this user as you or the bot is lower than them in the hierarchy.");
            return;
        }

        server.banUser(user, Duration.ZERO, reason)
                .thenAccept(banned -> {
                    val id = MystiGuardianDatabaseHandler.SoftBan.setSoftBanRecord(
                            server.getIdAsString(), user.getIdAsString(), reason, durationAsLong.intValue());
                    MystiGuardianDatabaseHandler.AmountOfBans.updateAmountOfBans(
                            server.getIdAsString(), user.getIdAsString());

                    replyUtils.sendSuccess(
                            "Banned user " + user.getDiscriminatedName() + " for " + durationAsLong + " days");

                    MystiGuardianConfig.getEventDispatcher()
                            .dispatchEvent(new ModerationActionTriggerEvent(
                                            MystiGuardianUtils.ModerationTypes.SOFT_BAN,
                                            event.getApi(),
                                            event.getServer().get().getIdAsString(),
                                            event.getUser().getIdAsString())
                                    .setModerationActionId(id)
                                    .setUserId(user.getIdAsString())
                                    .setReason(reason)
                                    .setSoftBanAmountOfDays(durationAsLong.intValue()));
                })
                .exceptionally(throwable -> {
                    replyUtils.sendError("Failed to ban user " + user.getDiscriminatedName() + "as a result of "
                            + throwable.getMessage());
                    return null;
                });
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
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createUserOption("user", "The user to ban", true),
                SlashCommandOption.createStringOption("reason", "The reason for the ban", true),
                SlashCommandOption.createLongOption("duration", "The duration of the ban in days", true, 0, 360));
    }

    @Override
    public EnumSet<PermissionType> getRequiredPermissions() {
        return EnumSet.of(PermissionType.BAN_MEMBERS);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
