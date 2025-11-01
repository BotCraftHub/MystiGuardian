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
package io.github.yusufsdiscordbot.mystiguardian.api.scraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.api.job.HigherinApprenticeship;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Scraper specifically for Higher In (Rate My Apprenticeship) apprenticeships. Handles scraping
 * from higherin.com across multiple category pages.
 */
@Slf4j
public record HigherinScraper(OkHttpClient client, ObjectMapper mapper) {

    public static final String BASE_URL =
            "https://www.higherin.com/search-jobs/degree-apprenticeship/";

    private static final List<String> CATEGORIES =
            Arrays.asList(

                    // Technology related categories
                    "computer-science",
                    "cyber-security",
                    "data-analysis",
                    "front-end-development",
                    "information-technology",
                    "software-engineering",
                    "artificial-intelligence",

                    // Accountancy and tax
                    "accounting",
                    "actuary",
                    "audit",
                    "tax",

                    // Banking
                    "banking",
                    "commercial-banking",
                    "investment-banking",
                    "retail-banking",

                    // Business
                    "business-management",
                    "business-operations",
                    "management-consulting",
                    "market-research",
                    "procurement",
                    "project-management",
                    "sales",
                    "sustainability",

                    // Construction and trades
                    "construction",
                    "carpentry-and-joinery",
                    "electrician",
                    "plumbing",

                    // Design
                    "architecture",
                    "fashion-design",
                    "graphic-design",
                    "product-design",
                    "ux-ui-design",

                    // Engineering and Manufacturing
                    "aeronautical-and-aerospace-engineering",
                    "automotive-engineering",
                    "chemical-engineering",
                    "civil-engineering",
                    "computer-systems-engineering",
                    "electronic-and-electrical-engineering",
                    "engineering",
                    "manufacturing",
                    "material-and-mineral-engineering",
                    "mechanical-engineering",

                    // Financial services
                    "economics",
                    "finances",
                    "insurance-and-risk-management",

                    // FMCG and Retail
                    "consumer-product-fmcg",
                    "consumer-services",
                    "retail-manager",
                    "merchandising",

                    // Hospitality
                    "hospitality-management",
                    "bar-and-waiting",
                    "catering",

                    // HR and Recruitment
                    "human-resources",
                    "recruitment",

                    // Legal and Law
                    "commercial-law",
                    "corporate-law",
                    "employment-law",
                    "intellectual-property-law",
                    "legal-law",

                    // Marketing
                    "advertising",
                    "digital-marketing",
                    "marketing",
                    "pr-and-communications",
                    "social-media-marketing",

                    // Property
                    "property-development",
                    "property-management",
                    "surveying",
                    "property-planning",

                    // Public Sector
                    "teaching",
                    "government",
                    "social-work",
                    "armed-forces",
                    "prison-officer",
                    "healthcare",
                    "firefighter",
                    "police-officer",

                    // Science
                    "chemistry",
                    "environmental-science",
                    "medicine",
                    "pharmaceutical",
                    "research",
                    "science");

    private static final int BATCH_SIZE = 10; // Process categories in batches

    /**
     * Scrapes all Higher In apprenticeships across all configured categories.
     *
     * @return List of unique Higher In apprenticeships
     */
    public List<HigherinApprenticeship> scrapeApprenticeships() {
        Map<String, HigherinApprenticeship> uniqueApprenticeships = new HashMap<>();

        logger.info("Starting Higher In scraping across {} categories", CATEGORIES.size());

        // Process categories in batches to reduce memory pressure
        for (int i = 0; i < CATEGORIES.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, CATEGORIES.size());
            List<String> batch = CATEGORIES.subList(i, endIndex);

            logger.info(
                    "Processing batch {}/{} ({} categories)",
                    (i / BATCH_SIZE) + 1,
                    (CATEGORIES.size() + BATCH_SIZE - 1) / BATCH_SIZE,
                    batch.size());

            for (String category : batch) {
                try {
                    scrapeCategory(category, uniqueApprenticeships);
                } catch (Exception e) {
                    logger.error("Failed to scrape Higher In category {}: {}", category, e.getMessage());
                }
            }

            // Hint to JVM that GC would be appropriate after every 3 batches
            if (i % (BATCH_SIZE * 3) == 0) {
                System.gc();
            }

            // Rate limiting between batches (only if not the last batch)
            if (endIndex < CATEGORIES.size()) {
                rateLimitDelay(500);
            }
        }

        logger.info(
                "Completed Higher In scraping. Total unique apprenticeships: {}",
                uniqueApprenticeships.size());
        return new ArrayList<>(uniqueApprenticeships.values());
    }

    private void scrapeCategory(
            String category, Map<String, HigherinApprenticeship> uniqueApprenticeships)
            throws IOException {
        String url = BASE_URL + category;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // 404 means no apprenticeships available for this category - not an error
                if (response.code() == 404) {
                    logger.debug("No apprenticeships found for category: {}", category);
                } else {
                    logger.warn("Failed to fetch category {}: {}", category, response.code());
                }
                return;
            }

            String html = response.body().string();
            String jsonData = extractJsonData(html);

            // Clear HTML from memory immediately
            html = null;

            JsonNode root = mapper.readTree(jsonData);

            // Clear JSON string from memory
            jsonData = null;

            JsonNode apprenticeships = root.get("data");

            if (apprenticeships != null && apprenticeships.isArray()) {
                for (JsonNode apprenticeshipNode : apprenticeships) {
                    String apprenticeshipId = getJsonText(apprenticeshipNode, "id");

                    if (apprenticeshipId == null || apprenticeshipId.isEmpty()) {
                        continue;
                    }

                    // Only create apprenticeship object if it's new
                    // The categories are already correctly set from the API's relevantFor field
                    if (!uniqueApprenticeships.containsKey(apprenticeshipId)) {
                        HigherinApprenticeship newApprenticeship =
                                createApprenticeship(apprenticeshipNode, apprenticeshipId, category);
                        uniqueApprenticeships.put(apprenticeshipId, newApprenticeship);
                    }
                }
            }

            // Clear root node
            root = null;
        }
    }

    private HigherinApprenticeship createApprenticeship(
            JsonNode apprenticeshipNode, String apprenticeshipId, String category) {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();

        // New JSON structure uses camelCase field names
        apprenticeship.setId(apprenticeshipId);

        // Changed from "title" to "jobTitle"
        apprenticeship.setTitle(getJsonText(apprenticeshipNode, "jobTitle"));

        // Company info is now direct fields, not nested under "company"
        apprenticeship.setCompanyName(getJsonText(apprenticeshipNode, "companyName", "Not Available"));
        apprenticeship.setCompanyLogo(getJsonText(apprenticeshipNode, "smallLogo", "Not Available"));

        // Changed from "jobLocations" to "jobLocationNames"
        apprenticeship.setLocation(getJsonText(apprenticeshipNode, "jobLocationNames"));
        apprenticeship.setSalary(getJsonText(apprenticeshipNode, "salary", "Not specified"));
        apprenticeship.setUrl(getJsonText(apprenticeshipNode, "url"));
        apprenticeship.setCategory(category);

        // Get actual apprenticeship categories from the API's relevantFor field
        String relevantFor = getJsonText(apprenticeshipNode, "relevantFor");
        if (relevantFor != null && !relevantFor.isEmpty()) {
            // Split by comma and trim each category
            List<String> actualCategories =
                    Arrays.stream(relevantFor.split(","))
                            .map(String::trim)
                            .filter(cat -> !cat.isEmpty())
                            .collect(Collectors.toList());
            apprenticeship.setCategories(actualCategories);
        }

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
