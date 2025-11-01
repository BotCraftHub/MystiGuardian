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
package io.github.yusufsdiscordbot.mystiguardian.api.scrapper;

import io.github.yusufsdiscordbot.mystiguardian.api.job.FindAnApprenticeshipJob;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scraper specifically for GOV.UK Find an Apprenticeship service. Handles scraping from
 * findapprenticeship.service.gov.uk across all route categories.
 */
@Slf4j
public record FindAnApprenticeshipScraper(OkHttpClient client) {

    public static final String BASE_URL =
            "https://www.findapprenticeship.service.gov.uk/apprenticeships?sort=DistanceAsc&searchTerm=&location=&distance=all&levelIds=6&routeIds=";

    // Find an Apprenticeship route IDs mapping
    // Route IDs correspond to different apprenticeship categories on GOV.UK
    private static final Map<String, Integer> ROUTE_CATEGORIES =
            Map.ofEntries(
                    Map.entry("Agriculture, environmental and animal care", 1),
                    Map.entry("Business and administration", 2),
                    Map.entry("Care services", 3),
                    Map.entry("Catering and hospitality", 4),
                    Map.entry("Construction and the built environment", 5),
                    Map.entry("Creative and design", 6),
                    Map.entry("Digital", 7),
                    Map.entry("Education and early years", 8),
                    Map.entry("Engineering and manufacturing", 9),
                    Map.entry("Hair and beauty", 10),
                    Map.entry("Health and science", 11),
                    Map.entry("Legal, finance and accounting", 12),
                    Map.entry("Protective services", 13),
                    Map.entry("Sales, marketing and procurement", 14),
                    Map.entry("Transport and logistics", 15));

    private static final int MAX_CONSECUTIVE_ERRORS = 3;

    /**
     * Scrapes all Find an Apprenticeship listings across all route categories.
     *
     * @return List of unique Find an Apprenticeship jobs
     */
    public List<FindAnApprenticeshipJob> scrapeApprenticeships() {
        Map<String, FindAnApprenticeshipJob> uniqueApprenticeships = new HashMap<>();

        logger.info(
                "Starting Find an Apprenticeship scraping from {} categories", ROUTE_CATEGORIES.size());

        // Iterate through all route IDs
        for (Map.Entry<String, Integer> route : ROUTE_CATEGORIES.entrySet()) {
            String categoryName = route.getKey();
            int routeId = route.getValue();

            try {
                scrapeCategory(categoryName, routeId, uniqueApprenticeships);
            } catch (Exception e) {
                logger.error("Failed to scrape category {}: {}", categoryName, e.getMessage());
            }

            // Rate limiting between categories
            rateLimitDelay(2000);
        }

        logger.info(
                "Completed Find an Apprenticeship scraping. Total unique apprenticeships: {}",
                uniqueApprenticeships.size());
        return new ArrayList<>(uniqueApprenticeships.values());
    }

    private void scrapeCategory(
            String categoryName,
            int routeId,
            Map<String, FindAnApprenticeshipJob> uniqueApprenticeships) {

        logger.info("Scraping category: {} (routeId={})", categoryName, routeId);

        int pageNumber = 1;
        boolean hasMorePages = true;
        int consecutiveErrors = 0;

        while (hasMorePages && consecutiveErrors < MAX_CONSECUTIVE_ERRORS) {
            String pageUrl = String.format("%s%d&pageNumber=%d", BASE_URL, routeId, pageNumber);

            Request request =
                    new Request.Builder().url(pageUrl).header("User-Agent", "Mozilla/5.0").build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    consecutiveErrors++;
                    logger.warn(
                            "Failed to fetch page {} for category {}: {}",
                            pageNumber,
                            categoryName,
                            response.code());
                    continue;
                }

                String html = response.body().string();
                Document doc = Jsoup.parse(html);

                // Clear HTML string from memory
                html = null;

                Elements apprenticeshipListings = doc.select("li.das-search-results__list-item");

                if (apprenticeshipListings.isEmpty()) {
                    hasMorePages = false;
                    continue;
                }

                // Reset consecutive errors on success
                consecutiveErrors = 0;

                for (Element listing : apprenticeshipListings) {
                    try {
                        FindAnApprenticeshipJob apprenticeship = parseApprenticeship(listing);
                        if (apprenticeship != null && apprenticeship.getId() != null) {
                            // Only add if not already present (avoid duplicates across categories)
                            if (!uniqueApprenticeships.containsKey(apprenticeship.getId())) {
                                apprenticeship.setCategory(categoryName);
                                uniqueApprenticeships.put(apprenticeship.getId(), apprenticeship);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(
                                "Failed to parse apprenticeship listing on page {} for category {}: {}",
                                pageNumber,
                                categoryName,
                                e.getMessage());
                    }
                }

                // Clear document from memory
                doc = null;

                pageNumber++;

                // Rate limiting between page requests
                rateLimitDelay(1000);

                // Periodic GC hint for long scraping sessions
                if (pageNumber % 10 == 0) {
                    System.gc();
                    logger.info(
                            "Processed {} pages for category {}, {} total apprenticeships found",
                            pageNumber - 1,
                            categoryName,
                            uniqueApprenticeships.size());
                }

            } catch (Exception e) {
                consecutiveErrors++;
                logger.error(
                        "Failed to process page {} for category {}: {}",
                        pageNumber,
                        categoryName,
                        e.getMessage());
            }
        }

        if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
            logger.error(
                    "Stopped scraping category {} after {} consecutive errors",
                    categoryName,
                    consecutiveErrors);
        }

        logger.info(
                "Completed scraping category: {}. Found {} apprenticeships so far",
                categoryName,
                uniqueApprenticeships.size());
    }

    private FindAnApprenticeshipJob parseApprenticeship(Element listing) {
        FindAnApprenticeshipJob apprenticeship = new FindAnApprenticeshipJob();

        Element linkElement = listing.selectFirst("a.das-search-results__link");
        if (linkElement != null) {
            String href = linkElement.attr("href");
            String id = href.substring(href.lastIndexOf("/") + 1);
            apprenticeship.setId(id);
            apprenticeship.setName(linkElement.text().trim());
            apprenticeship.setUrl("https://www.findapprenticeship.service.gov.uk" + href);
        }

        Elements paragraphs = listing.select("p.govuk-body");
        if (!paragraphs.isEmpty()) {
            apprenticeship.setCompanyName(paragraphs.first().text().trim());
        }

        if (paragraphs.size() > 1) {
            apprenticeship.setLocation(paragraphs.get(1).text().trim());
        }

        Element salaryElement = listing.selectFirst("p:contains(Wage)");
        if (salaryElement != null) {
            String salary = salaryElement.text().replace("Wage", "").trim();
            apprenticeship.setSalary(salary);
        }

        Element closingDateElement = listing.selectFirst("p:contains(Closes)");
        if (closingDateElement != null) {
            String closingDateStr = closingDateElement.text();
            LocalDate closingDate = parseDate(closingDateStr);
            apprenticeship.setClosingDate(closingDate);
        }

        Element postedDateElement = listing.selectFirst("p:contains(Posted)");
        if (postedDateElement != null) {
            String postedDateStr = postedDateElement.text();
            LocalDate postedDate = parseDate(postedDateStr);
            apprenticeship.setCreatedAtDate(postedDate);
        }

        return apprenticeship;
    }

    private LocalDate parseDate(String dateStr) {
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
                            .replaceAll("[()]", "")
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

    /**
     * Helper method for rate limiting delays. Extracted to a separate method to clarify intent and
     * avoid busy-waiting warnings.
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
