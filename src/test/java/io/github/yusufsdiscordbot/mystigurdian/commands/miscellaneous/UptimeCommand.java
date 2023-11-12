package io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
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
public class UptimeCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event) {
        val startTime = MystiGuardian.startTime;
        val currentTime = Instant.now();
        val uptime = Duration.between(startTime, currentTime);
        val formattedUptime = MystiGuardianUtils.formatUptimeDuration(uptime);

        assert startTime == Instant.ofEpochSecond(0L);

        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Uptime");
        embedBuilder.setDescription(STR."The bot has been up for \{formattedUptime}");
        embedBuilder.setFooter(STR."Requested by \{event.getUser().getName()}", event.getUser().getAvatar());
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());

        val embedAsJson = MystiGuardianTestUtils.embedToJson(embedBuilder);

        assert embedAsJson.get("title").asText().equals("Uptime");
        assert embedAsJson.get("description").asText().equals(STR."The bot has been up for \{formattedUptime}");
        assert embedAsJson.get("footer").get("text").asText().equals(STR."Requested by \{event.getUser().getName()}");
        assert embedAsJson.get("footer").get("icon_url").asText().equals(event.getUser().getAvatar().getUrl().toString());
        assert embedAsJson.get("color").asInt() == (MystiGuardianUtils.getBotColor().getRGB() & 0xFFFFFF);

        MystiGuardianTestUtils.logger.info("Uptime command test passed!");
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
