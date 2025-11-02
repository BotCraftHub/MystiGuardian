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
package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class ViewApprenticeshipsCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        event.deferReply(true).queue(); // Ephemeral response

        try {
            // Generate a temporary access token
            String accessToken =
                    io.github.yusufsdiscordbot.mystiguardian.web.ApprenticeshipTokenManager
                            .generateAccessToken();

            // Get the web service base URL from config
            var webServiceConfig = MystiGuardianUtils.getMainConfig().webService();
            String baseUrl =
                    webServiceConfig != null ? webServiceConfig.baseUrl() : "http://localhost:25590";

            // Build the temporary URL
            String webUrl = baseUrl + "/apprenticeships?token=" + accessToken;

            val embed =
                    new EmbedBuilder()
                            .setColor(MystiGuardianUtils.getBotColor())
                            .setTitle("🎓 View Available Apprenticeships")
                            .setDescription(
                                    "Click the link below to view all available apprenticeships in an interactive web interface!")
                            .addField(
                                    "🔗 Access Link", "[Click here to view apprenticeships](" + webUrl + ")", false)
                            .addField("⏰ Link Expires", "This link will expire in 24 hours", false)
                            .addField(
                                    "✨ Features",
                                    "• Filter by category\n"
                                            + "• Sort by closing date\n"
                                            + "• Search by company or title\n"
                                            + "• View all details in one place",
                                    false)
                            .setFooter("This link is private and only visible to you")
                            .build();

            event
                    .getHook()
                    .sendMessageEmbeds(embed)
                    .queue(
                            success -> {},
                            error ->
                                    event
                                            .getHook()
                                            .sendMessage("❌ Failed to generate access link. Please try again.")
                                            .queue());

        } catch (Exception e) {
            event
                    .getHook()
                    .sendMessage("❌ An error occurred while generating the access link: " + e.getMessage())
                    .queue();
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "view-apprenticeships";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get a temporary link to view and filter available apprenticeships in a web interface";
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
