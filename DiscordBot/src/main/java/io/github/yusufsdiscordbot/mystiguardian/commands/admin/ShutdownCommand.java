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

import io.github.yusufsdiscordbot.mystiguardian.exception.ShutdownException;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import io.github.yusufsdiscordbot.mystiguardian.utils.SystemWrapper;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ShutdownCommand implements ISlashCommand {
    public SystemWrapper systemWrapper = new SystemWrapper();

    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils, PermChecker permChecker) {
        replyUtils.sendInfo("Shutting down");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            MystiGuardianUtils.logger.error("Error while sleeping", e);
        }

        event.getApi().disconnect().thenAccept((v) -> {
            try {
                systemWrapper.exit(MystiGuardianUtils.CloseCodes.OWNER_REQUESTED.getCode());
            } catch (ShutdownException e) {
                MystiGuardianUtils.logger.error("Error while shutting down", e.getCause());
            }
        });
    }

    @NotNull
    @Override
    public String getName() {
        return "shutdown";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Shutdowns the bot";
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
