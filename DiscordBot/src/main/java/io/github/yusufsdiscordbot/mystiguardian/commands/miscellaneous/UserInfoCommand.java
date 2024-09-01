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
package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
                        .setTitle("User Information")
                        .addField("Name", user.getName(), false)
                        .addField("ID", user.getId(), false)
                        .addField(
                                "Created",
                                OffsetDateTime.ofInstant(user.getTimeCreated().toInstant(), ZoneOffset.UTC)
                                        .format(MystiGuardianUtils.DATE_TIME_FORMATTER),
                                false)
                        .addField("Bot", user.isBot() ? "Yes" : "No", false);

        if (user.getAvatarUrl() != null) {
            embed.setThumbnail(user.getAvatarUrl());
        }

        val timeJoined = member.getTimeJoined();
        val joinedDateTime = OffsetDateTime.ofInstant(timeJoined.toInstant(), ZoneOffset.UTC);

        embed.addField("Joined", joinedDateTime.format(MystiGuardianUtils.DATE_TIME_FORMATTER), false);

        val roles =
                member.getRoles().stream().map(role -> role.getName() + " (" + role.getId() + ")").toList();

        val permissions =
                member.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission -> permission.getName() + " (" + permission.getRawValue() + ")")
                        .toList();

        embed.addField("Roles", String.join("\n", roles), false);
        embed.addField("Permissions", String.join("\n", permissions), false);

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
