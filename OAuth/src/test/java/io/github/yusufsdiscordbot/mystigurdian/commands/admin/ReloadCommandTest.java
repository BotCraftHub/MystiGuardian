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
package io.github.yusufsdiscordbot.mystigurdian.commands.admin;

import static io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils.setCommonVariables;
import static org.mockito.Mockito.*;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.oauth.command.ReloadCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ReloadCommandTest {

    @Mock private DiscordApi api;

    @Mock private SlashCommandInteraction event;

    @Mock private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock private SlashCommandInteractionOption option;

    @Mock private PermChecker permChecker;

    private ReloadCommand command;

    @Mock private User user;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);
        command = new ReloadCommand();
        command.isTest = true;
        setCommonVariables(api, user, event);
    }

    @Test
    public void shouldHandleMissingReason() {
        when(event.getOptionByName("reason")).thenReturn(java.util.Optional.empty());

        command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);

        verify(replyUtils).sendError("Please provide a reason");
    }

    @Test
    public void shouldHandleProvidedReason() {
        when(event.getOptionByName("reason")).thenReturn(java.util.Optional.of(option));
        when(option.getStringValue()).thenReturn(java.util.Optional.of("Test reason"));
        when(event.getApi().disconnect()).thenReturn(CompletableFuture.completedFuture(null));

        try (MockedStatic<MystiGuardianDatabaseHandler.ReloadAudit> mocked =
                Mockito.mockStatic(MystiGuardianDatabaseHandler.ReloadAudit.class)) {
            mocked
                    .when(
                            () ->
                                    MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(
                                            anyString(), anyString()))
                    .thenAnswer(invocation -> null);

            command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);

            verify(replyUtils).sendInfo("Reloading the bot");
        }
    }
}
