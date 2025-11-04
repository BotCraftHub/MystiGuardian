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
package io.github.yusufsdiscordbot.mystiguardian.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ApprenticeshipCategoryGroup}.
 *
 * <p>Tests the category grouping system including group membership, validation, and category
 * lookup.
 */
@DisplayName("ApprenticeshipCategoryGroup Tests")
class ApprenticeshipCategoryGroupTest {

    @Nested
    @DisplayName("Group Membership")
    class GroupMembershipTests {

        @Test
        @DisplayName("TECHNOLOGY should contain technology categories")
        void testTechnologyGroup() {
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("software-engineering"));
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("cyber-security"));
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("artificial-intelligence"));
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("data-analysis"));
        }

        @Test
        @DisplayName("FINANCE should contain finance categories")
        void testFinanceGroup() {
            assertTrue(ApprenticeshipCategoryGroup.FINANCE.contains("accounting"));
            assertTrue(ApprenticeshipCategoryGroup.FINANCE.contains("investment-banking"));
            assertTrue(ApprenticeshipCategoryGroup.FINANCE.contains("insurance-and-risk-management"));
        }

        @Test
        @DisplayName("BUSINESS should contain business categories")
        void testBusinessGroup() {
            assertTrue(ApprenticeshipCategoryGroup.BUSINESS.contains("business-management"));
            assertTrue(ApprenticeshipCategoryGroup.BUSINESS.contains("project-management"));
            assertTrue(ApprenticeshipCategoryGroup.BUSINESS.contains("procurement"));
        }

        @Test
        @DisplayName("ENGINEERING should contain engineering categories")
        void testEngineeringGroup() {
            assertTrue(ApprenticeshipCategoryGroup.ENGINEERING.contains("mechanical-engineering"));
            assertTrue(ApprenticeshipCategoryGroup.ENGINEERING.contains("civil-engineering"));
            assertTrue(
                    ApprenticeshipCategoryGroup.ENGINEERING.contains(
                            "aeronautical-and-aerospace-engineering"));
        }

        @Test
        @DisplayName("Group should not contain categories from other groups")
        void testGroupExclusion() {
            assertFalse(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("accounting"));
            assertFalse(ApprenticeshipCategoryGroup.FINANCE.contains("software-engineering"));
            assertFalse(ApprenticeshipCategoryGroup.BUSINESS.contains("cyber-security"));
        }
    }

    @Nested
    @DisplayName("Category Normalization")
    class CategoryNormalizationTests {

        @Test
        @DisplayName("Should handle case-insensitive matching")
        void testCaseInsensitive() {
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("Software-Engineering"));
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("SOFTWARE-ENGINEERING"));
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("software-engineering"));
        }

        @Test
        @DisplayName("Should normalize spaces to hyphens")
        void testSpaceNormalization() {
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("software engineering"));
            assertTrue(ApprenticeshipCategoryGroup.FINANCE.contains("investment banking"));
        }

        @Test
        @DisplayName("Should handle mixed case and spaces")
        void testMixedNormalization() {
            assertTrue(ApprenticeshipCategoryGroup.TECHNOLOGY.contains("Software Engineering"));
            assertTrue(ApprenticeshipCategoryGroup.FINANCE.contains("Investment Banking"));
        }
    }

    @Nested
    @DisplayName("Finding Groups for Category")
    class FindGroupsTests {

        @Test
        @DisplayName("Should find group for technology category")
        void testFindTechnologyGroup() {
            List<ApprenticeshipCategoryGroup> groups =
                    ApprenticeshipCategoryGroup.findGroupsForCategory("software-engineering");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.TECHNOLOGY));
        }

        @Test
        @DisplayName("Should find group for finance category")
        void testFindFinanceGroup() {
            List<ApprenticeshipCategoryGroup> groups =
                    ApprenticeshipCategoryGroup.findGroupsForCategory("accounting");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.FINANCE));
        }

        @Test
        @DisplayName("Should return empty list for unknown category")
        void testFindUnknownCategory() {
            List<ApprenticeshipCategoryGroup> groups =
                    ApprenticeshipCategoryGroup.findGroupsForCategory("unknown-category-xyz");

            assertNotNull(groups);
            assertTrue(groups.isEmpty());
        }

        @Test
        @DisplayName("Should handle case-insensitive category search")
        void testFindGroupCaseInsensitive() {
            List<ApprenticeshipCategoryGroup> groups1 =
                    ApprenticeshipCategoryGroup.findGroupsForCategory("software-engineering");
            List<ApprenticeshipCategoryGroup> groups2 =
                    ApprenticeshipCategoryGroup.findGroupsForCategory("SOFTWARE-ENGINEERING");

            assertEquals(groups1, groups2);
        }
    }

    @Nested
    @DisplayName("Category Validation")
    class CategoryValidationTests {

        @Test
        @DisplayName("Should validate known technology categories")
        void testValidTechnologyCategories() {
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("software-engineering"));
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("cyber-security"));
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("data-analysis"));
        }

        @Test
        @DisplayName("Should validate known finance categories")
        void testValidFinanceCategories() {
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("accounting"));
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("investment-banking"));
        }

        @Test
        @DisplayName("Should reject unknown categories")
        void testInvalidCategories() {
            assertFalse(ApprenticeshipCategoryGroup.isValidCategory("unknown-category"));
            assertFalse(ApprenticeshipCategoryGroup.isValidCategory("xyz-abc-123"));
        }

        @Test
        @DisplayName("Should validate with case insensitivity")
        void testValidationCaseInsensitive() {
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("SOFTWARE-ENGINEERING"));
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("Software-Engineering"));
        }

        @Test
        @DisplayName("Should validate with space normalization")
        void testValidationSpaceNormalization() {
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("software engineering"));
            assertTrue(ApprenticeshipCategoryGroup.isValidCategory("investment banking"));
        }
    }

    @Nested
    @DisplayName("All Valid Categories")
    class AllValidCategoriesTests {

        @Test
        @DisplayName("Should return non-empty set of categories")
        void testGetAllCategories() {
            Set<String> allCategories = ApprenticeshipCategoryGroup.getAllValidCategories();

            assertNotNull(allCategories);
            assertFalse(allCategories.isEmpty());
        }

        @Test
        @DisplayName("Should include technology categories")
        void testIncludesTechnologyCategories() {
            Set<String> allCategories = ApprenticeshipCategoryGroup.getAllValidCategories();

            assertTrue(allCategories.contains("software-engineering"));
            assertTrue(allCategories.contains("cyber-security"));
        }

        @Test
        @DisplayName("Should include finance categories")
        void testIncludesFinanceCategories() {
            Set<String> allCategories = ApprenticeshipCategoryGroup.getAllValidCategories();

            assertTrue(allCategories.contains("accounting"));
            assertTrue(allCategories.contains("investment-banking"));
        }

        @Test
        @DisplayName("Should include all 14 group categories")
        void testIncludesAllGroups() {
            Set<String> allCategories = ApprenticeshipCategoryGroup.getAllValidCategories();

            // Sample one category from each of the 14 groups
            assertTrue(allCategories.contains("software-engineering")); // TECHNOLOGY
            assertTrue(allCategories.contains("accounting")); // FINANCE
            assertTrue(allCategories.contains("business-management")); // BUSINESS
            assertTrue(allCategories.contains("mechanical-engineering")); // ENGINEERING
            assertTrue(allCategories.contains("marketing")); // MARKETING
            assertTrue(allCategories.contains("graphic-design")); // DESIGN
            assertTrue(allCategories.contains("commercial-law")); // LEGAL
            assertTrue(allCategories.contains("construction")); // CONSTRUCTION
            assertTrue(allCategories.contains("retail-manager")); // RETAIL
            assertTrue(allCategories.contains("hospitality-management")); // HOSPITALITY
            assertTrue(allCategories.contains("human-resources")); // HR
            assertTrue(allCategories.contains("property-management")); // PROPERTY
            assertTrue(allCategories.contains("teaching")); // PUBLIC_SECTOR
            assertTrue(allCategories.contains("chemistry")); // SCIENCE
        }

        @Test
        @DisplayName("Should have expected minimum number of categories")
        void testCategoryCount() {
            Set<String> allCategories = ApprenticeshipCategoryGroup.getAllValidCategories();

            // At minimum, should have categories for all sectors
            // Based on the enum definition, there are 80+ categories
            assertTrue(allCategories.size() >= 80, "Should have at least 80 categories");
        }
    }

    @Nested
    @DisplayName("Enum Properties")
    class EnumPropertiesTests {

        @Test
        @DisplayName("Should have 14 category groups")
        void testGroupCount() {
            ApprenticeshipCategoryGroup[] groups = ApprenticeshipCategoryGroup.values();
            assertEquals(14, groups.length);
        }

        @Test
        @DisplayName("Each group should have categories")
        void testEachGroupHasCategories() {
            for (ApprenticeshipCategoryGroup group : ApprenticeshipCategoryGroup.values()) {
                assertNotNull(group.getCategories());
                assertFalse(group.getCategories().isEmpty(), group.name() + " should have categories");
            }
        }

        @Test
        @DisplayName("Should retrieve group by name")
        void testValueOf() {
            assertEquals(
                    ApprenticeshipCategoryGroup.TECHNOLOGY,
                    ApprenticeshipCategoryGroup.valueOf("TECHNOLOGY"));
            assertEquals(
                    ApprenticeshipCategoryGroup.FINANCE, ApprenticeshipCategoryGroup.valueOf("FINANCE"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            assertFalse(ApprenticeshipCategoryGroup.isValidCategory(""));
            assertTrue(ApprenticeshipCategoryGroup.findGroupsForCategory("").isEmpty());
        }

        @Test
        @DisplayName("Should handle category with extra hyphens")
        void testExtraHyphens() {
            // Should normalize properly
            assertFalse(ApprenticeshipCategoryGroup.isValidCategory("software--engineering"));
        }

        @Test
        @DisplayName("Should handle category normalization")
        void testCategoryNormalization() {
            // The normalization converts spaces to hyphens and lowercases
            // but doesn't trim whitespace
            String normalizedCategory = "software-engineering ";
            // This will look for "software-engineering-" which doesn't exist
            assertFalse(ApprenticeshipCategoryGroup.isValidCategory(normalizedCategory));
        }
    }
}
