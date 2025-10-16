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
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
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

    @Mock private RestAction<Long> restPingAction;

    @Mock private ReplyCallbackAction deferReplyAction;

    @Mock private InteractionHook hook;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebhookMessageCreateAction webhookAction;

    private PingCommand command;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new PingCommand();
        setCommonVariables(jda, user, event);

        // Mock deferReply
        when(event.deferReply()).thenReturn(deferReplyAction);
        doAnswer(invocation -> null).when(deferReplyAction).queue();

        // Mock getHook
        when(event.getHook()).thenReturn(hook);
        when(hook.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(webhookAction);
        doAnswer(invocation -> null).when(webhookAction).queue();
    }

    @Test
    public void shouldSendPingResponse() {
        when(jda.getGatewayPing()).thenReturn(100L);
        when(jda.getRestPing()).thenReturn(restPingAction);
        when(restPingAction.complete()).thenReturn(150L);

        command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);

        verify(event).deferReply();
        verify(hook).sendMessageEmbeds(any(MessageEmbed.class));
    }
}
