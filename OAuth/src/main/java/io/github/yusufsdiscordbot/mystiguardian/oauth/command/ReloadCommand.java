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
package io.github.yusufsdiscordbot.mystiguardian.oauth.command;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import java.io.IOException;
import java.util.List;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ReloadCommand implements ISlashCommand {
    public boolean isTest = false;

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        val reason = event.getOptionByName("reason").orElse(null);

        if (reason == null) {
            replyUtils.sendError("Please provide a reason");
            return;
        }

        replyUtils.sendInfo("Reloading the bot");

        MystiGuardianDatabaseHandler.ReloadAudit.setReloadAuditRecord(
                event.getUser().getIdAsString(), reason.getStringValue().orElse("No reason provided"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error while sleeping", e);
        }

        event.getApi().disconnect().thenAccept((v) -> {
            MystiGuardianConfig.getDatabase().getDs().close();
            MystiGuardianConfig.reloading = true;
            MystiGuardianConfig.mainThread.cancel(true);
        });

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
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.create(SlashCommandOptionType.STRING, "reason", "The reason for reloading", true));
    }
}
