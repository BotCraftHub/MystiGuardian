package io.github.yusufsdiscordbot.mystiguardian.commands.moderation;

import io.github.yusufsdiscordbot.mystiguardian.MystiGuardian;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabaseHandler;
import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("unused")
public class WarnCommand implements ISlashCommand {
    @Override
    public void onSlashCommandInteractionEvent(@NotNull SlashCommandInteraction event, MystiGuardianUtils.ReplyUtils replyUtils) {
        val user = event.getOptionByName("user").orElse(null);
        val reason = event.getOptionByName("reason").orElse(null);
        val server = event.getServer().orElse(null);


        if (user == null || reason == null) {
            replyUtils.sendError("Please provide a user and a reason");
            return;
        }

        if (server == null) {
            replyUtils.sendError("This command can only be used in servers");
            return;
        }

        val userObj = user.getUserValue().orElse(null);
        val reasonStr = reason.getStringValue().orElse(null);

        if (userObj == null || reasonStr == null) {
            replyUtils.sendError("Please provide a user and a reason");
            return;
        }

        val warnId = MystiGuardianDatabaseHandler.Warns.setWarnsRecord(server.getIdAsString(), userObj.getIdAsString(), reasonStr);
        MystiGuardianDatabaseHandler.AmountOfWarns.updateAmountOfWarns(server.getIdAsString(), userObj.getIdAsString());
        MystiGuardian.getEventDispatcher()
                .dispatchEvent(new ModerationActionTriggerEvent(MystiGuardianUtils.ModerationTypes.WARN, event.getApi(),
                        event.getServer().get().getIdAsString(), userObj.getIdAsString(), reasonStr, event.getUser().getIdAsString(), warnId));

        replyUtils.sendSuccess(STR. "Warned \{ userObj.getMentionTag() } for \{ reasonStr }" );
    }

    @NotNull
    @Override
    public String getName() {
        return "warn";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Warns a user";
    }

    @Override
    public List<SlashCommandOption> getOptions() {
        return List.of(
                SlashCommandOption.createUserOption("user", "The user to warn", true),
                SlashCommandOption.createStringOption("reason", "The reason for the warn", true)
        );
    }

    @Override
    public EnumSet<PermissionType> getRequiredPermissions() {
        return EnumSet.of(PermissionType.KICK_MEMBERS);
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
