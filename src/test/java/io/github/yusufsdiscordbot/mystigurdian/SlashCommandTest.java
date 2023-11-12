package io.github.yusufsdiscordbot.mystigurdian;

import com.zaxxer.hikari.HikariDataSource;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystigurdian.builder.SlashCommandBuilder;
import io.github.yusufsdiscordbot.mystigurdian.commands.admin.ReloadCommand;
import io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous.PingCommand;
import io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous.UptimeCommand;
import io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.DiscordApiImpl;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;

public class SlashCommandTest {
    private final DiscordApiImpl apiImpl;
    private final MystiGuardianTester mystiGuardian;
    private final SlashCommandInteraction slashCommandInteraction;
    private final MockedStatic<MystiGuardian> mystiGuardianStaticInstance;
    private final MystiGuardian mystiGuardianInstance;
    private final MystiGuardianDatabase database;
    private final MockedStatic<MystiGuardianDatabaseHandler> databaseHandler;
    private final HikariDataSource ds;

    public SlashCommandTest(DiscordApiImpl apiImpl, MystiGuardianTester mystiGuardian, SlashCommandInteraction slashCommandInteraction, MockedStatic<MystiGuardian> mystiGuardianStaticInstance, MystiGuardian mystiGuardianInstance, MystiGuardianDatabase database, MockedStatic<MystiGuardianDatabaseHandler> databaseHandler) {
        this.apiImpl = apiImpl;
        this.mystiGuardian = mystiGuardian;
        this.slashCommandInteraction = slashCommandInteraction;
        this.mystiGuardianStaticInstance = mystiGuardianStaticInstance;
        this.mystiGuardianInstance = mystiGuardianInstance;
        this.database = database;
        this.databaseHandler = databaseHandler;
        this.ds = Mockito.mock(HikariDataSource.class);
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

    public void testReloadCommand() {
        val reloadCommand = new ReloadCommand();

        val commandName = reloadCommand.getName();
        val commandDescription = reloadCommand.getDescription();
        val commandOptions = reloadCommand.getOptions();

        val slashCommandBuilder = new SlashCommandBuilder(apiImpl, commandName, commandDescription)
                .addOptions(commandOptions);

        val slashCommand = slashCommandBuilder.build();

        assert slashCommand.getName().equals(commandName);
        assert slashCommand.getDescription().equals(commandDescription);

        slashCommand.getOptions().forEach(option -> {
            commandOptions.forEach(commandOption -> {
                assert option.getName().equals(commandOption.getName());
                assert option.getDescription().equals(commandOption.getDescription());
                assert option.isRequired() == commandOption.isRequired();
                assert option.getType().getValue() == commandOption.getType().getValue();
            });
        });

        when(slashCommandInteraction.getCommandName()).thenReturn(commandName);
        when(slashCommandInteraction.getOptions()).thenReturn(List.of(MystiGuardianTestUtils.getOptionByName(apiImpl, "reason", "Test reason")));

        val reloadAudit = Mockito.mockStatic(MystiGuardianDatabaseHandler.ReloadAudit.class);

        when(slashCommandInteraction.getApi().disconnect()).thenReturn(CompletableFuture.completedFuture(null));
        when(database.getDs()).thenReturn(ds);

        reloadCommand.onSlashCommandInteractionEvent(slashCommandInteraction);

        reloadAudit.close();
    }

    public void finish() {
        mystiGuardianStaticInstance.close();
    }
}
