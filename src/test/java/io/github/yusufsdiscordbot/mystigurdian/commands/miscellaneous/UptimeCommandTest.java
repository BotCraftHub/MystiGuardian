package io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous.UptimeCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.entity.IconImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils.setCommonVariables;
import static org.mockito.Mockito.*;

public class UptimeCommandTest {

    @Mock
    private DiscordApi api;

    @Mock
    private SlashCommandInteraction event;

    @Mock
    private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock
    private User user;

    private UptimeCommand command;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);
        user = mock(User.class); // Add this line
        command = new UptimeCommand();
        setCommonVariables(api, user, event);
    }

    @Test
    public void shouldSendUptimeEmbed() {
        command.onSlashCommandInteractionEvent(event, replyUtils);
        verify(replyUtils).sendEmbed(any(EmbedBuilder.class));
    }

    @Test
    public void shouldCalculateUptimeCorrectly() {
        MystiGuardian.startTime = Instant.now().minus(Duration.ofHours(1));

        command.onSlashCommandInteractionEvent(event, replyUtils);

        String expectedUptime = "1 hour";
        verify(replyUtils).sendEmbed(argThat(embed -> MystiGuardianTestUtils.getEmbedDescription(embed).contains(expectedUptime)));
    }
}