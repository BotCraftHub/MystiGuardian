package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@SuppressWarnings("unused")
public class UptimeCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        val startTime = MystiGuardian.startTime;
        val currentTime = Instant.now();
        val uptime = Duration.between(startTime, currentTime);
        val formattedUptime = MystiGuardianUtils.formatUptimeDuration(uptime);

        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Uptime");
        embedBuilder.setDescription(STR."The bot has been up for \{formattedUptime}");
        embedBuilder.setFooter(STR."Requested by \{event.getUser().getName()}", event.getUser().getAvatar());
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());

        event.createImmediateResponder().addEmbed(embedBuilder)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    @NotNull
    @Override
    public String getName() {
        return "uptime";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get the bots uptime";
    }
}
