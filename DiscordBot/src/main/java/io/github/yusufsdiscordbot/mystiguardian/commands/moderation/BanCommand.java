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
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BanCommand implements ISlashCommand {
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

        val messageDurationOption = event.getOptionByName("message_duration");

        Duration messageDuration;
        messageDuration = messageDurationOption
                .flatMap(SlashCommandInteractionOption::getLongValue)
                .map(Duration::ofDays)
                .orElse(Duration.ZERO);

        val server = event.getServer().orElseThrow(() -> new IllegalArgumentException("Server is not present"));

        val canCommandRun = permChecker(event.getApi().getYourself(), event.getUser(), user, server, replyUtils);

        if (!canCommandRun) {
            replyUtils.sendError("You cannot ban this user as you or the bot is lower than them in the hierarchy.");
            return;
        }

        server.banUser(user, messageDuration, reason)
                .thenAccept(ban -> {
                    val banId = MystiGuardianDatabaseHandler.Ban.setBanRecord(
                            server.getIdAsString(), user.getIdAsString(), reason);

                    MystiGuardianDatabaseHandler.AmountOfBans.updateAmountOfBans(
                            server.getIdAsString(), user.getIdAsString());

                    MystiGuardianConfig.getEventDispatcher()
                            .dispatchEvent(new ModerationActionTriggerEvent(
                                            MystiGuardianUtils.ModerationTypes.BAN,
                                            event.getApi(),
                                            event.getServer().get().getIdAsString(),
                                            event.getUser().getIdAsString())
                                    .setModerationActionId(banId)
                                    .setUserId(user.getIdAsString())
                                    .setReason(reason));

                    replyUtils.sendSuccess("Successfully banned the user");
                })
                .exceptionally(throwable -> {
                    replyUtils.sendError("Failed to ban user: " + throwable.getMessage());
                    return null;
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
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createUserOption("user", "The user to ban", true),
                SlashCommandOption.createStringOption("reason", "The reason for the ban", true),
                SlashCommandOption.createLongOption(
                        "message_duration", "The amount of days to delete the messages of the user", false, 0, 7));
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
