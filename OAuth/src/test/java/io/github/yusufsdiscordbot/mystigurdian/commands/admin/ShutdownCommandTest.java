/*
 * Copyright 2025 RealYusufIsmail.
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
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import io.github.yusufsdiscordbot.mystiguardian.utils.SystemWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ShutdownCommandTest {

    @Mock private JDA jda;

    @Mock private SlashCommandInteractionEvent event;

    @Mock private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock private PermChecker permChecker;

    @Mock private SystemWrapper systemWrapper;

    private ShutdownCommand command;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new ShutdownCommand();

        systemWrapper = mock(SystemWrapper.class);
        command.systemWrapper = systemWrapper;

        when(event.getJDA()).thenReturn(jda);
    }

    @Test
    public void shouldHandleShutdownCommand() {
        doNothing().when(systemWrapper).exit(MystiGuardianUtils.CloseCodes.OWNER_REQUESTED.getCode());

        command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);

        verify(replyUtils).sendInfo("Shutting down");
    }
}
