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

import io.github.yusufsdiscordbot.mystiguardian.config.ApprenticeshipCategoryGroup;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an apprenticeship listing from Higher In (formerly Rate My Apprenticeship).
 *
 * <p>This implementation of the {@link Apprenticeship} interface provides detailed information
 * about apprenticeship opportunities scraped from the Higher In platform at higherin.com.
 *
 * <p>Higher In apprenticeships include:
 *
 * <ul>
 *   <li>Detailed category information for filtering
 *   <li>Company logos for visual identification
 *   <li>Opening and closing dates for application windows
 *   <li>Rich formatting in Discord embeds with modern styling
 * </ul>
 *
 * <p>Equality is based solely on the apprenticeship ID, allowing easy deduplication.
 *
 * @see Apprenticeship
 * @see ApprenticeshipSource#RATE_MY_APPRENTICESHIP
 */
@Getter
@Setter
@ToString
@Slf4j
public class HigherinApprenticeship implements Apprenticeship {
    private String id;
    private String title;
    private String companyName;
    private String companyLogo;
    private String location;
    private List<String> categories;
    private String salary;
    @Nullable private LocalDate openingDate;
    @Nullable private LocalDate closingDate;
    private String url;
    private String category;

    /** Constructs a new Higher In apprenticeship with an empty categories list. */
    public HigherinApprenticeship() {
        this.categories = new ArrayList<>();
    }

    /**
     * Sets the unique identifier for this apprenticeship.
     *
     * @param id the apprenticeship ID, must not be null
     * @throws NullPointerException if id is null
     */
    public void setId(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "HigherinApprenticeship ID cannot be null");
    }

    /**
     * Sets the categories for this apprenticeship. Creates a defensive copy of the provided list.
     *
     * @param categories the list of categories, or null for empty list
     */
    public void setCategories(List<String> categories) {
        this.categories = categories != null ? new ArrayList<>(categories) : new ArrayList<>();
    }

    /**
     * Generates a Discord embed for this apprenticeship.
     *
     * <p>The embed includes:
     *
     * <ul>
     *   <li>Modern teal color (#00B8A9)
     *   <li>Company logo as thumbnail (if available)
     *   <li>Formatted title with company name
     *   <li>Location and salary information
     *   <li>Opening and closing dates with Discord timestamps
     *   <li>Category tags
     *   <li>Application link
     * </ul>
     *
     * @return a formatted MessageEmbed ready for Discord posting
     */
    public MessageEmbed getEmbed() {
        // Log warning if title is missing
        if (title == null || title.isEmpty()) {
            logger.warn("Apprenticeship {} has no title!", id);
        }
        if (companyName == null || companyName.isEmpty()) {
            logger.warn("Apprenticeship {} has no company name!", id);
        }

        val embed =
                new EmbedBuilder()
                        .setColor(Color.decode("#00B8A9")) // Modern teal color
                        .setTitle(formatEmbedTitle())
                        .setDescription(formatDescription())
                        .setFooter("Source: Higher Education", null);

        if (companyLogo != null && !companyLogo.isEmpty() && !companyLogo.equals("Not Available")) {
            embed.setThumbnail(companyLogo);
        }

        addFields(embed);

        return embed.build();
    }

    /**
     * Formats the embed title with emoji and company name. Falls back to "Apprenticeship Opportunity"
     * if title is missing.
     *
     * @return formatted title string
     */
    @NotNull
    private String formatEmbedTitle() {
        String apprenticeshipTitle =
                (title != null && !title.isEmpty()) ? title : "Apprenticeship Opportunity";
        String company =
                (companyName != null && !companyName.isEmpty() && !companyName.equals("Not Available"))
                        ? companyName
                        : null;

        if (company != null) {
            return "🎓 " + apprenticeshipTitle + " @ " + company;
        }
        return "🎓 " + apprenticeshipTitle;
    }

    /**
     * Formats the embed description with location and salary information. Uses emojis for visual
     * clarity.
     *
     * @return formatted description string
     */
    @NotNull
    private String formatDescription() {
        val desc = new StringBuilder();

        if (location != null && !location.isEmpty()) {
            desc.append("📍 **Location:** ").append(location).append("\n\n");
        }

        if (salary != null && !salary.isEmpty() && !salary.equals("Not specified")) {
            desc.append("💰 **Salary:** ").append(salary);
        }

        return desc.toString();
    }

    /**
     * Adds date, category, and application link fields to the embed. Uses Discord timestamp
     * formatting for dates. Filters out invalid categories before adding.
     *
     * @param embed the EmbedBuilder to add fields to
     */
    private void addFields(EmbedBuilder embed) {
        if (openingDate != null) {
            long epochSeconds = openingDate.toEpochDay() * 86400;
            embed.addField("📅 Opening Date", "<t:" + epochSeconds + ":D>", true);
        }

        if (closingDate != null) {
            long epochSeconds = closingDate.toEpochDay() * 86400;
            embed.addField("⏰ Closing Date", "<t:" + epochSeconds + ":D>", true);
            embed.addField("⌛ Time Left", "<t:" + epochSeconds + ":R>", true);
        }

        if (!categories.isEmpty()) {
            // Filter out invalid categories and format the valid ones
            val validCategories =
                    categories.stream()
                            .filter(ApprenticeshipCategoryGroup::isValidCategory)
                            .map(this::formatCategory)
                            .collect(Collectors.joining("\n"));

            // Only add the field if there are valid categories
            if (!validCategories.isEmpty()) {
                long validCount =
                        categories.stream().filter(ApprenticeshipCategoryGroup::isValidCategory).count();

                embed.addField(validCount == 1 ? "📚 Category" : "📚 Categories", validCategories, false);
            }
        }

        embed.addField("🔗 Apply Now", "[Click here to apply](" + url + ")", false);
    }

    /**
     * Formats a category name with proper capitalization and bullet point. Converts hyphenated
     * categories to title case (e.g., "software-development" → "• Software Development").
     *
     * @param category the raw category string
     * @return formatted category with bullet point, or empty string if null/empty
     */
    private String formatCategory(String category) {
        if (category == null || category.isEmpty()) {
            return "";
        }
        String[] words = category.replace("-", " ").split("\\s+");
        val result = new StringBuilder("• ");

        for (val word : words) {
            if (!word.isEmpty()) {
                result
                        .append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HigherinApprenticeship apprenticeship)) return false;
        return Objects.equals(id, apprenticeship.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
