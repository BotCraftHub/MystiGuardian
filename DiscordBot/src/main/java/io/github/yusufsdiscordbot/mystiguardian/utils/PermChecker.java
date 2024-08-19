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

import lombok.val;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

public class PermChecker {
    private final SlashCommandInteraction interaction;

    public PermChecker(SlashCommandInteraction interaction) {
        this.interaction = interaction;
    }

    private boolean isOwner() {
        val user = interaction.getUser();
        val server = interaction.getServer().orElse(null);

        if (server == null) {
            return false;
        }

        val owner = server.getOwner();

        return owner.isPresent() && owner.get().getId() == user.getId();
    }

    public boolean canInteract(User targetUser) {
        val server = interaction.getServer().orElse(null);

        if (server == null) {
            return false;
        }

        if (isOwner()) {
            return true;
        }

        return canInteract(targetUser, interaction.getUser());
    }

    public boolean canBotInteract(User targetUser) {
        val server = interaction.getServer().orElse(null);

        if (server == null) {
            return false;
        }

        return canInteract(targetUser, interaction.getApi().getYourself());
    }

    private boolean canInteract(User targetUser, User user) {
        val server = interaction.getServer().orElse(null);

        if (server == null) {
            return false;
        }

        val userRoles = interaction.getServer().orElseThrow().getHighestRole(user).orElseThrow();

        val targetUserRoles =
                interaction.getServer().orElseThrow().getHighestRole(targetUser).orElseThrow();

        val compare = userRoles.compareTo(targetUserRoles);

        return compare > 0;
    }
}
