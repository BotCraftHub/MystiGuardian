/*
 * Copyright 2023 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.utils;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.formatOffsetDateTime;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.formatString;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.val;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record5;
import org.jooq.Record6;

public class EmbedHolder {

    private static EmbedBuilder moderationEmbedBuilder(
            MystiGuardianUtils.ModerationTypes moderationType,
            InteractionBase event,
            User user,
            int currentIndex,
            @Nullable List<Record5<String, String, String, Long, OffsetDateTime>> normalModerationLogs,
            @Nullable List<Record6<OffsetDateTime, String, String, String, Long, OffsetDateTime>> timeOutModerationLogs,
            @Nullable List<Record6<String, String, String, Integer, Long, OffsetDateTime>> softBanModerationLogs) {
        val moderationNameWithCapitalFirstLetter =
                moderationType.name().substring(0, 1).toUpperCase()
                        + moderationType.name().substring(1).toLowerCase();

        val embed = new EmbedBuilder()
                .setTitle(formatString("%s Audit Logs", moderationNameWithCapitalFirstLetter))
                .setDescription(formatString(
                        "Here are the bots %s audit logs for %s.", moderationType.name(), user.getMentionTag()))
                .setColor(MystiGuardianUtils.getBotColor())
                .setTimestamp(Instant.now())
                .setFooter(
                        formatString("Requested by %s", event.getUser().getDiscriminatedName()),
                        event.getUser().getAvatar());

        int startIndex = currentIndex * 10;
        if (normalModerationLogs != null) {
            int endIndex = Math.min(startIndex + 10, normalModerationLogs.size());

            for (int i = startIndex; i < endIndex; i++) {
                val moderationLog = normalModerationLogs.get(i);
                buildModerationLogsFields(
                        embed,
                        user,
                        moderationLog.value1(),
                        moderationLog.value5(),
                        moderationLog.value4(),
                        null,
                        null);
            }
        } else if (timeOutModerationLogs != null) {
            int endIndex = Math.min(startIndex + 10, timeOutModerationLogs.size());

            for (int i = startIndex; i < endIndex; i++) {
                val moderationLog = timeOutModerationLogs.get(i);
                buildModerationLogsFields(
                        embed,
                        user,
                        moderationLog.value2(),
                        moderationLog.value6(),
                        moderationLog.value5(),
                        moderationLog.value1(),
                        null);
            }
        } else if (softBanModerationLogs != null) {
            int endIndex = Math.min(startIndex + 10, softBanModerationLogs.size());

            for (int i = startIndex; i < endIndex; i++) {
                val moderationLog = softBanModerationLogs.get(i);
                buildModerationLogsFields(
                        embed,
                        user,
                        moderationLog.value2(),
                        moderationLog.value6(),
                        moderationLog.value5(),
                        null,
                        moderationLog.value4());
            }
        }

        return embed;
    }

    public static EmbedBuilder norm(
            MystiGuardianUtils.ModerationTypes moderationType,
            InteractionBase event,
            User user,
            int currentIndex,
            @Nullable List<Record5<String, String, String, Long, OffsetDateTime>> normalModerationLogs) {
        return moderationEmbedBuilder(moderationType, event, user, currentIndex, normalModerationLogs, null, null);
    }

    public static EmbedBuilder timeOut(
            MystiGuardianUtils.ModerationTypes moderationType,
            InteractionBase event,
            User user,
            int currentIndex,
            @Nullable
                    List<Record6<OffsetDateTime, String, String, String, Long, OffsetDateTime>> timeOutModerationLogs) {
        return moderationEmbedBuilder(moderationType, event, user, currentIndex, null, timeOutModerationLogs, null);
    }

    public static EmbedBuilder softBan(
            MystiGuardianUtils.ModerationTypes moderationType,
            InteractionBase event,
            User user,
            int currentIndex,
            @Nullable List<Record6<String, String, String, Integer, Long, OffsetDateTime>> softBanModerationLogs) {
        return moderationEmbedBuilder(moderationType, event, user, currentIndex, null, null, softBanModerationLogs);
    }

    private static void buildModerationLogsFields(
            EmbedBuilder embed,
            User user,
            String reason,
            OffsetDateTime time,
            long id,
            @Nullable OffsetDateTime duration,
            @Nullable Integer amountOfDays) {
        val stringBuilder = new StringBuilder();
        stringBuilder
                .append("User: ")
                .append(user.getMentionTag())
                .append("\nReason: ")
                .append(reason)
                .append("\nHappened at: ")
                .append(formatOffsetDateTime(time))
                .append("\nID: ")
                .append(id);

        if (duration != null) {
            stringBuilder.append("\nUntil: ").append(formatOffsetDateTime(duration));
        }

        if (amountOfDays != null) {
            stringBuilder.append("\nAmount of days: ").append(amountOfDays);

            // take the time and add the amount of days to it and get the time and see how long left. If no then say
            // it's over
            val timeNow = OffsetDateTime.now();
            val timeNowPlusDays = time.plusDays(amountOfDays);
            val timeLeft = timeNowPlusDays.compareTo(timeNow);

            if (timeLeft > 0) {
                stringBuilder.append("\nTime left: ").append(timeLeft);
            } else {
                stringBuilder.append("\nTime left: ").append("Over");
            }
        }

        embed.addField("Moderation Log", stringBuilder.toString(), true);
    }
}
