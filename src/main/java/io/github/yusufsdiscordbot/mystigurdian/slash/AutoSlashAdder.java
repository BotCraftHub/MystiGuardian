package io.github.yusufsdiscordbot.mystigurdian.slash;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.javacord.api.DiscordApi;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.github.yusufsdiscordbot.mystigurdian.utils.MystiGurdianUtils.logger;

public class AutoSlashAdder extends SlashCommandsHandler {
    public AutoSlashAdder(DiscordApi api) {
        super(api);

        registerSlashCommands(loadCommands().stream()
                .map(clazz -> {
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (Exception e) {
                        logger.error(STR."Failed to load class \{clazz.getName()}", e);
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
