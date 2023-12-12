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
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

public interface ISlashCommand {

    void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker);

    @NotNull
    String getName();

    @NotNull
    String getDescription();

    default List<SlashCommandOption> getOptions() {
        return Collections.emptyList();
    }

    default EnumSet<PermissionType> getRequiredPermissions() {
        return null;
    }

    default boolean isGlobal() {
        return true;
    }

    default boolean isOwnerOnly() {
        return false;
    }
}
