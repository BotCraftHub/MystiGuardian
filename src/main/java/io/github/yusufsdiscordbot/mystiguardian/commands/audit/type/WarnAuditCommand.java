package io.github.yusufsdiscordbot.mystiguardian.commands.audit.type;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jooq.Record5;

import java.time.OffsetDateTime;
import java.util.List;

import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.AuditCommand.WARN_AUDIT_OPTION_NAME;
import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.moderationEmbedBuilder;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

public class WarnAuditCommand {
    public static void sendWarnAuditRecordsEmbed(InteractionBase event, int currentIndex, User user) {
        val server = event.getServer();

        if (server.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("This command can only be used in a server.")
                    .respond();
            return;
        }

        val auditRecords = MystiGuardianDatabaseHandler.Warns.getWarnsRecords(server.get().getIdAsString(), user.getIdAsString());
        List<Record5<String, String, String, Long, OffsetDateTime>> auditRecordsAsList = new java.util.ArrayList<>(auditRecords.size());
        auditRecordsAsList.addAll(auditRecords);

        val auditRecordsEmbed = moderationEmbedBuilder(MystiGuardianUtils.ModerationTypes.WARN, event, user, currentIndex, auditRecordsAsList);

        if (auditRecords.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("There are no warn audit logs for " + user.getMentionTag() + ".")
                    .respond();
        }

        ActionRow buttonRow = getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.WARN_AUDIT, user.getIdAsString());

        event.createImmediateResponder()
                .addEmbed(auditRecordsEmbed)
                .addComponents(buttonRow)
                .respond();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val user = event.getOptionByName(WARN_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("user")
                .orElseThrow()
                .getUserValue()
                .orElseThrow();

        int currentIndex = 0;

        sendWarnAuditRecordsEmbed(event, currentIndex, user);
    }
}
