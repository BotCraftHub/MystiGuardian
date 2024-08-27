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
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.StringJoiner;

public class ResultStorage {
    private static final String RESULTS_DIRECTORY = "search_results";

    public static void storeResults(String query, JsonNode results) throws IOException {
        File directory = new File(RESULTS_DIRECTORY);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + RESULTS_DIRECTORY);
        }

        File finalFile = new File(directory, query + "_" + LocalDate.now() + ".json");

        // Manually construct the JSON string
        StringJoiner jsonBuilder = new StringJoiner(",\n", "{\n", "\n}");
        jsonBuilder.add("\"date\": \"" + LocalDate.now() + "\"");
        jsonBuilder.add("\"results\": " + results.toPrettyString());

        String jsonString = jsonBuilder.toString();

        try {
            Files.writeString(Paths.get(finalFile.toURI()), jsonString);
        } catch (IOException e) {
            MystiGuardianUtils.logger.error("Failed to write JSON to file", e);
            throw e;
        }
    }
}
