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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ChangeLogCommand implements ISlashCommand {

    private static final String CHANGELOG_URL =
            "https://raw.githubusercontent.com/BotCraftHub/MystiGuardian/main/CHANGELOG.md";
    private static final Pattern CHANGELOG_PATTERN =
            Pattern.compile("## \\[(\\d+\\.\\d+\\.\\d+)] - (\\d{2}/\\d{2}/\\d{4})\\n([^#]+)");
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "## \\[(\\d+\\.\\d+\\.\\d+)] - (\\d{2}/\\d{2}/\\d{4})\\n((?:(?!## \\[\\d+\\.\\d+\\.\\d+]).)*)",
            Pattern.DOTALL);

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        try {
            String readmeContent = getReadmeContent(CHANGELOG_URL);

            // Extracting the specified version from the command
            String version = event.getOptionByName("version")
                    .flatMap(option -> option.getStringValue().map(String::toLowerCase))
                    .orElse("latest");

            // Adjust the regular expression to match any version
            Matcher versionMatcher = VERSION_PATTERN.matcher(readmeContent);

            // Find the requested version or the latest version
            while (versionMatcher.find()) {
                String foundVersion = versionMatcher.group(1);
                if (version.equals("latest") || version.equals(foundVersion)) {
                    String date = versionMatcher.group(2);
                    String changelogEntries = extractChangelogEntries(readmeContent, foundVersion);

                    replyUtils.sendEmbed(replyUtils
                            .getDefaultEmbed()
                            .setTitle("Changelog for version %s (%s)".formatted(foundVersion, date))
                            .setDescription("%s".formatted(changelogEntries)));
                    return;
                }
            }

            replyUtils.sendError("Changelog not found for version " + version);
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

    private static String extractChangelogEntries(String readmeContent, String version) {
        // Adjust the regular expression to match the version and everything until the next version
        Matcher versionMatcher = VERSION_PATTERN.matcher(readmeContent);

        if (versionMatcher.find()) {
            int versionStartIndex = versionMatcher.start();
            int versionEndIndex = readmeContent.indexOf("## [", versionStartIndex + 1);

            if (versionEndIndex == -1) {
                val r = readmeContent.substring(versionStartIndex).trim();

                return r.substring(r.indexOf("\n") + 1).trim();
            } else {
                val content = readmeContent
                        .substring(versionMatcher.end(), versionEndIndex)
                        .trim();

                return content.substring(content.indexOf("\n") + 1).trim();
            }
        } else {
            return "Changelog not found for version " + version;
        }
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

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(SlashCommandOption.createStringOption("version", "The version to get the changelog for", false));
    }
}
