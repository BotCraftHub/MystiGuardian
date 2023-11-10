package io.github.yusufsdiscordbot.mystiguardian.commands.admin;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

@SuppressWarnings("unused")
public class ReloadCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        val reason = event.getOptionByName("reason").orElse(null);

        if (reason == null) {
            replyUtils.sendError("Please provide a reason");
            return;
        }

        replyUtils.sendInfo("Reloading the bot");

        MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(event.getUser().getIdAsString(), reason.getStringValue().orElse("No reason provided"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error while sleeping", e);
        }

        event.getApi().disconnect().
                thenAccept((v) -> {
                    MystiGuardian.getDatabase().getDs().close();
                    MystiGuardian.reloading = true;
                    MystiGuardian.mainThread.cancel(true);
                });

        new MystiGuardian().main();
    }

    @NotNull
    @Override
    public String getName() {
        return "reload";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Reloads the bot";
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.create(SlashCommandOptionType.STRING, "reason", "The reason for reloading", true)
        );
    }
}
