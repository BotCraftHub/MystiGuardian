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

import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.softBan;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.getPageActionRow;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.val;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jooq.Record6;

public class SoftBanAuditCommand {
    public static void sendSoftBanAuditRecordsEmbed(
            CommandInteraction interaction, IReplyCallback replyCallback, int currentIndex, User user) {
        val server = interaction.getGuild();

        if (server == null) {
            replyCallback.reply("This command can only be used in a server.").queue();
            return;
        }

        val softBanRecords =
                MystiGuardianDatabaseHandler.SoftBan.getSoftBanRecords(server.getId(), user.getId());

        List<Record6<String, String, String, Integer, Long, OffsetDateTime>> softBanRecordList =
                new java.util.ArrayList<>();

        for (var record : softBanRecords) {
            // Reorder: (id, guild_id, user_id, reason, days, time) -> (guild_id, user_id, reason, days,
            // id, time)
            softBanRecordList.add(
                    record.into(
                            record.field2(),
                            record.field3(),
                            record.field4(),
                            record.field5(),
                            record.field1(),
                            record.field6()));
        }

        val auditRecordsEmbed =
                softBan(
                        MystiGuardianUtils.ModerationTypes.SOFT_BAN,
                        interaction,
                        user,
                        currentIndex,
                        softBanRecordList);

        if (softBanRecordList.isEmpty()) {
            replyCallback
                    .reply(
                            MystiGuardianUtils.formatString(
                                    "There are no ban audit logs for %s.", user.getAsTag()))
                    .setEphemeral(true)
                    .queue();
            return;
        }

        replyCallback
                .replyEmbeds(auditRecordsEmbed.build())
                .addComponents(
                        getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.BAN_AUDIT, user.getId()))
                .queue();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteractionEvent event) {
        val user =
                Objects.requireNonNull(event.getOption("user", OptionMapping::getAsUser), "user is null");

        sendSoftBanAuditRecordsEmbed(event, event, 0, user);
    }
}
