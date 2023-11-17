/*
 * Copyright 2023 RealYusufIsmail.
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

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

// TODO: Add BanCommand
public class BanCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {

    }

    @NotNull
    @Override
    public String getName() {
        return "ban";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ban a user from the server";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return ISlashCommand.super.getOptions();
    }

    @Override
    public EnumSet<PermissionType> getRequiredPermissions() {
        return ISlashCommand.super.getRequiredPermissions();
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
