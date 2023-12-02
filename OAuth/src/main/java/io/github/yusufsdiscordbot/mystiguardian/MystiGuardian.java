package io.github.yusufsdiscordbot.mystiguardian;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.Getter;
import lombok.val;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.jConfig;

public class MystiGuardian {
    @Getter
    private static MystiGuardianConfig mystiGuardian;

    public static void main(String[] args) throws IOException {
        System.out.println("online");

        try {
            val token = jConfig.get("token") == null ? null : jConfig.get("token").asText();

            mystiGuardian = new MystiGuardianConfig();

            mystiGuardian.setAPI(new DiscordApiBuilder()
                    .setToken(token)
                    .login()
                    .join());

            mystiGuardian.handleRegistrations(mystiGuardian.getApi());

            mystiGuardian.handleConfig();
        } catch (Exception e) {
            MystiGuardianUtils.discordAuthLogger.error("Error while handling registrations for Bot", e);
        }

        try {
            new OAuth();
        } catch (Exception e) {
            MystiGuardianUtils.discordAuthLogger.error("Error while handling registrations for OAuth", e);
        }
    }
}
