/*
 * Copyright 2023 RealYusufIsmail.
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
package io.github.yusufsdiscordbot.mystiguardian.commands.audit.type;

import io.github.yusufsdiscordbot.mystiguardian.commands.audit.AuditCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.interaction.SlashCommandInteraction;

public class AmountAuditCommand {
    public void onSlashCommandInteractionEvent(SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        val server = event.getServer();
        val user = event.getOptionByName(AuditCommand.AMOUNT_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("user")
                .orElseThrow()
                .getUserValue()
                .orElseThrow();

        val choice = event.getOptionByName(AuditCommand.AMOUNT_AUDIT_OPTION_NAME)
                .orElseThrow()
                .getArgumentByName("choice")
                .orElseThrow()
                .getStringValue()
                .orElseThrow();

        if (server.isEmpty()) {
            replyUtils.sendError("This command can only be used in a server.");
            return;
        }

        switch (choice) {
            case "warn":
                warnAmountAuditRecordsEmbed(event, user);
                break;
            case "kick":
                kickAmountAuditRecordsEmbed(event, user);
                break;
            case "ban":
                banAmountAuditRecordsEmbed(event, user);
                break;
            case "time-out":
                timeOutAmountAuditRecordsEmbed(event, user);
                break;
            default:
                replyUtils.sendError("Invalid choice");
                break;
        }
    }
}
