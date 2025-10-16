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
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.urls.APIUrls;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class WhatHappenedOnThisDayCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        // Defer reply immediately to prevent timeout
        event.deferReply().queue();

        val okHttpClient = new OkHttpClient();

        val currentMonth = LocalDate.now().getMonth().getValue();
        val currentDay = LocalDate.now().getDayOfMonth();

        val newUrl = APIUrls.TODAY_API.getUrl() + "/" + currentMonth + "/" + currentDay;

        val request = new okhttp3.Request.Builder().url(newUrl).build();

        try {
            val response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                event.getHook().sendMessage("Failed to get what happened on this day").queue();
                return;
            }

            val body = response.body();

            val json = new ObjectMapper().readTree(body.string());

            val events = json.get("data").get("Events");

            val embed =
                    replyUtils
                            .getDefaultEmbed()
                            .setTitle("What happened on this day " + currentMonth + "/" + currentDay);

            if (events != null) {
                AtomicReference<Integer> amountOfEvents = new AtomicReference<>(0);
                List<String> eventList = new ArrayList<>();

                events.forEach(
                        event1 -> {
                            amountOfEvents.updateAndGet(v -> v + 1);

                            if (amountOfEvents.get() > 10) {
                                return;
                            }

                            val text = formatText(event1.get("text").asText());

                            eventList.add(text);
                        });

                embed.setDescription(String.join("\n", eventList));
            }

            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } catch (IOException e) {
            event.getHook().sendMessage("Something went wrong while trying to call the api").queue();
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
