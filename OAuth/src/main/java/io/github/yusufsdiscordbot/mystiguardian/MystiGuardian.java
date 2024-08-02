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
package io.github.yusufsdiscordbot.mystiguardian;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.jConfig;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.realyusufismail.jconfig.JConfig;
import io.github.yusufsdiscordbot.mystiguardian.oauth.OAuth;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import lombok.val;
import org.javacord.api.DiscordApiBuilder;

public class MystiGuardian {
    @Getter private static MystiGuardianConfig mystiGuardian;

    public static void main(String[] args) throws IOException {
        System.out.println("online");

        try {
            jConfig = JConfig.builder().setDirectoryPath("./").build();

            val token =
                    Optional.ofNullable(jConfig.get("token"))
                            .map(JsonNode::asText)
                            .orElseThrow(() -> new IllegalArgumentException("Token not found in config"));

            mystiGuardian = new MystiGuardianConfig();

            val api =
                    new DiscordApiBuilder()
                            .setToken(token)
                            .login()
                            .exceptionally(
                                    e -> {
                                        MystiGuardianUtils.discordAuthLogger.error("Error while logging in", e);
                                        return null;
                                    })
                            .join();

            mystiGuardian.setAPI(api);
            mystiGuardian.handleRegistrations();
            mystiGuardian.handleConfig();

        } catch (Exception e) {
            MystiGuardianUtils.discordAuthLogger.error("Error while handling registrations for Bot", e);
        }

        try {
            OAuth.runOAuth();
        } catch (Exception e) {
            MystiGuardianUtils.discordAuthLogger.error("Error while handling registrations for OAuth", e);
        }
    }
}
