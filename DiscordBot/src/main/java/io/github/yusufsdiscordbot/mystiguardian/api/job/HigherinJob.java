/*
 * Copyright 2024 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.api.job;

import io.github.yusufsdiscordbot.mystiguardian.config.JobCategoryGroup;
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

@Getter
@Setter
@ToString
@Slf4j
public class HigherinJob implements Job {
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

    public HigherinJob() {
        this.categories = new ArrayList<>();
    }

    public void setId(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "HigherinJob ID cannot be null");
    }

    public void setCategories(List<String> categories) {
        this.categories = categories != null ? new ArrayList<>(categories) : new ArrayList<>();
    }

    public MessageEmbed getEmbed() {
        // Log warning if title is missing
        if (title == null || title.isEmpty()) {
            logger.warn("Job {} has no title!", id);
        }
        if (companyName == null || companyName.isEmpty()) {
            logger.warn("Job {} has no company name!", id);
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

    @NotNull
    private String formatEmbedTitle() {
        String jobTitle = (title != null && !title.isEmpty()) ? title : "Job Opportunity";
        String company =
                (companyName != null && !companyName.isEmpty() && !companyName.equals("Not Available"))
                        ? companyName
                        : null;

        if (company != null) {
            return "🎓 " + jobTitle + " @ " + company;
        }
        return "🎓 " + jobTitle;
    }

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
                            .filter(JobCategoryGroup::isValidCategory)
                            .map(this::formatCategory)
                            .collect(Collectors.joining("\n"));

            // Only add the field if there are valid categories
            if (!validCategories.isEmpty()) {
                long validCount = categories.stream().filter(JobCategoryGroup::isValidCategory).count();

                embed.addField(validCount == 1 ? "📚 Category" : "📚 Categories", validCategories, false);
            }
        }

        embed.addField("🔗 Apply Now", "[Click here to apply](" + url + ")", false);
    }

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
        if (!(o instanceof HigherinJob job)) return false;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
