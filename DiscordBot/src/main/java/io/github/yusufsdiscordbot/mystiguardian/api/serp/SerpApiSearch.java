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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import okhttp3.*;

/**
 * This class is adapted from [<a href="https://github.com/serpapi/google-search-results-java">serp
 * api</a>] The original code is licensed under the MIT License. See the LICENSE file in the project
 * root for more information.
 */
public class SerpApiSearch extends Exception {
    /** Set of constants */
    public static final String API_KEY_NAME = "api_key";

    /** Default static key */
    public static String api_key_default;

    /** User's secret API key */
    @Nullable protected final String api_key;

    /** Current search engine */
    protected final String engine;

    /** Search parameters */
    public final Map<String, String> parameter;

    // Initialize ObjectMapper for Jackson
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** OkHttpClient for HTTP requests */
    private final OkHttpClient httpClient;

    /**
     * Constructor
     *
     * @param parameter user search parameters
     * @param api_key user's secret API key
     * @param engine service like: google, naver, yahoo...
     */
    public SerpApiSearch(Map<String, String> parameter, @Nullable String api_key, String engine) {
        this.parameter = parameter;
        this.api_key = api_key;
        this.engine = engine;
        this.httpClient = new OkHttpClient();
    }

    /**
     * Build a SerpAPI query by expanding existing parameters
     *
     * @param path backend HTTP path
     * @param output type of output format (json, html, json_with_images)
     * @return formatted parameter hash map
     * @throws SerpApiSearchException wraps backend error message
     */
    public Map<String, String> buildQuery(String path, String output) throws SerpApiSearchException {
        this.parameter.put("source", "java");

        this.parameter.putIfAbsent(
                API_KEY_NAME,
                api_key != null
                        ? api_key
                        : getApiKey()
                                .orElseThrow(() -> new SerpApiSearchException(API_KEY_NAME + " is not defined")));

        this.parameter.put("engine", this.engine);

        this.parameter.put("output", output);

        return this.parameter;
    }

    /**
     * @return current secret API key
     */
    public static Optional<String> getApiKey() {
        return Optional.ofNullable(api_key_default);
    }

    /**
     * Get HTML output
     *
     * @return raw HTML response from the search engine for custom parsing
     * @throws SerpApiSearchException wraps backend error message
     * @throws IOException wraps HTTP errors
     */
    public String getHtml() throws SerpApiSearchException, IOException {
        var query = buildQuery("/search", "html");
        return getResults(query);
    }

    /**
     * Get JSON output
     *
     * @return JsonNode parent node
     * @throws SerpApiSearchException wraps backend error message
     * @throws IOException wraps HTTP errors
     */
    public JsonNode getJson() throws SerpApiSearchException, IOException {
        var query = buildQuery("/search", "json");
        var jsonString = getResults(query);
        return asJson(jsonString);
    }

    /**
     * Convert HTTP content to JsonNode
     *
     * @param content raw JSON HTTP response
     * @return JsonNode created by Jackson parser
     * @throws IOException wraps parsing issues
     */
    public JsonNode asJson(String content) throws IOException {
        return objectMapper.readTree(content);
    }

    /**
     * Get results from SerpApi using OkHttp
     *
     * @param query Map of query parameters
     * @return String containing the response body
     * @throws IOException wraps HTTP errors
     */
    public String getResults(Map<String, String> query) throws IOException {
        var urlBuilder =
                Objects.requireNonNull(HttpUrl.parse("https://serpapi.com" + query.get("path")))
                        .newBuilder();
        query.forEach(urlBuilder::addQueryParameter);

        var request = new Request.Builder().url(urlBuilder.build()).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    /**
     * Get location
     *
     * @param q query
     * @param limit number of locations
     * @return JsonNode containing location data
     * @throws SerpApiSearchException wraps backend error message
     * @throws IOException wraps HTTP errors
     */
    public JsonNode getLocation(String q, Integer limit) throws SerpApiSearchException, IOException {
        var query = buildQuery("/locations.json", "json");
        query.remove("output");
        query.remove(API_KEY_NAME);
        query.put("q", q);
        query.put("limit", limit.toString());
        var jsonResponse = getResults(query);
        return asJson(jsonResponse);
    }

    /**
     * Get search result from the Search Archive API
     *
     * @param searchID archived search result = search_metadata.id
     * @return JsonNode containing the search result
     * @throws SerpApiSearchException wraps backend error message
     * @throws IOException wraps HTTP errors
     */
    public JsonNode getSearchArchive(String searchID) throws SerpApiSearchException, IOException {
        var query = buildQuery("/searches/" + searchID + ".json", "json");
        query.remove("output");
        query.remove("q");
        var jsonResponse = getResults(query);
        return asJson(jsonResponse);
    }

    /**
     * Get account information using Account API
     *
     * @return JsonNode containing account information
     * @throws SerpApiSearchException wraps backend error message
     * @throws IOException wraps HTTP errors
     */
    public JsonNode getAccount() throws SerpApiSearchException, IOException {
        var query = buildQuery("/account", "json");
        query.remove("output");
        query.remove("q");
        var jsonResponse = getResults(query);
        return asJson(jsonResponse);
    }
}
