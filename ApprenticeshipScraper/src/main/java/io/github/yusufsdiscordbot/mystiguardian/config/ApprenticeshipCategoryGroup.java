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
package io.github.yusufsdiscordbot.mystiguardian.config;

import java.util.*;
import lombok.Getter;

/**
 * Predefined groups of apprenticeship categories for role mapping and filtering.
 *
 * <p>Instead of mapping individual categories to Discord roles, you can map entire groups.
 * This enum organizes the 160+ Higher In categories into logical groupings:
 * <ul>
 *   <li>TECHNOLOGY - Software, cyber security, AI, data science</li>
 *   <li>FINANCE - Accounting, banking, economics, insurance</li>
 *   <li>BUSINESS - Management, consulting, sales, project management</li>
 *   <li>ENGINEERING - All engineering disciplines (mechanical, civil, aerospace, etc.)</li>
 *   <li>MARKETING - Advertising, digital marketing, PR, social media</li>
 *   <li>DESIGN - Architecture, fashion, graphic, UX/UI design</li>
 *   <li>LEGAL - Commercial, corporate, employment law</li>
 *   <li>And more sectors...</li>
 * </ul>
 *
 * <p>Use this enum to:
 * <ul>
 *   <li>Map category groups to Discord role pings in config.json</li>
 *   <li>Filter apprenticeships by sector</li>
 *   <li>Validate categories against known values</li>
 * </ul>
 *
 * @see DAConfig
 */
@Getter
public enum ApprenticeshipCategoryGroup {
    /** Technology sector: software engineering, cyber security, AI, data science, IT. */
    TECHNOLOGY(
            "computer-science",
            "cyber-security",
            "data-analysis",
            "front-end-development",
            "information-technology",
            "software-engineering",
            "artificial-intelligence"),

    /** Finance sector: accounting, banking, economics, insurance, audit. */
    FINANCE(
            "accounting",
            "actuary",
            "audit",
            "tax",
            "banking",
            "commercial-banking",
            "investment-banking",
            "retail-banking",
            "economics",
            "fiances",
            "insurance-and-risk-management"),

    /** Business sector: management, consulting, sales, project management, procurement. */
    BUSINESS(
            "business-management",
            "business-operations",
            "management-consulting",
            "market-research",
            "procurement",
            "project-management",
            "sales",
            "sustainability"),

    /** Engineering sector: all engineering disciplines including mechanical, civil, aerospace. */
    ENGINEERING(
            "aeronautical-and-aerospace-engineering",
            "automotive-engineering",
            "chemical-engineering",
            "civil-engineering",
            "computer-systems-engineering",
            "electronic-and-electrical-engineering",
            "engineering",
            "manufacturing",
            "material-and-mineral-engineering",
            "mechanical-engineering"),

    /** Marketing sector: advertising, digital marketing, PR, communications, social media. */
    MARKETING(
            "advertising",
            "digital-marketing",
            "marketing",
            "pr-and-communications",
            "social-media-marketing"),

    /** Design sector: architecture, fashion, graphic, product, UX/UI design. */
    DESIGN("architecture", "fashion-design", "graphic-design", "product-design", "ux-ui-design"),

    /** Legal sector: commercial, corporate, employment, intellectual property law. */
    LEGAL(
            "commercial-law",
            "corporate-law",
            "employment-law",
            "intellectual-property-law",
            "legal-law"),

    /** Construction and trades: construction, carpentry, electrical, plumbing. */
    CONSTRUCTION("construction", "carpentry-and-joinery", "electrician", "plumbing"),

    /** Retail sector: FMCG, consumer services, retail management, merchandising. */
    RETAIL("consumer-product-fmcg", "consumer-services", "retail-manager", "merchandising"),

    /** Hospitality sector: hospitality management, bar, waiting, catering. */
    HOSPITALITY("hospitality-management", "bar-and-waiting", "catering"),

    /** Human resources and recruitment sector. */
    HR("human-resources", "recruitment"),

    /** Property sector: development, management, surveying, planning. */
    PROPERTY("property-development", "property-management", "surveying", "property-planning"),

    /** Public sector: teaching, government, social work, armed forces, healthcare, emergency services. */
    PUBLIC_SECTOR(
            "teaching",
            "government",
            "social-work",
            "armed-forces",
            "prison-officer",
            "healthcare",
            "firefighter",
            "police-officer"),

    /** Science sector: chemistry, environmental science, medicine, pharmaceutical, research. */
    SCIENCE(
            "chemistry", "environmental-science", "medicine", "pharmaceutical", "research", "science");

    /** The set of category slugs belonging to this group. */
    private final Set<String> categories;

    /**
     * Constructs a category group with the given category slugs.
     *
     * @param categories the category slugs (e.g., "software-engineering")
     */
    ApprenticeshipCategoryGroup(String... categories) {
        this.categories = new HashSet<>(Arrays.asList(categories));
    }

    /**
     * Check if a given category belongs to this group.
     *
     * @param category the category slug to check
     * @return true if the category belongs to this group, false otherwise
     */
    public boolean contains(String category) {
        return categories.contains(category.toLowerCase().replace(" ", "-"));
    }

    /**
     * Find which group(s) a category belongs to.
     *
     * @param category the category slug to search for
     * @return list of groups containing the category (may be empty)
     */
    public static List<ApprenticeshipCategoryGroup> findGroupsForCategory(String category) {
        String normalized = category.toLowerCase().replace(" ", "-");
        List<ApprenticeshipCategoryGroup> groups = new ArrayList<>();
        for (ApprenticeshipCategoryGroup group : values()) {
            if (group.contains(normalized)) {
                groups.add(group);
            }
        }
        return groups;
    }

    /**
     * Check if a category is valid (belongs to any group).
     *
     * @param category the category slug to validate
     * @return true if the category belongs to any group, false otherwise
     */
    public static boolean isValidCategory(String category) {
        String normalized = category.toLowerCase().replace(" ", "-");
        for (ApprenticeshipCategoryGroup group : values()) {
            if (group.contains(normalized)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all valid categories across all groups.
     *
     * @return set of all category slugs from all groups
     */
    public static Set<String> getAllValidCategories() {
        Set<String> allCategories = new HashSet<>();
        for (ApprenticeshipCategoryGroup group : values()) {
            allCategories.addAll(group.categories);
        }
        return allCategories;
    }
}
