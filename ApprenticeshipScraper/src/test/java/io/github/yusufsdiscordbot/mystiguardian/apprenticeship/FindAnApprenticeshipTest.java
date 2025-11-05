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
import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FindAnApprenticeship}.
 *
 * <p>Tests the FindAnApprenticeship model from GOV.UK including property management and embed
 * generation.
 */
@DisplayName("FindAnApprenticeship Tests")
class FindAnApprenticeshipTest {

    private FindAnApprenticeship apprenticeship;

    @BeforeEach
    void setUp() {
        apprenticeship = new FindAnApprenticeship();
    }

    @Nested
    @DisplayName("Property Management")
    class PropertyManagementTests {

        @Test
        @DisplayName("Should set and get basic properties")
        void testBasicProperties() {
            apprenticeship.setId("VAC-123456");
            apprenticeship.setName("Data Analyst Degree Apprenticeship");
            apprenticeship.setCompanyName("Government Agency");
            apprenticeship.setLocation("Manchester");
            apprenticeship.setSalary("£22,000 per year");
            apprenticeship.setUrl("https://www.findapprenticeship.service.gov.uk/123456");
            apprenticeship.setCategory("Digital");

            assertEquals("VAC-123456", apprenticeship.getId());
            assertEquals("Data Analyst Degree Apprenticeship", apprenticeship.getName());
            assertEquals("Government Agency", apprenticeship.getCompanyName());
            assertEquals("Manchester", apprenticeship.getLocation());
            assertEquals("£22,000 per year", apprenticeship.getSalary());
            assertEquals("https://www.findapprenticeship.service.gov.uk/123456", apprenticeship.getUrl());
            assertEquals("Digital", apprenticeship.getCategory());
        }

        @Test
        @DisplayName("Should handle dates properly")
        void testDateProperties() {
            LocalDate createdDate = LocalDate.of(2025, 1, 15);
            LocalDate closingDate = LocalDate.of(2025, 3, 31);

            apprenticeship.setCreatedAtDate(createdDate);
            apprenticeship.setClosingDate(closingDate);

            assertEquals(createdDate, apprenticeship.getCreatedAtDate());
            assertEquals(closingDate, apprenticeship.getClosingDate());
        }

        @Test
        @DisplayName("Should handle null dates")
        void testNullDates() {
            apprenticeship.setCreatedAtDate(null);
            apprenticeship.setClosingDate(null);

            assertNull(apprenticeship.getCreatedAtDate());
            assertNull(apprenticeship.getClosingDate());
        }

        @Test
        @DisplayName("Should throw exception when setting null ID")
        void testSetNullId() {
            assertThrows(
                    NullPointerException.class,
                    () -> apprenticeship.setId(null),
                    "FindAnApprenticeship ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Apprenticeship Interface Methods")
    class InterfaceMethodsTests {

        @Test
        @DisplayName("Should implement getTitle from interface")
        void testGetTitle() {
            apprenticeship.setName("Software Developer Apprenticeship");
            assertEquals("Software Developer Apprenticeship", apprenticeship.getTitle());
        }

        @Test
        @DisplayName("Should return empty list for getCategories")
        void testGetCategories() {
            List<String> categories = apprenticeship.getCategories();
            assertNotNull(categories);
            assertTrue(categories.isEmpty());
        }

        @Test
        @DisplayName("Should return unified categories from single category")
        void testGetUnifiedCategories() {
            apprenticeship.setCategory("Digital");

            List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

            assertNotNull(unifiedCategories);
            assertTrue(unifiedCategories.contains("Technology"));
        }

        @Test
        @DisplayName("Should return empty unified categories for null category")
        void testGetUnifiedCategoriesNull() {
            apprenticeship.setCategory(null);

            List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

            assertNotNull(unifiedCategories);
            assertTrue(unifiedCategories.isEmpty());
        }

        @Test
        @DisplayName("Should return empty unified categories for unknown category")
        void testGetUnifiedCategoriesUnknown() {
            apprenticeship.setCategory("UnknownCategory");

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
            apprenticeship.setId("VAC-123456");
            apprenticeship.setName("Cyber Security Degree Apprenticeship");
            apprenticeship.setCompanyName("GCHQ");
            apprenticeship.setLocation("Cheltenham");
            apprenticeship.setSalary("£28,000 per year");
            apprenticeship.setUrl("https://www.findapprenticeship.service.gov.uk/123456");
            apprenticeship.setCategory("Digital");
            apprenticeship.setCreatedAtDate(LocalDate.of(2025, 1, 15));
            apprenticeship.setClosingDate(LocalDate.of(2025, 3, 31));

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed);
            assertNotNull(embed.getTitle());
            assertTrue(embed.getTitle().contains("Cyber Security Degree Apprenticeship"));
            assertTrue(embed.getTitle().contains("GCHQ"));
            assertNotNull(embed.getDescription());
            assertNotNull(embed.getColor());
        }

        @Test
        @DisplayName("Should handle missing name gracefully")
        void testGenerateEmbedWithoutName() {
            apprenticeship.setId("VAC-123456");
            apprenticeship.setCompanyName("Test Company");
            apprenticeship.setLocation("London");
            apprenticeship.setSalary("£20,000");
            apprenticeship.setUrl("https://www.findapprenticeship.service.gov.uk/123456");

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed);
            assertNotNull(embed.getTitle());
            // Should fall back to "Apprenticeship Opportunity"
            assertTrue(embed.getTitle().contains("Apprenticeship Opportunity"));
        }

        @Test
        @DisplayName("Should include footer in embed")
        void testEmbedFooter() {
            apprenticeship.setId("VAC-123456");
            apprenticeship.setName("Test Apprenticeship");
            apprenticeship.setCompanyName("Test Corp");
            apprenticeship.setLocation("Test Location");
            apprenticeship.setSalary("£20,000");
            apprenticeship.setUrl("https://example.com");

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed.getFooter());
            assertEquals("Source: Find an Apprenticeship", embed.getFooter().getText());
        }

        @Test
        @DisplayName("Should use GOV.UK blue color")
        void testEmbedColor() {
            apprenticeship.setId("VAC-123456");
            apprenticeship.setName("Test");
            apprenticeship.setCompanyName("Test Corp");
            apprenticeship.setLocation("Test Location");
            apprenticeship.setSalary("£20,000");
            apprenticeship.setUrl("https://example.com");

            MessageEmbed embed = apprenticeship.getEmbed();

            assertNotNull(embed.getColor());
            // GOV.UK blue is #1D70B8
            // getColorRaw() returns the packed RGB value as a signed 32-bit integer
            // where the alpha channel in the most significant byte can result in negative values
            java.awt.Color expectedColor = java.awt.Color.decode("#1D70B8");
            assertEquals(expectedColor.getRGB(), embed.getColorRaw());
        }
    }

    @Nested
    @DisplayName("Equality and HashCode")
    class EqualityTests {

        @Test
        @DisplayName("Should not have custom equals implementation")
        void testDefaultEquality() {
            FindAnApprenticeship app1 = new FindAnApprenticeship();
            app1.setId("VAC-123456");
            app1.setName("Name 1");

            FindAnApprenticeship app2 = new FindAnApprenticeship();
            app2.setId("VAC-123456");
            app2.setName("Name 1");

            // FindAnApprenticeship uses default Object equals (reference equality)
            assertNotEquals(app1, app2, "Different instances should not be equal without custom equals");
        }

        @Test
        @DisplayName("Should be equal to itself")
        void testSelfEquality() {
            FindAnApprenticeship app = new FindAnApprenticeship();
            app.setId("VAC-123456");

            assertEquals(app, app);
        }

        @Test
        @DisplayName("Should not equal null")
        void testNotEqualToNull() {
            FindAnApprenticeship app = new FindAnApprenticeship();
            app.setId("VAC-123456");

            assertNotEquals(null, app);
        }
    }

    @Nested
    @DisplayName("Interface Compliance")
    class InterfaceComplianceTests {

        @Test
        @DisplayName("Should provide all required interface methods")
        void testInterfaceMethods() {
            apprenticeship.setId("VAC-123456");
            apprenticeship.setName("Test Name");
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
