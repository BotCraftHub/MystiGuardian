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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for unified categories functionality in apprenticeship implementations.
 *
 * <p>Tests the {@link Apprenticeship#getUnifiedCategories()} method and its integration with
 * different apprenticeship sources.
 */
@DisplayName("Apprenticeship Unified Categories Tests")
class ApprenticeshipUnifiedCategoriesTest {

    @Test
    @DisplayName("HigherinApprenticeship should return unified categories")
    void testHigherinApprenticeshipUnifiedCategories() {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();
        apprenticeship.setId("test-1");
        apprenticeship.setCategories(Arrays.asList("software-engineering", "cyber-security"));

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertFalse(unifiedCategories.isEmpty());
        assertEquals(1, unifiedCategories.size());
        assertEquals("Technology", unifiedCategories.get(0));
    }

    @Test
    @DisplayName(
            "HigherinApprenticeship with multiple sectors should return multiple unified categories")
    void testHigherinApprenticeshipMultipleSectors() {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();
        apprenticeship.setId("test-2");
        apprenticeship.setCategories(Arrays.asList("software-engineering", "accounting", "marketing"));

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertEquals(3, unifiedCategories.size());
        assertTrue(unifiedCategories.contains("Technology"));
        assertTrue(unifiedCategories.contains("Finance"));
        assertTrue(unifiedCategories.contains("Marketing"));
    }

    @Test
    @DisplayName(
            "HigherinApprenticeship with empty categories should return empty unified categories")
    void testHigherinApprenticeshipEmptyCategories() {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();
        apprenticeship.setId("test-3");
        apprenticeship.setCategories(Collections.emptyList());

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertTrue(unifiedCategories.isEmpty());
    }

    @Test
    @DisplayName("FindAnApprenticeship should return unified categories")
    void testFindAnApprenticeshipUnifiedCategories() {
        FindAnApprenticeship apprenticeship = new FindAnApprenticeship();
        apprenticeship.setId("test-4");
        apprenticeship.setCategory("Digital");

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertEquals(1, unifiedCategories.size());
        assertEquals("Technology", unifiedCategories.get(0));
    }

    @Test
    @DisplayName(
            "FindAnApprenticeship with multi-mapped category should return multiple unified categories")
    void testFindAnApprenticeshipMultiMapped() {
        FindAnApprenticeship apprenticeship = new FindAnApprenticeship();
        apprenticeship.setId("test-5");
        apprenticeship.setCategory("Legal, finance and accounting");

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertEquals(2, unifiedCategories.size());
        assertTrue(unifiedCategories.contains("Legal"));
        assertTrue(unifiedCategories.contains("Finance"));
    }

    @Test
    @DisplayName("FindAnApprenticeship with null category should return empty unified categories")
    void testFindAnApprenticeshipNullCategory() {
        FindAnApprenticeship apprenticeship = new FindAnApprenticeship();
        apprenticeship.setId("test-6");
        apprenticeship.setCategory(null);

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertTrue(unifiedCategories.isEmpty());
    }

    @Test
    @DisplayName("Source categories should be preserved while unified categories are derived")
    void testSourceCategoriesPreserved() {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();
        apprenticeship.setId("test-7");
        List<String> sourceCategories = Arrays.asList("software-engineering", "data-analysis");
        apprenticeship.setCategories(sourceCategories);

        // Source categories should be unchanged
        List<String> retrievedSourceCategories = apprenticeship.getCategories();
        assertEquals(2, retrievedSourceCategories.size());
        assertTrue(retrievedSourceCategories.contains("software-engineering"));
        assertTrue(retrievedSourceCategories.contains("data-analysis"));

        // Unified categories should be mapped
        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();
        assertEquals(1, unifiedCategories.size());
        assertEquals("Technology", unifiedCategories.get(0));
    }

    @Test
    @DisplayName("Default implementation should handle unknown categories gracefully")
    void testUnknownCategories() {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();
        apprenticeship.setId("test-8");
        apprenticeship.setCategories(Arrays.asList("unknown-category-xyz"));

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertTrue(unifiedCategories.isEmpty());
    }

    @Test
    @DisplayName("Should deduplicate unified categories when source categories map to same group")
    void testDeduplicationInUnifiedCategories() {
        HigherinApprenticeship apprenticeship = new HigherinApprenticeship();
        apprenticeship.setId("test-9");
        // All three map to Technology
        apprenticeship.setCategories(
                Arrays.asList("software-engineering", "cyber-security", "artificial-intelligence"));

        List<String> unifiedCategories = apprenticeship.getUnifiedCategories();

        assertNotNull(unifiedCategories);
        assertEquals(1, unifiedCategories.size());
        assertEquals("Technology", unifiedCategories.get(0));
    }
}
