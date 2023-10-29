import io.github.realyusufismail.jconfig.util.JConfigUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.logging.Logger;

private static final Logger logger = Logger.getLogger("MystiGurdian");

void main() {
    String token = JConfigUtils.getString("token");

    if (token == null) {
        logger.severe("Token is null! Please set the token in config.json!");
        return;
    }

    DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
}