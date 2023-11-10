package io.github.yusufsdiscordbot.mystiguardian.button;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.event.interaction.ButtonClickEvent;

import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.BanAuditCommand.sendBanAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.KickAuditCommand.sendKickAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.ReloadAuditCommand.sendReloadAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.TimeOutAuditCommand.sendTimeOutAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.WarnAuditCommand.sendWarnAuditRecordsEmbed;

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
            buttonClickEvent.getButtonInteraction().getMessage().delete().join();
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
