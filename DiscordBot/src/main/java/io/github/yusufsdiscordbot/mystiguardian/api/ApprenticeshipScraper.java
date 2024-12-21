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
package io.github.yusufsdiscordbot.mystiguardian.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.api.job.Job;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApprenticeshipScraper {
    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    public static final String BASE_URL =
            "https://www.ratemyapprenticeship.co.uk/search-jobs/degree-apprenticeship/it/";
    private static final List<String> CATEGORIES =
            Arrays.asList(
                    "computer-science", "cyber-security", "data-analysis", "information-technology");

    public ApprenticeshipScraper() {
        this.client = new OkHttpClient();
    }

    public List<Job> scrapeJobs() throws IOException {
        List<Job> allJobs = new ArrayList<>();
        for (String category : CATEGORIES) {
            String url = BASE_URL + category;
            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                String html = response.body().string();
                String jsonData = extractJsonData(html);
                JsonNode root = mapper.readTree(jsonData);
                JsonNode jobs = root.get("data");
                for (JsonNode jobNode : jobs) {
                    JsonNode company = jobNode.get("company");

                    Job job = new Job();

                    job.setId(getJsonText(jobNode, "id"));
                    job.setTitle(getJsonText(jobNode, "title"));
                    job.setCompanyName(getJsonText(company, "name", "Not Available"));
                    job.setCompanyLogo(getJsonText(company, "small_logo", "Not Available"));
                    job.setLocation(getJsonText(jobNode, "jobLocations"));
                    job.setCategory(category);
                    job.setSalary(getJsonText(jobNode, "salary", "Not specified"));
                    job.setUrl(getJsonText(jobNode, "url"));

                    String deadline = getJsonText(jobNode, "deadline");
                    if (deadline != null && !deadline.isEmpty()) {
                        try {
                            job.setClosingDate(parseDate(deadline));
                        } catch (Exception e) {
                            MystiGuardianUtils.logger.error(e.getMessage());
                        }
                    }
                    allJobs.add(job);
                }
            }
        }
        return allJobs;
    }

    private String getJsonText(JsonNode node, String fieldName) {
        return getJsonText(node, fieldName, null);
    }

    private String getJsonText(JsonNode node, String fieldName, String defaultValue) {
        JsonNode field = node.get(fieldName);
        return field != null ? field.asText(defaultValue) : defaultValue;
    }

    private String extractJsonData(String html) {
        String searchString = "window.__RMP_SEARCH_RESULTS_INITIAL_STATE__ = ";
        int startIndex = html.indexOf(searchString);

        if (startIndex == -1) {
            throw new IllegalStateException("Could not find search results data in HTML");
        }

        startIndex += searchString.length();

        // Look for various possible end patterns
        int endIndex = -1;
        String[] endPatterns = {";</script>", ";\n</script>", "};"};

        for (String pattern : endPatterns) {
            int index = html.indexOf(pattern, startIndex);
            if (index != -1) {
                endIndex = index;
                break;
            }
        }

        if (endIndex == -1) {
            throw new IllegalStateException("Could not find end of JSON data");
        }

        String json = html.substring(startIndex, endIndex);
        if (!json.trim().startsWith("{")) {
            throw new IllegalStateException("Extracted data is not valid JSON");
        }

        return json;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) return null;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            try {
                DateTimeFormatter altFormatter =
                        DateTimeFormatter.ofPattern("dd['st']['nd']['rd']['th'] MMMM yyyy", Locale.ENGLISH);
                return LocalDate.parse(dateStr, altFormatter);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
