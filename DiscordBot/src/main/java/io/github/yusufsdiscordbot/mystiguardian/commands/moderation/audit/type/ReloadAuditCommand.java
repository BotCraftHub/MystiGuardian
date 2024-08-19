/*
 * Copyright 2024 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.type;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.*;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.Instant;
import lombok.val;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ReloadAuditCommand {
    public static void sendReloadAuditRecordsEmbed(InteractionBase event, int currentIndex) {
        val auditRecords = MystiGuardianDatabaseHandler.ReloadAudit.getReloadAuditRecords();
        val auditRecordsEmbed =
                new EmbedBuilder()
                        .setTitle("Reload Audit Logs")
                        .setDescription("Here are the bots reload audit logs.")
                        .setColor(MystiGuardianUtils.getBotColor())
                        .setTimestamp(Instant.now())
                        .setFooter(
                                MystiGuardianUtils.formatString(
                                        "Requested by %s", event.getUser().getDiscriminatedName()),
                                event.getUser().getAvatar());

        int startIndex = currentIndex * 10;
        int endIndex = Math.min(startIndex + 10, auditRecords.size());

        for (int i = startIndex; i < endIndex; i++) {
            val auditRecord = auditRecords.get(i);
            val auditRecordTime = formatOffsetDateTime(auditRecord.getTime());
            val userId = auditRecord.getUserId();
            val user = event.getApi().getUserById(userId).join();
            val reason = auditRecord.getReason();

            auditRecordsEmbed.addField(
                    "Reload Audit Log",
                    MystiGuardianUtils.formatString(
                            "User: %s\nReason: %s\nTime: %s", user.getMentionTag(), reason, auditRecordTime),
                    true);
        }

        if (auditRecords.isEmpty()) {
            event.createImmediateResponder().setContent("There are no reload audit logs.").respond();
            return;
        }

        // Create "Previous" and "Next" buttons for pagination
        ActionRow buttonRow = getPageActionRow(currentIndex, PageNames.RELOAD_AUDIT);

        // Send the embed with buttons
        event.createImmediateResponder().addEmbed(auditRecordsEmbed).addComponents(buttonRow).respond();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        if (!event.getUser().getIdAsString().equals(MystiGuardianUtils.getMainConfig().ownerId())) {
            event.createImmediateResponder().setContent("You are not the owner of the bot.").respond();
            return;
        }

        sendReloadAuditRecordsEmbed(event, 0);
    }
}
