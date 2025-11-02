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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class TriviaQuizCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        // Defer reply immediately to prevent timeout
        event.deferReply(true).queue(); // true = ephemeral

        try {
            // Fetch trivia questions
            List<JsonNode> questions = fetchTriviaQuestions();
            if (questions.isEmpty()) {
                event
                        .getHook()
                        .sendMessageEmbeds(
                                new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setTitle("Trivia Quiz")
                                        .setDescription("Sorry, no trivia questions available right now.")
                                        .build())
                        .queue();
                return;
            }

            JsonNode questionNode = questions.get(new Random().nextInt(questions.size()));

            String question = questionNode.get("question").get("text").asText();
            String correctAnswer = questionNode.get("correctAnswer").asText();

            List<String> allAnswers =
                    StreamSupport.stream(questionNode.get("incorrectAnswers").spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.toList());

            allAnswers.add(correctAnswer);
            Collections.shuffle(allAnswers);

            long userId = event.getUser().getIdLong();
            ButtonClickHandler.storeTriviaAnswer(userId, correctAnswer);

            List<Button> buttons =
                    allAnswers.stream()
                            .map(answer -> Button.primary("trivia:" + answer, answer))
                            .collect(Collectors.toList());

            EmbedBuilder embed =
                    new EmbedBuilder()
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setTitle("Trivia Quiz")
                            .setDescription(question)
                            .setTimestamp(event.getTimeCreated())
                            .addField("Category", questionNode.get("category").asText(), true)
                            .setFooter("Select the correct answer:");

            event.getHook().sendMessageEmbeds(embed.build()).addComponents(ActionRow.of(buttons)).queue();
        } catch (IOException e) {
            event
                    .getHook()
                    .sendMessageEmbeds(
                            new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setTitle("Trivia Quiz")
                                    .setDescription("An error occurred while fetching trivia questions.")
                                    .build())
                    .queue();
        }
    }

    private List<JsonNode> fetchTriviaQuestions() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(APIUrls.TRIVA_API.getUrl()).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());

            if (root.isArray()) {
                return StreamSupport.stream(root.spliterator(), false).collect(Collectors.toList());
            } else {
                throw new IOException("Unexpected JSON format");
            }
        }
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
