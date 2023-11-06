package io.github.yusufsdiscordbot.mystigurdian;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystigurdian.builder.SlashCommandBuilder;
import io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous.PingCommand;
import io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous.UptimeCommand;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.DiscordApiImpl;
import org.mockito.MockedStatic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;

public class SlashCommandTest {
    private final DiscordApiImpl apiImpl;
    private final MystiGuardianTester mystiGuardian;
    private final SlashCommandInteraction slashCommandInteraction;
    private final MockedStatic<MystiGuardian> mystiGuardianStaticInstance;
    private final MystiGuardian mystiGuardianInstance;

    public SlashCommandTest(DiscordApiImpl apiImpl, MystiGuardianTester mystiGuardian, SlashCommandInteraction slashCommandInteraction, MockedStatic<MystiGuardian> mystiGuardianStaticInstance, MystiGuardian mystiGuardianInstance) {
        this.apiImpl = apiImpl;
        this.mystiGuardian = mystiGuardian;
        this.slashCommandInteraction = slashCommandInteraction;
        this.mystiGuardianStaticInstance = mystiGuardianStaticInstance;
        this.mystiGuardianInstance = mystiGuardianInstance;
    }


    public void testPingCommand() {
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

    public void testUpTimeCommand() {
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
