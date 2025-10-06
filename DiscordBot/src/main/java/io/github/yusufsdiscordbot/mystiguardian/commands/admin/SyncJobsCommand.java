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
package io.github.yusufsdiscordbot.mystiguardian.commands.admin;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@SlashEventBus
public class SyncJobsCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {

        // Check if user is the bot owner
        String ownerId = MystiGuardianUtils.getMainConfig().ownerId();
        if (!event.getUser().getId().equals(ownerId)) {
            replyUtils.sendError("Only the bot owner can sync jobs.");
            return;
        }

        event.deferReply().queue();

        MystiGuardianUtils.runInVirtualThread(
                () -> {
                    try {
                        event
                                .getHook()
                                .sendMessage("🔄 Starting sync of spreadsheet jobs to Discord channels...")
                                .queue();

                        // Get the job spreadsheet manager from the bot config
                        var jobManager = MystiGuardianConfig.getJobSpreadsheetManager();
                        if (jobManager == null) {
                            event.getHook().sendMessage("❌ Job spreadsheet manager not initialized.").queue();
                            return;
                        }

                        // Perform the sync
                        jobManager.syncSpreadsheetToDiscord(event.getJDA());

                        event
                                .getHook()
                                .sendMessage(
                                        "✅ Sync completed! Check the configured channels for any missing jobs that were posted.")
                                .queue();
                    } catch (Exception e) {
                        event.getHook().sendMessage("❌ Failed to sync jobs: " + e.getMessage()).queue();
                    }
                });
    }

    @NotNull
    @Override
    public String getName() {
        return "sync-jobs";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Sync spreadsheet jobs to Discord channels (Owner only)";
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
