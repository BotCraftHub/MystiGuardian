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

import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString
@Slf4j
public class FindAnApprenticeshipJob implements Job {
    private String id;
    private String name;
    private String url;
    private String companyName;
    private String salary;
    private String location;
    private LocalDate createdAtDate;
    private LocalDate closingDate;

    public void setId(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "FindAnApprenticeshipJob ID cannot be null");
    }

    @Override
    public MessageEmbed getEmbed() {
        // Log warning if name is missing
        if (name == null || name.isEmpty()) {
            logger.warn("GOV.UK Job {} has no name!", id);
        }
        if (companyName == null || companyName.isEmpty()) {
            logger.warn("GOV.UK Job {} has no company name!", id);
        }

        val embed =
                new EmbedBuilder()
                        .setColor(Color.cyan)
                        .setTitle(formatEmbedTitle())
                        .setDescription(formatDescription());

        addFields(embed);

        // No longer add notification inside embed - it will be sent as message content

        return embed.build();
    }

    @NotNull
    private String formatEmbedTitle() {
        String jobTitle = (name != null && !name.isEmpty()) ? name : "Apprenticeship Opportunity";
        String company = (companyName != null && !companyName.isEmpty()) ? companyName : null;

        if (company != null) {
            return jobTitle + " | " + company;
        }
        return jobTitle;
    }

    @NotNull
    private String formatDescription() {
        val desc = new StringBuilder();
        if (location != null && !location.isEmpty()) {
            desc.append("📍 ").append(location).append("\n");
        }
        if (salary != null && !salary.isEmpty()) {
            desc.append("💰 ").append(salary);
        }
        return desc.toString();
    }

    private void addFields(EmbedBuilder embed) {
        if (createdAtDate != null) {
            embed.addField("Posted Date", createdAtDate.toString(), true);
        }

        if (closingDate != null) {
            embed.addField("Closing Date", closingDate.toString(), true);
        }

        embed.addField("Apply Here", url, false);
    }

    @Override
    public String getTitle() {
        return name;
    }
}