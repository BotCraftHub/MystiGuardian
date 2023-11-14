package io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous.PingCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils.setCommonVariables;
import static org.mockito.Mockito.*;

public class PingCommandTest {

    @Mock
    private SlashCommandInteraction event;

    @Mock
    private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock
    private DiscordApi api;

    @Mock
    private User user;

    private PingCommand command;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);
        command = new PingCommand();
        setCommonVariables(api, user, event);
    }

    @Test
    public void shouldSendPingResponse() throws MalformedURLException {
        when(api.getLatestGatewayLatency()).thenReturn(Duration.ofMillis(100));
        when(api.measureRestLatency()).thenReturn(CompletableFuture.completedFuture(Duration.ofMillis(200)));

        command.onSlashCommandInteractionEvent(event, replyUtils);

        verify(replyUtils).sendEmbed(any());
    }
}