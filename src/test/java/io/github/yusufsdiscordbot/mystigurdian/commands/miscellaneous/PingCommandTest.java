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
package io.github.yusufsdiscordbot.mystigurdian.commands.miscellaneous;

import static io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils.setCommonVariables;
import static org.mockito.Mockito.*;

import io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous.PingCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
