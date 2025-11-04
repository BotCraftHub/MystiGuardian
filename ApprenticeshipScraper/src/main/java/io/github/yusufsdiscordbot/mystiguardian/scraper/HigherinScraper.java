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
package io.github.yusufsdiscordbot.mystiguardian.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.ApprenticeshipSource;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.HigherinApprenticeship;
import io.github.yusufsdiscordbot.mystiguardian.categories.HigherinCategories;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Scraper for Higher In (formerly Rate My Apprenticeship) apprenticeships.
 *
 * <p>This scraper extracts apprenticeship data from higherin.com by:
 *
 * <ul>
 *   <li>Iterating through 80+ predefined categories across multiple sectors
 *   <li>Extracting JSON data embedded in HTML pages
 *   <li>Processing apprenticeships in batches to manage memory efficiently
 *   <li>Deduplicating apprenticeships that appear in multiple categories
 *   <li>Implementing rate limiting to respect the source website
 * </ul>
 *
 * <p>The scraper handles multiple sectors including:
 *
 * <ul>
 *   <li>Technology (Software Engineering, Cyber Security, AI, etc.)
 *   <li>Finance (Accounting, Banking, Insurance)
 *   <li>Engineering (Civil, Mechanical, Aerospace, etc.)
 *   <li>Business &amp; Management
 *   <li>Legal, Marketing, Healthcare, and more
 * </ul>
 *
 * <p>This is a record class that requires an {@link OkHttpClient} for HTTP requests and an {@link
 * ObjectMapper} for JSON parsing.
 *
 * @param client the HTTP client for making requests to Higher In
 * @param mapper the JSON mapper for parsing apprenticeship data
 * @see HigherinApprenticeship
 * @see HigherinCategories
 * @see ApprenticeshipSource#RATE_MY_APPRENTICESHIP
 */
@Slf4j
public record HigherinScraper(OkHttpClient client, ObjectMapper mapper) {

    /** Base URL for Higher In degree apprenticeship search pages. */
    public static final String BASE_URL =
            "https://www.higherin.com/search-jobs/degree-apprenticeship/";

    /** Number of categories to process in each batch for memory efficiency. */
    private static final int BATCH_SIZE = 10;

    /**
     * Scrapes all Higher In apprenticeships across all configured categories.
     *
     * <p>This method:
     *
     * <ul>
     *   <li>Processes categories in batches to manage memory
     *   <li>Deduplicates apprenticeships by ID
     *   <li>Implements rate limiting between batches
     *   <li>Suggests garbage collection after processing multiple batches
     * </ul>
     *
     * @return List of unique Higher In apprenticeships from all categories
     */
    public List<HigherinApprenticeship> scrapeApprenticeships() {
        Map<String, HigherinApprenticeship> uniqueApprenticeships = new HashMap<>();
        List<String> categories = HigherinCategories.getAllCategories();

        logger.info("Starting Higher In scraping across {} categories", categories.size());

        for (int i = 0; i < categories.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, categories.size());
            List<String> batch = categories.subList(i, endIndex);

            logger.info(
                    "Processing batch {}/{} ({} categories)",
                    (i / BATCH_SIZE) + 1,
                    (categories.size() + BATCH_SIZE - 1) / BATCH_SIZE,
                    batch.size());

            for (String category : batch) {
                try {
                    scrapeCategory(category, uniqueApprenticeships);
                } catch (Exception e) {
                    logger.error("Failed to scrape Higher In category {}: {}", category, e.getMessage());
                }
            }

            if (i % (BATCH_SIZE * 3) == 0) {
                System.gc();
            }

            if (endIndex < categories.size()) {
                rateLimitDelay(500);
            }
        }

        logger.info(
                "Completed Higher In scraping. Total unique apprenticeships: {}",
                uniqueApprenticeships.size());
        return new ArrayList<>(uniqueApprenticeships.values());
    }

    /**
     * Scrapes a single category page from Higher In.
     *
     * <p>Handles HTTP responses, JSON extraction, and adds apprenticeships to the unique collection.
     * Gracefully handles 404 responses (no apprenticeships in category).
     *
     * @param category the category slug to scrape (e.g., "software-engineering")
     * @param uniqueApprenticeships map to store deduplicated apprenticeships
     * @throws IOException if HTTP request fails
     */
    private void scrapeCategory(
            String category, Map<String, HigherinApprenticeship> uniqueApprenticeships)
            throws IOException {
        String url = BASE_URL + category;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                if (response.code() == 404) {
                    logger.debug("No apprenticeships found for category: {}", category);
                } else {
                    logger.warn("Failed to fetch category {}: {}", category, response.code());
                }
                return;
            }

            String html = response.body().string();
            String jsonData = extractJsonData(html);

            JsonNode root = mapper.readTree(jsonData);

            JsonNode apprenticeships = root.get("data");

            if (apprenticeships != null && apprenticeships.isArray()) {
                for (JsonNode apprenticeshipNode : apprenticeships) {
                    String apprenticeshipId = getJsonText(apprenticeshipNode, "id");

                    if (apprenticeshipId == null || apprenticeshipId.isEmpty()) {
                        continue;
                    }

                    if (!uniqueApprenticeships.containsKey(apprenticeshipId)) {
                        HigherinApprenticeship newApprenticeship =
                                createApprenticeship(apprenticeshipNode, apprenticeshipId, category);
                        uniqueApprenticeships.put(apprenticeshipId, newApprenticeship);
                    }
                }
            }
        }
    }

    /**
     * Creates a HigherinApprenticeship object from JSON data.
     *
     * <p>Maps JSON fields to apprenticeship properties including:
     *
     * <ul>
     *   <li>Basic info (ID, title, company, location)
     *   <li>Company logo and salary
     *   <li>Categories from the API's "relevantFor" field
     *   <li>Closing date with flexible parsing
     * </ul>
     *
     * @param apprenticeshipNode the JSON node containing apprenticeship data
     * @param apprenticeshipId the unique apprenticeship identifier
     * @param category the category this apprenticeship was found in
     * @return a fully populated HigherinApprenticeship object
     */
    private HigherinApprenticeship createApprenticeship(
            JsonNode apprenticeshipNode, String apprenticeshipId, String category) {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();

        apprenticeship.setId(apprenticeshipId);

        apprenticeship.setTitle(getJsonText(apprenticeshipNode, "jobTitle"));

        apprenticeship.setCompanyName(getJsonText(apprenticeshipNode, "companyName", "Not Available"));
        apprenticeship.setCompanyLogo(getJsonText(apprenticeshipNode, "smallLogo", "Not Available"));

        apprenticeship.setLocation(getJsonText(apprenticeshipNode, "jobLocationNames"));
        apprenticeship.setSalary(getJsonText(apprenticeshipNode, "salary", "Not specified"));
        apprenticeship.setUrl(getJsonText(apprenticeshipNode, "url"));
        apprenticeship.setCategory(category);

        // Use the search category slug as the actual category instead of relevantFor field
        // The relevantFor field contains academic year info (3rd-year, 4th-year) which is not useful
        // for categorization
        // The category parameter contains the actual topic category (software-engineering,
        // cyber-security, etc.)
        apprenticeship.setCategories(Collections.singletonList(category));

        String deadline = getJsonText(apprenticeshipNode, "deadline");
        if (deadline != null && !deadline.isEmpty()) {
            try {
                apprenticeship.setClosingDate(parseDate(deadline));
            } catch (Exception e) {
                logger.error(
                        "Failed to parse date for apprenticeship {}: {}", apprenticeshipId, e.getMessage());
            }
        }

        return apprenticeship;
    }

    /**
     * Safely extracts text from a JSON field.
     *
     * @param node the JSON node to extract from
     * @param fieldName the field name to extract
     * @return the field value as string, or null if not present
     */
    private String getJsonText(JsonNode node, String fieldName) {
        return getJsonText(node, fieldName, null);
    }

    /**
     * Safely extracts text from a JSON field with a default value.
     *
     * @param node the JSON node to extract from
     * @param fieldName the field name to extract
     * @param defaultValue the value to return if field is missing
     * @return the field value as string, or defaultValue if not present
     */
    private String getJsonText(JsonNode node, String fieldName, String defaultValue) {
        JsonNode field = node.get(fieldName);
        return field != null ? field.asText(defaultValue) : defaultValue;
    }

    /**
     * Extracts JSON data embedded in HTML page.
     *
     * <p>Higher In embeds search results in a JavaScript variable within the HTML. This method
     * extracts that JSON data by finding the variable assignment and parsing the JSON string.
     *
     * @param html the HTML page source
     * @return extracted JSON string
     * @throws IllegalStateException if JSON data cannot be found or extracted
     */
    private String extractJsonData(String html) {
        String searchString = "window.__RMP_SEARCH_RESULTS_INITIAL_STATE__ = ";
        int startIndex = html.indexOf(searchString);

        if (startIndex == -1) {
            throw new IllegalStateException("Could not find search results data in HTML");
        }

        startIndex += searchString.length();

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

    /**
     * Parses a date string with multiple format support.
     *
     * <p>Attempts to parse dates in the following formats:
     *
     * <ul>
     *   <li>yyyy-MM-dd (ISO format)
     *   <li>dd[st/nd/rd/th] MMMM yyyy (e.g., "1st January 2024")
     * </ul>
     *
     * @param dateStr the date string to parse
     * @return parsed LocalDate, or null if parsing fails
     */
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

    /**
     * Helper method for rate limiting delays between batches. Extracted to a separate method to
     * clarify intent and avoid busy-waiting warnings.
     *
     * @param milliseconds the delay in milliseconds
     */
    private void rateLimitDelay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Rate limit delay interrupted");
        }
    }
}
