/*
 * Copyright 2024 RealYusufIsmail.
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
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PingCommandTest {

    @Mock private SlashCommandInteractionEvent event;

    @Mock private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock private PermChecker permChecker;

    @Mock private JDA jda;

    @Mock private User user;

    @Mock private RestAction<Long> restAction;

    private PingCommand command;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new PingCommand();
        setCommonVariables(jda, user, event);
    }

    @Test
    public void shouldSendPingResponse() {
        when(jda.getGatewayPing()).thenReturn(100L);
        when(jda.getRestPing()).thenReturn(restAction);

        doAnswer(
                        invocation -> {
                            Consumer<Long> onSuccess = invocation.getArgument(0);
                            onSuccess.accept(150L);
                            return null;
                        })
                .when(restAction)
                .queue(any(Consumer.class));

        command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);

        verify(replyUtils).sendEmbed(any());
    }
}
