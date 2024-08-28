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
import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonClickHandler {
    private final ButtonInteractionEvent buttonClickEvent;

    public ButtonClickHandler(ButtonInteractionEvent buttonClickEvent) {
        this.buttonClickEvent = buttonClickEvent;
        handleButtonClick();
    }

    private void handleButtonClick() {
        val customId = buttonClickEvent.getId();
        val user = buttonClickEvent.getUser();
        val userWhoCreatedTheEmbed = buttonClickEvent.getInteraction().getUser();

        Long userMessageId = userWhoCreatedTheEmbed.getIdLong();
        Long userId = user.getIdLong();

        if (!userMessageId.equals(userId)) {
            buttonClickEvent.reply("Do not click buttons that are not yours").setEphemeral(true).queue();
            return;
        }

        if (customId.startsWith("prev_") || customId.startsWith("next_")) {
            int currentIndex = Integer.parseInt(customId.split("_")[1]);

            if (customId.startsWith("prev_")) {
                currentIndex = Math.max(0, currentIndex - 1);
            } else if (customId.startsWith("next_")) {
                currentIndex++;
            }

            buttonClickEvent.getMessage().delete().queue();

            val slashCommandName = customId.split("_")[2];

            handleAudit(slashCommandName, customId, currentIndex);

            // Acknowledge the button interaction
            buttonClickEvent.deferReply().queue();
        }

        if (customId.equals("delete")) {
            buttonClickEvent
                    .getMessage()
                    .delete()
                    .queue(
                            message -> {},
                            exception -> {
                                MystiGuardianUtils.logger.error("Failed to delete message", exception);
                            });
        }
    }

    private void handleAudit(String slashCommandName, String customId, int currentIndex) {
        if (slashCommandName.equals(MystiGuardianUtils.PageNames.RELOAD_AUDIT.name())) {
            sendReloadAuditRecordsEmbed(buttonClickEvent, currentIndex);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.WARN_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getJDA().getUserById(userId);
            sendWarnAuditRecordsEmbed(buttonClickEvent, currentIndex, user);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.KICK_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getJDA().getUserById(userId);
            sendKickAuditRecordsEmbed(buttonClickEvent, currentIndex, user);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.BAN_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getJDA().getUserById(userId);
            sendBanAuditRecordsEmbed(buttonClickEvent, currentIndex, user);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.TIME_OUT_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getJDA().getUserById(userId);
            sendTimeOutAuditRecordsEmbed(buttonClickEvent, currentIndex, user);
        }
    }
}
