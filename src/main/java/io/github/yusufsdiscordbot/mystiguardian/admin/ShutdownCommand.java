package io.github.yusufsdiscordbot.mystiguardian.admin;

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystigurdian.utils.MystiGurdianUtils;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import static java.lang.System.exit;

@SuppressWarnings("unused")
public class ShutdownCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        event.createImmediateResponder().setContent("Shutting down...")
                .respond();

        exit(MystiGurdianUtils.CloseCodes.OWNER_REQUESTED.getCode());
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
