package io.github.yusufsdiscordbot.mystigurdian;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.EventDispatcher;
import lombok.val;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.IconImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;

import static org.mockito.Mockito.*;

public final class MystiGuardianTester {
    private static final Long genericDiscordId = 123456789L;
    public static Long applicationId = 123456789L;
    public static Long slashId = 987654321L;
    private static MockedStatic<MystiGuardian> mystiGuardian;
    private static MockedStatic<MystiGuardianDatabaseHandler> databaseHandler;
    private DiscordApiImpl apiImpl;
    private SlashCommandInteraction slashCommandInteraction;
    private MystiGuardianDatabase database;
    private EventDispatcher eventDispatcher;
    private MystiGuardian mystiGuardianInstance;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        apiImpl = mock(DiscordApiImpl.class);
        mystiGuardian = mockStatic(MystiGuardian.class);
        database = mock(MystiGuardianDatabase.class);
        eventDispatcher = mock(EventDispatcher.class);
        mystiGuardianInstance = mock(MystiGuardian.class);
        databaseHandler = mockStatic(MystiGuardianDatabaseHandler.class);

        slashCommandInteraction = mock(SlashCommandInteraction.class);
        User user = mock(User.class);

        when(user.getName()).thenReturn("Bob");
        when(user.getAvatar()).thenReturn(new IconImpl(apiImpl, URI.create("https://cdn.discordapp.com/avatars/422708001976221697/f41bc30da291dbb710d67cf216fa8de2.webp?size=1024&width=0&height=512").toURL()));
        when(slashCommandInteraction.getUser()).thenReturn(user);
        when(slashCommandInteraction.getApi()).thenReturn(apiImpl);

        TextChannel textChannel = mock(TextChannel.class);
        when(textChannel.getId()).thenReturn(genericDiscordId);

        when(slashCommandInteraction.getChannel()).thenReturn(Optional.of(textChannel));

        mystiGuardian.when(MystiGuardian::getDatabase).thenReturn(database);
        mystiGuardian.when(MystiGuardian::getEventDispatcher).thenReturn(eventDispatcher);
    }

}
