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
package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BotInfoCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event,
            @NotNull MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val embed = replyUtils.getDefaultEmbed();

        val serverCount = event.getApi().getServers().size();
        var userCount = 0;

        for (val server : event.getApi().getServers()) {
            userCount += server.getMemberCount();
        }

        val channelCount = event.getApi().getChannels().size();

        embed.setTitle(event.getApi().getYourself().getName() + " Information");
        val info =
                """
                Server Count: %s
                Member Count: %s
                Channel Count: %s
                Ping: %s
                Memory Usage: %s
                CPU Usage: %s
                Operating System: %s
                Java Version: %s
                Java Vendor: %s
                """
                        .formatted(
                                serverCount,
                                userCount,
                                channelCount,
                                event.getApi().getLatestGatewayLatency().toMillis() + "ms",
                                MystiGuardianUtils.getMemoryUsage(),
                                String.valueOf(MystiGuardianUtils.getCpuUsage(1000)),
                                MystiGuardianUtils.getOperatingSystem(),
                                MystiGuardianUtils.getJavaVersion(),
                                MystiGuardianUtils.getJavaVendor());

        embed.setDescription("```" + info + "```");

        replyUtils.sendEmbed(embed);
    }

    @NotNull
    @Override
    public String getName() {
        return "bot-info";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get information about the bot";
    }
}
