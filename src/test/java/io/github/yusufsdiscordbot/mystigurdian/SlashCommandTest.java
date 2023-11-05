package io.github.yusufsdiscordbot.mystigurdian;

import io.github.yusufsdiscordbot.mystigurdian.builder.SlashCommandBuilder;
import io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous.PingCommand;
import io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous.UptimeCommand;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.DiscordApiImpl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;

public class SlashCommandTest {


    public void testPingCommand(DiscordApiImpl apiImpl, SlashCommandInteraction slashCommandInteraction) {
        val pingCommand = new PingCommand();

        val commandName = pingCommand.getName();
        val commandDescription = pingCommand.getDescription();

        val slashCommandBuilder = new SlashCommandBuilder(apiImpl, commandName, commandDescription);

        val slashCommand = slashCommandBuilder.build();

        assert slashCommand.getName().equals(commandName);
        assert slashCommand.getDescription().equals(commandDescription);
        assert slashCommand.getOptions().isEmpty();

        when(slashCommandInteraction.getCommandName()).thenReturn(commandName);
        when(slashCommandInteraction.getOptions()).thenReturn(new ArrayList<>());
        when(apiImpl.getLatestGatewayLatency()).thenReturn(Duration.ofMillis(0L));
        when(apiImpl.measureRestLatency()).thenReturn(CompletableFuture.completedFuture(Duration.ofMillis(0L)));

        pingCommand.onSlashCommandInteractionEvent(slashCommandInteraction);
    }

    public void testUpTimeCommand(DiscordApiImpl apiImpl, SlashCommandInteraction slashCommandInteraction) {
        val uptimeCommand = new UptimeCommand();

        val commandName = uptimeCommand.getName();
        val commandDescription = uptimeCommand.getDescription();

        val slashCommandBuilder = new SlashCommandBuilder(apiImpl, commandName, commandDescription);

        val slashCommand = slashCommandBuilder.build();

        assert slashCommand.getName().equals(commandName);
        assert slashCommand.getDescription().equals(commandDescription);
        assert slashCommand.getOptions().isEmpty();

        when(slashCommandInteraction.getCommandName()).thenReturn(commandName);
        when(slashCommandInteraction.getOptions()).thenReturn(new ArrayList<>());

        uptimeCommand.onSlashCommandInteractionEvent(slashCommandInteraction);
    }
}
