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
package io.github.yusufsdiscordbot.mystiguardian.commands.admin.database;

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.List;
import org.javacord.api.interaction.*;
import org.jetbrains.annotations.NotNull;

// TODO: Implement this
public class ClearDatabaseCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {}

    @NotNull
    @Override
    public String getName() {
        return "clear_database";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Clears certain tables in the database";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(SlashCommandOption.createWithChoices(
                SlashCommandOptionType.STRING,
                "table",
                "The table to clear",
                true,
                new SlashCommandOptionChoiceBuilder().setName("all").setValue("all"),
                new SlashCommandOptionChoiceBuilder().setName("warn").setValue("warn"),
                new SlashCommandOptionChoiceBuilder().setName("mute").setValue("mute"),
                new SlashCommandOptionChoiceBuilder().setName("kick").setValue("kick"),
                new SlashCommandOptionChoiceBuilder().setName("ban").setValue("ban"),
                new SlashCommandOptionChoiceBuilder().setName("audit").setValue("audit")));
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
