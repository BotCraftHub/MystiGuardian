package io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystigurdian.annotations.TestableCommand;
import io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils;
import lombok.val;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.entity.message.embed.EmbedImpl;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
@TestableCommand
public class PingCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        assert event.getCommandName().equals(getName());

        var unFormattedGatewayLatency = (Duration) event.getApi().getLatestGatewayLatency();
        var unFormattedRestLatency = (Duration) event.getApi().measureRestLatency().join();
        val now = Instant.now();


        val gatewayLatency = STR."\{unFormattedGatewayLatency.toMillis()}ms";
        val restLatency = STR."\{unFormattedRestLatency.toMillis()}ms";

        assert gatewayLatency.equals("0ms");
        assert restLatency.equals("0ms");


        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Pong!");
        embedBuilder.addField("Gateway latency", gatewayLatency, true);
        embedBuilder.addField("REST latency", restLatency, true);
        val embedAsJson = MystiGuardianTestUtils.embedToJson(embedBuilder);

        assert embedAsJson.get("title").asText().equals("Pong!");
        assert embedAsJson.get("fields").get(0).get("name").asText().equals("Gateway latency");
        assert embedAsJson.get("fields").get(0).get("value").asText().equals("0ms");
        assert embedAsJson.get("fields").get(0).get("inline").asBoolean();
        assert embedAsJson.get("fields").get(1).get("name").asText().equals("REST latency");
        assert embedAsJson.get("fields").get(1).get("value").asText().equals("0ms");
        assert embedAsJson.get("fields").get(1).get("inline").asBoolean();

        MystiGuardianTestUtils.logger.info("Ping command test passed!");
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
