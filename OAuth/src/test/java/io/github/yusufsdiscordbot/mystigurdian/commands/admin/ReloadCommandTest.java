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

import static io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils.setCommonVariables;
import static org.mockito.Mockito.*;

import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.oauth.command.ReloadCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ReloadCommandTest {

    @Mock private JDA jda;

    @Mock private SlashCommandInteractionEvent event;

    @Mock private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock private PermChecker permChecker;

    private ReloadCommand command;

    @Mock private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        command = new ReloadCommand();
        command.isTest = true;
        setCommonVariables(jda, user, event);
    }

    @Test
    public void shouldHandleMissingReason() {
        when(event.getOption("reason", OptionMapping::getAsString)).thenReturn("");

        command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);

        verify(replyUtils).sendError("Please provide a reason");
    }

    @Test
    public void shouldHandleProvidedReason() {
        try (MockedStatic<MystiGuardianDatabaseHandler.ReloadAudit> mocked =
                Mockito.mockStatic(MystiGuardianDatabaseHandler.ReloadAudit.class)) {
            mocked
                    .when(
                            () ->
                                    MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(
                                            anyString(), anyString()))
                    .thenAnswer(invocation -> null);

            command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);
        }
    }
}
