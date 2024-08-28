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

import static io.github.yusufsdiscordbot.mystiguardian.utils.EmbedHolder.*;
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
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jooq.Record5;

public class WarnAuditCommand {
    public static void sendWarnAuditRecordsEmbed(
            Interaction event, IReplyCallback replyCallback, int currentIndex, User user) {
        val server = event.getGuild();

        if (server == null) {
            replyCallback.reply("This command can only be used in a server.").queue();
            return;
        }

        val auditRecords =
                MystiGuardianDatabaseHandler.Warns.getWarnsRecords(server.getId(), user.getId());

        List<Record5<String, String, String, Long, OffsetDateTime>> auditRecordsAsList =
                new java.util.ArrayList<>(auditRecords.size());
        auditRecordsAsList.addAll(auditRecords);

        val auditRecordsEmbed =
                norm(
                        MystiGuardianUtils.ModerationTypes.WARN, event, user, currentIndex, auditRecordsAsList);

        if (auditRecords.isEmpty()) {
            replyCallback
                    .reply(
                            MystiGuardianUtils.formatString(
                                    "There are no warn audit logs for %s.", user.getAsTag()))
                    .queue();
        }

        ActionRow buttonRow =
                getPageActionRow(currentIndex, MystiGuardianUtils.PageNames.WARN_AUDIT, user.getId());

        replyCallback.replyEmbeds(auditRecordsEmbed.build()).addComponents(buttonRow).queue();
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteractionEvent event) {
        val user =
                Objects.requireNonNull(event.getOption("user", OptionMapping::getAsUser), "user is null");

        sendWarnAuditRecordsEmbed(event, event,0, user);
    }
}
