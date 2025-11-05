/*
 * Copyright 2025 RealYusufIsmail.
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

import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * Discord slash command for interacting with GitHub's AI models.
 *
 * <p>This command allows users to ask questions to AI models. It supports:
 *
 * <ul>
 *   <li>Choosing different AI models
 *   <li>Starting new chat sessions
 *   <li>Maintaining conversation history
 * </ul>
 *
 * <p>This command is owner-only and requires administrator permissions.
 */
@Slf4j
@SlashEventBus
public class AICommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            @NotNull MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        val question = event.getOption("question", OptionMapping::getAsString);
        var newChat = Optional.ofNullable(event.getOption("new-chat", OptionMapping::getAsBoolean));
        val model = Optional.ofNullable(event.getOption("model", OptionMapping::getAsString));

        val githubAIModel =
                MystiGuardianUtils.getGithubAIModel(
                        event.getGuild().getIdLong(), event.getMember().getIdLong(), model);

        event.deferReply().queue();

        githubAIModel
                .askQuestion(question, event.getMember().getIdLong(), newChat.orElse(Boolean.FALSE))
                .thenAccept((answer) -> event.getHook().editOriginal(answer).queue())
                .exceptionally(
                        throwable -> {
                            log.error("Error occurred while asking AI question", throwable);
                            event.getHook()
                                    .editOriginal("Error: An error occurred while asking the question")
                                    .queue();
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
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "question", "The question to ask the AI model", true),
                new OptionData(OptionType.BOOLEAN, "new-chat", "Start a new chat session", false),
                new OptionData(OptionType.STRING, "model", "The model to use", false));
    }

    @Override
    public EnumSet<Permission> getRequiredPermissions() {
        return EnumSet.of(Permission.ADMINISTRATOR);
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
