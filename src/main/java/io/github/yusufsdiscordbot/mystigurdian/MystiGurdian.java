import io.github.realyusufismail.jconfig.util.JConfigUtils;
import io.github.yusufsdiscordbot.mystigurdian.slash.AutoSlashAdder;
import io.github.yusufsdiscordbot.mystigurdian.utils.MystiGurdianUtils;
import lombok.val;
import org.javacord.api.DiscordApiBuilder;

void main() {
    val token = JConfigUtils.getString("token");

    if (token == null) {
        MystiGurdianUtils.logger.error("Token is null, exiting...");
        return;
    }

    val api = new DiscordApiBuilder().setToken(token).login().join();

    try {
        new AutoSlashAdder(api);
    } catch (Exception e) {
        MystiGurdianUtils.logger.error("Failed to load slash commands", e);
    }
}