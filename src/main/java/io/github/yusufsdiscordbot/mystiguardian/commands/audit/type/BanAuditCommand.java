package io.github.yusufsdiscordbot.mystiguardian.commands.audit.type;

import io.github.yusufsdiscordbot.mystiguardian.commands.audit.AuditCommand;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.time.Instant;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.formatOffsetDateTime;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

public class BanAuditCommand {
    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val user = event.getOptionByName(AuditCommand.BAN_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("user")
                .orElseThrow()
                .getUserValue()
                .orElseThrow();

        int currentIndex = 0;

        sendBanAuditRecordsEmbed(event, currentIndex, user);
    }

    public static void sendBanAuditRecordsEmbed(InteractionBase event, int currentIndex, User user) {
        val server = event.getServer();

        if (server.isEmpty()) {
            event.createImmediateResponder()
                .setContent("This command can only be used in a server.")
                .respond();
            return;
        }

        val auditRecords = MystiGuardianDatabaseHandler.Ban.getBanRecords(server.get().getIdAsString(), user.getIdAsString());
        val auditRecordsEmbed = new EmbedBuilder()
                .setTitle("Ban Audit Logs")
                .setDescription("Here are the bots ban audit logs for " + user.getMentionTag() + ".")
                .setColor(MystiGuardianUtils.getBotColor())
                .setTimestamp(Instant.now())
                .setFooter("Requested by " + event.getUser().getDiscriminatedName(), event.getUser().getAvatar());

        int startIndex = currentIndex * 10;
        int endIndex = Math.min(startIndex + 10, auditRecords.size());

        for (int i = startIndex; i < endIndex; i++) {
            val auditRecord = auditRecords.get(i);
            val auditRecordTime = formatOffsetDateTime(auditRecord.getTime());
            val reason = auditRecord.getReason();

            auditRecordsEmbed.addField("Ban Audit Log", "User: " + user.getMentionTag() + "\nReason: " + reason + "\nTime: " + auditRecordTime, true);
        }

        if (auditRecords.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("There are no ban audit logs for " + user.getMentionTag() + ".")
                    .respond();
        }

        event.createImmediateResponder()
                .addEmbed(auditRecordsEmbed)
                .addComponents(getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.BAN_AUDIT, user.getIdAsString()))
                .respond();
    }
}
