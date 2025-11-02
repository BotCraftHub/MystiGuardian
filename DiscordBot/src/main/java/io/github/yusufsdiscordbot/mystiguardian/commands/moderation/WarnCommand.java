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
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class WarnCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        // Defer reply FIRST to prevent timeout
        event.deferReply().queue();

        val user = event.getOption("user", OptionMapping::getAsUser);
        val reason = event.getOption("reason", OptionMapping::getAsString);
        val guild = event.getGuild();

        if (user == null || reason == null) {
            event.getHook().sendMessage("❌ Please provide a user and a reason").queue();
            return;
        }

        if (guild == null) {
            event.getHook().sendMessage("❌ This command can only be used in servers").queue();
            return;
        }

        val member = guild.getMember(user);

        if (member != null) {
            if (!permChecker.canInteract(member)) {
                event
                        .getHook()
                        .sendMessage("❌ You cannot warn this user as they have a higher role than you")
                        .queue();
                return;
            }

            if (!permChecker.canBotInteract(member)) {
                event
                        .getHook()
                        .sendMessage("❌ I cannot warn this user as they have a higher role than me")
                        .queue();
                return;
            }
        }

        val warnId =
                MystiGuardianDatabaseHandler.Warns.setWarnsRecord(guild.getId(), user.getId(), reason);

        MystiGuardianDatabaseHandler.AmountOfWarns.updateAmountOfWarns(guild.getId(), user.getId());

        MystiGuardianConfig.getEventDispatcher()
                .dispatchEvent(
                        new ModerationActionTriggerEvent(
                                        MystiGuardianUtils.ModerationTypes.WARN,
                                        event.getJDA(),
                                        guild.getId(),
                                        event.getUser().getId())
                                .setModerationActionId(warnId)
                                .setUserId(user.getId())
                                .setReason(reason));

        event
                .getHook()
                .sendMessage("⚠️ Successfully warned **" + user.getAsTag() + "** | Reason: " + reason)
                .queue();
    }

    @NotNull
    @Override
    public String getName() {
        return "warn";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Warns a user";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to warn", true),
                new OptionData(OptionType.STRING, "reason", "The reason for the warn", true));
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
