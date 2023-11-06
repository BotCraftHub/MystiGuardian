package io.github.yusufsdiscordbot.mystiguardian.commands.audit.type;

import io.github.yusufsdiscordbot.mystiguardian.commands.audit.AuditCommand;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jooq.Record5;

import java.time.OffsetDateTime;
import java.util.List;

import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.moderationEmbedBuilder;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

public class KickAuditCommand {
    public static void sendKickAuditRecordsEmbed(InteractionBase event, int currentIndex, User user) {
        val server = event.getServer();

        if (server.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("This command can only be used in a server.")
                    .respond();
            return;
        }

        val auditRecords = MystiGuardianDatabaseHandler.Kick.getKickRecords(server.get().getIdAsString(), user.getIdAsString());
        List<Record5<String, String, String, Long, OffsetDateTime>> auditRecordsAsList = new java.util.ArrayList<>(auditRecords.size());
        auditRecordsAsList.addAll(auditRecords);

        val auditRecordsEmbed = moderationEmbedBuilder(MystiGuardianUtils.ModerationTypes.KICK, event, user, currentIndex, auditRecordsAsList);

        if (auditRecords.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("There are no kick audit logs for " + user.getMentionTag() + ".")
                    .respond();
            return;
        }

        event.createImmediateResponder()
                .addEmbed(auditRecordsEmbed)
                .addComponents(getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.KICK_AUDIT, user.getIdAsString()))
                .respond();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val user = event.getOptionByName(AuditCommand.KICK_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("user")
                .orElseThrow()
                .getUserValue()
                .orElseThrow();

        int currentIndex = 0;

        sendKickAuditRecordsEmbed(event, currentIndex, user);
    }
}