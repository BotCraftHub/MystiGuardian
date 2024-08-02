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
package io.github.yusufsdiscordbot.mystiguardian.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

public class GithubAIModel {
    private final String model;
    private final String token;
    private final String initialPrompt;
    private final List<String> context = new ArrayList<>();
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public static Map<Long, String> userIdAndContext = new HashMap<>();

    public GithubAIModel(String model, String initialPrompt) {
        this.model = model;
        this.token = MystiGuardianUtils.getGithubToken();
        this.initialPrompt = initialPrompt;
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public GithubAIModel(Map<Long, Map<String, String>> userIdAndModelAndContext) {
        this.model = userIdAndModelAndContext.get(0).get("model");
        this.token = MystiGuardianUtils.getGithubToken();
        this.initialPrompt = userIdAndModelAndContext.get(0).get("initialPrompt");
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }

    public CompletableFuture<String> askQuestion(String question) {
        context.add(question);
        return sendRequest();
    }

    private CompletableFuture<String> sendRequest() {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String url = "https://models.inference.ai.azure.com/chat/completions";

            RequestBody requestBody = getRequestBody();
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    MystiGuardianUtils.logger.error("Error while sending request to AI model", e);
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        MystiGuardianUtils.logger.debug("Request sent to AI model successfully");
                        future.complete(parseResponse(responseBody));
                    } else {
                        MystiGuardianUtils.logger.error(
                                "Error while sending request to AI model. Response code: {}", response.code());
                        future.completeExceptionally(
                                new RuntimeException("Request failed with status code: " + response.code()));
                    }
                }
            });
        } catch (JsonProcessingException e) {
            MystiGuardianUtils.logger.error("Error while processing JSON for AI model request", e);
            future.completeExceptionally(e);
        }
        return future;
    }

    private @NotNull RequestBody getRequestBody() throws JsonProcessingException {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("model", model);

        ArrayNode messages = payload.putArray("messages");
        for (String messageContent : context) {
            ObjectNode message = messages.addObject();
            message.put("role", "user");
            message.put("content", messageContent);
        }

        ObjectNode systemMessage = messages.insertObject(0);
        systemMessage.put("role", "system");
        systemMessage.put("content", initialPrompt);

        String jsonInputString = mapper.writeValueAsString(payload);
        return RequestBody.create(jsonInputString, MediaType.get("application/json"));
    }

    private String parseResponse(String responseBody) {
        // Assuming the response body contains the AI's reply in a field called "reply"
        // You need to adjust this method based on the actual structure of the API response
        try {
            ObjectNode responseJson = (ObjectNode) mapper.readTree(responseBody);
            return responseJson.get("reply").asText();
        } catch (JsonProcessingException e) {
            MystiGuardianUtils.logger.error("Error while parsing AI model response", e);
            return null;
        }
    }
}
