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
package io.github.yusufsdiscordbot.mystiguardian.apprenticeship;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link HigherinApprenticeship}.
 *
 * <p>Tests the HigherinApprenticeship model including property management, category handling, and
 * embed generation.
 */
@DisplayName("HigherinApprenticeship Tests")
class HigherinApprenticeshipTest {

    private HigherinApprenticeship apprenticeship;

    @BeforeEach
    void setUp() {
        apprenticeship = new HigherinApprenticeship();
    }

    @Nested
    @DisplayName("Property Management")
    class PropertyManagementTests {

        @Test
        @DisplayName("Should set and get basic properties")
        void testBasicProperties() {
            apprenticeship.setId("12345");
            apprenticeship.setTitle("Software Engineer Degree Apprenticeship");
            apprenticeship.setCompanyName("Tech Corp");
            apprenticeship.setLocation("London");
            apprenticeship.setSalary("£25,000");
            apprenticeship.setUrl("https://example.com/apprenticeship/12345");

            assertEquals("12345", apprenticeship.getId());
            assertEquals("Software Engineer Degree Apprenticeship", apprenticeship.getTitle());
            assertEquals("Tech Corp", apprenticeship.getCompanyName());
            assertEquals("London", apprenticeship.getLocation());
            assertEquals("£25,000", apprenticeship.getSalary());
            assertEquals("https://example.com/apprenticeship/12345", apprenticeship.getUrl());
        }

        @Test
        @DisplayName("Should handle dates properly")
        void testDateProperties() {
            LocalDate openingDate = LocalDate.of(2025, 1, 1);
            LocalDate closingDate = LocalDate.of(2025, 12, 31);

            apprenticeship.setOpeningDate(openingDate);
            apprenticeship.setClosingDate(closingDate);

            assertEquals(openingDate, apprenticeship.getOpeningDate());
            assertEquals(closingDate, apprenticeship.getClosingDate());
        }

        @Test
        @DisplayName("Should handle null dates")
        void testNullDates() {
            apprenticeship.setOpeningDate(null);
            apprenticeship.setClosingDate(null);

            assertNull(apprenticeship.getOpeningDate());
            assertNull(apprenticeship.getClosingDate());
        }

        @Test
        @DisplayName("Should set company logo")
        void testCompanyLogo() {
            apprenticeship.setCompanyLogo("https://example.com/logo.png");
            assertEquals("https://example.com/logo.png", apprenticeship.getCompanyLogo());
        }

        @Test
        @DisplayName("Should throw exception when setting null ID")
        void testSetNullId() {
            assertThrows(
                    NullPointerException.class,
                    () -> apprenticeship.setId(null),
                    "HigherinApprenticeship ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Category Management")
    class CategoryManagementTests {

        @Test
        @DisplayName("Should initialize with empty categories list")
        void testEmptyCategoriesInitialization() {
            HigherinApprenticeship newApprenticeship = new HigherinApprenticeship();
            assertNotNull(newApprenticeship.getCategories());
            assertTrue(newApprenticeship.getCategories().isEmpty());
        }

        @Test
        @DisplayName("Should set categories list")
        void testSetCategories() {
            List<String> categories = Arrays.asList("software-engineering", "cyber-security");
            apprenticeship.setCategories(categories);

            assertEquals(2, apprenticeship.getCategories().size());
            assertTrue(apprenticeship.getCategories().contains("software-engineering"));
            assertTrue(apprenticeship.getCategories().contains("cyber-security"));
        }

        @Test
        @DisplayName("Should create defensive copy of categories")
        void testCategoriesDefensiveCopy() {
            // Use ArrayList to create a mutable list
            List<String> categories = new java.util.ArrayList<>(Arrays.asList("software-engineering"));
            apprenticeship.setCategories(categories);

            // Modify original list
            categories.clear();

            // Apprenticeship should still have the category
            assertEquals(1, apprenticeship.getCategories().size());
            assertTrue(apprenticeship.getCategories().contains("software-engineering"));
        }

        @Test
        @DisplayName("Should handle null categories as empty list")
        void testNullCategories() {
            apprenticeship.setCategories(null);
            assertNotNull(apprenticeship.getCategories());
            assertTrue(apprenticeship.getCategories().isEmpty());
        }

        @Test
        @DisplayName("Should return unified categories")
        void testGetUnifiedCategories() {
            apprenticeship.setCategories(Arrays.asList("software-engineering", "accounting"));

            List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

            assertNotNull(unifiedCategories);
            assertTrue(unifiedCategories.contains("Technology"));
            assertTrue(unifiedCategories.contains("Finance"));
        }

        @Test
        @DisplayName("Should return empty unified categories for empty source categories")
        void testEmptyUnifiedCategories() {
            apprenticeship.setCategories(Collections.emptyList());

            List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

            assertNotNull(unifiedCategories);
            assertTrue(unifiedCategories.isEmpty());
        }
    }

    @Nested
    @DisplayName("Embed Generation")
    class EmbedGenerationTests {

        @Test
        @DisplayName("Should generate embed with complete information")
        void testGenerateCompleteEmbed() {
            apprenticeship.setId("12345");
            apprenticeship.setTitle("Software Engineer Degree Apprenticeship");
            apprenticeship.setCompanyName("Tech Corp");
            apprenticeship.setLocation("London");
            apprenticeship.setSalary("£25,000");
            apprenticeship.setUrl("https://example.com/apprenticeship/12345");
            apprenticeship.setCategories(Arrays.asList("software-engineering"));
            apprenticeship.setOpeningDate(LocalDate.of(2025, 1, 1));
            apprenticeship.setClosingDate(LocalDate.of(2025, 12, 31));
            apprenticeship.setCompanyLogo("https://example.com/logo.png");

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed);
            assertNotNull(embed.getTitle());
            assertTrue(embed.getTitle().contains("Software Engineer Degree Apprenticeship"));
            assertTrue(embed.getTitle().contains("Tech Corp"));
            assertNotNull(embed.getDescription());
            assertNotNull(embed.getColor());
            assertNotNull(embed.getThumbnail());
        }

        @Test
        @DisplayName("Should generate embed without company logo")
        void testGenerateEmbedWithoutLogo() {
            apprenticeship.setId("12345");
            apprenticeship.setTitle("Software Engineer");
            apprenticeship.setCompanyName("Tech Corp");
            apprenticeship.setLocation("Remote");
            apprenticeship.setSalary("£30,000");
            apprenticeship.setUrl("https://example.com/apprenticeship/12345");

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed);
            assertNull(embed.getThumbnail());
        }

        @Test
        @DisplayName("Should handle missing title gracefully")
        void testGenerateEmbedWithoutTitle() {
            apprenticeship.setId("12345");
            apprenticeship.setCompanyName("Tech Corp");
            apprenticeship.setLocation("London");
            apprenticeship.setSalary("£25,000");
            apprenticeship.setUrl("https://example.com/apprenticeship/12345");

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed);
            assertNotNull(embed.getTitle());
            // Should fall back to "Apprenticeship Opportunity"
            assertTrue(embed.getTitle().contains("Apprenticeship Opportunity"));
        }

        @Test
        @DisplayName("Should include footer in embed")
        void testEmbedFooter() {
            apprenticeship.setId("12345");
            apprenticeship.setTitle("Test");
            apprenticeship.setCompanyName("Test Corp");
            apprenticeship.setLocation("Test Location");
            apprenticeship.setSalary("£20,000");
            apprenticeship.setUrl("https://example.com");

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed.getFooter());
            assertEquals("Source: Higher Education", embed.getFooter().getText());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode")
    class EqualityTests {

        @Test
        @DisplayName("Should be equal when IDs match")
        void testEqualityById() {
            HigherinApprenticeship app1 = new HigherinApprenticeship();
            app1.setId("12345");
            app1.setTitle("Title 1");

            HigherinApprenticeship app2 = new HigherinApprenticeship();
            app2.setId("12345");
            app2.setTitle("Title 2");

            assertEquals(app1, app2);
            assertEquals(app1.hashCode(), app2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when IDs differ")
        void testInequalityByDifferentId() {
            HigherinApprenticeship app1 = new HigherinApprenticeship();
            app1.setId("12345");

            HigherinApprenticeship app2 = new HigherinApprenticeship();
            app2.setId("67890");

            assertNotEquals(app1, app2);
        }

        @Test
        @DisplayName("Should not equal null")
        void testNotEqualToNull() {
            HigherinApprenticeship app = new HigherinApprenticeship();
            app.setId("12345");

            assertNotEquals(null, app);
        }
    }

    @Nested
    @DisplayName("Interface Compliance")
    class InterfaceComplianceTests {

        @Test
        @DisplayName("Should implement Apprenticeship interface")
        void testImplementsInterface() {
            assertTrue(apprenticeship instanceof Apprenticeship);
        }

        @Test
        @DisplayName("Should provide all required interface methods")
        void testInterfaceMethods() {
            apprenticeship.setId("12345");
            apprenticeship.setTitle("Test Title");
            apprenticeship.setCompanyName("Test Company");
            apprenticeship.setLocation("Test Location");
            apprenticeship.setSalary("£20,000");
            apprenticeship.setUrl("https://example.com");

            // Test all interface methods
            assertNotNull(apprenticeship.getId());
            assertNotNull(apprenticeship.getTitle());
            assertNotNull(apprenticeship.getCompanyName());
            assertNotNull(apprenticeship.getLocation());
            assertNotNull(apprenticeship.getSalary());
            assertNotNull(apprenticeship.getUrl());
            assertNotNull(apprenticeship.getEmbed());
            assertNotNull(apprenticeship.getCategories());
            assertNotNull(apprenticeship.getUnifiedCategories());
        }
    }
}
