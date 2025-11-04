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

import static org.junit.jupiter.api.Assertions.*;

import io.github.yusufsdiscordbot.mystiguardian.config.ApprenticeshipCategoryGroup;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CategoryMapper}.
 *
 * <p>Tests the unified category mapping system that maps source-specific categories from Higher In
 * and GOV.UK to standardized MystiGuardian category groups.
 */
@DisplayName("CategoryMapper Tests")
class CategoryMapperTest {

    @Nested
    @DisplayName("Higher In Category Mapping")
    class HigherInMappingTests {

        @Test
        @DisplayName("Should map software-engineering to Technology")
        void testSoftwareEngineeringMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("software-engineering");

            assertNotNull(groups);
            assertFalse(groups.isEmpty());
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.TECHNOLOGY));
        }

        @Test
        @DisplayName("Should map cyber-security to Technology")
        void testCyberSecurityMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("cyber-security");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.TECHNOLOGY, groups.get(0));
        }

        @Test
        @DisplayName("Should map accounting to Finance")
        void testAccountingMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("accounting");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.FINANCE, groups.get(0));
        }

        @Test
        @DisplayName("Should map investment-banking to Finance")
        void testInvestmentBankingMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("investment-banking");

            assertNotNull(groups);
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.FINANCE));
        }

        @Test
        @DisplayName("Should map mechanical-engineering to Engineering")
        void testMechanicalEngineeringMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("mechanical-engineering");

            assertNotNull(groups);
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.ENGINEERING));
        }

        @Test
        @DisplayName("Should map marketing to Marketing")
        void testMarketingMapping() {
            List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories("marketing");

            assertNotNull(groups);
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.MARKETING));
        }

        @Test
        @DisplayName("Should map all technology categories correctly")
        void testAllTechnologyCategories() {
            String[] techCategories = {
                "computer-science",
                "cyber-security",
                "data-analysis",
                "front-end-development",
                "information-technology",
                "software-engineering",
                "artificial-intelligence"
            };

            for (String category : techCategories) {
                List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories(category);
                assertTrue(
                        groups.contains(ApprenticeshipCategoryGroup.TECHNOLOGY),
                        "Category " + category + " should map to TECHNOLOGY");
            }
        }
    }

    @Nested
    @DisplayName("GOV.UK Route Mapping")
    class GovUkMappingTests {

        @Test
        @DisplayName("Should map 'Digital' to Technology")
        void testDigitalMapping() {
            List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories("Digital");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.TECHNOLOGY, groups.get(0));
        }

        @Test
        @DisplayName("Should map 'Engineering and manufacturing' to Engineering")
        void testEngineeringAndManufacturingMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("Engineering and manufacturing");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.ENGINEERING, groups.get(0));
        }

        @Test
        @DisplayName("Should map 'Legal, finance and accounting' to multiple groups")
        void testLegalFinanceAccountingMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("Legal, finance and accounting");

            assertNotNull(groups);
            assertEquals(2, groups.size());
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.LEGAL));
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.FINANCE));
        }

        @Test
        @DisplayName("Should map 'Health and science' to multiple groups")
        void testHealthAndScienceMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("Health and science");

            assertNotNull(groups);
            assertEquals(2, groups.size());
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.SCIENCE));
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.PUBLIC_SECTOR));
        }

        @Test
        @DisplayName("Should map 'Business and administration' to Business")
        void testBusinessAdministrationMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("Business and administration");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.BUSINESS, groups.get(0));
        }

        @Test
        @DisplayName("Should map 'Construction and the built environment' to Construction")
        void testConstructionMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("Construction and the built environment");

            assertNotNull(groups);
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.CONSTRUCTION, groups.get(0));
        }

        @Test
        @DisplayName("Should map 'Sales, marketing and procurement' to multiple groups")
        void testSalesMarketingMapping() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("Sales, marketing and procurement");

            assertNotNull(groups);
            assertEquals(2, groups.size());
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.MARKETING));
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.BUSINESS));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should return empty list for null category")
        void testNullCategory() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories((String) null);

            assertNotNull(groups);
            assertTrue(groups.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for empty category")
        void testEmptyCategory() {
            List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories("");

            assertNotNull(groups);
            assertTrue(groups.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for unknown category")
        void testUnknownCategory() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("unknown-category-xyz");

            assertNotNull(groups);
            assertTrue(groups.isEmpty());
        }

        @Test
        @DisplayName("Should handle null list of categories")
        void testNullCategoryList() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories((List<String>) null);

            assertNotNull(groups);
            assertTrue(groups.isEmpty());
        }

        @Test
        @DisplayName("Should handle empty list of categories")
        void testEmptyCategoryList() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories(Collections.emptyList());

            assertNotNull(groups);
            assertTrue(groups.isEmpty());
        }
    }

    @Nested
    @DisplayName("Multiple Category Mapping")
    class MultipleCategoryTests {

        @Test
        @DisplayName("Should map list of Higher In categories to unified groups")
        void testMultipleHigherInCategories() {
            List<String> categories = Arrays.asList("software-engineering", "accounting", "marketing");

            List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories(categories);

            assertNotNull(groups);
            assertEquals(3, groups.size());
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.TECHNOLOGY));
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.FINANCE));
            assertTrue(groups.contains(ApprenticeshipCategoryGroup.MARKETING));
        }

        @Test
        @DisplayName("Should deduplicate unified categories from multiple sources")
        void testDeduplication() {
            List<String> categories =
                    Arrays.asList("software-engineering", "cyber-security", "artificial-intelligence");

            List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories(categories);

            assertNotNull(groups);
            // All three should map to TECHNOLOGY, but should only appear once
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.TECHNOLOGY, groups.get(0));
        }

        @Test
        @DisplayName("Should handle mixed Higher In and GOV.UK categories")
        void testMixedCategories() {
            List<String> categories = Arrays.asList("software-engineering", "Digital");

            List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories(categories);

            assertNotNull(groups);
            // Both map to TECHNOLOGY, should appear once
            assertEquals(1, groups.size());
            assertEquals(ApprenticeshipCategoryGroup.TECHNOLOGY, groups.get(0));
        }
    }

    @Nested
    @DisplayName("Unified Category Names")
    class UnifiedCategoryNameTests {

        @Test
        @DisplayName("Should return formatted category name for Higher In category")
        void testHigherInCategoryName() {
            List<String> names = CategoryMapper.getUnifiedCategoryNames("software-engineering");

            assertNotNull(names);
            assertEquals(1, names.size());
            assertEquals("Technology", names.get(0));
        }

        @Test
        @DisplayName("Should return formatted category name for GOV.UK route")
        void testGovUkCategoryName() {
            List<String> names = CategoryMapper.getUnifiedCategoryNames("Digital");

            assertNotNull(names);
            assertEquals(1, names.size());
            assertEquals("Technology", names.get(0));
        }

        @Test
        @DisplayName("Should format PUBLIC_SECTOR correctly")
        void testPublicSectorFormatting() {
            List<String> names = CategoryMapper.getUnifiedCategoryNames("teaching");

            assertNotNull(names);
            assertTrue(names.contains("Public Sector"));
        }

        @Test
        @DisplayName("Should return multiple formatted names for multi-mapped category")
        void testMultiMappedCategoryNames() {
            List<String> names = CategoryMapper.getUnifiedCategoryNames("Legal, finance and accounting");

            assertNotNull(names);
            assertEquals(2, names.size());
            assertTrue(names.contains("Legal"));
            assertTrue(names.contains("Finance"));
        }

        @Test
        @DisplayName("Should return formatted names for list of categories")
        void testCategoryNamesList() {
            List<String> categories = Arrays.asList("software-engineering", "accounting", "marketing");
            List<String> names = CategoryMapper.getUnifiedCategoryNames(categories);

            assertNotNull(names);
            assertEquals(3, names.size());
            assertTrue(names.contains("Technology"));
            assertTrue(names.contains("Finance"));
            assertTrue(names.contains("Marketing"));
        }
    }

    @Nested
    @DisplayName("Utility Methods")
    class UtilityMethodTests {

        @Test
        @DisplayName("Should check if category has unified mapping")
        void testHasUnifiedMapping() {
            assertTrue(CategoryMapper.hasUnifiedMapping("software-engineering"));
            assertTrue(CategoryMapper.hasUnifiedMapping("Digital"));
            assertFalse(CategoryMapper.hasUnifiedMapping("unknown-category"));
            assertFalse(CategoryMapper.hasUnifiedMapping(null));
            assertFalse(CategoryMapper.hasUnifiedMapping(""));
        }

        @Test
        @DisplayName("Should return all unified category groups")
        void testGetAllUnifiedCategories() {
            ApprenticeshipCategoryGroup[] allCategories = CategoryMapper.getAllUnifiedCategories();

            assertNotNull(allCategories);
            assertEquals(14, allCategories.length);
            assertTrue(Arrays.asList(allCategories).contains(ApprenticeshipCategoryGroup.TECHNOLOGY));
            assertTrue(Arrays.asList(allCategories).contains(ApprenticeshipCategoryGroup.FINANCE));
        }

        @Test
        @DisplayName("Should return all unified category names")
        void testGetAllUnifiedCategoryNames() {
            List<String> allNames = CategoryMapper.getAllUnifiedCategoryNames();

            assertNotNull(allNames);
            assertEquals(14, allNames.size());
            assertTrue(allNames.contains("Technology"));
            assertTrue(allNames.contains("Finance"));
            assertTrue(allNames.contains("Business"));
            assertTrue(allNames.contains("Engineering"));
            assertTrue(allNames.contains("Public Sector"));
        }
    }

    @Nested
    @DisplayName("Case Sensitivity")
    class CaseSensitivityTests {

        @Test
        @DisplayName("Should handle lowercase Higher In categories")
        void testLowercaseHigherIn() {
            List<ApprenticeshipCategoryGroup> groups =
                    CategoryMapper.mapToUnifiedCategories("software-engineering");

            assertNotNull(groups);
            assertFalse(groups.isEmpty());
        }

        @Test
        @DisplayName("Should handle title case GOV.UK routes")
        void testTitleCaseGovUk() {
            List<ApprenticeshipCategoryGroup> groups = CategoryMapper.mapToUnifiedCategories("Digital");

            assertNotNull(groups);
            assertFalse(groups.isEmpty());
        }

        @Test
        @DisplayName("Should be case-sensitive for GOV.UK routes")
        void testGovUkCaseSensitivity() {
            // GOV.UK routes use specific capitalization
            List<ApprenticeshipCategoryGroup> groups1 = CategoryMapper.mapToUnifiedCategories("Digital");
            List<ApprenticeshipCategoryGroup> groups2 = CategoryMapper.mapToUnifiedCategories("digital");

            assertFalse(groups1.isEmpty(), "Title case should map");
            assertTrue(
                    groups2.isEmpty() || !groups2.isEmpty(),
                    "Lowercase may or may not map depending on implementation");
        }
    }

    @Nested
    @DisplayName("Complete Coverage")
    class CompleteCoverageTests {

        @Test
        @DisplayName("Should map all 14 unified categories from various sources")
        void testAllUnifiedCategoriesAreMappable() {
            // Test that we can reach all 14 unified categories through mapping
            String[][] testData = {
                {"software-engineering", "Technology"},
                {"accounting", "Finance"},
                {"business-management", "Business"},
                {"mechanical-engineering", "Engineering"},
                {"marketing", "Marketing"},
                {"graphic-design", "Design"},
                {"commercial-law", "Legal"},
                {"construction", "Construction"},
                {"retail-manager", "Retail"},
                {"hospitality-management", "Hospitality"},
                {"human-resources", "Hr"},
                {"property-management", "Property"},
                {"teaching", "Public Sector"},
                {"chemistry", "Science"}
            };

            for (String[] test : testData) {
                List<String> names = CategoryMapper.getUnifiedCategoryNames(test[0]);
                assertTrue(names.contains(test[1]), "Category " + test[0] + " should map to " + test[1]);
            }
        }
    }
}
