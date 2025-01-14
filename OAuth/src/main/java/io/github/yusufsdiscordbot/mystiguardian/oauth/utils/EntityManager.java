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
package io.github.yusufsdiscordbot.mystiguardian.oauth.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.yusufsdiscordbot.mystiguardian.oauth.entites.impl.OAuthGuildImpl;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import lombok.val;
import net.dv8tion.jda.api.Permission;

public class EntityManager {

    public static String getGuildsThatUserCanManage(String guilds) throws JsonProcessingException {
        val jsonNode = MystiGuardianUtils.objectMapper.readTree(guilds);

        val guildsThatUserCanManage = MystiGuardianUtils.objectMapper.createArrayNode();

        for (val guild : jsonNode) {
            val guildObject = new OAuthGuildImpl(guild);

            val usersPerms = guildObject.getPermissions();

            // need to convert it to a bigint or similar, and then use the bitwise AND operation with the
            // Manage Server
            // bit
            val canManage =
                    (usersPerms & Permission.MANAGE_SERVER.getRawValue())
                            == Permission.MANAGE_SERVER.getRawValue();

            if (canManage) {
                guildsThatUserCanManage.add(guildObject.getJson());
            }
        }

        return guildsThatUserCanManage.toString();
    }
}
