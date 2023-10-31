package io.github.yusufsdiscordbot.mystiguardian.admin;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

@SuppressWarnings("unused")
public class ReloadCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        AtomicReference<Long> chanelId = new AtomicReference<>();

        event.getChannel().ifPresentOrElse(channel -> {
            chanelId.set(channel.getId());
        }, () -> {
            event.createImmediateResponder().setContent("Failed to get channel id")
                    .respond();
        });

        event.createImmediateResponder().setContent("Reloading..., please wait")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error while sleeping", e);
        }

        event.getApi().disconnect();

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
}
