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
 * Predefined groups of job categories for easier configuration. Instead of mapping individual
 * categories, you can map entire groups to roles.
 */
@Getter
public enum JobCategoryGroup {
    TECHNOLOGY(
            "computer-science",
            "cyber-security",
            "data-analysis",
            "front-end-development",
            "information-technology",
            "software-engineering",
            "artificial-intelligence"),

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

    BUSINESS(
            "business-management",
            "business-operations",
            "management-consulting",
            "market-research",
            "procurement",
            "project-management",
            "sales",
            "sustainability"),

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

    MARKETING(
            "advertising",
            "digital-marketing",
            "marketing",
            "pr-and-communications",
            "social-media-marketing"),

    DESIGN("architecture", "fashion-design", "graphic-design", "product-design", "ux-ui-design"),

    LEGAL(
            "commercial-law",
            "corporate-law",
            "employment-law",
            "intellectual-property-law",
            "legal-law"),

    CONSTRUCTION("construction", "carpentry-and-joinery", "electrician", "plumbing"),

    RETAIL("consumer-product-fmcg", "consumer-services", "retail-manager", "merchandising"),

    HOSPITALITY("hospitality-management", "bar-and-waiting", "catering"),

    HR("human-resources", "recruitment"),

    PROPERTY("property-development", "property-management", "surveying", "property-planning"),

    PUBLIC_SECTOR(
            "teaching",
            "government",
            "social-work",
            "armed-forces",
            "prison-officer",
            "healthcare",
            "firefighter",
            "police-officer"),

    SCIENCE(
            "chemistry", "environmental-science", "medicine", "pharmaceutical", "research", "science");

    private final Set<String> categories;

    JobCategoryGroup(String... categories) {
        this.categories = new HashSet<>(Arrays.asList(categories));
    }

    /** Check if a given category belongs to this group. */
    public boolean contains(String category) {
        return categories.contains(category.toLowerCase().replace(" ", "-"));
    }

    /** Find which group(s) a category belongs to. */
    public static List<JobCategoryGroup> findGroupsForCategory(String category) {
        String normalized = category.toLowerCase().replace(" ", "-");
        List<JobCategoryGroup> groups = new ArrayList<>();
        for (JobCategoryGroup group : values()) {
            if (group.contains(normalized)) {
                groups.add(group);
            }
        }
        return groups;
    }

    /** Check if a category is valid (belongs to any group). */
    public static boolean isValidCategory(String category) {
        String normalized = category.toLowerCase().replace(" ", "-");
        for (JobCategoryGroup group : values()) {
            if (group.contains(normalized)) {
                return true;
            }
        }
        return false;
    }

    /** Get all valid categories across all groups. */
    public static Set<String> getAllValidCategories() {
        Set<String> allCategories = new HashSet<>();
        for (JobCategoryGroup group : values()) {
            allCategories.addAll(group.categories);
        }
        return allCategories;
    }
}
