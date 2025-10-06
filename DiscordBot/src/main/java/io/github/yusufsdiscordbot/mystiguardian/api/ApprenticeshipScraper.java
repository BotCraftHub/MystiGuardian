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
import io.github.yusufsdiscordbot.mystiguardian.api.job.FindAnApprenticeshipJob;
import io.github.yusufsdiscordbot.mystiguardian.api.job.HigherinJob;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class ApprenticeshipScraper {

    public static final String HIGHERIN_BASE_URL =
            "https://www.higherin.com/search-jobs/degree-apprenticeship/";

    public static final String FIND_AN_APPRENTICESHIP_BASE_URL =
            "https://www.findapprenticeship.service.gov.uk/apprenticeships?sort=DistanceAsc&searchTerm=&location=&distance=all&levelIds=6&routeIds=7";

    private static final List<String> HIGHERIN_CATEGORIES =
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
                    "fiances",
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
                    "property=planning",

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

    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final int BATCH_SIZE = 10; // Process categories in batches

    public ApprenticeshipScraper() {
        this.client =
                new OkHttpClient.Builder()
                        .connectionPool(new okhttp3.ConnectionPool(5, 5, java.util.concurrent.TimeUnit.MINUTES))
                        .build();
    }

    public List<HigherinJob> scrapeRateMyApprenticeshipJobs() throws IOException {
        Map<String, HigherinJob> uniqueJobs = new HashMap<>();
        Map<String, Set<String>> jobCategories = new HashMap<>();

        // Process categories in batches to reduce memory pressure
        for (int i = 0; i < HIGHERIN_CATEGORIES.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, HIGHERIN_CATEGORIES.size());
            List<String> batch = HIGHERIN_CATEGORIES.subList(i, endIndex);

            for (String category : batch) {
                String url = HIGHERIN_BASE_URL + category;
                Request request = new Request.Builder().url(url).build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        // 404 means no jobs available for this category - not an error
                        if (response.code() == 404) {
                            logger.debug("No jobs found for category: {}", category);
                        } else {
                            logger.warn("Failed to fetch category {}: {}", category, response.code());
                        }
                        continue;
                    }

                    String html = response.body().string();
                    String jsonData = extractJsonData(html);

                    // Clear HTML from memory immediately
                    html = null;

                    JsonNode root = mapper.readTree(jsonData);

                    // Clear JSON string from memory
                    jsonData = null;

                    JsonNode jobs = root.get("data");

                    if (jobs != null && jobs.isArray()) {
                        for (JsonNode jobNode : jobs) {
                            String jobId = getJsonText(jobNode, "id");

                            if (jobId == null || jobId.isEmpty()) {
                                continue;
                            }

                            // Only create job object if it's new
                            if (!uniqueJobs.containsKey(jobId)) {
                                HigherinJob newJob = createHigherinJob(jobNode, jobId, category);
                                uniqueJobs.put(jobId, newJob);
                                jobCategories.put(jobId, new HashSet<>());
                            }

                            jobCategories.get(jobId).add(category);
                        }
                    }

                    // Clear root node
                    root = null;

                } catch (Exception e) {
                    logger.error("Failed to scrape category {}: {}", category, e.getMessage());
                }
            }

            // Small delay between batches and suggest GC
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Hint to JVM that GC would be appropriate
            if (i % (BATCH_SIZE * 3) == 0) {
                System.gc();
            }
        }

        // Set categories for each job
        uniqueJobs.forEach(
                (jobId, job) -> {
                    Set<String> categories = jobCategories.get(jobId);
                    if (categories != null) {
                        job.setCategories(new ArrayList<>(categories));
                    }
                });

        // Clear the temporary map
        jobCategories.clear();

        return new ArrayList<>(uniqueJobs.values());
    }

    private HigherinJob createHigherinJob(JsonNode jobNode, String jobId, String category) {
        HigherinJob newJob = new HigherinJob();

        // New JSON structure uses camelCase field names
        newJob.setId(jobId);

        // Changed from "title" to "jobTitle"
        newJob.setTitle(getJsonText(jobNode, "jobTitle"));

        // Company info is now direct fields, not nested under "company"
        newJob.setCompanyName(getJsonText(jobNode, "companyName", "Not Available"));
        newJob.setCompanyLogo(getJsonText(jobNode, "smallLogo", "Not Available"));

        // Changed from "jobLocations" to "jobLocationNames"
        newJob.setLocation(getJsonText(jobNode, "jobLocationNames"));
        newJob.setSalary(getJsonText(jobNode, "salary", "Not specified"));
        newJob.setUrl(getJsonText(jobNode, "url"));
        newJob.setCategory(category);

        // Get actual job categories from the API's relevantFor field
        String relevantFor = getJsonText(jobNode, "relevantFor");
        if (relevantFor != null && !relevantFor.isEmpty()) {
            // Split by comma and trim each category
            List<String> actualCategories = Arrays.stream(relevantFor.split(","))
                    .map(String::trim)
                    .filter(cat -> !cat.isEmpty())
                    .collect(Collectors.toList());
            newJob.setCategories(actualCategories);
        }

        String deadline = getJsonText(jobNode, "deadline");
        if (deadline != null && !deadline.isEmpty()) {
            try {
                newJob.setClosingDate(parseRateMyApprenticeshipDate(deadline));
            } catch (Exception e) {
                logger.error("Failed to parse date for job {}: {}", jobId, e.getMessage());
            }
        }

        return newJob;
    }

    public List<FindAnApprenticeshipJob> scrapeFindAnApprenticeshipJobs() throws IOException {
        List<FindAnApprenticeshipJob> allJobs = new ArrayList<>();
        int pageNumber = 1;
        boolean hasMorePages = true;
        int consecutiveErrors = 0;
        final int MAX_CONSECUTIVE_ERRORS = 3;

        while (hasMorePages && consecutiveErrors < MAX_CONSECUTIVE_ERRORS) {
            String pageUrl =
                    String.format("%s&pageNumber=%d", FIND_AN_APPRENTICESHIP_BASE_URL, pageNumber);

            Request request =
                    new Request.Builder().url(pageUrl).header("User-Agent", "Mozilla/5.0").build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    consecutiveErrors++;
                    logger.warn("Failed to fetch page {}: {}", pageNumber, response.code());
                    continue;
                }

                String html = response.body().string();
                Document doc = Jsoup.parse(html);

                // Clear HTML string from memory
                html = null;

                Elements jobListings = doc.select("li.das-search-results__list-item");

                if (jobListings.isEmpty()) {
                    hasMorePages = false;
                    continue;
                }

                // Reset consecutive errors on success
                consecutiveErrors = 0;

                for (Element listing : jobListings) {
                    try {
                        FindAnApprenticeshipJob job = createFindAnApprenticeshipJob(listing);
                        if (job != null && job.getId() != null) {
                            allJobs.add(job);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to parse job listing on page {}: {}", pageNumber, e.getMessage());
                    }
                }

                // Clear document from memory
                doc = null;

                pageNumber++;

                // Rate limiting
                Thread.sleep(1000);

                // Periodic GC hint for long scraping sessions
                if (pageNumber % 10 == 0) {
                    System.gc();
                    logger.info("Processed {} pages, {} jobs found", pageNumber - 1, allJobs.size());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                hasMorePages = false;
            } catch (Exception e) {
                consecutiveErrors++;
                logger.error("Failed to process page {}: {}", pageNumber, e.getMessage());
            }
        }

        if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
            logger.error("Stopped scraping after {} consecutive errors", consecutiveErrors);
        }

        return allJobs;
    }

    private FindAnApprenticeshipJob createFindAnApprenticeshipJob(Element listing) {
        FindAnApprenticeshipJob job = new FindAnApprenticeshipJob();

        Element linkElement = listing.selectFirst("a.das-search-results__link");
        if (linkElement != null) {
            String href = linkElement.attr("href");
            String id = href.substring(href.lastIndexOf("/") + 1);
            job.setId(id);
            job.setName(linkElement.text().trim());
            job.setUrl("https://www.findapprenticeship.service.gov.uk" + href);
        }

        Elements paragraphs = listing.select("p.govuk-body");
        if (!paragraphs.isEmpty()) {
            job.setCompanyName(paragraphs.first().text().trim());
        }

        if (paragraphs.size() > 1) {
            job.setLocation(paragraphs.get(1).text().trim());
        }

        Element salaryElement = listing.selectFirst("p:contains(Wage)");
        if (salaryElement != null) {
            String salary = salaryElement.text().replace("Wage", "").trim();
            job.setSalary(salary);
        }

        Element closingDateElement = listing.selectFirst("p:contains(Closes)");
        if (closingDateElement != null) {
            String closingDateStr = closingDateElement.text();
            LocalDate closingDate = parseFindAnApprenticeshipDate(closingDateStr);
            job.setClosingDate(closingDate);
        }

        Element postedDateElement = listing.selectFirst("p:contains(Posted)");
        if (postedDateElement != null) {
            String postedDateStr = postedDateElement.text();
            LocalDate postedDate = parseFindAnApprenticeshipDate(postedDateStr);
            job.setCreatedAtDate(postedDate);
        }

        return job;
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

    private LocalDate parseRateMyApprenticeshipDate(String dateStr) {
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

    private LocalDate parseFindAnApprenticeshipDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            // Clean the date string
            String cleanDate =
                    dateStr
                            .replace("Closes in", "")
                            .replace("Posted", "")
                            .replace("Closes on", "")
                            .replaceAll("\\d+ days", "")
                            .replaceAll("\\(|\\)", "")
                            .trim();

            // Split into parts
            String[] parts = cleanDate.split("\\s+");

            // Check if year is already present (last part is a 4-digit number)
            boolean hasYear = parts.length > 0 && parts[parts.length - 1].matches("\\d{4}");

            String day, month;
            int year;

            if (hasYear) {
                // Format: "Friday 17 October 2025" or "17 October 2025" or "10 September 2025"
                year = Integer.parseInt(parts[parts.length - 1]);
                month = parts[parts.length - 2];

                if (parts.length >= 3) {
                    // Find the day number (the part that's just digits, not the year)
                    day = parts[parts.length - 3];
                } else {
                    throw new IllegalArgumentException("Unexpected date format: " + dateStr);
                }
            } else {
                // Format without year: "Sunday 5 January" or "5 January"
                if (parts.length == 3) {
                    // Has day name: ["Sunday", "5", "January"]
                    day = parts[1];
                    month = parts[2];
                } else if (parts.length == 2) {
                    // No day name: ["5", "January"]
                    day = parts[0];
                    month = parts[1];
                } else {
                    throw new IllegalArgumentException("Unexpected date format: " + dateStr);
                }

                year = LocalDate.now().getYear();
            }

            String fullDateStr = String.format("%s %s %d", day, month, year);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK);
            LocalDate date = LocalDate.parse(fullDateStr, formatter);

            // Only adjust year if it wasn't explicitly provided and the date is in the past
            if (!hasYear && date.isBefore(LocalDate.now())) {
                date = date.plusYears(1);
            }

            return date;
        } catch (Exception e) {
            logger.debug("Date string before parsing: '{}'", dateStr);
            logger.error("Failed to parse date '{}': {}", dateStr, e.getMessage());
            return null;
        }
    }
}
