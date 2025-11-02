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
package io.github.yusufsdiscordbot.mystiguardian.event.events;

import io.github.yusufsdiscordbot.mystiguardian.button.ButtonClickHandler;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordEvents extends ListenerAdapter {
    private final SlashCommandsHandler handler;

    public DiscordEvents(SlashCommandsHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSessionDisconnect(@NotNull SessionDisconnectEvent event) {
        if (event.getCloseCode() == null) {
            return;
        }

        event
                .getJDA()
                .getGuildById(MystiGuardianUtils.getLogConfig().logGuildId())
                .getChannelById(TextChannel.class, MystiGuardianUtils.getLogConfig().logChannelId())
                .sendMessage(
                        "The bot has lost connection to Discord API due to "
                                + event.getCloseCode().getMeaning()
                                + " With code: "
                                + event.getCloseCode().getCode());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        handler.onSlashCommandCreateEvent(event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        new ButtonClickHandler(event);
    }
}
