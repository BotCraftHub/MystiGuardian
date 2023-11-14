package io.github.yusufsdiscordbot.mystigurdian.commands.admin;

import io.github.yusufsdiscordbot.mystiguardian.commands.admin.ShutdownCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.SystemWrapper;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class ShutdownCommandTest {

    @Mock
    private DiscordApi api;

    @Mock
    private SlashCommandInteraction event;

    @Mock
    private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock
    private User user;

    @Mock
    private SystemWrapper systemWrapper;

    private ShutdownCommand command;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new ShutdownCommand();
        command.systemWrapper = systemWrapper;
        when(event.getApi()).thenReturn(api);
        when(api.disconnect()).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    public void shouldHandleShutdownCommand() {
        command.onSlashCommandInteractionEvent(event, replyUtils);

        verify(replyUtils).sendInfo("Shutting down");
        verify(systemWrapper).exit(MystiGuardianUtils.CloseCodes.OWNER_REQUESTED.getCode());
    }
}