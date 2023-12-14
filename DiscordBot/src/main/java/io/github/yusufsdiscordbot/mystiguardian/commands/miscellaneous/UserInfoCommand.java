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

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.val;
import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class UserInfoCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event,
            @NotNull MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        val user = event.getOptionByName("user")
                .flatMap(SlashCommandInteractionOption::getUserValue)
                .orElse(event.getUser());

        val server = event.getServer().orElse(null);

        val embed = replyUtils.getDefaultEmbed().setThumbnail(user.getAvatar());

        var info =
                """
               Name: %s
               ID: %s
               Created: %s
               Bot: %s
                """
                        .formatted(
                                user.getName() + "#" + user.getDiscriminator(),
                                user.getIdAsString(),
                                OffsetDateTime.ofInstant(user.getCreationTimestamp(), ZoneOffset.UTC)
                                        .format(MystiGuardianUtils.DATE_TIME_FORMATTER),
                                user.isBot() ? "Yes" : "No");

        if (server != null && server.getMemberById(user.getId()).isPresent()) {
            val serverMember = server.getMemberById(user.getId()).get();

            serverMember.getJoinedAtTimestamp(server).ifPresent(joinedAt -> {
                val joinedDateTime = OffsetDateTime.ofInstant(joinedAt, ZoneOffset.UTC);

                embed.addField("Joined", joinedDateTime.format(MystiGuardianUtils.DATE_TIME_FORMATTER));
            });

            val roles = serverMember.getRoles(server).stream()
                    .map(Nameable::getName)
                    .toList();

            val permissions = serverMember.getRoles(server).stream()
                    .map(Role::getPermissions)
                    .map(Permissions::getAllowedBitmask)
                    .map(Long::toBinaryString)
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
    public List<SlashCommandOption> getOptions() {
        return List.of(SlashCommandOption.createUserOption("user", "The user to get information about", false));
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
