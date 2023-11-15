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
package io.github.yusufsdiscordbot.mystiguardian.commands.miscellaneous;

import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.time.Instant;
import lombok.val;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PingCommand implements ISlashCommand {

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        var unFormattedGatewayLatency = event.getApi().getLatestGatewayLatency();
        var unFormattedRestLatency = event.getApi().measureRestLatency().join();
        val now = Instant.now();

        val gatewayLatency = MystiGuardianUtils.formatString("%dms", unFormattedGatewayLatency.toMillis());
        val restLatency = MystiGuardianUtils.formatString("%dms", unFormattedRestLatency.toMillis());

        val embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Pong!");
        embedBuilder.addField("Gateway latency", gatewayLatency, true);
        embedBuilder.addField("REST latency", restLatency, true);
        embedBuilder.setFooter(
                MystiGuardianUtils.formatString(
                        "Requested by %s", event.getUser().getName()),
                event.getUser().getAvatar());
        embedBuilder.setColor(MystiGuardianUtils.getBotColor());

        replyUtils.sendEmbed(embedBuilder);
    }

    @NotNull
    @Override
    public String getName() {
        return "ping";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Get the bots websocket and REST latency";
    }
}
