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
package io.github.yusufsdiscordbot.mystiguardian.api.entities;

import com.fasterxml.jackson.databind.JsonNode;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.val;
import org.javacord.api.DiscordApi;
import org.javacord.api.Javacord;
import org.javacord.api.entity.DiscordClient;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserFlag;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.core.entity.IconImpl;
import org.javacord.core.listener.user.InternalUserAttachableListenerManager;

public class MystiUserImpl implements User, InternalUserAttachableListenerManager {

    private final JsonNode json;

    public MystiUserImpl(JsonNode json) {
        this.json = json;
    }

    @Override
    public String getDiscriminator() {
        return json.get("discriminator").asText();
    }

    @Override
    public boolean isBot() {
        return json.get("bot").asBoolean();
    }

    @Override
    public Set<Activity> getActivities() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public UserStatus getStatus() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public UserStatus getStatusOnClient(DiscordClient client) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public EnumSet<UserFlag> getUserFlags() {
        val publicFlags = json.get("public_flags").asInt();
        val userFlags = EnumSet.noneOf(UserFlag.class);

        for (UserFlag flag : UserFlag.values()) {
            if ((flag.asInt() & publicFlags) == flag.asInt()) {
                userFlags.add(flag);
            }
        }

        return userFlags;
    }

    @Override
    public Optional<String> getAvatarHash() {
        return Optional.ofNullable(json.get("avatar").asText());
    }

    @Override
    public Icon getAvatar() {
        StringBuilder url = getUserAvatar();
        url.append("?size=").append(1024);
        try {
            return new IconImpl(null, new URL(url.toString()));
        } catch (MalformedURLException e) {
            throw new AssertionError(
                    "An issue occurred while creating the avatar URL, either update to the latest version of the library or report this issue to the developer.");
        }
    }

    @Override
    public Icon getAvatar(int size) {
        StringBuilder url = getUserAvatar();
        url.append("?size=").append(size);
        try {
            return new IconImpl(null, new URL(url.toString()));
        } catch (MalformedURLException e) {
            throw new AssertionError(
                    "An issue occurred while creating the avatar URL, either update to the latest version of the library or report this issue to the developer.");
        }
    }

    private StringBuilder getUserAvatar() {
        StringBuilder url = new StringBuilder("https://" + Javacord.DISCORD_CDN_DOMAIN + "/");
        if (getAvatarHash().isEmpty()) {
            url.append("embed/avatars/")
                    .append(Integer.parseInt(getDiscriminator()) % 5)
                    .append(".png");
        } else {
            url.append("avatars/")
                    .append(getId())
                    .append('/')
                    .append(getAvatarHash())
                    .append(getAvatarHash().get().startsWith("a_") ? ".gif" : ".png");
        }

        return url;
    }

    @Override
    public Optional<String> getServerAvatarHash(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<Icon> getServerAvatar(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<Icon> getServerAvatar(Server server, int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Icon getEffectiveAvatar(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Icon getEffectiveAvatar(Server server, int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean hasDefaultAvatar() {
        return getAvatarHash().isEmpty();
    }

    @Override
    public Set<Server> getMutualServers() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getDisplayName(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<String> getNickname(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<Instant> getServerBoostingSinceTimestamp(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<Instant> getTimeout(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isPending(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isSelfMuted(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isSelfDeafened(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<Instant> getJoinedAtTimestamp(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Role> getRoles(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<Color> getRoleColor(Server server) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<PrivateChannel> getPrivateChannel() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public CompletableFuture<PrivateChannel> openPrivateChannel() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public DiscordApi getApi() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long getId() {
        return json.get("id").asLong();
    }

    @Override
    public String getName() {
        return json.get("username").asText();
    }
}
