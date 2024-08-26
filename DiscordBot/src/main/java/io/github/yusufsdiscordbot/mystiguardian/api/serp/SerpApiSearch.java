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
import io.github.yusufsdiscordbot.mystiguardian.urls.APIUrls;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.util.*;
import javax.annotation.Nullable;
import lombok.val;
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

    /** Required query parameters for the API */
    private static final Set<String> REQUIRED_PARAMETERS = Set.of(API_KEY_NAME, "q");

    /**
     * Constructor
     *
     * @param parameter user search parameters
     * @param api_key user's secret API key
     * @param engine service like: google, naver, yahoo...
     */
    public SerpApiSearch(Map<String, String> parameter, @Nullable String api_key, String engine) {
        this.parameter = new HashMap<>(parameter);
        this.api_key = api_key;
        this.engine = engine;
        this.httpClient = new OkHttpClient();
    }

    /**
     * Build a SerpAPI query by expanding existing parameters
     *
     * @return formatted parameter hash map
     * @throws SerpApiSearchException wraps backend error message
     */
    public Map<String, String> buildQuery() throws SerpApiSearchException {
        this.parameter.putIfAbsent(
                API_KEY_NAME,
                api_key != null
                        ? api_key
                        : getApiKey()
                                .orElseThrow(() -> new SerpApiSearchException(API_KEY_NAME + " is not defined")));

        this.parameter.put("engine", this.engine != null ? this.engine : "google");

        return new HashMap<>(this.parameter);
    }

    /**
     * Validate the query parameters for required fields and URL
     *
     * @param query Map of query parameters
     * @throws SerpApiSearchException if required parameters are missing or URL is invalid
     */
    private void validateQueryParameters(Map<String, String> query) throws SerpApiSearchException {
        for (String param : REQUIRED_PARAMETERS) {
            if (!query.containsKey(param)) {
                throw new SerpApiSearchException("Missing required parameter: " + param);
            }
        }

        val urlBuilder =
                Objects.requireNonNull(HttpUrl.parse(APIUrls.SERP_API.getUrl() + ".json")).newBuilder();

        query.forEach(urlBuilder::addQueryParameter);

        try {
            HttpUrl url = urlBuilder.build();
            if (!url.isHttps()) {
                throw new SerpApiSearchException("Invalid URL: " + urlBuilder.build());
            }
        } catch (Exception e) {
            throw new SerpApiSearchException("URL construction error: " + e.getMessage());
        }
    }

    /**
     * Get results from SerpApi using OkHttp
     *
     * @param query Map of query parameters
     * @return String containing the response body
     * @throws IOException wraps HTTP errors
     */
    public String getResults(Map<String, String> query) throws IOException, SerpApiSearchException {
        try {
            validateQueryParameters(query);

            val urlBuilder =
                    Objects.requireNonNull(HttpUrl.parse(APIUrls.SERP_API.getUrl() + ".json")).newBuilder();

            query.forEach(urlBuilder::addQueryParameter);

            var request = new Request.Builder().url(urlBuilder.build()).build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException(MystiGuardianUtils.handleAPIError(response));
                }
                return response.body().string();
            }
        } catch (SerpApiSearchException e) {
            MystiGuardianUtils.logger.error("Query validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * @return current secret API key
     */
    public static Optional<String> getApiKey() {
        return Optional.ofNullable(api_key_default);
    }

    /**
     * Get JSON output
     *
     * @return JsonNode parent node
     * @throws SerpApiSearchException wraps backend error message
     * @throws IOException wraps HTTP errors
     */
    public JsonNode getJson() throws SerpApiSearchException, IOException {
        var query = buildQuery();
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
     * Get location
     *
     * @param q query
     * @param limit number of locations
     * @return JsonNode containing location data
     * @throws SerpApiSearchException wraps backend error message
     * @throws IOException wraps HTTP errors
     */
    public JsonNode getLocation(String q, Integer limit) throws SerpApiSearchException, IOException {
        var query = buildQuery();
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
        var query = buildQuery();
        query.remove("output");
        query.remove("q");
        query.put("search_id", searchID);
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
        var query = buildQuery();
        query.remove("output");
        query.remove("q");
        var jsonResponse = getResults(query);
        return asJson(jsonResponse);
    }
}
