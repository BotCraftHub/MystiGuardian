package io.github.yusufsdiscordbot.mystiguardian.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.Getter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GithubAIModel {
    private final String model;
    private final String token;
    private final List<Message> context = new ArrayList<>();
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public GithubAIModel(String model, String initialPrompt) {
        this.model = model;
        this.token = MystiGuardianUtils.getGithubToken();
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        this.context.add(new Message("system", initialPrompt));
    }

    public CompletableFuture<String> askQuestion(String question) {
        context.add(new Message("user", question));
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
                        MystiGuardianUtils.logger.error("Error while sending request to AI model. Response code: {}, Response body: {}", response.code(), response.body().string());
                        future.completeExceptionally(new RuntimeException("Request failed with status code: " + response.code()));
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
        ArrayNode messages = payload.putArray("messages");

        for (Message message : context) {
            ObjectNode messageNode = messages.addObject();
            messageNode.put("role", message.role());
            messageNode.put("content", message.content());
        }

        payload.put("model", model);

        String jsonInputString = mapper.writeValueAsString(payload);

        MystiGuardianUtils.logger.debug("Request to AI model: {}", jsonInputString);
        return RequestBody.create(jsonInputString, MediaType.get("application/json"));
    }

    private String parseResponse(String responseBody) {
        MystiGuardianUtils.logger.debug("Response from AI model: {}", responseBody);
        try {
            ObjectNode responseJson = (ObjectNode) mapper.readTree(responseBody);
            String assistantResponse = responseJson.get("choices").get(0).get("message").get("content").asText();
            context.add(new Message("assistant", assistantResponse));
            return assistantResponse;
        } catch (JsonProcessingException e) {
            MystiGuardianUtils.logger.error("Error while parsing AI model response", e);
            return null;
        }
    }

    private record Message(String role, String content) {}
}