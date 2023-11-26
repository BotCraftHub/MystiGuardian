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
package io.github.yusufsdiscordbot.mystigurdian.commands.admin;

import static org.mockito.Mockito.*;

import io.github.yusufsdiscordbot.mystiguardian.commands.admin.ShutdownCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.SystemWrapper;
import java.util.concurrent.CompletableFuture;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

        systemWrapper = mock(SystemWrapper.class);
        command.systemWrapper = systemWrapper;

        when(event.getApi()).thenReturn(api);
        when(api.disconnect()).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    public void shouldHandleShutdownCommand() {
        doNothing().when(systemWrapper).exit(MystiGuardianUtils.CloseCodes.OWNER_REQUESTED.getCode());

        command.onSlashCommandInteractionEvent(event, replyUtils);

        verify(replyUtils).sendInfo("Shutting down");
        verify(systemWrapper).exit(MystiGuardianUtils.CloseCodes.OWNER_REQUESTED.getCode());
    }
}
