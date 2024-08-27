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
package io.github.yusufsdiscordbot.mystiguardian.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;

public class DiscordLogAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent eventObject) {
        String logMessage = eventObject.getFormattedMessage();

        if (logMessage.contains("WARN")) {
            sendWarningToDiscord(logMessage);
        } else if (logMessage.contains("ERROR")) {
            sendErrorToDiscord(logMessage);
        }
    }

    private void sendWarningToDiscord(String message) {
        MystiGuardianConfig.getInstance()
                .getApi()
                .getServerById(MystiGuardianUtils.getLogConfig().logGuildId())
                .flatMap(
                        server -> server.getTextChannelById(MystiGuardianUtils.getLogConfig().logChannelId()))
                .ifPresent(channel -> channel.sendMessage("```WARN: " + message + "```"));
    }

    private void sendErrorToDiscord(String message) {
        MystiGuardianConfig.getInstance()
                .getApi()
                .getServerById(MystiGuardianUtils.getLogConfig().logGuildId())
                .flatMap(
                        server -> server.getTextChannelById(MystiGuardianUtils.getLogConfig().logChannelId()))
                .ifPresent(channel -> channel.sendMessage("```ERROR: " + message + "```"));
    }
}
