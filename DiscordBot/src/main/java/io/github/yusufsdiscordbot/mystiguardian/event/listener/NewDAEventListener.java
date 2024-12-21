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
package io.github.yusufsdiscordbot.mystiguardian.event.listener;

import io.github.yusufsdiscordbot.mystiguardian.event.events.NewDAEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.NewDAEventHandler;
import java.util.ArrayList;
import lombok.val;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class NewDAEventListener implements NewDAEventHandler {
    @Override
    public void onNewDA(NewDAEvent event) {
        val embeds = new ArrayList<MessageEmbed>();
        event
                .jobs()
                .forEach(
                        job -> {
                            embeds.add(job.getEmbed());
                        });

        event.textChannel().sendMessageEmbeds(embeds).queue();
    }
}
