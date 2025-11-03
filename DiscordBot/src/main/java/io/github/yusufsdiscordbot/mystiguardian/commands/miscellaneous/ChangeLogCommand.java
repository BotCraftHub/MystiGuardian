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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

@Slf4j
@SlashEventBus
public class ChangeLogCommand implements ISlashCommand {

    private static final String CHANGELOG_URL =
            "https://raw.githubusercontent.com/BotCraftHub/MystiGuardian/main/CHANGELOG.md";
    private static final String CHANGELOG_WEB_URL =
            "https://github.com/BotCraftHub/MystiGuardian/blob/main/CHANGELOG.md";
    private static final int MAX_EMBED_DESCRIPTION_LENGTH = 4096;
    private static final Pattern VERSION_PATTERN =
            Pattern.compile(
                    "## \\[(\\d+\\.\\d+\\.\\d+)] - (\\d{2}/\\d{2}/\\d{4})\\n((?:(?!## \\[\\d+\\.\\d+\\.\\d+]).)*)",
                    Pattern.DOTALL);

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        try {
            String readmeContent = getReadmeContent();

            // Extracting the specified version from the command
            String version =
                    event.getOption("version", "latest", OptionMapping::getAsString).toLowerCase();

            // Adjust the regular expression to match any version
            Matcher versionMatcher = VERSION_PATTERN.matcher(readmeContent);

            // Find the requested version or the latest version
            while (versionMatcher.find()) {
                String foundVersion = versionMatcher.group(1);
                if (version.equals("latest") || version.equals(foundVersion)) {
                    String date = versionMatcher.group(2);
                    String changelogEntries = extractChangelogEntries(readmeContent, foundVersion);
                    String description = truncateIfNeeded(changelogEntries, foundVersion);

                    replyUtils.sendEmbed(
                            replyUtils
                                    .getDefaultEmbed()
                                    .setTitle("Changelog for version %s (%s)".formatted(foundVersion, date))
                                    .setDescription(description));
                    return;
                }
            }

            replyUtils.sendError("Changelog not found for version " + version);
        } catch (IOException e) {
            logger.error("Failed to get changelog", e);
            replyUtils.sendError("Failed to get changelog");
        }
    }

    private static String getReadmeContent() throws IOException {
        try (BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(new URL(ChangeLogCommand.CHANGELOG_URL).openStream()))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            return content.toString();
        }
    }

    private static String extractChangelogEntries(String readmeContent, String version) {
        // Find the index of the latest version
        int latestVersionIndex = readmeContent.lastIndexOf("## [" + version + "]");

        if (latestVersionIndex != -1) {
            // Find the index of the next version after the latest version
            int nextVersionIndex = readmeContent.indexOf("## [", latestVersionIndex + 1);

            return formatForDiscord(getContent(readmeContent, nextVersionIndex, latestVersionIndex));
        } else {
            return "Changelog not found for version " + version;
        }
    }

    /**
     * Formats the changelog content to render nicely in Discord embeds. Converts Markdown headers to
     * Discord-friendly format.
     *
     * @param content The raw changelog content
     * @return Discord-formatted content
     */
    @NotNull
    private static String formatForDiscord(String content) {
        // Convert ### headers to **bold** with newlines for better Discord rendering
        // e.g., "### Added" becomes "\n**Added**"
        content = content.replaceAll("(?m)^### (.+)$", "\n**$1**");

        // Convert nested bullet points (2 spaces + -) to single level with emojis for visual hierarchy
        content = content.replaceAll("(?m)^  - (.+)$", "  ├─ $1");

        // Ensure proper spacing between sections
        content = content.replaceAll("\n\n\n+", "\n\n");

        return content.trim();
    }

    @NotNull
    private static String getContent(
            String readmeContent, int nextVersionIndex, int latestVersionIndex) {
        String content;
        if (nextVersionIndex == -1) {
            content = readmeContent.substring(latestVersionIndex).trim();
        } else {
            content = readmeContent.substring(latestVersionIndex, nextVersionIndex).trim();
        }

        // Remove lines starting with ## and any subsequent empty lines
        content =
                content
                        .replaceAll("^\\s*##.*?\\n\\s*(?:\\n\\s*)*", "")
                        .replaceAll("\\[.*?] - .*?\\n", "")
                        .trim();
        return content;
    }

    /**
     * Truncates the changelog content if it exceeds Discord's embed description limit.
     *
     * @param content The changelog content to check
     * @param version The version number for the link
     * @return The original content if under limit, or truncated content with a link to full changelog
     */
    @NotNull
    private static String truncateIfNeeded(String content, String version) {
        if (content.length() <= MAX_EMBED_DESCRIPTION_LENGTH) {
            return content;
        }

        // Calculate space needed for the truncation message
        String truncationMessage =
                "\n\n... *(Changelog truncated due to length)*\n"
                        + "[View full changelog on GitHub](%s)".formatted(CHANGELOG_WEB_URL);

        int maxContentLength = MAX_EMBED_DESCRIPTION_LENGTH - truncationMessage.length();

        // Find the last newline before the max length to avoid cutting in the middle of a line
        // If the last newline is too far back (less than half the max length), we'll truncate
        // at the max length to ensure users see a reasonable amount of content
        int truncateAt = content.lastIndexOf('\n', maxContentLength);
        int minAcceptableTruncationPoint = maxContentLength / 2;
        if (truncateAt == -1 || truncateAt < minAcceptableTruncationPoint) {
            // If no newline found or it's too far back, just truncate at max length
            truncateAt = maxContentLength;
        }

        return content.substring(0, truncateAt) + truncationMessage;
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
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(
                        OptionType.STRING, "version", "The version to get the changelog for", false));
    }
}
