package io.github.yusufsdiscordbot.mystigurdian.util;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class ReplyUtilsTest {

    @Mock
    private InteractionImmediateResponseBuilder builder;

    private MystiGuardianUtils.ReplyUtils replyUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        replyUtils = new MystiGuardianUtils.ReplyUtils(builder);
    }

    @Test
    public void shouldSendError() {
        String message = "An error occurred";
        replyUtils.sendError(message);
        verify(builder).setContent("Error: " + message);
        verify(builder).setFlags(MessageFlag.EPHEMERAL, MessageFlag.URGENT);
        verify(builder).respond();
    }

    @Test
    public void shouldSendSuccess() {
        String message = "Operation successful";
        replyUtils.sendSuccess(message);
        verify(builder).setContent("Success: " + message);
        verify(builder).setFlags(MessageFlag.EPHEMERAL, MessageFlag.URGENT);
        verify(builder).respond();
    }

    @Test
    public void shouldSendInfo() {
        String message = "Information message";
        replyUtils.sendInfo(message);
        verify(builder).setContent("Info: " + message);
        verify(builder).addComponents(any(ActionRow[].class));
        verify(builder).respond();
    }

    @Test
    public void shouldSendEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        replyUtils.sendEmbed(embedBuilder);
        verify(builder).addEmbed(embedBuilder);
        verify(builder).addComponents(any(ActionRow[].class));
        verify(builder).respond();
    }
}