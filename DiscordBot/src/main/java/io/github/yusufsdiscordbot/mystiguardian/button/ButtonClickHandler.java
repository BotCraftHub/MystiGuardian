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
package io.github.yusufsdiscordbot.mystiguardian.button;

import static io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.type.BanAuditCommand.sendBanAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.type.KickAuditCommand.sendKickAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.type.ReloadAuditCommand.sendReloadAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.type.TimeOutAuditCommand.sendTimeOutAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.moderation.audit.type.WarnAuditCommand.sendWarnAuditRecordsEmbed;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonClickHandler {
    private final ButtonInteractionEvent buttonClickEvent;
    private static final Map<Long, String> triviaAnswerMap = new HashMap<>();

    public ButtonClickHandler(ButtonInteractionEvent buttonClickEvent) {
        this.buttonClickEvent = buttonClickEvent;
        handleButtonClick();
    }

    private void handleButtonClick() {
        String customId = buttonClickEvent.getComponentId();
        long userId = buttonClickEvent.getUser().getIdLong();
        long messageOwnerId = buttonClickEvent.getInteraction().getUser().getIdLong();

        if (userId != messageOwnerId) {
            replyEphemeral("Do not click buttons that are not yours.");
            return;
        }

        if (customId.startsWith("trivia:")) {
            handleTriviaButtonClick(customId, userId);
        } else if (customId.startsWith("prev_") || customId.startsWith("next_")) {
            handlePagination(customId);
        } else if ("delete".equals(customId)) {
            deleteMessage();
        }
    }

    private void handlePagination(String customId) {
        String[] parts = customId.split("_");
        int currentIndex = Integer.parseInt(parts[1]);
        String action = parts[0];
        String slashCommandName = parts[2];

        if ("prev".equals(action)) {
            currentIndex = Math.max(0, currentIndex - 1);
        } else if ("next".equals(action)) {
            currentIndex++;
        }

        buttonClickEvent.getMessage().delete().queue();
        handleAudit(slashCommandName, customId, currentIndex);
        buttonClickEvent.deferReply().queue(); // Acknowledge the button interaction
    }

    private void handleAudit(String slashCommandName, String customId, int currentIndex) {
        String[] parts = customId.split("_");
        long targetUserId = parts.length > 3 ? Long.parseLong(parts[3]) : 0;
        var jda = buttonClickEvent.getJDA();
        var user = targetUserId != 0 ? jda.getUserById(targetUserId) : null;

        switch (slashCommandName) {
            case "RELOAD_AUDIT":
                sendReloadAuditRecordsEmbed(buttonClickEvent, buttonClickEvent, currentIndex);
                break;
            case "WARN_AUDIT":
                sendWarnAuditRecordsEmbed(buttonClickEvent, buttonClickEvent, currentIndex, user);
                break;
            case "KICK_AUDIT":
                sendKickAuditRecordsEmbed(buttonClickEvent, buttonClickEvent, currentIndex, user);
                break;
            case "BAN_AUDIT":
                sendBanAuditRecordsEmbed(buttonClickEvent, buttonClickEvent, currentIndex, user);
                break;
            case "TIME_OUT_AUDIT":
                sendTimeOutAuditRecordsEmbed(buttonClickEvent, buttonClickEvent, currentIndex, user);
                break;
            default:
                MystiGuardianUtils.logger.warn("Unknown audit command: " + slashCommandName);
                break;
        }
    }

    private void handleTriviaButtonClick(String customId, long userId) {
        String selectedAnswer = customId.substring("trivia:".length());
        String correctAnswer = triviaAnswerMap.get(userId);

        if (correctAnswer == null) {
            replyEphemeral("No trivia session found!");
            return;
        }

        if (selectedAnswer.equals(correctAnswer)) {
            replyEphemeral("Correct! 🎉");
        } else {
            replyEphemeral("Oops! The correct answer was: " + correctAnswer);
        }

        triviaAnswerMap.remove(userId);
    }

    private void deleteMessage() {
        buttonClickEvent
                .getMessage()
                .delete()
                .queue(
                        null,
                        exception -> MystiGuardianUtils.logger.error("Failed to delete message", exception));
    }

    private void replyEphemeral(String message) {
        buttonClickEvent.reply(message).setEphemeral(true).queue();
    }

    public static void storeTriviaAnswer(long userId, String correctAnswer) {
        triviaAnswerMap.put(userId, correctAnswer);
    }
}
