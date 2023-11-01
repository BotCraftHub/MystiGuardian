package io.github.yusufsdiscordbot.mystiguardian.admin.audit;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.time.Instant;


import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.*;

public class ReloadAuditCommand {
    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val ownerId = jConfig.get("owner-id");

        if (ownerId == null) {
            event.createImmediateResponder()
                .setContent("Owner ID is not set in the config file.")
                .respond();
            return;
        }

        if (!event.getUser().getIdAsString().equals(ownerId.asText())) {
            event.createImmediateResponder()
                .setContent("You are not the owner of the bot.")
                .respond();
            return;
        }


        // Assume currentIndex is a variable that keeps track of the current page's index
        int currentIndex = 0;

        sendAuditRecordsEmbed(event, currentIndex);
    }

    public static void sendAuditRecordsEmbed(InteractionBase event, int currentIndex) {
        val auditRecords = MystiGuardianDatabaseHandler.ReloadAudit.getReloadAuditRecords();
        val auditRecordsEmbed = new EmbedBuilder()
                .setTitle("Reload Audit Logs")
                .setDescription("Here are the bot's reload audit logs.")
                .setColor(MystiGuardianUtils.getBotColor())
                .setTimestamp(Instant.now())
                .setFooter("Requested by " + event.getUser().getDiscriminatedName(), event.getUser().getAvatar());

        int startIndex = currentIndex * 10;
        int endIndex = Math.min(startIndex + 10, auditRecords.size());

        for (int i = startIndex; i < endIndex; i++) {
            val auditRecord = auditRecords.get(i);
            val auditRecordTime = formatOffsetDateTime(auditRecord.getTime());
            val userId = auditRecord.getUserId();
            val user = event.getApi().getUserById(userId).join();
            val reason = auditRecord.getReason();

            auditRecordsEmbed.addField("Reload Audit Log", "User: " + user.getMentionTag() + "\nReason: " + reason + "\nTime: " + auditRecordTime, true);
        }

        if (auditRecords.isEmpty()) {
            auditRecordsEmbed.addField("No reload audit logs", "There are no reload audit logs.", true);
        }

        // Create "Previous" and "Next" buttons for pagination
        ActionRow buttonRow = getPageActionRow(currentIndex, PageNames.RELOAD_AUDIT);

        // Send the embed with buttons
        event.createImmediateResponder()
                .addEmbed(auditRecordsEmbed)
                .addComponents(buttonRow)
                .respond();
    }
}
