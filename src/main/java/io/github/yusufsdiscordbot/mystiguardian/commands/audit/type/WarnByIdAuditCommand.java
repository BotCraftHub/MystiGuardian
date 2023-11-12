package io.github.yusufsdiscordbot.mystiguardian.commands.audit.type;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.WarnsRecord;
import lombok.val;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.AuditCommand.WARN_BY_ID_AUDIT_OPTION_NAME;

public class WarnByIdAuditCommand {
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        val id = event.getOptionByName(WARN_BY_ID_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("warn-id")
                .orElseThrow()
                .getStringValue()
                .orElseThrow();

        //check if it is a valid long and then cast it to a long

        if (!MystiGuardianUtils.isLong(id)) {
            replyUtils.sendError("Please provide a valid warn id");
            return;
        }

        val idAsLong = Long.parseLong(id);

        val server = event.getServer()
                .orElseThrow();

        val auditRecords = MystiGuardianDatabaseHandler.Warns.getWarnRecordById(server.getIdAsString(), idAsLong);

        if (auditRecords == null) {
            replyUtils.sendError("No audit records found for that warn id");
            return;
        }

        sendSingleWarnAuditRecordsEmbed(event, auditRecords);
    }

    private void sendSingleWarnAuditRecordsEmbed(@NotNull InteractionBase event,
                                                 @NotNull WarnsRecord auditRecords) {

        val auditRecordsEmbed = new EmbedBuilder();
        auditRecordsEmbed.setTitle(STR."Warn Audit Log for user \{event.getApi().getUserById(auditRecords.getUserId()).join().getDiscriminatedName()}");
        auditRecordsEmbed.setDescription(STR."Here are the bots warn audit log for warn id \{auditRecords.getId()}");
        auditRecordsEmbed.setColor(MystiGuardianUtils.getBotColor());
        auditRecordsEmbed.setTimestamp(Instant.now());
        auditRecordsEmbed.setFooter(STR."Requested by \{event.getUser().getDiscriminatedName()}", event.getUser().getAvatar());

        val auditRecordTime = MystiGuardianUtils.formatOffsetDateTime(auditRecords.getTime());
        val reason = auditRecords.getReason();

        auditRecordsEmbed.addField("Info", STR."User: \{auditRecords.getUserId()}\nReason: \{reason}\nWhen: \{auditRecordTime}", true);

        event.createImmediateResponder()
                .addEmbed(auditRecordsEmbed)
                .respond();
    }
}
