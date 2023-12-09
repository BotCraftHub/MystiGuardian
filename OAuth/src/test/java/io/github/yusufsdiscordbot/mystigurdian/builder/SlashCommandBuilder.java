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
package io.github.yusufsdiscordbot.mystigurdian.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystigurdian.util.MystiGuardianTestUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.interaction.SlashCommandImpl;

public class SlashCommandBuilder {
    private final DiscordApiImpl ap;
    private final String name;
    private final String description;
    private final List<SlashCommandOption> options = new ArrayList<>();
    private boolean isOwnerOnly = false;

    public SlashCommandBuilder(DiscordApiImpl ap, String name, String description) {
        this.ap = ap;
        this.name = name;
        this.description = description;
    }

    public SlashCommandBuilder addOption(SlashCommandOption option) {
        options.add(option);
        return this;
    }

    public SlashCommandBuilder addOptions(List<SlashCommandOption> options) {
        this.options.addAll(options);
        return this;
    }

    public SlashCommandBuilder setOwnerOnly(boolean isOwnerOnly) {
        this.isOwnerOnly = isOwnerOnly;
        return this;
    }

    public SlashCommand build() {
        val slashJson = new ObjectMapper().createObjectNode();
        slashJson.put("id", MystiGuardianTestUtils.randomString(10));
        slashJson.put("application_id", MystiGuardianTestUtils.randomString(10));
        slashJson.put("name", name);
        slashJson.put("description", description);
        slashJson.put("dm_permissions", isOwnerOnly);
        val optionsArray = slashJson.putArray("options");
        for (val option : options) {
            val optionJson = new ObjectMapper().createObjectNode();
            optionJson.put("name", option.getName());
            optionJson.put("description", option.getDescription());
            optionJson.put("required", option.isRequired());
            optionJson.put("type", option.getType().getValue());
            optionsArray.add(optionJson);
        }
        slashJson.putArray("options").addAll(optionsArray);

        return new SlashCommandImpl(ap, slashJson);
    }
}
