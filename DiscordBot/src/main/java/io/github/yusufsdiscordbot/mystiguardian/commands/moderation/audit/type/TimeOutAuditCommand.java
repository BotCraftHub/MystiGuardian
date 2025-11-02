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

import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.timeOut;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.formatOffsetDateTime;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.val;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jooq.Record6;

public class TimeOutAuditCommand {
    public static void sendTimeOutAuditRecordsEmbed(
            Interaction interaction, IReplyCallback replyCallback, int currentIndex, User user) {
        val server = interaction.getGuild();

        if (server == null) {
            replyCallback.reply("This command can only be used in a server.").queue();
            return;
        }

        val auditRecords =
                MystiGuardianDatabaseHandler.TimeOut.getTimeOutRecords(server.getId(), user.getId());

        List<Record6<OffsetDateTime, String, String, String, Long, OffsetDateTime>> auditRecordsAsList =
                new java.util.ArrayList<>();

        for (var record : auditRecords) {
            // Reorder: (id, guild_id, user_id, reason, duration, time) -> (duration, reason, guild_id,
            // user_id, id, time)
            auditRecordsAsList.add(
                    record.into(
                            record.field5(),
                            record.field4(),
                            record.field2(),
                            record.field3(),
                            record.field1(),
                            record.field6()));
        }
        val auditRecordsEmbed =
                timeOut(
                        MystiGuardianUtils.ModerationTypes.TIME_OUT,
                        interaction,
                        user,
                        currentIndex,
                        auditRecordsAsList);

        int startIndex = currentIndex * 10;
        int endIndex = Math.min(startIndex + 10, auditRecords.size());

        for (int i = startIndex; i < endIndex; i++) {
            val auditRecord = auditRecords.get(i);
            val auditRecordTime = formatOffsetDateTime(auditRecord.getTime());
            val reason = auditRecord.getReason();

            auditRecordsEmbed.addField(
                    "Time Out Audit Log",
                    MystiGuardianUtils.formatString(
                            "User: %s\nReason: %s\nTime: %s", user.getAsTag(), reason, auditRecordTime),
                    true);
        }

        if (auditRecords.isEmpty()) {
            replyCallback
                    .reply(
                            MystiGuardianUtils.formatString(
                                    "There are no time out audit logs for %s.", user.getAsTag()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        replyCallback
                .replyEmbeds(auditRecordsEmbed.build())
                .addComponents(
                        getPageActionRow(
                                currentIndex, MystiGuardianUtils.PageNames.TIME_OUT_AUDIT, user.getId()))
                .queue();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteractionEvent event) {
        val user =
                Objects.requireNonNull(event.getOption("user", OptionMapping::getAsUser), "user is null");

        sendTimeOutAuditRecordsEmbed(event, event, 0, user);
    }
}
