package io.github.yusufsdiscordbot.mystigurdian;

import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.IconImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MystiGuardianTester {
    private DiscordApiImpl apiImpl;
    public static Long applicationId = 123456789L;
    public static Long slashId = 987654321L;
    private static final Long genericDiscordId = 123456789L;
    private SlashCommandInteraction slashCommandInteraction;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        apiImpl = mock(DiscordApiImpl.class);
        slashCommandInteraction = mock(SlashCommandInteraction.class);
        User user = mock(User.class);

        when(user.getName()).thenReturn("Bob");
        when(user.getAvatar()).thenReturn(new IconImpl(apiImpl, URI.create("https://cdn.discordapp.com/avatars/422708001976221697/f41bc30da291dbb710d67cf216fa8de2.webp?size=1024&width=0&height=512").toURL()));
        when(slashCommandInteraction.getUser()).thenReturn(user);
        when(slashCommandInteraction.getApi()).thenReturn(apiImpl);
    }

    @Test
    void testSlashCommands() {
        new SlashCommandTest().testPingCommand(apiImpl, slashCommandInteraction);
        new SlashCommandTest().testUpTimeCommand(apiImpl, slashCommandInteraction);
    }
}
