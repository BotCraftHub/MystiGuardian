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
package io.github.yusufsdiscordbot.mystiguardian.scraper;

import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.ApprenticeshipSource;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.FindAnApprenticeship;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import io.github.yusufsdiscordbot.mystiguardian.categories.GovUkRoutes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scraper for GOV.UK's Find an Apprenticeship service.
 *
 * <p>This scraper extracts apprenticeship data from findapprenticeship.service.gov.uk by:
 * <ul>
 *   <li>Iterating through 15 predefined route categories (sectors)</li>
 *   <li>Paginating through search results for each category</li>
 *   <li>Parsing HTML using JSoup to extract apprenticeship details</li>
 *   <li>Handling date formats with flexible parsing (with/without year)</li>
 *   <li>Implementing rate limiting to respect the government service</li>
 * </ul>
 *
 * <p>Route categories include:
 * <ul>
 *   <li>Digital, Engineering and manufacturing</li>
 *   <li>Legal, finance and accounting</li>
 *   <li>Health and science</li>
 *   <li>Business and administration</li>
 *   <li>And 11 other sectors</li>
 * </ul>
 *
 * <p>The scraper focuses on Level 6+ (degree) apprenticeships and searches
 * across all UK locations.
 *
 * <p>This is a record class that requires an {@link OkHttpClient} for HTTP requests.
 *
 * @param client the HTTP client for making requests to GOV.UK
 *
 * @see FindAnApprenticeship
 * @see GovUkRoutes
 * @see ApprenticeshipSource#GOV_UK
 */
@Slf4j
public record FindAnApprenticeshipScraper(OkHttpClient client) {

    /**
     * Base URL for GOV.UK apprenticeship search.
     * Filters for Level 6+ (degree) apprenticeships across all UK locations.
     */
    public static final String BASE_URL =
            "https://www.findapprenticeship.service.gov.uk/apprenticeships?sort=DistanceAsc&searchTerm=&location=&distance=all&levelIds=6&routeIds=";

    /** Maximum number of consecutive errors before stopping category scraping. */
    private static final int MAX_CONSECUTIVE_ERRORS = 3;

    /**
     * Scrapes all Find an Apprenticeship listings across all route categories.
     *
     * <p>This method:
     * <ul>
     *   <li>Iterates through all 15 route categories</li>
     *   <li>Deduplicates apprenticeships by ID</li>
     *   <li>Implements rate limiting (2 seconds between categories)</li>
     *   <li>Handles errors gracefully without stopping entire scrape</li>
     * </ul>
     *
     * @return List of unique Find an Apprenticeship jobs from all categories
     */
    public List<FindAnApprenticeship> scrapeApprenticeships() {
        Map<String, FindAnApprenticeship> uniqueApprenticeships = new HashMap<>();
        Map<String, Integer> routes = GovUkRoutes.getAllRoutes();

        logger.info(
                "Starting Find an Apprenticeship scraping from {} categories", routes.size());

        for (Map.Entry<String, Integer> route : routes.entrySet()) {
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

    /**
     * Scrapes all pages of a single route category.
     *
     * <p>Paginates through search results, parsing each apprenticeship listing.
     * Stops after MAX_CONSECUTIVE_ERRORS or when no more pages exist.
     * Implements rate limiting between page requests (1 second).
     *
     * @param categoryName the human-readable category name
     * @param routeId the GOV.UK route ID for this category
     * @param uniqueApprenticeships map to store deduplicated apprenticeships
     */
    private void scrapeCategory(
            String categoryName,
            int routeId,
            Map<String, FindAnApprenticeship> uniqueApprenticeships) {

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

                Elements apprenticeshipListings = doc.select("li.das-search-results__list-item");

                if (apprenticeshipListings.isEmpty()) {
                    hasMorePages = false;
                    continue;
                }

                consecutiveErrors = 0;

                for (Element listing : apprenticeshipListings) {
                    try {
                        FindAnApprenticeship apprenticeship = parseApprenticeship(listing);
                        if (apprenticeship != null && apprenticeship.getId() != null) {
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

                pageNumber++;

                rateLimitDelay(1000);

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

    /**
     * Parses a single apprenticeship listing element from the HTML page.
     *
     * <p>Extracts:
     * <ul>
     *   <li>Apprenticeship ID from the URL</li>
     *   <li>Title/name of the apprenticeship</li>
     *   <li>Company name, location, salary</li>
     *   <li>Posted date and closing date</li>
     * </ul>
     *
     * @param listing the JSoup element containing the apprenticeship listing
     * @return a populated FindAnApprenticeship object, or null if parsing fails
     */
    private FindAnApprenticeship parseApprenticeship(Element listing) {
        FindAnApprenticeship apprenticeship = new FindAnApprenticeship();

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

    /**
     * Parses a date string from GOV.UK with flexible format support.
     *
     * <p>Handles various formats including:
     * <ul>
     *   <li>"Friday 17 October 2025" - with day name and year</li>
     *   <li>"17 October 2025" - without day name</li>
     *   <li>"Sunday 5 January" - without year (infers current/next year)</li>
     *   <li>"5 January" - minimal format</li>
     *   <li>"Closes in 30 days at 11:59pm" - relative format (extracts date)</li>
     * </ul>
     *
     * <p>If no year is specified and the date is in the past, assumes next year.
     *
     * @param dateStr the date string to parse
     * @return parsed LocalDate, or null if parsing fails
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            String[] parts = getParts(dateStr);

            List<String> filteredParts = new ArrayList<>();
            for (String part : parts) {
                if (!part.isEmpty()) {
                    filteredParts.add(part);
                }
            }
            parts = filteredParts.toArray(new String[0]);

            if (parts.length == 0) {
                throw new IllegalArgumentException("No date information found in: " + dateStr);
            }

            boolean hasYear = parts.length > 0 && parts[parts.length - 1].matches("\\d{4}");

            String day, month;
            int year;

            if (hasYear) {
                year = Integer.parseInt(parts[parts.length - 1]);
                month = parts[parts.length - 2];

                if (parts.length >= 3) {
                    day = null;
                    for (int i = parts.length - 3; i >= 0; i--) {
                        if (parts[i].matches("\\d{1,2}")) {
                            day = parts[i];
                            break;
                        }
                    }
                    if (day == null) {
                        throw new IllegalArgumentException("Could not find day number in: " + dateStr);
                    }
                } else {
                    throw new IllegalArgumentException("Unexpected date format: " + dateStr);
                }
            } else {
                if (parts.length >= 3) {
                    day = parts[1];
                    month = parts[2];
                } else if (parts.length == 2) {
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
     * Cleans and splits a date string into component parts.
     *
     * <p>Removes common prefixes like "Closes in", "Posted", relative terms like
     * "30 days", times, and parentheses. Splits the cleaned string into words.
     *
     * @param dateStr the raw date string from GOV.UK
     * @return array of cleaned date component strings
     */
    private static String @NotNull [] getParts(String dateStr) {
        String cleanDate =
                dateStr
                        .replace("Closes in", "")
                        .replace("Posted", "")
                        .replace("Closes on", "")
                        .replaceAll("\\d+ days", "")
                        .replaceAll("at \\d+:\\d+[ap]m", "")
                        .replaceAll("[()]", "")
                        .trim();

        return cleanDate.split("\\s+");
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
