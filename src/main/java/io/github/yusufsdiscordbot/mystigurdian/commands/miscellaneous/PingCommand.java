package io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystigurdian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystigurdian.utils.MystiGurdianUtils;
import lombok.val;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@SuppressWarnings("unused")
public class PingCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(SlashCommandInteraction event) {
        var unFormattedGatewayLatency = event.getApi().getLatestGatewayLatency();
        var unFormattedRestLatency = event.getApi().measureRestLatency().join();
        val now = Instant.now();

        unFormattedRestLatency = Duration.between(now, Instant.now());

        val gatewayLatency = STR."\{unFormattedGatewayLatency.toMillis()}ms";
        val restLatency = STR."\{unFormattedRestLatency.toMillis()}ms";

        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Pong!");
        embedBuilder.addField("Gateway latency", gatewayLatency, true);
        embedBuilder.addField("REST latency", restLatency, true);
        embedBuilder.setFooter(STR."Requested by \{event.getUser().getName()}", event.getUser().getAvatar());
        embedBuilder.setColor(MystiGurdianUtils.getBotColor());

        event.createImmediateResponder().addEmbed(embedBuilder)
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    @NotNull
    @Override
    public String getName() {
        return "ping";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get the bots websocket and REST latency";
    }
}
