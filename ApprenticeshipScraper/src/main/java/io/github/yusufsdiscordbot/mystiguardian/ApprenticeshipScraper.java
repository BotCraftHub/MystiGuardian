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
package io.github.yusufsdiscordbot.mystiguardian;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.FindAnApprenticeship;
import io.github.yusufsdiscordbot.mystiguardian.apprenticeship.HigherinApprenticeship;
import io.github.yusufsdiscordbot.mystiguardian.scraper.FindAnApprenticeshipScraper;
import io.github.yusufsdiscordbot.mystiguardian.scraper.HigherinScraper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

/**
 * Facade class for apprenticeship scraping. Delegates to specialized scrapers for Higher In and
 * Find an Apprenticeship.
 */
@Slf4j
public class ApprenticeshipScraper {

    private final HigherinScraper higherinScraper;
    private final FindAnApprenticeshipScraper findAnApprenticeshipScraper;

    /** Default constructor that creates scrapers with default HTTP client configuration. */
    public ApprenticeshipScraper() {
        OkHttpClient sharedClient =
                new OkHttpClient.Builder()
                        .connectionPool(new okhttp3.ConnectionPool(5, 5, java.util.concurrent.TimeUnit.MINUTES))
                        .build();

        this.higherinScraper = new HigherinScraper(sharedClient, new ObjectMapper());
        this.findAnApprenticeshipScraper = new FindAnApprenticeshipScraper(sharedClient);
    }

    /**
     * Constructor for dependency injection, useful for testing. Allows providing custom scraper
     * instances.
     *
     * @param higherinScraper the Higher In scraper instance
     * @param findAnApprenticeshipScraper the Find an Apprenticeship scraper instance
     */
    public ApprenticeshipScraper(
            HigherinScraper higherinScraper, FindAnApprenticeshipScraper findAnApprenticeshipScraper) {
        this.higherinScraper = higherinScraper;
        this.findAnApprenticeshipScraper = findAnApprenticeshipScraper;
    }

    /**
     * Scrapes Higher In (Rate My Apprenticeship) apprenticeships. Delegates to {@link
     * HigherinScraper}.
     *
     * @return List of Higher In apprenticeships
     */
    public List<HigherinApprenticeship> scrapeRateMyApprenticeshipJobs() {
        logger.info("Starting Higher In apprenticeship scraping");
        return higherinScraper.scrapeApprenticeships();
    }

    /**
     * Scrapes GOV.UK Find an Apprenticeship listings. Delegates to {@link
     * FindAnApprenticeshipScraper}.
     *
     * @return List of Find an Apprenticeship jobs
     */
    public List<FindAnApprenticeship> scrapeFindAnApprenticeshipJobs() {
        logger.info("Starting Find an Apprenticeship scraping");
        return findAnApprenticeshipScraper.scrapeApprenticeships();
    }
}
