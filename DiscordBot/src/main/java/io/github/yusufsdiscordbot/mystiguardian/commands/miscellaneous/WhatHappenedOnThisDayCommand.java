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
package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.val;
import okhttp3.OkHttpClient;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class WhatHappenedOnThisDayCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        val okHttpClient = new OkHttpClient();
        val url = "https://today.zenquotes.io/api";

        val currentMonth = LocalDate.now().getMonth().getValue();
        val currentDay = LocalDate.now().getDayOfMonth();

        val newUrl = url + "/" + currentMonth + "/" + currentDay;

        System.out.println(newUrl);
        val request = new okhttp3.Request.Builder().url(newUrl).build();

        try {
            val response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                replyUtils.sendError("Failed to get what happened on this day");
                return;
            }

            val body = response.body();

            val json = new ObjectMapper().readTree(body.string());

            val events = json.get("data").get("Events");

            val embed = replyUtils
                    .getDefaultEmbed()
                    .setTitle("What happened on this day " + currentMonth + "/" + currentDay);

            if (events != null) {
                AtomicReference<Integer> amountOfEvents = new AtomicReference<>(0);
                List<String> eventList = new ArrayList<>();

                events.forEach(event1 -> {
                    amountOfEvents.updateAndGet(v -> v + 1);

                    if (amountOfEvents.get() > 10) {
                        return;
                    }

                    val text = formatText(event1.get("text").asText());

                    eventList.add(text);
                });

                embed.setDescription(String.join("\n", eventList));
            }

            replyUtils.sendEmbed(embed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatText(String text) {
        return text.replaceAll("&#8211;", "-").replaceAll("&#160;", " ");
    }

    @NotNull
    @Override
    public String getName() {
        return "what-happened-on-this-day";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get what happened on this day";
    }
}
