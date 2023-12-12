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

import io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.AuditCommand;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;

public class AmountAuditCommand {
    public void onSlashCommandInteractionEvent(
            SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        val server = event.getServer();
        val user = event.getOptionByName(AuditCommand.AMOUNT_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("user")
                .orElseThrow()
                .getUserValue()
                .orElseThrow();

        val choice = event.getOptionByName(AuditCommand.AMOUNT_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("choice")
                .orElseThrow()
                .getStringValue()
                .orElseThrow();

        if (server.isEmpty()) {
            replyUtils.sendError("This command can only be used in a server.");
            return;
        }

        switch (choice) {
            case "warn":
                warnAmountAuditRecordsEmbed(event, user, replyUtils, permChecker);
                break;
            case "kick":
                kickAmountAuditRecordsEmbed(event, user, replyUtils, permChecker);
                break;
            case "ban":
                banAmountAuditRecordsEmbed(event, user, replyUtils, permChecker);
                break;
            case "time-out":
                timeOutAmountAuditRecordsEmbed(event, user, replyUtils, permChecker);
                break;
            default:
                replyUtils.sendError("Invalid choice");
                break;
        }
    }

    private void warnAmountAuditRecordsEmbed(
            SlashCommandInteraction event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val server = event.getServer().orElseThrow();
        val embed = new EmbedBuilder()
                .setTitle("Warn Amount Audit Records")
                .setDescription("This is the amount of warns a user has received in this server.")
                .setColor(Color.YELLOW)
                .setThumbnail(user.getAvatar())
                .setFooter(
                        "Requested by " + event.getUser().getDiscriminatedName(),
                        event.getUser().getAvatar());

        val warnAmountAuditRecords = MystiGuardianDatabaseHandler.AmountOfWarns.getAmountOfWarnsRecords(
                server.getIdAsString(), user.getIdAsString());
        if (warnAmountAuditRecords.isEmpty()) {
            embed.addField("Warn Amount Audit Records", "This user has never been warned in this server.", true);
            replyUtils.sendEmbed(embed);
            return;
        }

        val amountOfWarns = new AtomicInteger();
        warnAmountAuditRecords.forEach(
                warnAmountAuditRecord -> amountOfWarns.addAndGet(warnAmountAuditRecord.getAmountOfWarns()));

        embed.addField(
                "Warn Amount Audit Records",
                "This user has been warned " + amountOfWarns.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }

    private void kickAmountAuditRecordsEmbed(
            SlashCommandInteraction event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val server = event.getServer().orElseThrow();
        val embed = new EmbedBuilder()
                .setTitle("Kick Amount Audit Records")
                .setDescription("This is the amount of kicks a user has received in this server.")
                .setColor(Color.YELLOW)
                .setThumbnail(user.getAvatar())
                .setFooter(
                        "Requested by " + event.getUser().getDiscriminatedName(),
                        event.getUser().getAvatar());

        val kickAmountAuditRecords = MystiGuardianDatabaseHandler.AmountOfKicks.getAmountOfKicksRecords(
                server.getIdAsString(), user.getIdAsString());
        if (kickAmountAuditRecords.isEmpty()) {
            embed.addField("Kick Amount Audit Records", "This user has never been kicked in this server.", true);
            replyUtils.sendEmbed(embed);
            return;
        }

        val amountOfKicks = new AtomicInteger();
        kickAmountAuditRecords.forEach(
                kickAmountAuditRecord -> amountOfKicks.addAndGet(kickAmountAuditRecord.getAmountOfKicks()));

        embed.addField(
                "Kick Amount Audit Records",
                "This user has been kicked " + amountOfKicks.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }

    private void banAmountAuditRecordsEmbed(
            SlashCommandInteraction event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val server = event.getServer().orElseThrow();
        val embed = new EmbedBuilder()
                .setTitle("Ban Amount Audit Records")
                .setDescription("This is the amount of bans a user has received in this server.")
                .setColor(Color.YELLOW)
                .setThumbnail(user.getAvatar())
                .setFooter(
                        "Requested by " + event.getUser().getDiscriminatedName(),
                        event.getUser().getAvatar());

        val banAmountAuditRecords = MystiGuardianDatabaseHandler.AmountOfBans.getAmountOfBansRecords(
                server.getIdAsString(), user.getIdAsString());
        if (banAmountAuditRecords.isEmpty()) {
            embed.addField("Ban Amount Audit Records", "This user has never been banned in this server.", true);
            replyUtils.sendEmbed(embed);
            return;
        }

        val amountOfBans = new AtomicInteger();
        banAmountAuditRecords.forEach(
                banAmountAuditRecord -> amountOfBans.addAndGet(banAmountAuditRecord.getAmountOfBans()));

        embed.addField(
                "Ban Amount Audit Records",
                "This user has been banned " + amountOfBans.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }

    private void timeOutAmountAuditRecordsEmbed(
            SlashCommandInteraction event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val server = event.getServer().orElseThrow();
        val embed = new EmbedBuilder()
                .setTitle("Time Out Amount Audit Records")
                .setDescription("This is the amount of time outs a user has received in this server.")
                .setColor(Color.YELLOW)
                .setThumbnail(user.getAvatar())
                .setFooter(
                        "Requested by " + event.getUser().getDiscriminatedName(),
                        event.getUser().getAvatar());

        val timeOutAmountAuditRecords = MystiGuardianDatabaseHandler.AmountOfTimeOuts.getAmountOfTimeOutsRecords(
                server.getIdAsString(), user.getIdAsString());
        if (timeOutAmountAuditRecords.isEmpty()) {
            embed.addField(
                    "Time Out Amount Audit Records", "This user has never been time outed in this server.", true);
            replyUtils.sendEmbed(embed);
            return;
        }

        val amountOfTimeOuts = new AtomicInteger();
        timeOutAmountAuditRecords.forEach(
                timeOutAmountAuditRecord -> amountOfTimeOuts.addAndGet(timeOutAmountAuditRecord.getAmountOfTimeOuts()));

        embed.addField(
                "Time Out Amount Audit Records",
                "This user has been time outed " + amountOfTimeOuts.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }
}
