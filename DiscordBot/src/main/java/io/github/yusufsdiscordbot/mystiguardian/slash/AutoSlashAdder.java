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
package io.github.yusufsdiscordbot.mystiguardian.slash;

import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.logger;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.javacord.api.DiscordApi;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AutoSlashAdder extends SlashCommandsHandler {
    public AutoSlashAdder(DiscordApi api) {
        super(api);

        registerSlashCommands(loadCommands().stream()
                .map(clazz -> {
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (Exception e) {
                        logger.error(MystiGuardianUtils.formatString("Failed to load class %s", clazz.getName()), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        sendSlash();
    }

    @NotNull
    @Contract(" -> new")
    private List<Class<? extends ISlashCommand>> loadCommands() {
        try (ScanResult result = new ClassGraph().enableClassInfo().scan()) {
            return new ArrayList<>(result.getAllClasses()
                    .filter(classInfo -> classInfo.implementsInterface(ISlashCommand.class))
                    .loadClasses(ISlashCommand.class));
        }
    }
}
