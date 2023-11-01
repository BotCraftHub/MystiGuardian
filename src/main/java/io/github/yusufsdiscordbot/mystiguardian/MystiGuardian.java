package io.github.yusufsdiscordbot.mystiguardian;

import io.github.yusufsdiscordbot.mystiguardian.database.DatabaseTables;
import io.github.yusufsdiscordbot.mystiguardian.database.MystiGuardianDatabase;
import io.github.yusufsdiscordbot.mystiguardian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystiguardian.slash.SlashCommandsHandler;
import lombok.Getter;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.jooq.DSLContext;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;

import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.BanAuditCommand.sendBanAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.KickAuditCommand.sendKickAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.ReloadAuditCommand.sendReloadAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.TimeOutAuditCommand.sendTimeOutAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.commands.audit.type.WarnAuditCommand.sendWarnAuditRecordsEmbed;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.*;

public class MystiGuardian {
    public static Instant startTime;
    public static Future<?> mainThread;
    private SlashCommandsHandler slashCommandsHandler;
    private Long reloadChannelId;
    @Getter
    private static MystiGuardianDatabase database;
    private boolean reloading = false;
    @Getter
    private static DSLContext context;

    @SuppressWarnings("unused")
    public MystiGuardian() {
    }

    public MystiGuardian(Long reloadChannelId) {
        this.reloadChannelId = reloadChannelId;
        this.reloading = true;
    }

    public void main() {
        mainThread = getExecutorService().submit(this::run);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down...");

            try {
                if (database != null) {
                    database.getDs().getConnection().close();
                } else {
                    logger.warn("Database is null");
                }
            } catch (SQLException e) {
                logger.error("Failed to close database connection", e);
            }

            mainThread.cancel(true);

            logger.info("Shutdown complete");
        }));
    }

    public void run() {
        val token = Objects.requireNonNull(jConfig.get("token")).asText();

        if (token == null) {
            logger.error("Token is null, exiting...");
            return;
        }

        val api = new DiscordApiBuilder().setToken(token).login()
                .join();

        logger.info(STR."Logged in as \{api.getYourself().getName()}");
        startTime = Instant.now();
        val ownerId = Objects.requireNonNull(jConfig.get("owner-id")).asText();

        if (reloading) {
            if (api.getUserById(ownerId) != null) {
                Optional.ofNullable(api.getUserById(ownerId).join()).ifPresentOrElse(user -> {
                    user.openPrivateChannel().join().sendMessage("Reloaded successfully").join();
                }, () -> api.getChannelById(reloadChannelId).ifPresentOrElse(channel -> channel.asTextChannel().ifPresentOrElse(textChannel -> {
                    textChannel.sendMessage("Reloaded successfully").join();
                }, () -> logger.error("Reload channel is not a text channel")), () -> logger.error("Reload channel does not exist")));
            }
        }

        api.updateActivity(ActivityType.LISTENING, "to your commands");

        handleRegistrations(api);

        api.addSlashCommandCreateListener(slashCommandsHandler::onSlashCommandCreateEvent);
        api.addButtonClickListener(this::onButtonClickEvent);
    }


    private void handleRegistrations(DiscordApi api) {
        try {
            this.slashCommandsHandler = new AutoSlashAdder(api);
        } catch (Exception e) {
            logger.error("Failed to load slash commands", e);
            return;
        }

        try {
            database = new MystiGuardianDatabase();
            context = database.getContext();
            new DatabaseTables(database.getContext());
        } catch (Exception e) {
            logger.error("Failed to load database", e);
        }
    }

    private void onButtonClickEvent(ButtonClickEvent buttonClickEvent) {
        String customId = buttonClickEvent.getButtonInteraction().getCustomId();

        if (customId.startsWith("prev_") || customId.startsWith("next_")) {
            // Extract the currentIndex from the customId
            int currentIndex = Integer.parseInt(customId.split("_")[1]);

            if (customId.startsWith("prev_")) {
                // User clicked the "Previous" button
                currentIndex = Math.max(0, currentIndex - 1);
            } else if (customId.startsWith("next_")) {
                // User clicked the "Next" button
                currentIndex++;
            }

            buttonClickEvent.getButtonInteraction().getMessage().delete().join();

            val slashCommandName = customId.split("_")[2];

            if (slashCommandName.equals(PageNames.RELOAD_AUDIT.name())) {
                sendReloadAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex);
            } else if (slashCommandName.equals(PageNames.WARN_AUDIT.name())) {
                val userId = customId.split("_")[3];
                val user = buttonClickEvent.getApi().getUserById(userId).join();
                sendWarnAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
            } else if (slashCommandName.equals(PageNames.KICK_AUDIT.name())) {
                val userId = customId.split("_")[3];
                val user = buttonClickEvent.getApi().getUserById(userId).join();
                sendKickAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
            } else if (slashCommandName.equals(PageNames.BAN_AUDIT.name())) {
                val userId = customId.split("_")[3];
                val user = buttonClickEvent.getApi().getUserById(userId).join();
                sendBanAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
            } else if (slashCommandName.equals(PageNames.TIME_OUT_AUDIT.name())) {
                val userId = customId.split("_")[3];
                val user = buttonClickEvent.getApi().getUserById(userId).join();
                sendTimeOutAuditRecordsEmbed(buttonClickEvent.getButtonInteraction(), currentIndex, user);
            }

            // Acknowledge the button interaction
            buttonClickEvent.getButtonInteraction().createImmediateResponder().respond();
        } else if (customId.equals("delete")) {
            buttonClickEvent.getButtonInteraction().getMessage().delete().join();
        }
    }
}
