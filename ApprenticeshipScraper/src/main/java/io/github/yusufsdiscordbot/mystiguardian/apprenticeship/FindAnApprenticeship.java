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

import java.awt.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an apprenticeship listing from GOV.UK's Find an Apprenticeship service.
 *
 * <p>This implementation of the {@link Apprenticeship} interface provides information about
 * apprenticeship opportunities scraped from the official UK government service at
 * findapprenticeship.service.gov.uk.
 *
 * <p>GOV.UK apprenticeships include:
 *
 * <ul>
 *   <li>Official government-verified listings
 *   <li>Creation date (when posted) and closing date
 *   <li>Official GOV.UK blue branding (#1D70B8)
 *   <li>Simpler structure compared to commercial platforms
 * </ul>
 *
 * <p>The name is kept as "FindAnApprenticeship" (not "Apprenticeship") to maintain consistency with
 * the source service naming.
 *
 * @see Apprenticeship
 * @see ApprenticeshipSource#GOV_UK
 */
@Getter
@Setter
@ToString
@Slf4j
public class FindAnApprenticeship implements Apprenticeship {
    private String id;
    private String name;
    private String url;
    private String companyName;
    private String salary;
    private String location;
    private String category;
    private LocalDate createdAtDate;
    private LocalDate closingDate;

    /**
     * Sets the unique identifier for this apprenticeship.
     *
     * @param id the apprenticeship ID, must not be null
     * @throws NullPointerException if id is null
     */
    public void setId(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "FindAnApprenticeship ID cannot be null");
    }

    /**
     * Generates a Discord embed for this GOV.UK apprenticeship.
     *
     * <p>The embed includes:
     *
     * <ul>
     *   <li>GOV.UK official blue color (#1D70B8)
     *   <li>Formatted title with company name
     *   <li>Location and salary information
     *   <li>Posted date and closing date with Discord timestamps
     *   <li>Application link
     * </ul>
     *
     * @return a formatted MessageEmbed ready for Discord posting
     */
    @Override
    public MessageEmbed getEmbed() {
        // Log warning if name is missing
        if (name == null || name.isEmpty()) {
            logger.warn("GOV.UK Apprenticeship {} has no name!", id);
        }
        if (companyName == null || companyName.isEmpty()) {
            logger.warn("GOV.UK Apprenticeship {} has no company name!", id);
        }

        val embed =
                new EmbedBuilder()
                        .setColor(Color.decode("#1D70B8")) // GOV.UK blue color
                        .setTitle(formatEmbedTitle())
                        .setDescription(formatDescription())
                        .setFooter("Source: Find an Apprenticeship", null);

        addFields(embed);

        return embed.build();
    }

    /**
     * Formats the embed title with emoji and company name. Falls back to "Apprenticeship Opportunity"
     * if name is missing.
     *
     * @return formatted title string
     */
    @NotNull
    private String formatEmbedTitle() {
        String jobTitle = (name != null && !name.isEmpty()) ? name : "Apprenticeship Opportunity";
        String company = (companyName != null && !companyName.isEmpty()) ? companyName : null;

        if (company != null) {
            return "🎓 " + jobTitle + " @ " + company;
        }
        return "🎓 " + jobTitle;
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

        if (salary != null && !salary.isEmpty()) {
            desc.append("💰 **Salary:** ").append(salary);
        }

        return desc.toString();
    }

    /**
     * Adds date and application link fields to the embed. Uses Discord timestamp formatting for
     * dates.
     *
     * @param embed the EmbedBuilder to add fields to
     */
    private void addFields(EmbedBuilder embed) {
        if (createdAtDate != null) {
            embed.addField("📅 Posted", "<t:" + createdAtDate.toEpochDay() * 86400 + ":R>", true);
        }

        if (closingDate != null) {
            long epochSeconds = closingDate.toEpochDay() * 86400;
            embed.addField("⏰ Closing Date", "<t:" + epochSeconds + ":D>", true);
            embed.addField("⌛ Time Left", "<t:" + epochSeconds + ":R>", true);
        }

        embed.addField("🔗 Apply Now", "[Click here to apply](" + url + ")", false);
    }

    /**
     * Gets the title of the apprenticeship. Maps the internal 'name' field to the interface's 'title'
     * method.
     *
     * @return the apprenticeship name/title
     */
    @Override
    public String getTitle() {
        return name;
    }

    /**
     * Gets the categories/tags associated with this GOV.UK apprenticeship.
     *
     * <p>Returns the GOV.UK route category (e.g., "Digital", "Engineering and manufacturing")
     * as a single-item list for consistency with the Apprenticeship interface.
     *
     * @return a list containing the route category, or empty list if no category is set
     */
    @Override
    public List<String> getCategories() {
        if (category != null && !category.isEmpty()) {
            return Collections.singletonList(category);
        }
        return Collections.emptyList();
    }
}
