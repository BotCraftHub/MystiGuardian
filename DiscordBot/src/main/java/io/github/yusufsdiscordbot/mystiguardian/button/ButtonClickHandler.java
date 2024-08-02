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
import org.javacord.api.event.interaction.ButtonClickEvent;

public class ButtonClickHandler {
    private final ButtonClickEvent buttonClickEvent;

    public ButtonClickHandler(ButtonClickEvent buttonClickEvent) {
        this.buttonClickEvent = buttonClickEvent;

        handleButtonClick();
    }

    private void handleButtonClick() {
        String customId = buttonClickEvent.getButtonInteraction().getCustomId();

        if (customId.startsWith("prev_") || customId.startsWith("next_")) {
            int currentIndex = Integer.parseInt(customId.split("_")[1]);

            if (customId.startsWith("prev_")) {
                currentIndex = Math.max(0, currentIndex - 1);
            } else if (customId.startsWith("next_")) {
                currentIndex++;
            }

            buttonClickEvent.getButtonInteraction().getMessage().delete().join();

            val slashCommandName = customId.split("_")[2];

            handleAudit(slashCommandName, customId, currentIndex);

            // Acknowledge the button interaction
            buttonClickEvent.getButtonInteraction().createImmediateResponder().respond();
        }

        if (customId.equals("delete")) {
            buttonClickEvent
                    .getButtonInteraction()
                    .getMessage()
                    .delete()
                    .exceptionally(
                            throwable -> {
                                MystiGuardianUtils.logger.error("Failed to delete message", throwable);
                                return null;
                            })
                    .join();
        }
    }

    private void handleAudit(String slashCommandName, String customId, int currentIndex) {
        if (slashCommandName.equals(MystiGuardianUtils.PageNames.RELOAD_AUDIT.name())) {
            sendReloadAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.WARN_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getApi().getUserById(userId).join();
            sendWarnAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.KICK_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getApi().getUserById(userId).join();
            sendKickAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.BAN_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getApi().getUserById(userId).join();
            sendBanAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
        } else if (slashCommandName.equals(MystiGuardianUtils.PageNames.TIME_OUT_AUDIT.name())) {
            val userId = customId.split("_")[3];
            val user = buttonClickEvent.getApi().getUserById(userId).join();
            sendTimeOutAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
        }
    }
}
