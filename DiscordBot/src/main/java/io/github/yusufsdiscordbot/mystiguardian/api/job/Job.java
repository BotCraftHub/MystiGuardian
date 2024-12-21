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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@ToString
public class Job {
    private String id;
    private String title;
    private String companyName;
    private String companyLogo;
    private String location;
    private String category;
    private String salary;
    @Nullable private LocalDate openingDate;
    @Nullable private LocalDate closingDate;
    private String url;

    public void setId(@NotNull String id) {
        this.id = Objects.requireNonNull(id, "Job ID cannot be null");
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder embed = getEmbedBuilder();
        embed.setColor(Color.cyan);

        if (companyName.equals("Not Available")) {
            embed.setTitle(title);
        } else {
            embed.setTitle(title + " at `" + companyName + "`");
        }

        if (!companyLogo.equals("Not Available")) {
            embed.setThumbnail(companyLogo);
        }

        return embed.build();
    }

    @NotNull
    private EmbedBuilder getEmbedBuilder() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.addField("Location:", location, false);
        embed.addField("Salary:", salary, false);
        embed.addField(
                "Opening Date:", openingDate != null ? openingDate.toString() : "Not specified", false);
        embed.addField(
                "Closing Date:", closingDate != null ? closingDate.toString() : "Not specified", false);
        embed.addField("URL:", url, false);
        embed.addField("Category:", category, false);
        return embed;
    }
}
