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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Ping command to measure Discord API latency.
 *
 * <p>This command measures two types of latency:
 *
 * <ul>
 *   <li><b>Gateway Latency</b> - WebSocket connection latency (heartbeat roundtrip)
 *   <li><b>REST Latency</b> - HTTP API latency (measured by timing a REST request)
 * </ul>
 *
 * <p>The command defers its reply immediately to prevent timeout while measuring REST latency,
 * which requires completing a blocking HTTP request.
 *
 * <p>Results are displayed in a formatted embed with:
 *
 * <ul>
 *   <li>Gateway latency in milliseconds
 *   <li>REST latency in milliseconds
 *   <li>Requester information in footer
 * </ul>
 *
 * <p>This command is useful for:
 *
 * <ul>
 *   <li>Diagnosing connection issues
 *   <li>Verifying bot responsiveness
 *   <li>Monitoring Discord API performance
 * </ul>
 *
 * @see ISlashCommand
 */
@SlashEventBus
public class PingCommand implements ISlashCommand {

    /**
     * Handles the ping command execution.
     *
     * <p>Measures both gateway and REST latency, then displays results in an embed. Defers reply
     * immediately to avoid timeout during REST ping measurement.
     *
     * @param event the slash command interaction event
     * @param replyUtils utility for sending replies
     * @param permChecker permission checking utility
     */
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        // Defer reply immediately to prevent timeout during REST ping measurement
        event.deferReply().queue();

        var unFormattedGatewayLatency = event.getJDA().getGatewayPing();
        var unFormattedRestLatency = event.getJDA().getRestPing().complete();

        val gatewayLatency = MystiGuardianUtils.formatString("%dms", unFormattedGatewayLatency);
        val restLatency = MystiGuardianUtils.formatString("%dms", unFormattedRestLatency);

        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("🏓 Pong!");
        embedBuilder.addField("Gateway latency", gatewayLatency, true);
        embedBuilder.addField("REST latency", restLatency, true);
        embedBuilder.setFooter(
                MystiGuardianUtils.formatString("Requested by %s", event.getUser().getName()),
                event.getUser().getAvatarUrl());
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());

        event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Gets the command name.
     *
     * @return "ping"
     */
    @NotNull
    @Override
    public String getName() {
        return "ping";
    }

    /**
     * Gets the command description shown in Discord.
     *
     * @return description explaining the command measures WebSocket and REST latency
     */
    @NotNull
    @Override
    public String getDescription() {
        return "Get the bots websocket and REST latency";
    }
}
