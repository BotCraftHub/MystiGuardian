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

import io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus;
import io.github.yusufsdiscordbot.mystiguardian.exception.ShutdownException;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import io.github.yusufsdiscordbot.mystiguardian.utils.PermChecker;
import io.github.yusufsdiscordbot.mystiguardian.utils.SystemWrapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Admin command to gracefully shut down the bot.
 *
 * <p>This command is restricted to the bot owner only and performs a graceful shutdown:
 *
 * <ul>
 *   <li>Sends a confirmation message to the user
 *   <li>Waits 1 second to allow the message to send
 *   <li>Calls {@link net.dv8tion.jda.api.JDA#shutdown()} to disconnect from Discord
 *   <li>Exits the JVM with code {@link MystiGuardianUtils.CloseCodes#OWNER_REQUESTED}
 * </ul>
 *
 * <p>The shutdown process runs asynchronously to ensure the confirmation message is sent before the
 * bot disconnects.
 *
 * <p><b>Security:</b> This command can only be executed by the bot owner as defined in config.json.
 * Attempting to use it as a non-owner will be denied.
 *
 * <p><b>Warning:</b> This command will terminate the bot process. Ensure proper deployment and
 * restart mechanisms are in place if automatic recovery is desired.
 *
 * @see ISlashCommand
 * @see SystemWrapper
 */
@Slf4j
@SlashEventBus
public class ShutdownCommand implements ISlashCommand {
    /** Wrapper for system calls to allow testing without actual JVM exit. */
    public SystemWrapper systemWrapper = new SystemWrapper();

    /**
     * Handles the shutdown command execution.
     *
     * <p>Sends confirmation, waits 1 second, then shuts down JDA and exits the JVM.
     *
     * @param event the slash command interaction event
     * @param replyUtils utility for sending replies
     * @param permChecker permission checking utility (owner check is enforced by isOwnerOnly())
     */
    @Override
    public void onSlashCommandInteractionEvent(
            @NotNull SlashCommandInteractionEvent event,
            MystiGuardianUtils.ReplyUtils replyUtils,
            PermChecker permChecker) {
        replyUtils.sendInfo("Shutting down");

        CompletableFuture.runAsync(
                () -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        logger.error("Shutdown process interrupted", e);
                        return;
                    }

                    try {
                        shutdown(event);
                    } catch (ShutdownException e) {
                        logger.error("Error during shutdown", e);
                    }
                });
    }

    /**
     * Performs the actual shutdown sequence.
     *
     * <p>Shuts down the JDA connection and exits the JVM with the owner-requested close code.
     *
     * @param event the slash command interaction event (for accessing JDA)
     * @throws ShutdownException if an error occurs during shutdown
     */
    private void shutdown(SlashCommandInteractionEvent event) throws ShutdownException {
        event.getJDA().shutdown();

        systemWrapper.exit(MystiGuardianUtils.CloseCodes.OWNER_REQUESTED.getCode());
    }

    /**
     * Gets the command name.
     *
     * @return "shutdown"
     */
    @NotNull
    @Override
    public String getName() {
        return "shutdown";
    }

    /**
     * Gets the command description shown in Discord.
     *
     * @return description explaining the command shuts down the bot
     */
    @NotNull
    @Override
    public String getDescription() {
        return "Shutdowns the bot";
    }

    /**
     * Indicates this command is restricted to the bot owner.
     *
     * @return true, as only the bot owner can shut down the bot
     */
    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
