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

import io.github.yusufsdiscordbot.mystiguardian.api.job.Job;
import io.github.yusufsdiscordbot.mystiguardian.config.JobCategoryGroup;
import io.github.yusufsdiscordbot.mystiguardian.event.events.NewDAEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.handler.NewDAEventHandler;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Slf4j
public class NewDAEventListener implements NewDAEventHandler {
    @Override
    public void onNewDA(NewDAEvent event) {
        logger.info("New DA event: {}", event.toString());

        val config = MystiGuardianUtils.getMainConfig();
        val embeds = new ArrayList<MessageEmbed>();
        val rolesToPingSet = new HashSet<String>();

        // Process each job to collect embeds and determine which roles to ping
        for (Job job : event.jobs()) {
            embeds.add(job.getEmbed());

            // Get roles to ping based on job categories
            List<String> jobCategories = job.getCategories();
            if (jobCategories != null && !jobCategories.isEmpty()) {
                // Check if any job category matches configured category mappings
                for (String category : jobCategories) {
                    String normalizedCategory = category.toLowerCase().replace(" ", "-");

                    // First check individual category mappings
                    List<String> categoryRoles = config.categoryRoleMappings().get(normalizedCategory);
                    if (categoryRoles != null && !categoryRoles.isEmpty()) {
                        rolesToPingSet.addAll(categoryRoles);
                    }

                    // Then check category group mappings
                    List<JobCategoryGroup> groups =
                            JobCategoryGroup.findGroupsForCategory(normalizedCategory);
                    for (JobCategoryGroup group : groups) {
                        List<String> groupRoles = config.categoryGroupMappings().get(group.name());
                        if (groupRoles != null && !groupRoles.isEmpty()) {
                            rolesToPingSet.addAll(groupRoles);
                        }
                    }
                }
            }
        }

        // If no category-specific roles found, use default rolesToPing
        if (rolesToPingSet.isEmpty() && config.rolesToPing() != null) {
            rolesToPingSet.addAll(config.rolesToPing());
        }

        // Build the ping message
        String pingMessage = null;
        if (!rolesToPingSet.isEmpty()) {
            pingMessage =
                    rolesToPingSet.stream()
                            .filter(roleId -> roleId != null && !roleId.isEmpty())
                            .map(roleId -> String.format("<@&%s>", roleId))
                            .collect(Collectors.joining(" "));
        }

        // Send the message with pings as content (outside embed)
        if (pingMessage != null && !pingMessage.isEmpty()) {
            event.textChannel().sendMessage(pingMessage).setEmbeds(embeds).queue();
        } else {
            // No pings, just send embeds
            event.textChannel().sendMessageEmbeds(embeds).queue();
        }
    }
}
