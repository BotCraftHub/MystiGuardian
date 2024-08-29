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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.button.ButtonClickHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.urls.APIUrls;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@SlashEventBus
public class TriviaQuizCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        try {
            // Fetch trivia questions
            List<JsonNode> questions = fetchTriviaQuestions();
            if (questions.isEmpty()) {
                replyUtils.sendEmbed(
                        new EmbedBuilder()
                                .setColor(Color.RED)
                                .setTitle("Trivia Quiz")
                                .setDescription("Sorry, no trivia questions available right now."));
                return;
            }

            // Pick a random question
            JsonNode questionNode = questions.get(new Random().nextInt(questions.size()));

            // Parse the question and answers
            String question = questionNode.get("question").get("text").asText();
            String correctAnswer = questionNode.get("correctAnswer").asText();
            List<String> allAnswers =
                    StreamSupport.stream(questionNode.get("incorrectAnswers").spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.toList());
            allAnswers.add(correctAnswer);
            Collections.shuffle(allAnswers);

            Long userId = event.getUser().getIdLong();
            ButtonClickHandler.storeTriviaAnswer(userId, correctAnswer);

            List<Button> buttons =
                    allAnswers.stream()
                            .map(
                                    answer ->
                                            net.dv8tion.jda.api.interactions.components.buttons.Button.primary(
                                                    "trivia:" + answer, answer))
                            .collect(Collectors.toList());

            EmbedBuilder embed =
                    new EmbedBuilder()
                            .setColor(Color.BLUE)
                            .setTitle("Trivia Quiz")
                            .setDescription(question)
                            .setFooter("Select the correct answer:");

            event.replyEmbeds(embed.build()).addActionRow(buttons).queue();
        } catch (IOException e) {
            replyUtils.sendEmbed(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Trivia Quiz")
                            .setDescription("An error occurred while fetching trivia questions."));
        }
    }

    private List<JsonNode> fetchTriviaQuestions() throws IOException {
        URL url = new URL(APIUrls.TRIVA_API.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(connection.getInputStream());
        return StreamSupport.stream(root.spliterator(), false).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public String getName() {
        return "trivia";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Trivia quiz!";
    }
}
