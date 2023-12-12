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
import lombok.val;
import okhttp3.OkHttpClient;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class QuoteOfTheDayCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        val okHttpClient = new OkHttpClient();
        val url = "https://zenquotes.io/api/today";
        val request = new okhttp3.Request.Builder().url(url).build();

        try {
            val response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                replyUtils.sendError("Failed to get quote of the day");
                return;
            }

            val body = response.body();
            val json = new ObjectMapper().readTree(body.string());

            val quote = json.get(0).get("q").asText();
            val author = json.get(0).get("a").asText();

            val embed = replyUtils
                    .getDefaultEmbed()
                    .setTitle("Quote of the day")
                    .setDescription(quote)
                    .setFooter("Author: " + author);

            replyUtils.sendEmbed(embed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "quoteoftheday";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get a quote of the day";
    }
}
