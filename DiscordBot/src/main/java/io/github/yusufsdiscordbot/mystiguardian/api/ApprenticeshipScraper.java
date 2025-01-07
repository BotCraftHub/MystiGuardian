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
import io.github.yusufsdiscordbot.mystiguardian.api.job.RateMyApprenticeshipJob;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ApprenticeshipScraper {
    public static final String RATE_MY_APPRENTICESHIP_BASE_URL =
            "https://www.ratemyapprenticeship.co.uk/search-jobs/degree-apprenticeship/it/";
    public static final String FIND_AN_APPRENTICESHIP_BASE_URL =
            "https://www.findapprenticeship.service.gov.uk/apprenticeships?sort=DistanceAsc&searchTerm=&location=&distance=all&levelIds=6&routeIds=7";
    private static final List<String> RATE_MY_APPRENTICESHIP_CATEGORIES =
            Arrays.asList(
                    "computer-science", "cyber-security", "data-analysis", "information-technology");
    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApprenticeshipScraper() {
        this.client = new OkHttpClient();
    }

    public List<RateMyApprenticeshipJob> scrapeRateMyApprenticeshipJobs() throws IOException {
        Map<String, Set<String>> jobCategories = new HashMap<>();
        Map<String, RateMyApprenticeshipJob> uniqueJobs = new HashMap<>();

        for (String category : RATE_MY_APPRENTICESHIP_CATEGORIES) {
            String url = RATE_MY_APPRENTICESHIP_BASE_URL + category;
            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                String html = response.body().string();
                String jsonData = extractJsonData(html);
                JsonNode root = mapper.readTree(jsonData);
                JsonNode jobs = root.get("data");

                for (JsonNode jobNode : jobs) {
                    String jobId = getJsonText(jobNode, "id");

                    uniqueJobs.computeIfAbsent(
                            jobId,
                            k -> {
                                RateMyApprenticeshipJob newJob = new RateMyApprenticeshipJob();
                                newJob.setId(jobId);
                                newJob.setTitle(getJsonText(jobNode, "title"));
                                JsonNode company = jobNode.get("company");
                                newJob.setCompanyName(getJsonText(company, "name", "Not Available"));
                                newJob.setCompanyLogo(getJsonText(company, "small_logo", "Not Available"));
                                newJob.setLocation(getJsonText(jobNode, "jobLocations"));
                                newJob.setSalary(getJsonText(jobNode, "salary", "Not specified"));
                                newJob.setUrl(getJsonText(jobNode, "url"));

                                String deadline = getJsonText(jobNode, "deadline");
                                if (deadline != null && !deadline.isEmpty()) {
                                    try {
                                        newJob.setClosingDate(parseRateMyApprenticeshipDate(deadline));
                                    } catch (Exception e) {
                                        MystiGuardianUtils.logger.error(
                                                "Failed to parse date for job {}: {}. Setting to 'Not specified'.",
                                                jobId,
                                                e.getMessage());
                                        newJob.setClosingDate(null); // or set to "Not specified" if string
                                    }
                                }
                                return newJob;
                            });

                    jobCategories.computeIfAbsent(jobId, k -> new HashSet<>()).add(category);
                }
            } catch (Exception e) {
                MystiGuardianUtils.logger.error(
                        "Failed to process category {}: {}", category, e.getMessage());
            }
        }

        uniqueJobs.forEach(
                (jobId, job) -> {
                    Set<String> categories = jobCategories.get(jobId);
                    job.setCategories(new ArrayList<>(categories));
                });

        return new ArrayList<>(uniqueJobs.values());
    }

    public List<FindAnApprenticeshipJob> scrapeFindAnApprenticeshipJobs() throws IOException {
        List<FindAnApprenticeshipJob> allJobs = new ArrayList<>();
        int pageNumber = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            String pageUrl =
                    String.format("%s&pageNumber=%d", FIND_AN_APPRENTICESHIP_BASE_URL, pageNumber);

            Request request =
                    new Request.Builder().url(pageUrl).header("User-Agent", "Mozilla/5.0").build();

            try (Response response = client.newCall(request).execute()) {
                String html = response.body().string();
                Document doc = Jsoup.parse(html);
                Elements jobListings = doc.select("li.das-search-results__list-item");

                if (jobListings.isEmpty()) {
                    hasMorePages = false;
                    continue;
                }

                for (Element listing : jobListings) {
                    try {
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

                        allJobs.add(job);
                    } catch (Exception e) {
                        MystiGuardianUtils.logger.error(
                                "Failed to parse job listing on page {}: {}", pageNumber, e.getMessage());
                    }
                }

                pageNumber++;

                Thread.sleep(1000);

            } catch (Exception e) {
                MystiGuardianUtils.logger.error(
                        "Failed to process page {}: {}", pageNumber, e.getMessage());
                hasMorePages = false;
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

    private LocalDate parseRateMyApprenticeshipDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            // Ignore and proceed to alternative formats
        }

        try {
            DateTimeFormatter altFormatter =
                    DateTimeFormatter.ofPattern("d['st']['nd']['rd']['th'] MMMM yyyy", Locale.ENGLISH);
            return LocalDate.parse(dateStr.replaceAll("(\\d+)(st|nd|rd|th)", "$1"), altFormatter);
        } catch (Exception e) {
            // Ignore
        }

        try {
            if (dateStr.toLowerCase().contains("closes in")) {
                Matcher matcher = Pattern.compile("\\d+").matcher(dateStr);
                if (matcher.find()) {
                    int days = Integer.parseInt(matcher.group());
                    return LocalDate.now().plusDays(days);
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        try {
            Matcher matcher = Pattern.compile("(\\d{1,2} [A-Za-z]+)").matcher(dateStr);
            if (matcher.find()) {
                String extractedDate =
                        matcher.group(1) + " " + LocalDate.now().getYear(); // Append current year
                DateTimeFormatter fallbackFormatter =
                        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
                return LocalDate.parse(extractedDate, fallbackFormatter);
            }
        } catch (Exception e) {
            // Ignore parsing failure
        }

        MystiGuardianUtils.logger.error("Unable to parse date string: {}", dateStr);
        return null;
    }

    private LocalDate parseFindAnApprenticeshipDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            String cleanDate =
                    dateStr
                            .replace("Closes in", "")
                            .replace("Posted", "")
                            .replace("Closes on", "")
                            .replaceAll("\\d+ days", "")
                            .replaceAll("\\(|\\)", "")
                            .trim();

            // Split into parts (e.g. ["Sunday", "5", "January"])
            String[] parts = cleanDate.split("\\s+");

            String day, month;
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

            int year = LocalDate.now().getYear();
            String fullDateStr = String.format("%s %s %d", day, month, year);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.UK);
            LocalDate date = LocalDate.parse(fullDateStr, formatter);

            if (date.isBefore(LocalDate.now())) {
                date = date.plusYears(1);
            }

            return date;
        } catch (Exception e) {
            MystiGuardianUtils.logger.debug("Date string before parsing: '{}'", dateStr);
            MystiGuardianUtils.logger.error("Failed to parse date '{}': {}", dateStr, e.getMessage());
            return null;
        }
    }
}
