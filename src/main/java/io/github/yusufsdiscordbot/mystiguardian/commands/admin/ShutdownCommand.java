package io.github.yusufsdiscordbot.mystiguardian.commands.admin;

import io.github.yusufsdiscordbot.mystiguardian.errors.ShutdownException;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.SystemWrapper;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import static java.lang.System.exit;

@SuppressWarnings("unused")
public class ShutdownCommand implements ISlashCommand {
    public SystemWrapper systemWrapper = new SystemWrapper();

    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        replyUtils.sendInfo("Shutting down");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            MystiGuardianUtils.logger.error("Error while sleeping", e);
        }

        event.getApi().disconnect().thenAccept((v) -> {
            try {
                systemWrapper.exit(MystiGuardianUtils.CloseCodes.OWNER_REQUESTED.getCode());
            } catch (ShutdownException e) {
                MystiGuardianUtils.logger.error("Error while shutting down", e.getCause());
            }
        });
    }

    @NotNull
    @Override
    public String getName() {
        return "shutdown";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Shutdowns the bot";
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
