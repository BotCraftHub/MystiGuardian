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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ResultFilter {

    public static Set<String> getNewResults(
            String query, JsonNode newResults, ObjectMapper objectMapper) throws IOException {
        Set<String> newLinks = new HashSet<>();
        Set<String> oldLinks = loadOldLinks(query, objectMapper);

        for (JsonNode result : newResults.path("organic_results")) {
            String link = result.path("link").asText();
            if (!oldLinks.contains(link)) {
                newLinks.add(link);
            }
        }

        return newLinks;
    }

    private static Set<String> loadOldLinks(String query, ObjectMapper objectMapper)
            throws IOException {
        Set<String> links = new HashSet<>();
        File directory = new File("search_results");
        File[] files =
                directory.listFiles((dir, name) -> name.startsWith(query) && name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                JsonNode rootNode = objectMapper.readTree(file);
                for (JsonNode result : rootNode.path("results").path("organic_results")) {
                    String link = result.path("link").asText();
                    links.add(link);
                }
            }
        }

        return links;
    }

    public static JsonNode filterResultsByLinks(
            JsonNode results, Set<String> newLinks, ObjectMapper objectMapper) {
        ObjectNode filteredResults = objectMapper.createObjectNode();
        ArrayNode filteredArray = filteredResults.putArray("organic_results");

        for (JsonNode result : results.path("organic_results")) {
            String link = result.path("link").asText();
            if (newLinks.contains(link)) {
                filteredArray.add(result);
            }
        }

        return filteredResults;
    }
}
