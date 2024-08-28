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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import java.util.Objects;
import lombok.val;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PermChecker {
    private final SlashCommandInteractionEvent interaction;

    public PermChecker(SlashCommandInteractionEvent interaction) {
        this.interaction = interaction;
    }

    private boolean isOwner() {
        val user = interaction.getUser();
        val server = interaction.getGuild();

        if (server == null) {
            return false;
        }

        val owner = server.getOwner();

        return owner != null && owner.equals(user);
    }

    public boolean canInteract(Member targetMember) {
        val server = interaction.getGuild();

        if (server == null) {
            return false;
        }

        if (isOwner()) {
            return true;
        }

        return canInteract(targetMember, interaction.getUser());
    }

    public boolean canBotInteract(Member targetMember) {
        val server = interaction.getGuild();

        if (server == null) {
            return false;
        }

        return canInteract(targetMember, interaction.getJDA().getSelfUser());
    }

    private boolean canInteract(Member targetMember, User user) {
        val server = interaction.getGuild();

        if (server == null) {
            return false;
        }

        val membersHighestRole =
                Objects.requireNonNull(interaction.getGuild().getMember(user), "Invalid member")
                        .getRoles()
                        .stream()
                        .max(Comparable::compareTo);

        val targetMemberHighestRole =
                Objects.requireNonNull(targetMember, "Invalid member").getRoles().stream()
                        .max(Comparable::compareTo);

        var canInteract = false;

        if (membersHighestRole.isPresent() && targetMemberHighestRole.isPresent()) {
            canInteract = membersHighestRole.get().compareTo(targetMemberHighestRole.get()) > 0;
        }

        return canInteract;
    }
}
