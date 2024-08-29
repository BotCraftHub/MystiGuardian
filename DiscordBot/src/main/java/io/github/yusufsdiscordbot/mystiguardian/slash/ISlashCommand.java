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
package io.github.yusufsdiscordbot.mystiguardian.slash;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public interface ISlashCommand {

    /**
     * Handles the slash command interaction event
     *
     * @param event The slash command interaction event
     * @param replyUtils The reply utils
     * @param permChecker The permission checker
     */
    void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker);

    @NotNull
    String getName();

    @NotNull
    String getDescription();

    default List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    default List<SubcommandData> getSubcommands() {
        return Collections.emptyList();
    }

    default EnumSet<Permission> getRequiredPermissions() {
        return null;
    }

    default boolean isGlobal() {
        return true;
    }

    default boolean isOwnerOnly() {
        return false;
    }
}
