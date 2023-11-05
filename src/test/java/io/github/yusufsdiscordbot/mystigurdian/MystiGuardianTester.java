package io.github.yusufsdiscordbot.mystigurdian;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.yusufsdiscordbot.mystigurdian.builder.SlashCommandBuilder;
import io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous.PingCommand;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.DiscordLocale;
import org.javacord.api.interaction.InteractionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.message.embed.EmbedBuilderDelegateImpl;
import org.javacord.core.event.interaction.SlashCommandCreateEventImpl;
import org.javacord.core.interaction.SlashCommandInteractionImpl;
import org.javacord.core.util.event.DispatchQueueSelector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MystiGuardianTester {
    private DiscordApiImpl apiImpl;
    public static Long applicationId = 123456789L;
    public static Long slashId = 987654321L;
    private static Long genericDiscordId = 123456789L;
    public static String token = "11i2i29ei29ri29ruj29ru";
    private DispatchQueueSelector queueSelector;
    private final List<Server> servers = new ArrayList<>();
    private final List<TextChannel> textChannels = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private SlashCommandInteraction slashCommandInteraction;

    @BeforeEach
    public void setUp() {
        apiImpl = mock(DiscordApiImpl.class);
        queueSelector = mock(DispatchQueueSelector.class);
        val server = mock(Server.class);
        val textChannel = mock(TextChannel.class);
        val user = mock(User.class);
        servers.add(server);
        textChannels.add(textChannel);
        users.add(user);
        slashCommandInteraction = mock(SlashCommandInteraction.class);

    }


    @Test
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
        when(slashCommandInteraction.getUser()).thenReturn(users.get(0));
        when(slashCommandInteraction.getApi()).thenReturn(apiImpl);

        pingCommand.onSlashCommandInteractionEvent(slashCommandInteraction);
    }
}
