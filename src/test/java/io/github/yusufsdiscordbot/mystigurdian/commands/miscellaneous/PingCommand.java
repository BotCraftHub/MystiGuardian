package io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils;
import lombok.val;
import mystigurdian.annotations.TestableCommand;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

@SuppressWarnings("unused")
@TestableCommand
public class PingCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        assert event.getCommandName().equals(getName());

        var unFormattedGatewayLatency = (Duration) event.getApi().getLatestGatewayLatency();
        var unFormattedRestLatency = (Duration) event.getApi().measureRestLatency().join();
        val now = Instant.now();


        val gatewayLatency = STR."\{ unFormattedGatewayLatency.toMillis() }ms" ;
        val restLatency = STR."\{ unFormattedRestLatency.toMillis() }ms" ;

        assert gatewayLatency.equals("0ms");
        assert restLatency.equals("0ms");


        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Pong!");
        embedBuilder.addField("Gateway latency", gatewayLatency, true);
        embedBuilder.addField("REST latency", restLatency, true);
        embedBuilder.setFooter(STR. "Requested by \{ event.getUser().getName() }" , event.getUser().getAvatar());
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());
        val embedAsJson = MystiGuardianTestUtils.embedToJson(embedBuilder);

        assert embedAsJson.get("title").asText().equals("Pong!");
        assert embedAsJson.get("fields").get(0).get("name").asText().equals("Gateway latency");
        assert embedAsJson.get("fields").get(0).get("value").asText().equals("0ms");
        assert embedAsJson.get("fields").get(0).get("inline").asBoolean();
        assert embedAsJson.get("fields").get(1).get("name").asText().equals("REST latency");
        assert embedAsJson.get("fields").get(1).get("value").asText().equals("0ms");
        assert embedAsJson.get("fields").get(1).get("inline").asBoolean();
        assert embedAsJson.get("footer").get("text").asText().equals(STR. "Requested by \{ event.getUser().getName() }" );
        assert embedAsJson.get("footer").get("icon_url").asText().equals(event.getUser().getAvatar().getUrl().toString());
        assert embedAsJson.get("color").asInt() == (MystiGuardianUtils.getBotColor().getRGB() & 0xFFFFFF);

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
