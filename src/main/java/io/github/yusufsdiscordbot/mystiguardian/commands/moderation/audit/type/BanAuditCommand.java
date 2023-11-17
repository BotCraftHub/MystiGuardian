/*
 * Copyright 2023 RealYusufIsmail.
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

import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.moderationEmbedBuilder;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

import io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.AuditCommand;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.val;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jooq.Record5;

public class BanAuditCommand {
    public static void sendBanAuditRecordsEmbed(InteractionBase event, int currentIndex, User user) {
        val server = event.getServer();

        if (server.isEmpty()) {
            event.createImmediateResponder()
                    .setContent("This command can only be used in a server.")
                    .respond();
            return;
        }

        val auditRecords =
                MystiGuardianDatabaseHandler.Ban.getBanRecords(server.get().getIdAsString(), user.getIdAsString());
        List<Record5<String, String, String, Long, OffsetDateTime>> auditRecordsAsList =
                new java.util.ArrayList<>(auditRecords.size());
        auditRecordsAsList.addAll(auditRecords);
        val auditRecordsEmbed = moderationEmbedBuilder(
                MystiGuardianUtils.ModerationTypes.BAN, event, user, currentIndex, auditRecordsAsList);

        if (auditRecords.isEmpty()) {
            event.createImmediateResponder()
                    .setContent(MystiGuardianUtils.formatString(
                            "There are no ban audit logs for %s.", user.getMentionTag()))
                    .respond();
        }

        event.createImmediateResponder()
                .addEmbed(auditRecordsEmbed)
                .addComponents(
                        getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.BAN_AUDIT, user.getIdAsString()))
                .respond();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val user = event.getOptionByName(AuditCommand.BAN_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("user")
                .orElseThrow()
                .getUserValue()
                .orElseThrow();

        sendBanAuditRecordsEmbed(event, 0, user);
    }
}
