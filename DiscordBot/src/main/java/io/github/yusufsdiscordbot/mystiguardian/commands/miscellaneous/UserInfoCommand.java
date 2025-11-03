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
package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.List;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class UserInfoCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            @NotNull MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        var member = event.getOption("user", OptionMapping::getAsMember);

        if (member == null) {
            member = event.getMember();

            if (member == null) {
                replyUtils.sendError("This command can only be used in a server");
                return;
            }
        }

        val user = member.getUser();
        val server = event.getGuild();

        if (server == null) {
            replyUtils.sendError("This command can only be used in a server");
            return;
        }

        EmbedBuilder embed =
                replyUtils
                        .getDefaultEmbed()
                        .setTitle("👤 User Information")
                        .addField("Name", user.getName(), true)
                        .addField("ID", user.getId(), true)
                        .addField("Bot", user.isBot() ? "✅ Yes" : "❌ No", true)
                        .addField(
                                "Account Created", "<t:" + user.getTimeCreated().toEpochSecond() + ":R>", false)
                        .addField(
                                "Joined Server", "<t:" + member.getTimeJoined().toEpochSecond() + ":R>", false);

        if (user.getAvatarUrl() != null) {
            embed.setThumbnail(user.getAvatarUrl());
        }

        val roles = member.getRoles();
        if (!roles.isEmpty()) {
            // Limit to prevent embed field overflow (Discord limit is 1024 chars per field)
            val roleList = roles.stream().limit(20).map(role -> role.getAsMention()).toList();
            String rolesText = String.join(" ", roleList);
            if (roles.size() > 20) {
                rolesText += " *... and " + (roles.size() - 20) + " more*";
            }
            embed.addField("Roles (" + roles.size() + ")", rolesText, false);
        } else {
            embed.addField("Roles", "No roles", false);
        }

        // Show key permissions only to prevent overflow
        val keyPermissions =
                member.getPermissions().stream()
                        .filter(perm -> perm.isGuild()) // Only show important guild permissions
                        .limit(10)
                        .map(perm -> "`" + perm.getName() + "`")
                        .toList();

        if (!keyPermissions.isEmpty()) {
            embed.addField("Key Permissions", String.join(", ", keyPermissions), false);
        }

        replyUtils.sendEmbed(embed);
    }

    @NotNull
    @Override
    public String getName() {
        return "user-info";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get information about a user";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to get information about", false));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
