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
package io.github.yusufsdiscordbot.mystiguardian.categories;

import io.github.yusufsdiscordbot.mystiguardian.config.ApprenticeshipCategoryGroup;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Maps apprenticeship categories from different sources to unified MystiGuardian category groups.
 *
 * <p>This mapper creates a consistent categorization system across:
 *
 * <ul>
 *   <li>Higher In (83 specific categories like "software-engineering", "cyber-security")
 *   <li>GOV.UK (15 broad routes like "Digital", "Engineering and manufacturing")
 * </ul>
 *
 * <p>The unified system uses {@link ApprenticeshipCategoryGroup} enum with 14 sectors: Technology,
 * Finance, Business, Engineering, Marketing, Design, Legal, Construction, Retail, Hospitality, HR,
 * Property, Public Sector, and Science.
 *
 * <p>Usage examples:
 *
 * <pre>{@code
 * // Map Higher In category
 * List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories("software-engineering");
 * // Returns: [TECHNOLOGY]
 *
 * // Map GOV.UK route
 * List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories("Digital");
 * // Returns: [TECHNOLOGY]
 *
 * // Get unified category names
 * List<String> names = CategoryMapper.getUnifiedCategoryNames("cyber-security");
 * // Returns: ["Technology"]
 * }</pre>
 *
 * @see ApprenticeshipCategoryGroup
 * @see HigherinCategories
 * @see GovUkRoutes
 */
@Slf4j
public final class CategoryMapper {

    private CategoryMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Map of GOV.UK route names to their corresponding unified category groups.
     *
     * <p>GOV.UK routes are broader than Higher In categories, so some routes map to multiple category
     * groups.
     */
    private static final Map<String, List<ApprenticeshipCategoryGroup>> GOV_UK_ROUTE_MAPPING =
            Map.ofEntries(
                    Map.entry(
                            "Agriculture, environmental and animal care",
                            List.of(ApprenticeshipCategoryGroup.SCIENCE)),
                    Map.entry("Business and administration", List.of(ApprenticeshipCategoryGroup.BUSINESS)),
                    Map.entry("Care services", List.of(ApprenticeshipCategoryGroup.PUBLIC_SECTOR)),
                    Map.entry("Catering and hospitality", List.of(ApprenticeshipCategoryGroup.HOSPITALITY)),
                    Map.entry(
                            "Construction and the built environment",
                            List.of(ApprenticeshipCategoryGroup.CONSTRUCTION)),
                    Map.entry("Creative and design", List.of(ApprenticeshipCategoryGroup.DESIGN)),
                    Map.entry("Digital", List.of(ApprenticeshipCategoryGroup.TECHNOLOGY)),
                    Map.entry(
                            "Education and early years", List.of(ApprenticeshipCategoryGroup.PUBLIC_SECTOR)),
                    Map.entry(
                            "Engineering and manufacturing", List.of(ApprenticeshipCategoryGroup.ENGINEERING)),
                    Map.entry("Hair and beauty", List.of(ApprenticeshipCategoryGroup.RETAIL)),
                    Map.entry(
                            "Health and science",
                            List.of(
                                    ApprenticeshipCategoryGroup.SCIENCE, ApprenticeshipCategoryGroup.PUBLIC_SECTOR)),
                    Map.entry(
                            "Legal, finance and accounting",
                            List.of(ApprenticeshipCategoryGroup.LEGAL, ApprenticeshipCategoryGroup.FINANCE)),
                    Map.entry("Protective services", List.of(ApprenticeshipCategoryGroup.PUBLIC_SECTOR)),
                    Map.entry(
                            "Sales, marketing and procurement",
                            List.of(ApprenticeshipCategoryGroup.MARKETING, ApprenticeshipCategoryGroup.BUSINESS)),
                    Map.entry("Transport and logistics", List.of(ApprenticeshipCategoryGroup.BUSINESS)));

    /**
     * Maps a source-specific category to unified MystiGuardian category groups.
     *
     * <p>This method handles both Higher In categories (e.g., "software-engineering") and GOV.UK
     * routes (e.g., "Digital", "Engineering and manufacturing").
     *
     * <p>For Higher In categories, uses {@link
     * ApprenticeshipCategoryGroup#findGroupsForCategory(String)}. For GOV.UK routes, uses the
     * predefined {@link #GOV_UK_ROUTE_MAPPING}.
     *
     * @param sourceCategory the category from the scraping source
     * @return list of unified category groups (may be empty if no mapping found, may contain multiple
     *     groups)
     */
    public static List<ApprenticeshipCategoryGroup> mapToUnifiedCategories(String sourceCategory) {
        if (sourceCategory == null || sourceCategory.isEmpty()) {
            return Collections.emptyList();
        }

        // First try to map as Higher In category (lowercase with hyphens)
        List<ApprenticeshipCategoryGroup> groups =
                ApprenticeshipCategoryGroup.findGroupsForCategory(sourceCategory);

        // If no match, try GOV.UK route mapping (case-sensitive, with spaces)
        if (groups.isEmpty() && GOV_UK_ROUTE_MAPPING.containsKey(sourceCategory)) {
            groups = GOV_UK_ROUTE_MAPPING.get(sourceCategory);
        }

        // Log if no mapping found
        if (groups.isEmpty()) {
            logger.debug("No unified category mapping found for source category: {}", sourceCategory);
        }

        return groups;
    }

    /**
     * Maps a list of source categories to unified category groups.
     *
     * <p>This method processes multiple categories and returns a deduplicated list of all matching
     * unified category groups.
     *
     * @param sourceCategories list of categories from the scraping source
     * @return deduplicated list of unified category groups
     */
    public static List<ApprenticeshipCategoryGroup> mapToUnifiedCategories(
            List<String> sourceCategories) {
        if (sourceCategories == null || sourceCategories.isEmpty()) {
            return Collections.emptyList();
        }

        Set<ApprenticeshipCategoryGroup> uniqueGroups = new LinkedHashSet<>();
        for (String category : sourceCategories) {
            uniqueGroups.addAll(mapToUnifiedCategories(category));
        }

        return new ArrayList<>(uniqueGroups);
    }

    /**
     * Gets the unified category names as strings for a source category.
     *
     * <p>Useful for display purposes, returns the enum names formatted as title case (e.g.,
     * "Technology", "Finance").
     *
     * @param sourceCategory the category from the scraping source
     * @return list of unified category names
     */
    public static List<String> getUnifiedCategoryNames(String sourceCategory) {
        return mapToUnifiedCategories(sourceCategory).stream()
                .map(CategoryMapper::formatCategoryGroupName)
                .toList();
    }

    /**
     * Gets the unified category names as strings for a list of source categories.
     *
     * @param sourceCategories list of categories from the scraping source
     * @return deduplicated list of unified category names
     */
    public static List<String> getUnifiedCategoryNames(List<String> sourceCategories) {
        return mapToUnifiedCategories(sourceCategories).stream()
                .map(CategoryMapper::formatCategoryGroupName)
                .toList();
    }

    /**
     * Formats a category group enum to a display-friendly name.
     *
     * <p>Converts enum names like "PUBLIC_SECTOR" to "Public Sector".
     *
     * @param group the category group enum
     * @return formatted category name
     */
    private static String formatCategoryGroupName(ApprenticeshipCategoryGroup group) {
        String name = group.name().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formatted
                        .append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return formatted.toString().trim();
    }

    /**
     * Checks if a source category has a unified category mapping.
     *
     * @param sourceCategory the category from the scraping source
     * @return true if a mapping exists, false otherwise
     */
    public static boolean hasUnifiedMapping(String sourceCategory) {
        return !mapToUnifiedCategories(sourceCategory).isEmpty();
    }

    /**
     * Gets all possible unified category groups.
     *
     * @return array of all category groups in the unified system
     */
    public static ApprenticeshipCategoryGroup[] getAllUnifiedCategories() {
        return ApprenticeshipCategoryGroup.values();
    }

    /**
     * Gets all unified category names as strings.
     *
     * @return list of all unified category names
     */
    public static List<String> getAllUnifiedCategoryNames() {
        return Arrays.stream(ApprenticeshipCategoryGroup.values())
                .map(CategoryMapper::formatCategoryGroupName)
                .toList();
    }
}
