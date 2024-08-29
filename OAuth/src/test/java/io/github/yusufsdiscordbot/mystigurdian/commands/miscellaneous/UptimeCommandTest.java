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

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous.UptimeCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UptimeCommandTest {

    @Mock private JDA jda;

    @Mock private SlashCommandInteractionEvent event;

    @Mock private MystiGuardianUtils.ReplyUtils replyUtils;

    @Mock private PermChecker permChecker;

    @Mock private User user;

    private UptimeCommand command;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);
        user = mock(User.class); // Add this line
        command = new UptimeCommand();
        setCommonVariables(jda, user, event);
    }

    @Test
    public void shouldSendUptimeEmbed() {
        command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);
        verify(replyUtils).sendEmbed(any(EmbedBuilder.class));
    }

    @Test
    public void shouldCalculateUptimeCorrectly() {
        MystiGuardianConfig.startTime = Instant.now().minus(Duration.ofHours(1));

        command.onSlashCommandInteractionEvent(event, replyUtils, permChecker);

        String expectedUptime = "1 hour";
        verify(replyUtils)
                .sendEmbed(
                        argThat(
                                embed ->
                                        MystiGuardianTestUtils.getEmbedDescription(embed).contains(expectedUptime)));
    }
}
