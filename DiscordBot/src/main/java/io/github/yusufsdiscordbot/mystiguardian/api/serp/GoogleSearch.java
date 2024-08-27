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
package io.github.yusufsdiscordbot.mystiguardian.api.serp;

import java.util.Map;

/**
 * This class is adapted from [<a href="https://github.com/serpapi/google-search-results-java">serp
 * api</a>] The original code is licensed under the MIT License. See the LICENSE file in the project
 * root for more information.
 *
 * <p>Google Search Results using SerpApi Usage ---
 *
 * <pre>{@code
 * Map<String, String> parameter = new HashMap<>();
 * parameter.put("q", "Coffee");
 * GoogleSearch google = new GoogleSearch(parameter, "secret api key");
 * JsonObject data = google.getJson();
 * JsonArray organic_results = data.get("organic_results").getAsJsonArray();
 * }</pre>
 */
public class GoogleSearch extends SerpApiSearch {

    /**
     * Constructor
     *
     * @param parameter search parameter
     * @param apiKey secret API key
     */
    public GoogleSearch(Map<String, String> parameter, String apiKey) {
        super(parameter, apiKey, "google");
    }
}
