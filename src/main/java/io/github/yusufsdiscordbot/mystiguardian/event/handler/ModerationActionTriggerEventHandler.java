package io.github.yusufsdiscordbot.mystiguardian.event.handler;

import io.github.yusufsdiscordbot.mystiguardian.event.events.ModerationActionTriggerEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.generic.GenericSubscribeEvent;
import io.github.yusufsdiscordbot.mystiguardian.event.generic.GenericSubscribeEventHandler;

public interface ModerationActionTriggerEventHandler extends GenericSubscribeEventHandler {

    void onModerationActionTriggerEvent(ModerationActionTriggerEvent event);

    @Override
    default void onGenericEvent(GenericSubscribeEvent event) {
        if (event instanceof ModerationActionTriggerEvent) {
            onModerationActionTriggerEvent((ModerationActionTriggerEvent) event);
        }
    }
}
