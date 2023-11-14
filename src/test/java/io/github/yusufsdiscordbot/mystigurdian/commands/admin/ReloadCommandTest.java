package io.github.yusufsdiscordbot.mystigurdian.commands.admin;

import io.github.yusufsdiscordbot.mystiguardian.commands.admin.ReloadCommand;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

import static io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils.setCommonVariables;
import static org.mockito.Mockito.*;

public class ReloadCommandTest {

    @Mock
    private DiscordApi api;

    @Mock
    private SlashCommandInteraction event;

    @Mock
    private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock
    private SlashCommandInteractionOption option;

    private ReloadCommand command;

    @Mock
    private User user;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);
        command = new ReloadCommand();
        setCommonVariables(api, user, event);
    }

    @Test
    public void shouldHandleMissingReason() {
        when(event.getOptionByName("reason")).thenReturn(java.util.Optional.empty());

        command.onSlashCommandInteractionEvent(event, replyUtils);

        verify(replyUtils).sendError("Please provide a reason");
    }

    @Test
    public void shouldHandleProvidedReason() {
        when(event.getOptionByName("reason")).thenReturn(java.util.Optional.of(option));
        when(option.getStringValue()).thenReturn(java.util.Optional.of("Test reason"));
        when(event.getApi().disconnect()).thenReturn(CompletableFuture.completedFuture(null));

        try (MockedStatic<MystiGuardianDatabaseHandler.ReloadAudit> mocked = Mockito.mockStatic(MystiGuardianDatabaseHandler.ReloadAudit.class)) {
            mocked.when(() -> MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(anyString(), anyString()))
                    .thenAnswer(invocation -> null);

            command.onSlashCommandInteractionEvent(event, replyUtils);

            verify(replyUtils).sendInfo("Reloading the bot");
        }
    }
}