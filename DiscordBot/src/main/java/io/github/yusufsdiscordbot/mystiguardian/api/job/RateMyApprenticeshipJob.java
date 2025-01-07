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

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
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
public class RateMyApprenticeshipJob implements Job {
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

    public RateMyApprenticeshipJob() {
        this.categories = new ArrayList<>();
    }

    public void setId(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "RateMyApprenticeshipJob ID cannot be null");
    }

    public void setCategories(List<String> categories) {
        this.categories = categories != null ? new ArrayList<>(categories) : new ArrayList<>();
    }

    public MessageEmbed getEmbed() {
        val userIdToPing = MystiGuardianUtils.getMainConfig().ownerId();

        val embed =
                new EmbedBuilder()
                        .setColor(Color.cyan)
                        .setTitle(formatTitle())
                        .setDescription(formatDescription());

        if (!companyLogo.equals("Not Available")) {
            embed.setThumbnail(companyLogo);
        }

        addFields(embed);

        if (userIdToPing != null && !userIdToPing.isEmpty()) {
            embed.addField("Notification", String.format("<@%s>", userIdToPing), false);
        }

        return embed.build();
    }

    @NotNull
    private String formatTitle() {
        return companyName.equals("Not Available")
                ? title
                : String.format("%s at `%s`", title, companyName);
    }

    @NotNull
    private String formatDescription() {
        val desc = new StringBuilder();
        if (location != null && !location.isEmpty()) {
            desc.append("📍 ").append(location).append("\n\n");
        }
        if (salary != null && !salary.equals("Not specified")) {
            desc.append("💰 ").append(salary);
        }
        return desc.toString();
    }

    private void addFields(EmbedBuilder embed) {
        if (openingDate != null) {
            embed.addField("Opening Date", openingDate.toString(), true);
        }

        if (closingDate != null) {
            embed.addField("Closing Date", closingDate.toString(), true);
        }

        if (!categories.isEmpty()) {
            val formattedCategories =
                    categories.stream().map(this::formatCategory).collect(Collectors.joining("\n"));

            embed.addField(
                    categories.size() == 1 ? "Category 📚" : "Categories 📚", formattedCategories, false);
        }

        embed.addField("Apply Here", url, false);
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
        if (!(o instanceof RateMyApprenticeshipJob job)) return false;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
