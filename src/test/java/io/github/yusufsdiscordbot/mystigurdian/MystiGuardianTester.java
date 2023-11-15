/*
 * Copyright 2023 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.yusufsdiscordbot.mystigurdian;

import static org.mockito.Mockito.*;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.EventDispatcher;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.entity.IconImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;

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
        when(user.getAvatar())
                .thenReturn(new IconImpl(
                        apiImpl,
                        URI.create(
                                        "https://cdn.discordapp.com/avatars/422708001976221697/f41bc30da291dbb710d67cf216fa8de2.webp?size=1024&width=0&height=512")
                                .toURL()));
        when(slashCommandInteraction.getUser()).thenReturn(user);
        when(slashCommandInteraction.getApi()).thenReturn(apiImpl);

        TextChannel textChannel = mock(TextChannel.class);
        when(textChannel.getId()).thenReturn(genericDiscordId);

        when(slashCommandInteraction.getChannel()).thenReturn(Optional.of(textChannel));

        mystiGuardian.when(MystiGuardian::getDatabase).thenReturn(database);
        mystiGuardian.when(MystiGuardian::getEventDispatcher).thenReturn(eventDispatcher);
    }
}
