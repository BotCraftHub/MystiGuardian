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
package io.github.yusufsdiscordbot.mystiguardian.oauth.command;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * Discord slash command for reloading the bot.
 *
 * <p>This command allows administrators to trigger a bot reload with an audit trail. The reload
 * reason is logged to the database for tracking purposes.
 */
@Slf4j
@SlashEventBus
public class ReloadCommand implements ISlashCommand {
    /** Test flag for conditional behavior. */
    public boolean isTest = false;


    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        val reason = event.getOption("reason", OptionMapping::getAsString);

        if (reason == null) {
            replyUtils.sendError("Please provide a reason");
            return;
        }

        replyUtils.sendInfo("Reloading the bot");

        MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(event.getUser().getId(), reason);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error while sleeping", e);
        }

        MystiGuardianConfig.getDatabase().getDs().close();
        MystiGuardianConfig.reloading = true;
        MystiGuardianConfig.mainThread.cancel(true);

        event.getJDA().shutdown();

        if (!isTest) {
            try {
                MystiGuardian.main(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @NotNull
    @Override
    public String getName() {
        return "reload";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Reloads the bot";
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "reason", "The reason for reloading", true));
    }
}
