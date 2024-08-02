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
package io.github.yusufsdiscordbot.mystiguardian.commands.admin;

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.EnumSet;
import java.util.List;
import lombok.val;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class AICommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event,
            @NotNull MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val question = event.getOptionByName("question")
                .orElseThrow(() -> new IllegalArgumentException("Question is not present"))
                .getStringValue()
                .orElseThrow(() -> new IllegalArgumentException("Question is not present"));

        val githubAIModel = MystiGuardianUtils.getGithubAIModel(
                event.getServer().orElseThrow().getId());

        githubAIModel.askQuestion(question).thenAccept(replyUtils::sendSuccess).exceptionally(throwable -> {
            replyUtils.sendError("An error occurred while asking the question");
            return null;
        });
    }

    @NotNull
    @Override
    public String getName() {
        return "ai";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ask the AI model a question";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createStringOption("question", "The question to ask the AI model", true),
                SlashCommandOption.createStringOption("model", "The model to use", false));
    }

    @Override
    public EnumSet<PermissionType> getRequiredPermissions() {
        return EnumSet.of(PermissionType.ADMINISTRATOR);
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
