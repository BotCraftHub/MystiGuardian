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

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class AmountAuditCommand {

    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        Guild guild = event.getGuild();
        if (guild == null) {
            replyUtils.sendError("This command can only be used in a server.");
            return;
        }

        OptionMapping userOption = event.getOption("user");
        OptionMapping choiceOption = event.getOption("choice");

        if (userOption == null || choiceOption == null) {
            replyUtils.sendError("You must specify both user and choice options.");
            return;
        }

        User user = userOption.getAsUser();
        String choice = choiceOption.getAsString();

        // Defer the interaction to allow for processing
        event.deferReply().queue();

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
            SlashCommandInteractionEvent event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        Guild guild = event.getGuild();
        if (guild == null) return;

        EmbedBuilder embed =
                new EmbedBuilder()
                        .setTitle("Warn Amount Audit Records")
                        .setDescription("This is the amount of warns a user has received in this server.")
                        .setColor(Color.YELLOW)
                        .setThumbnail(user.getAvatarUrl())
                        .setFooter(
                                "Requested by " + event.getUser().getAsTag(), event.getUser().getAvatarUrl());

        val warnAmountAuditRecords =
                MystiGuardianDatabaseHandler.AmountOfWarns.getAmountOfWarnsRecords(
                        guild.getId(), user.getId());

        if (warnAmountAuditRecords.isEmpty()) {
            embed.addField(
                    "Warn Amount Audit Records", "This user has never been warned in this server.", true);
            replyUtils.sendEmbed(embed);
            return;
        }

        AtomicInteger amountOfWarns = new AtomicInteger();
        warnAmountAuditRecords.forEach(record -> amountOfWarns.addAndGet(record.getAmountOfWarns()));

        embed.addField(
                "Warn Amount Audit Records",
                "This user has been warned " + amountOfWarns.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }

    private void kickAmountAuditRecordsEmbed(
            SlashCommandInteractionEvent event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        Guild guild = event.getGuild();
        if (guild == null) return;

        val embed =
                new EmbedBuilder()
                        .setTitle("Kick Amount Audit Records")
                        .setDescription("This is the amount of kicks a user has received in this server.")
                        .setColor(Color.YELLOW)
                        .setThumbnail(user.getAvatarUrl())
                        .setFooter(
                                "Requested by " + event.getUser().getAsTag(), event.getUser().getAvatarUrl());

        val kickAmountAuditRecords =
                MystiGuardianDatabaseHandler.AmountOfKicks.getAmountOfKicksRecords(
                        guild.getId(), user.getId());

        if (kickAmountAuditRecords.isEmpty()) {
            embed.addField(
                    "Kick Amount Audit Records", "This user has never been kicked in this server.", true);
            replyUtils.sendEmbed(embed);
            return;
        }

        AtomicInteger amountOfKicks = new AtomicInteger();
        kickAmountAuditRecords.forEach(record -> amountOfKicks.addAndGet(record.getAmountOfKicks()));

        embed.addField(
                "Kick Amount Audit Records",
                "This user has been kicked " + amountOfKicks.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }

    private void banAmountAuditRecordsEmbed(
            SlashCommandInteractionEvent event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        Guild guild = event.getGuild();
        if (guild == null) return;

        val embed =
                new EmbedBuilder()
                        .setTitle("Ban Amount Audit Records")
                        .setDescription("This is the amount of bans a user has received in this server.")
                        .setColor(Color.YELLOW)
                        .setThumbnail(user.getAvatarUrl())
                        .setFooter(
                                "Requested by " + event.getUser().getAsTag(), event.getUser().getAvatarUrl());

        val banAmountAuditRecords =
                MystiGuardianDatabaseHandler.AmountOfBans.getAmountOfBansRecords(
                        guild.getId(), user.getId());

        if (banAmountAuditRecords.isEmpty()) {
            embed.addField(
                    "Ban Amount Audit Records", "This user has never been banned in this server.", true);
            replyUtils.sendEmbed(embed);
            return;
        }

        AtomicInteger amountOfBans = new AtomicInteger();
        banAmountAuditRecords.forEach(record -> amountOfBans.addAndGet(record.getAmountOfBans()));

        embed.addField(
                "Ban Amount Audit Records",
                "This user has been banned " + amountOfBans.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }

    private void timeOutAmountAuditRecordsEmbed(
            SlashCommandInteractionEvent event,
            User user,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        Guild guild = event.getGuild();
        if (guild == null) return;

        EmbedBuilder embed =
                new EmbedBuilder()
                        .setTitle("Time Out Amount Audit Records")
                        .setDescription("This is the amount of time outs a user has received in this server.")
                        .setColor(Color.YELLOW)
                        .setThumbnail(user.getAvatarUrl())
                        .setFooter(
                                "Requested by " + event.getUser().getAsTag(), event.getUser().getAvatarUrl());

        val timeOutAmountAuditRecords =
                MystiGuardianDatabaseHandler.AmountOfTimeOuts.getAmountOfTimeOutsRecords(
                        guild.getId(), user.getId());

        if (timeOutAmountAuditRecords.isEmpty()) {
            embed.addField(
                    "Time Out Amount Audit Records",
                    "This user has never been time outed in this server.",
                    true);
            replyUtils.sendEmbed(embed);
            return;
        }

        AtomicInteger amountOfTimeOuts = new AtomicInteger();
        timeOutAmountAuditRecords.forEach(
                record -> amountOfTimeOuts.addAndGet(record.getAmountOfTimeOuts()));

        embed.addField(
                "Time Out Amount Audit Records",
                "This user has been time outed " + amountOfTimeOuts.get() + " times in this server.",
                true);

        replyUtils.sendEmbed(embed);
    }

    @NotNull
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "user", "The user to get audit logs for", true),
                new OptionData(OptionType.STRING, "choice", "The type of audit log to retrieve", true)
                        .addChoice("warn", "warn")
                        .addChoice("kick", "kick")
                        .addChoice("ban", "ban")
                        .addChoice("time-out", "time-out"));
    }
}
