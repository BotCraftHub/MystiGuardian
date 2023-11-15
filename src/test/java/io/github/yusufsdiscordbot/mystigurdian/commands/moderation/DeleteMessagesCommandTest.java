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
package io.github.yusufsdiscordbot.mystigurdian.commands.moderation;

import static org.mockito.Mockito.*;

import io.github.yusufsdiscordbot.mystiguardian.commands.moderation.DeleteMessagesCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.concurrent.CompletableFuture;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DeleteMessagesCommandTest {

    @Mock
    private SlashCommandInteraction event;

    @Mock
    private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock
    private SlashCommandInteractionOption option;

    @Mock
    private TextChannel textChannel;

    private DeleteMessagesCommand command;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new DeleteMessagesCommand();
    }

    @Test
    public void shouldHandleDeleteMessagesCommand() {
        when(event.getOptionByName("amount")).thenReturn(java.util.Optional.of(option));
        when(option.getLongValue()).thenReturn(java.util.Optional.of(10L));
        when(event.getChannel()).thenReturn(java.util.Optional.of(textChannel));
        when(textChannel.getMessages(10)).thenReturn(CompletableFuture.completedFuture(null));

        command.onSlashCommandInteractionEvent(event, replyUtils);

        verify(replyUtils).sendInfo("Shutting down");
    }
}
