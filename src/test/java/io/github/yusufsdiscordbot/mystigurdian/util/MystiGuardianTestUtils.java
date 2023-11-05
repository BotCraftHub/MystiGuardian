package io.github.yusufsdiscordbot.mystigurdian.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.core.entity.message.embed.EmbedBuilderDelegateImpl;
import org.slf4j.Logger;

public class MystiGuardianTestUtils {
    public static Logger logger = org.slf4j.LoggerFactory.getLogger(MystiGuardianTestUtils.class);

    public static ObjectNode embedToJson(EmbedBuilder embed) {
        return ((EmbedBuilderDelegateImpl) embed.getDelegate()).toJsonNode();
    }
}
