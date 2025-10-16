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

import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class BotInfoCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            @NotNull MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        // Defer reply to prevent timeout during CPU calculation
        event.deferReply().queue();

        val embed = replyUtils.getDefaultEmbed();
        val jda = event.getJDA();
        val guilds = jda.getGuilds();

        val serverCount = guilds.size();

        var userCount = 0;

        for (val server : guilds) {
            userCount += server.getMemberCount();
        }

        val channelCount = jda.getChannelCache().size();

        embed.setTitle(jda.getSelfUser().getName() + " Information");
        val info =
                """
                **📊 Statistics**
                Server Count: %s
                Member Count: %s
                Channel Count: %s
                
                **⚡ Performance**
                Gateway Ping: %s
                Memory Usage: %s
                CPU Usage: %s
                
                **💻 System**
                Operating System: %s
                Java Version: %s
                Java Vendor: %s
                """
                        .formatted(
                                serverCount,
                                userCount,
                                channelCount,
                                jda.getGatewayPing() + "ms",
                                MystiGuardianUtils.getMemoryUsage(),
                                MystiGuardianUtils.getCpuUsage(1000) * 100 + "%",
                                MystiGuardianUtils.getOperatingSystem(),
                                MystiGuardianUtils.getJavaVersion(),
                                MystiGuardianUtils.getJavaVendor());

        embed.setDescription(info);

        event.getHook().sendMessageEmbeds(embed.build()).queue();
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
