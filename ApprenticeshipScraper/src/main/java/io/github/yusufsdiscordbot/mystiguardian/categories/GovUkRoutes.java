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

import io.github.yusufsdiscordbot.mystiguardian.scraper.FindAnApprenticeshipScraper;
import java.util.Map;

/**
 * Configuration class containing GOV.UK Find an Apprenticeship route categories.
 *
 * <p>Routes map human-readable category names to their GOV.UK route IDs. These IDs are used in
 * search URLs to filter apprenticeships by sector.
 *
 * <p>Available routes:
 *
 * <ul>
 *   <li>Agriculture, environmental and animal care (ID: 1)
 *   <li>Business and administration (ID: 2)
 *   <li>Care services (ID: 3)
 *   <li>Catering and hospitality (ID: 4)
 *   <li>Construction and the built environment (ID: 5)
 *   <li>Creative and design (ID: 6)
 *   <li>Digital (ID: 7)
 *   <li>Education and early years (ID: 8)
 *   <li>Engineering and manufacturing (ID: 9)
 *   <li>Hair and beauty (ID: 10)
 *   <li>Health and science (ID: 11)
 *   <li>Legal, finance and accounting (ID: 12)
 *   <li>Protective services (ID: 13)
 *   <li>Sales, marketing and procurement (ID: 14)
 *   <li>Transport and logistics (ID: 15)
 * </ul>
 *
 * @see FindAnApprenticeshipScraper
 */
public final class GovUkRoutes {

    private GovUkRoutes() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Map of all GOV.UK route categories with their corresponding IDs.
     *
     * @return immutable map of category names to route IDs
     */
    public static Map<String, Integer> getAllRoutes() {
        return Map.ofEntries(
                Map.entry("Agriculture, environmental and animal care", 1),
                Map.entry("Business and administration", 2),
                Map.entry("Care services", 3),
                Map.entry("Catering and hospitality", 4),
                Map.entry("Construction and the built environment", 5),
                Map.entry("Creative and design", 6),
                Map.entry("Digital", 7),
                Map.entry("Education and early years", 8),
                Map.entry("Engineering and manufacturing", 9),
                Map.entry("Hair and beauty", 10),
                Map.entry("Health and science", 11),
                Map.entry("Legal, finance and accounting", 12),
                Map.entry("Protective services", 13),
                Map.entry("Sales, marketing and procurement", 14),
                Map.entry("Transport and logistics", 15));
    }
}
