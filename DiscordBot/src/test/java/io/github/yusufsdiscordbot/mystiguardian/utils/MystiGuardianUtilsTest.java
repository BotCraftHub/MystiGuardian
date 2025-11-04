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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MystiGuardianUtils}.
 *
 * <p>Tests utility methods for date formatting, duration formatting, color handling, and string
 * validation.
 */
@DisplayName("MystiGuardianUtils Tests")
class MystiGuardianUtilsTest {

    @Nested
    @DisplayName("Duration Formatting")
    class DurationFormattingTests {

        @Test
        @DisplayName("Should format duration with days")
        void testFormatDurationWithDays() {
            Duration duration = Duration.ofDays(2).plusHours(5).plusMinutes(30).plusSeconds(45);
            String result = MystiGuardianUtils.formatUptimeDuration(duration);
            assertEquals("2 days, 5 hours, 30 minutes, 45 seconds", result);
        }

        @Test
        @DisplayName("Should format duration with hours only")
        void testFormatDurationWithHours() {
            Duration duration = Duration.ofHours(3).plusMinutes(15).plusSeconds(20);
            String result = MystiGuardianUtils.formatUptimeDuration(duration);
            assertEquals("3 hours, 15 minutes, 20 seconds", result);
        }

        @Test
        @DisplayName("Should format duration with minutes only")
        void testFormatDurationWithMinutes() {
            Duration duration = Duration.ofMinutes(45).plusSeconds(30);
            String result = MystiGuardianUtils.formatUptimeDuration(duration);
            assertEquals("45 minutes, 30 seconds", result);
        }

        @Test
        @DisplayName("Should format duration with seconds only")
        void testFormatDurationWithSeconds() {
            Duration duration = Duration.ofSeconds(55);
            String result = MystiGuardianUtils.formatUptimeDuration(duration);
            assertEquals("55 seconds", result);
        }

        @Test
        @DisplayName("Should format zero duration")
        void testFormatZeroDuration() {
            Duration duration = Duration.ZERO;
            String result = MystiGuardianUtils.formatUptimeDuration(duration);
            assertEquals("0 seconds", result);
        }

        @Test
        @DisplayName("Should format one day exactly")
        void testFormatOneDayExactly() {
            Duration duration = Duration.ofDays(1);
            String result = MystiGuardianUtils.formatUptimeDuration(duration);
            assertEquals("1 days, 0 hours, 0 minutes, 0 seconds", result);
        }
    }

    @Nested
    @DisplayName("DateTime Formatting")
    class DateTimeFormattingTests {

        @Test
        @DisplayName("Should format OffsetDateTime correctly")
        void testFormatOffsetDateTime() {
            OffsetDateTime dateTime = OffsetDateTime.of(2025, 11, 4, 14, 30, 45, 0, ZoneOffset.UTC);
            String result = MystiGuardianUtils.formatOffsetDateTime(dateTime);
            assertEquals("2025-11-04 14:30:45", result);
        }

        @Test
        @DisplayName("Should format OffsetDateTime with single digit month and day")
        void testFormatOffsetDateTimeSingleDigits() {
            OffsetDateTime dateTime = OffsetDateTime.of(2025, 1, 5, 9, 5, 3, 0, ZoneOffset.UTC);
            String result = MystiGuardianUtils.formatOffsetDateTime(dateTime);
            assertEquals("2025-01-05 09:05:03", result);
        }

        @Test
        @DisplayName("Should format midnight correctly")
        void testFormatMidnight() {
            OffsetDateTime dateTime = OffsetDateTime.of(2025, 12, 25, 0, 0, 0, 0, ZoneOffset.UTC);
            String result = MystiGuardianUtils.formatOffsetDateTime(dateTime);
            assertEquals("2025-12-25 00:00:00", result);
        }
    }

    @Nested
    @DisplayName("Color Utilities")
    class ColorUtilityTests {

        @Test
        @DisplayName("Should return bot color")
        void testGetBotColor() {
            Color color = MystiGuardianUtils.getBotColor();
            assertNotNull(color);
            assertEquals(148, color.getRed());
            assertEquals(87, color.getGreen());
            assertEquals(235, color.getBlue());
        }

        @Test
        @DisplayName("Should return new color instance each time")
        void testGetBotColorReturnsNewInstance() {
            Color color1 = MystiGuardianUtils.getBotColor();
            Color color2 = MystiGuardianUtils.getBotColor();
            assertNotSame(color1, color2);
            assertEquals(color1, color2);
        }
    }

    @Nested
    @DisplayName("Zone Offset")
    class ZoneOffsetTests {

        @Test
        @DisplayName("Should return UTC zone offset")
        void testGetZoneOffset() {
            ZoneOffset offset = MystiGuardianUtils.getZoneOffset();
            assertEquals(ZoneOffset.UTC, offset);
        }
    }

    @Nested
    @DisplayName("Long Validation")
    class LongValidationTests {

        @Test
        @DisplayName("Should validate positive long string")
        void testIsLongPositive() {
            assertTrue(MystiGuardianUtils.isLong("123456789"));
        }

        @Test
        @DisplayName("Should validate large positive long string")
        void testIsLongLargePositive() {
            assertTrue(MystiGuardianUtils.isLong("9223372036854775807")); // Long.MAX_VALUE
        }

        @Test
        @DisplayName("Should reject null string")
        void testIsLongNull() {
            assertFalse(MystiGuardianUtils.isLong(null));
        }

        @Test
        @DisplayName("Should reject empty string")
        void testIsLongEmpty() {
            assertFalse(MystiGuardianUtils.isLong(""));
        }

        @Test
        @DisplayName("Should reject whitespace string")
        void testIsLongWhitespace() {
            assertFalse(MystiGuardianUtils.isLong("   "));
        }

        @Test
        @DisplayName("Should reject non-numeric string")
        void testIsLongNonNumeric() {
            assertFalse(MystiGuardianUtils.isLong("abc123"));
        }

        @Test
        @DisplayName("Should reject negative number")
        void testIsLongNegative() {
            assertFalse(MystiGuardianUtils.isLong("-123"));
        }

        @Test
        @DisplayName("Should reject zero")
        void testIsLongZero() {
            assertFalse(MystiGuardianUtils.isLong("0"));
        }

        @Test
        @DisplayName("Should reject decimal number")
        void testIsLongDecimal() {
            assertFalse(MystiGuardianUtils.isLong("123.456"));
        }
    }

    @Nested
    @DisplayName("String Formatting")
    class StringFormattingTests {

        @Test
        @DisplayName("Should format string with arguments")
        void testFormatStringWithArgs() {
            String result =
                    MystiGuardianUtils.formatString("Hello %s, you are %d years old", "Alice", 25);
            assertEquals("Hello Alice, you are 25 years old", result);
        }

        @Test
        @DisplayName("Should format string without arguments")
        void testFormatStringNoArgs() {
            String result = MystiGuardianUtils.formatString("Hello World");
            assertEquals("Hello World", result);
        }

        @Test
        @DisplayName("Should return null for invalid format specifier")
        void testFormatStringInvalidFormat() {
            // Invalid format specifier (no type after %)
            String result = MystiGuardianUtils.formatString("Hello %", "test");
            assertNull(result);
        }

        @Test
        @DisplayName("Should format string with multiple placeholders")
        void testFormatStringMultiplePlaceholders() {
            String result =
                    MystiGuardianUtils.formatString("User: %s, ID: %d, Active: %b", "Bob", 12345, true);
            assertEquals("User: Bob, ID: 12345, Active: true", result);
        }
    }

    @Nested
    @DisplayName("Random ID Generation")
    class RandomIdTests {

        @Test
        @DisplayName("Should generate positive random ID")
        void testGetRandomIdPositive() {
            Long id = MystiGuardianUtils.getRandomId();
            assertNotNull(id);
            assertTrue(id > 0);
        }

        @Test
        @DisplayName("Should generate different IDs")
        void testGetRandomIdUnique() {
            Long id1 = MystiGuardianUtils.getRandomId();
            Long id2 = MystiGuardianUtils.getRandomId();
            assertNotEquals(id1, id2);
        }

        @Test
        @DisplayName("Should generate valid Discord ID range")
        void testGetRandomIdRange() {
            for (int i = 0; i < 10; i++) {
                Long id = MystiGuardianUtils.getRandomId();
                assertTrue(id > 0, "ID should be positive");
                assertTrue(id < Long.MAX_VALUE, "ID should be within Long range");
            }
        }
    }

    @Nested
    @DisplayName("Memory Usage")
    class MemoryUsageTests {

        @Test
        @DisplayName("Should return memory usage string")
        void testGetMemoryUsage() {
            String memoryUsage = MystiGuardianUtils.getMemoryUsage();
            assertNotNull(memoryUsage);
            assertTrue(memoryUsage.contains("Heap Memory"));
            assertTrue(memoryUsage.contains("Non-Heap Memory"));
        }

        @Test
        @DisplayName("Memory usage should have expected format")
        void testMemoryUsageFormat() {
            String memoryUsage = MystiGuardianUtils.getMemoryUsage();
            // Check that it contains expected sections
            assertTrue(memoryUsage.contains("Heap Memory:"));
            assertTrue(memoryUsage.contains("Non-Heap Memory:"));
            assertTrue(memoryUsage.contains("MB"));
        }
    }
}
