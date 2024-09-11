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
    private String model;
    private final String token;
    private final Map<Long, List<Message>> context = new HashMap<>();
    private final List<Message> initialMessages = new ArrayList<>();

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public GithubAIModel(String model, String initialPrompt, Long memberId) {
        this.model = model;
        this.token = MystiGuardianUtils.getMainConfig().githubToken();
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        initialMessages.add(new Message("system", initialPrompt));
        context.put(memberId, new ArrayList<>(initialMessages));
    }

    public CompletableFuture<String> askQuestion(String question, Long memberId, boolean newChat) {
        if (!context.containsKey(memberId)) {
            context.put(memberId, new ArrayList<>(initialMessages));
        }

        if (newChat) {
            context.put(memberId, new ArrayList<>(initialMessages));
        }

        context.get(memberId).add(new Message("user", question));
        return sendRequest(memberId);
    }

    @NotNull
    private CompletableFuture<String> sendRequest(long memberId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        try {
            String url = "https://models.inference.ai.azure.com/chat/completions";

            RequestBody requestBody = getRequestBody(memberId);
            Request request =
                    new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + token)
                            .build();

            client
                    .newCall(request)
                    .enqueue(
                            new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    future.completeExceptionally(e);
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response)
                                        throws IOException {
                                    if (response.isSuccessful()) {
                                        String responseBody = response.body().string();
                                        future.complete(parseResponse(responseBody, memberId));
                                    } else {
                                        future.completeExceptionally(
                                                new RuntimeException(
                                                        "Request failed with status code: " + response.code()));
                                    }
                                }
                            });
        } catch (JsonProcessingException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    private @NotNull RequestBody getRequestBody(Long userId) throws JsonProcessingException {
        ObjectNode payload = mapper.createObjectNode();
        ArrayNode messages = payload.putArray("messages");

        for (Message message : context.get(userId)) {
            ObjectNode messageNode = messages.addObject();
            messageNode.put("role", message.role());
            messageNode.put("content", message.content());
        }

        payload.put("model", model);
        return RequestBody.create(
                mapper.writeValueAsString(payload), MediaType.get("application/json"));
    }

    private String parseResponse(String responseBody, long memberId) {
        try {
            ObjectNode responseJson = (ObjectNode) mapper.readTree(responseBody);
            String assistantResponse =
                    responseJson.get("choices").get(0).get("message").get("content").asText();
            context.get(memberId).add(new Message("assistant", assistantResponse));
            return assistantResponse;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public void setNewModel(String model) {
        this.model = model;
    }

    private record Message(String role, String content) {}
}
