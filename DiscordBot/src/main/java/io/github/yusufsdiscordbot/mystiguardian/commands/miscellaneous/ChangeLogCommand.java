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

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ChangeLogCommand implements ISlashCommand {

    private static final String CHANGELOG_URL =
            "https://raw.githubusercontent.com/BotCraftHub/MystiGuardian/main/CHANGELOG.md";
    private static final Pattern CHANGELOG_PATTERN = Pattern.compile("## \\[(.+?)] - (.+?)\\n");

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        try {
            String readmeContent = getReadmeContent(CHANGELOG_URL);

            Matcher matcher = CHANGELOG_PATTERN.matcher(readmeContent);

            if (!matcher.find()) {
                replyUtils.sendError("Failed to parse changelog version and date");
                return;
            }

            String version = matcher.group(1);
            String date = matcher.group(2);

            // Extracting changelog entries
            String changelog = extractChangelogEntries(readmeContent);

            replyUtils.sendEmbed(replyUtils
                    .getDefaultEmbed()
                    .setTitle("Changelog")
                    .setDescription("Version: %s\nDate: %s\nChangelog:\n%s".formatted(version, date, changelog)));
        } catch (IOException e) {
            MystiGuardianUtils.logger.error("Failed to get changelog", e);
            replyUtils.sendError("Failed to get changelog");
        }
    }

    private static String getReadmeContent(String readmeUrl) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(readmeUrl).openStream()))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            return content.toString();
        }
    }

    private static String extractChangelogEntries(String readmeContent) {
        // Extracting changelog entries
        StringBuilder changes = new StringBuilder(readmeContent.split("### .")[1]);

        for (String line : changes.toString().split("\n")) {
            if (line.startsWith("###")) {
                break;
            }

            changes.append(line).append("\n");
        }

        return changes.toString();
    }

    @NotNull
    @Override
    public String getName() {
        return "changelog";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get the latest changes to the bot";
    }
}
