package io.github.yusufsdiscordbot.mystigurdian.commands.admin;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils;
import lombok.val;
import mystigurdian.annotations.TestableCommand;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
@TestableCommand
public class ReloadCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        val option = event.getOptions();

        assert !option.isEmpty();

        val reason = option.get(0);

        assert reason != null;

        MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(event.getUser().getIdAsString(), reason.getStringValue().orElse("No reason provided"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error while sleeping", e);
        }


        val close = event.getApi().disconnect().
                thenAccept((v) -> {
                    val db = MystiGuardian.getDatabase().getDs();

                    assert db != null;
                });

        assert close.isDone();

        MystiGuardianTestUtils.logger.info("Reload command test passed!");
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
