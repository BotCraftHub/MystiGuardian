package io.github.yusufsdiscordbot.mystiguardian.commands.audit.type;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystigurdian.db.tables.records.WarnsRecord;
import lombok.val;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.jooq.Result;

import java.time.Instant;

import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.AuditCommand.WARN_BY_ID_AUDIT_OPTION_NAME;

public class WarnByIdAuditCommand {
    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val id = event.getOptionByName(WARN_BY_ID_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("warn-id")
                .orElseThrow()
                .getStringValue()
                .orElseThrow();

        //check if it is a valid long and then cast it to a long

        if (!MystiGuardianUtils.isLong(id)) {
            event.createImmediateResponder()
                    .setContent("The id you provided is not a valid id.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        val idAsLong = Long.parseLong(id);

        val server = event.getServer()
                .orElseThrow();

        val auditRecords = MystiGuardianDatabaseHandler.Warns.getWarnRecordById(server.getIdAsString(), idAsLong);

        if (auditRecords == null) {
            event.createImmediateResponder()
                    .setContent("There are no warn audit logs for that id.")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        sendSingleWarnAuditRecordsEmbed(event, auditRecords);
    }

    private void sendSingleWarnAuditRecordsEmbed(@NotNull InteractionBase event,
                                                 @NotNull WarnsRecord auditRecords) {

        val auditRecordsEmbed = new EmbedBuilder();
        auditRecordsEmbed.setTitle(STR."Warn Audit Log for user \{event.getApi().getUserById(auditRecords.getUserId()).join().getDiscriminatedName()}");
        auditRecordsEmbed.setDescription(STR."Here are the bots warn audit log for warn id \{auditRecords.getUserId()}.");
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
