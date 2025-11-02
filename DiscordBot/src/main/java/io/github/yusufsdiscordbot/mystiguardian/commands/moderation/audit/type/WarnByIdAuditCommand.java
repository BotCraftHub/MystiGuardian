/*
 * Copyright 2025 RealYusufIsmail.
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

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.db.tables.records.WarnsRecord;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.time.Instant;
import java.util.Objects;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class WarnByIdAuditCommand {
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val id =
                Objects.requireNonNull(
                        event.getOption("warn-id", OptionMapping::getAsString), "warn id is null");

        // check if it is a valid long and then cast it to a long

        if (!MystiGuardianUtils.isLong(id)) {
            replyUtils.sendError("Please provide a valid warn id");
            return;
        }

        val idAsLong = Long.parseLong(id);

        val server = event.getGuild();

        val auditRecords =
                MystiGuardianDatabaseHandler.Warns.getWarnRecordById(server.getId(), idAsLong);

        if (auditRecords == null) {
            replyUtils.sendError("No audit records found for that warn id");
            return;
        }

        sendSingleWarnAuditRecordsEmbed(event, auditRecords);
    }

    private void sendSingleWarnAuditRecordsEmbed(
            @NotNull CommandInteraction event, @NotNull WarnsRecord auditRecords) {

        val auditRecordsEmbed = new EmbedBuilder();
        auditRecordsEmbed.setTitle(
                MystiGuardianUtils.formatString(
                        "Warn Audit Log for user %s",
                        event.getJDA().getUserById(auditRecords.getUserId()).getAsTag()));
        auditRecordsEmbed.setDescription(
                MystiGuardianUtils.formatString(
                        "Here are the bots warn audit log for warn id %d", auditRecords.getId()));
        auditRecordsEmbed.setColor(MystiGuardianUtils.getBotColor());
        auditRecordsEmbed.setTimestamp(Instant.now());
        auditRecordsEmbed.setFooter(
                MystiGuardianUtils.formatString("Requested by %s", event.getUser().getAsTag()),
                event.getUser().getAvatar().getUrl());

        val auditRecordTime = MystiGuardianUtils.formatOffsetDateTime(auditRecords.getTime());
        val reason = auditRecords.getReason();

        auditRecordsEmbed.addField(
                "Info",
                MystiGuardianUtils.formatString(
                        "User: %s\nReason: %s\nWhen: %s", auditRecords.getUserId(), reason, auditRecordTime),
                true);

        event.replyEmbeds(auditRecordsEmbed.build()).queue();
    }
}
