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

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@ToString
public class Job {
    private String id;
    private String title;
    private String location;
    private String category;
    private String salary;
    @Nullable private LocalDate openingDate;
    @Nullable private LocalDate closingDate;
    private String url;

    public String toJson() {
        return "{"
                + "\"id\": \""
                + id
                + "\","
                + "\"title\": \""
                + title
                + "\","
                + "\"location\": \""
                + location
                + "\","
                + "\"category\": \""
                + category
                + "\","
                + "\"salary\": \""
                + salary
                + "\","
                + "\"openingDate\": \""
                + openingDate
                + "\","
                + "\"closingDate\": \""
                + closingDate
                + "\","
                + "\"url\": \""
                + url
                + "\""
                + "}";
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("New Degree Apprenticeship: " + title);
        embed.setThumbnail(location);
        embed.addField("Category", category, false);
        embed.addField("Salary", salary, false);
        embed.addField("Opening Date", openingDate.toString(), false);
        embed.addField("Closing Date", closingDate.toString(), false);
        embed.addField("URL", url, false);
        return embed.build();
    }
}
