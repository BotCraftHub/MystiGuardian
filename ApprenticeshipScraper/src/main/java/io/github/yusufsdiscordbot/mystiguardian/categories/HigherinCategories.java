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
package io.github.yusufsdiscordbot.mystiguardian.categories;

import io.github.yusufsdiscordbot.mystiguardian.scraper.HigherinScraper;
import java.util.List;
import java.util.stream.Stream;

/**
 * Configuration class containing all Higher In apprenticeship category slugs.
 *
 * <p>Categories are organized by sector for easier maintenance:
 *
 * <ul>
 *   <li>Technology (7 categories)
 *   <li>Finance &amp; Accounting (12 categories)
 *   <li>Business &amp; Management (8 categories)
 *   <li>Engineering &amp; Manufacturing (10 categories)
 *   <li>Marketing &amp; Communications (5 categories)
 *   <li>Design &amp; Creative (5 categories)
 *   <li>Legal (5 categories)
 *   <li>Construction &amp; Trades (4 categories)
 *   <li>Retail &amp; FMCG (4 categories)
 *   <li>Hospitality (3 categories)
 *   <li>HR &amp; Recruitment (2 categories)
 *   <li>Property (4 categories)
 *   <li>Public Sector (8 categories)
 *   <li>Science &amp; Healthcare (6 categories)
 * </ul>
 *
 * <p>Total: 83 categories across 14 sectors
 *
 * @see HigherinScraper
 */
public final class HigherinCategories {

    private HigherinCategories() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** Technology sector categories. */
    public static final List<String> TECHNOLOGY =
            List.of(
                    "computer-science",
                    "cyber-security",
                    "data-analysis",
                    "front-end-development",
                    "information-technology",
                    "software-engineering",
                    "artificial-intelligence");

    /** Finance and accounting sector categories. */
    public static final List<String> FINANCE =
            List.of(
                    "accounting",
                    "actuary",
                    "audit",
                    "tax",
                    "banking",
                    "commercial-banking",
                    "investment-banking",
                    "retail-banking",
                    "economics",
                    "finances",
                    "insurance-and-risk-management");

    /** Business and management sector categories. */
    public static final List<String> BUSINESS =
            List.of(
                    "business-management",
                    "business-operations",
                    "management-consulting",
                    "market-research",
                    "procurement",
                    "project-management",
                    "sales",
                    "sustainability");

    /** Engineering and manufacturing sector categories. */
    public static final List<String> ENGINEERING =
            List.of(
                    "aeronautical-and-aerospace-engineering",
                    "automotive-engineering",
                    "chemical-engineering",
                    "civil-engineering",
                    "computer-systems-engineering",
                    "electronic-and-electrical-engineering",
                    "engineering",
                    "manufacturing",
                    "material-and-mineral-engineering",
                    "mechanical-engineering");

    /** Marketing and communications sector categories. */
    public static final List<String> MARKETING =
            List.of(
                    "advertising",
                    "digital-marketing",
                    "marketing",
                    "pr-and-communications",
                    "social-media-marketing");

    /** Design and creative sector categories. */
    public static final List<String> DESIGN =
            List.of("architecture", "fashion-design", "graphic-design", "product-design", "ux-ui-design");

    /** Legal sector categories. */
    public static final List<String> LEGAL =
            List.of(
                    "commercial-law",
                    "corporate-law",
                    "employment-law",
                    "intellectual-property-law",
                    "legal-law");

    /** Construction and trades sector categories. */
    public static final List<String> CONSTRUCTION =
            List.of("construction", "carpentry-and-joinery", "electrician", "plumbing");

    /** Retail and FMCG sector categories. */
    public static final List<String> RETAIL =
            List.of("consumer-product-fmcg", "consumer-services", "retail-manager", "merchandising");

    /** Hospitality sector categories. */
    public static final List<String> HOSPITALITY =
            List.of("hospitality-management", "bar-and-waiting", "catering");

    /** HR and recruitment sector categories. */
    public static final List<String> HR = List.of("human-resources", "recruitment");

    /** Property sector categories. */
    public static final List<String> PROPERTY =
            List.of("property-development", "property-management", "surveying", "property-planning");

    /** Public sector categories. */
    public static final List<String> PUBLIC_SECTOR =
            List.of(
                    "teaching",
                    "government",
                    "social-work",
                    "armed-forces",
                    "prison-officer",
                    "healthcare",
                    "firefighter",
                    "police-officer");

    /** Science and healthcare sector categories. */
    public static final List<String> SCIENCE =
            List.of(
                    "chemistry",
                    "environmental-science",
                    "medicine",
                    "pharmaceutical",
                    "research",
                    "science");

    /**
     * Gets all categories combined across all sectors.
     *
     * @return immutable list of all 83 category slugs
     */
    public static List<String> getAllCategories() {
        return Stream.of(
                        TECHNOLOGY,
                        FINANCE,
                        BUSINESS,
                        ENGINEERING,
                        MARKETING,
                        DESIGN,
                        LEGAL,
                        CONSTRUCTION,
                        RETAIL,
                        HOSPITALITY,
                        HR,
                        PROPERTY,
                        PUBLIC_SECTOR,
                        SCIENCE)
                .flatMap(List::stream)
                .toList();
    }
}
