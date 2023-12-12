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

        val serverMember = server != null ? server.getMemberById(user.getId()).orElse(null) : null;

        OffsetDateTime joinedAt = null;
        List<String> roles = null;
        List<String> permissions = null;

        if (serverMember != null) {
            if (serverMember.getJoinedAtTimestamp(server).isPresent()) {
                joinedAt = OffsetDateTime.ofInstant(
                        serverMember.getJoinedAtTimestamp(server).get(), ZoneOffset.UTC);
            }

            roles = serverMember.getRoles(server).stream()
                    .map(Nameable::getName)
                    .toList();

            permissions = serverMember.getRoles(server).stream()
                    .map(Role::getPermissions)
                    .map(Permissions::getAllowedBitmask)
                    .map(Long::toBinaryString)
                    .toList();
        }

        val embed = replyUtils
                .getDefaultEmbed()
                .addField("Name", user.getName() + "#" + user.getDiscriminator())
                .addField("ID", user.getIdAsString())
                .addField(
                        "Created",
                        OffsetDateTime.ofInstant(user.getCreationTimestamp(), ZoneOffset.UTC)
                                .toString())
                .addField("Bot", user.isBot() ? "Yes" : "No")
                .addField(
                        "Joined Server",
                        joinedAt != null ? joinedAt.format(MystiGuardianUtils.DATE_TIME_FORMATTER) : "Unknown")
                .addField("Roles", roles != null ? roles.stream().reduce("", (a, b) -> a + ", " + b) : "Unknown")
                .addField(
                        "Permissions",
                        permissions != null ? permissions.stream().reduce("", (a, b) -> a + ", " + b) : "Unknown")
                .setThumbnail(user.getAvatar());

        replyUtils.sendEmbed(embed);
    }

    @NotNull
    @Override
    public String getName() {
        return "userinfo";
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
