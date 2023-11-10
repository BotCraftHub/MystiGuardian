package io.github.yusufsdiscordbot.mystiguardian.utils;

import lombok.val;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record5;
import org.jooq.Record6;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.formatOffsetDateTime;

public class EmbedHolder {


    public static EmbedBuilder moderationEmbedBuilder(MystiGuardianUtils.ModerationTypes moderationType, InteractionBase event, User user, int currentIndex,
                                                      @Nullable List<Record5<String, String, String, Long, OffsetDateTime>> normalModerationLogs,
                                                      @Nullable List<Record6<OffsetDateTime, String, String, String, Long, OffsetDateTime>> timeOutModerationLogs) {
        val moderationNameWithCapitalFirstLetter = moderationType.name().substring(0, 1).toUpperCase() + moderationType.name().substring(1).toLowerCase();

        val embed = new EmbedBuilder()
                .setTitle(STR. "\{ moderationNameWithCapitalFirstLetter } Audit Logs" )
                .setDescription(STR. "Here are the bots \{ moderationType.name() } audit logs for \{ user.getMentionTag() }." )
                .setColor(MystiGuardianUtils.getBotColor())
                .setTimestamp(Instant.now())
                .setFooter(STR. "Requested by \{ event.getUser().getDiscriminatedName() }" , event.getUser().getAvatar());


        int startIndex = currentIndex * 10;
        if (normalModerationLogs != null) {
            int endIndex = Math.min(startIndex + 10, normalModerationLogs.size());

            for (int i = startIndex; i < endIndex; i++) {
                val moderationLog = normalModerationLogs.get(i);
                buildModerationLogsFields(embed, user, moderationLog.value1(), moderationLog.value5(), moderationLog.value4(), null);
            }
        } else if (timeOutModerationLogs != null) {
            int endIndex = Math.min(startIndex + 10, timeOutModerationLogs.size());

            for (int i = startIndex; i < endIndex; i++) {
                val moderationLog = timeOutModerationLogs.get(i);
                buildModerationLogsFields(embed, user, moderationLog.value2(), moderationLog.value6(), moderationLog.value5(), moderationLog.value1());
            }
        }

        return embed;
    }

    public static EmbedBuilder moderationEmbedBuilder(MystiGuardianUtils.ModerationTypes moderationType, InteractionBase event, User user, int currentIndex,
                                                      @Nullable List<Record5<String, String, String, Long, OffsetDateTime>> normalModerationLogs) {
        return moderationEmbedBuilder(moderationType, event, user, currentIndex, normalModerationLogs, null);
    }

    private static void buildModerationLogsFields(EmbedBuilder embed, User user, String reason, OffsetDateTime time, long id, @Nullable OffsetDateTime duration) {
        val stringBuilder = new StringBuilder();
        stringBuilder.append("User: ").append(user.getMentionTag()).append("\nReason: ").append(reason).append("\nHappened at: ").append(formatOffsetDateTime(time)).append("\nID: ").append(id);
        if (duration != null) {
            stringBuilder.append("\nUntil: ").append(formatOffsetDateTime(duration));
        }

        embed.addField("Moderation Log", stringBuilder.toString(), true);
    }
}
