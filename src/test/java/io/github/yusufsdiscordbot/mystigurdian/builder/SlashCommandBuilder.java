package io.github.yusufsdiscordbot.mystigurdian.builder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystigurdian.MystiGuardianTester;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.core.DiscordApiImpl;
import org.javacord.core.interaction.SlashCommandImpl;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandBuilder {
    private final DiscordApiImpl ap;
    private final String name;
    private final String description;
    private final List<SlashCommandOption> options = new ArrayList<>();

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

    public SlashCommand build() {
        val slashJson = new ObjectMapper().createObjectNode();
        slashJson.put("id", MystiGuardianTester.slashId);
        slashJson.put("application_id", MystiGuardianTester.applicationId);
        slashJson.put("name", name);
        slashJson.put("description", description);
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
