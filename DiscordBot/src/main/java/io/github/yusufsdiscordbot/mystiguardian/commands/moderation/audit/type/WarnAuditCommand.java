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

import static io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.AuditCommand.WARN_AUDIT_OPTION_NAME;
import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.*;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.val;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jooq.Record5;

public class WarnAuditCommand {
    public static void sendWarnAuditRecordsEmbed(InteractionBase event, int currentIndex, User user) {
        val server = event.getServer();

        if (server.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("This command can only be used in a server.")
                    .respond();
            return;
        }

        val auditRecords =
                MystiGuardianDatabaseHandler.Warns.getWarnsRecords(server.get().getIdAsString(), user.getIdAsString());
        List<Record5<String, String, String, Long, OffsetDateTime>> auditRecordsAsList =
                new java.util.ArrayList<>(auditRecords.size());
        auditRecordsAsList.addAll(auditRecords);

        val auditRecordsEmbed =
                norm(MystiGuardianUtils.ModerationTypes.WARN, event, user, currentIndex, auditRecordsAsList);

        if (auditRecords.isEmpty()) {
            event.createImmediateResponder()
                    .setContent(MystiGuardianUtils.formatString(
                            "There are no warn audit logs for %s.", user.getMentionTag()))
                    .respond();
        }

        ActionRow buttonRow =
                getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.WARN_AUDIT, user.getIdAsString());

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

        sendWarnAuditRecordsEmbed(event, 0, user);
    }
}
