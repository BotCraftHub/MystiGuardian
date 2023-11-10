package io.github.yusufsdiscordbot.mystiguardian.commands.audit.type;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jooq.Record6;

import java.time.OffsetDateTime;
import java.util.List;

import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.AuditCommand.TIME_OUT_AUDIT_OPTION_NAME;
import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.moderationEmbedBuilder;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.formatOffsetDateTime;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

public class TimeOutAuditCommand {
    public static void sendTimeOutAuditRecordsEmbed(InteractionBase event, int currentIndex, User user) {
        val server = event.getServer();

        if (server.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("This command can only be used in a server.")
                    .respond();
            return;
        }

        val auditRecords = MystiGuardianDatabaseHandler.TimeOut.getTimeOutRecords(server.get().getIdAsString(), user.getIdAsString());
        List<Record6<OffsetDateTime, String, String, String, Long, OffsetDateTime>> auditRecordsAsList = new java.util.ArrayList<>(auditRecords.size());
        auditRecordsAsList.addAll(auditRecords);
        val auditRecordsEmbed = moderationEmbedBuilder(MystiGuardianUtils.ModerationTypes.TIME_OUT, event, user, currentIndex, null, auditRecordsAsList);

        int startIndex = currentIndex * 10;
        int endIndex = Math.min(startIndex + 10, auditRecords.size());

        for (int i = startIndex; i < endIndex; i++) {
            val auditRecord = auditRecords.get(i);
            val auditRecordTime = formatOffsetDateTime(auditRecord.getTime());
            val reason = auditRecord.getReason();

            auditRecordsEmbed.addField("Time Out Audit Log", STR. "User: \{ user.getMentionTag() }\nReason: \{ reason }\nTime: \{ auditRecordTime }" , true);
        }

        if (auditRecords.isEmpty()) {
            event.createImmediateResponder()
                    .setContent(STR. "There are no time out audit logs for \{ user.getMentionTag() }." )
                    .respond();
            return;
        }

        event.createImmediateResponder()
                .addEmbed(auditRecordsEmbed)
                .addComponents(getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.TIME_OUT_AUDIT, user.getIdAsString()))
                .respond();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val user = event.getOptionByName(TIME_OUT_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("user")
                .orElseThrow()
                .getUserValue()
                .orElseThrow();

        sendTimeOutAuditRecordsEmbed(event, 0, user);
    }
}
