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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
@SuppressWarnings("unused")
public class UserInfoCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            @NotNull MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        val user = event.getOption("user", OptionMapping::getAsUser);

        val server = event.getGuild();

        val embed = replyUtils.getDefaultEmbed().setThumbnail(user.getAvatar().getUrl());

        var info =
                """
               Name: %s
               ID: %s
               Created: %s
               Bot: %s
                """
                        .formatted(
                                user.getName() + "#" + user.getDiscriminator(),
                                user.getId(),
                                OffsetDateTime.ofInstant(user.getTimeCreated().toInstant(), ZoneOffset.UTC)
                                        .format(MystiGuardianUtils.DATE_TIME_FORMATTER),
                                user.isBot() ? "Yes" : "No");

        if (server != null && server.getMemberById(user.getId()) != null) {
            val serverMember = server.getMemberById(user.getId());

            if (serverMember != null) {
                val timeJoined = serverMember.getTimeJoined();

                val joinedDateTime = OffsetDateTime.ofInstant(timeJoined.toInstant(), ZoneOffset.UTC);

                embed.addField("Joined", joinedDateTime.format(MystiGuardianUtils.DATE_TIME_FORMATTER), false);
            }

            val roles = serverMember.getRoles().stream()
                    .map(role -> role.getName() + " (" + role.getId() + ")")
                    .toList();

            val permissions = serverMember.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .map(permission -> permission.getName() + " (" + permission.getRawValue() + ")")
                    .toList();

            info += """
                    Roles: %s
                    """.formatted(String.join(", ", roles));

            info += """
                    Permissions: %s
                    """
                    .formatted(String.join(", ", permissions));
        }

        embed.setDescription("```" + info + "```");

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
                new OptionData(OptionType.USER, "user", "The user to get information about", false)
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
