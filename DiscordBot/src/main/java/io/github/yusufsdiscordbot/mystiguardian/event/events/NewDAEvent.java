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
package io.github.yusufsdiscordbot.mystiguardian.event.events;

import io.github.yusufsdiscordbot.mystiguardian.api.job.Apprenticeship;
import io.github.yusufsdiscordbot.mystiguardian.event.EventDispatcher;
import io.github.yusufsdiscordbot.mystiguardian.event.generic.GenericSubscribeEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.NewDAEventHandler;
import java.util.List;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * Event fired when new Digital Apprenticeships (DA) are discovered.
 *
 * <p>This event is triggered by the apprenticeship scraping system when new apprenticeships are
 * found that don't exist in the Google Sheets tracking system. It carries:
 *
 * <ul>
 *   <li>The Discord text channel where announcements should be posted
 *   <li>A list of new apprenticeships to announce
 * </ul>
 *
 * <p>Event handlers (like {@link NewDAEventHandler}) process this event by:
 *
 * <ul>
 *   <li>Creating Discord embeds for each apprenticeship
 *   <li>Batching embeds (10 per message) for efficient posting
 *   <li>Pinging relevant Discord roles based on apprenticeship categories
 * </ul>
 *
 * @param textChannel the Discord channel to post announcements to
 * @param apprenticeships list of new apprenticeships to announce
 * @see GenericSubscribeEvent
 * @see NewDAEventHandler
 * @see EventDispatcher
 */
public record NewDAEvent(TextChannel textChannel, List<Apprenticeship> apprenticeships)
        implements GenericSubscribeEvent {}
