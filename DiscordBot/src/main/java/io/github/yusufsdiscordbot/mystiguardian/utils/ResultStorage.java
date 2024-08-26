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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class ResultStorage {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String RESULTS_DIRECTORY = "search_results";

    public static void storeResults(String query, JsonNode results) throws IOException {
        File directory = new File(RESULTS_DIRECTORY);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + RESULTS_DIRECTORY);
        }

        File file = new File(directory, query + "_" + LocalDate.now() + ".json");
        boolean fileCreated = file.createNewFile();
        if (!fileCreated && !file.exists()) {
            throw new IOException("Failed to create file: " + file.getAbsolutePath());
        }

        ObjectNode node = objectMapper.createObjectNode();
        node.set("date", objectMapper.valueToTree(LocalDate.now()));
        node.set("results", results);

        objectMapper.writeValue(file, node);
    }
}
