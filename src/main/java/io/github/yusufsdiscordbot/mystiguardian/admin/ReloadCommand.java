package io.github.yusufsdiscordbot.mystiguardian.admin;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import lombok.val;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

@SuppressWarnings("unused")
public class ReloadCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        AtomicReference<Long> chanelId = new AtomicReference<>();
        val reason = event.getOptionByName("reason").orElse(null);

        if (reason == null) {
            event.createImmediateResponder().setContent("Please provide a reason")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        event.getChannel().ifPresentOrElse(channel -> {
            chanelId.set(channel.getId());
        }, () -> {
            event.createImmediateResponder().setContent("Failed to get channel id")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        });

        event.createImmediateResponder().setContent("Reloading..., please wait")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();

        MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(event.getUser().getIdAsString(), reason.getStringValue().orElse("No reason provided"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error while sleeping", e);
        }

        MystiGuardian.getDatabase().getDs().close();
        event.getApi().disconnect().join();

        MystiGuardian.mainThread.cancel(true);

        new MystiGuardian(chanelId.get()).main();
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
