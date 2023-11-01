package io.github.yusufsdiscordbot.mystiguardian.event;

import io.github.yusufsdiscordbot.mystiguardian.event.generic.GenericSubscribeEvent;

import java.util.HashMap;
import java.util.List;

/**
 * The {@code EventDispatcher} class serves as the pivotal point in an Event-Driven Architecture. It holds the
 * responsibility of delegating events to their suitable event handlers.
 *
 * <p>
 * Internally, it sustains a mapping of event types to their respective event handlers. This mapping ensures
 * that events are directed to the correct handlers efficiently. Such an approach supports the decentralization
 * and loose coupling of components, which are crucial elements of an Event-Driven Architecture.
 * </p>
 *
 * <p>
 * Event handlers for specific types of events can be registered using the {@code registerEventHandler} method.
 * This allows dynamic addition of event responses within the system. It is this function that ties an event
 * type to a specific sequence of operations encapsulated in an event handler.
 * </p>
 *
 * <p>
 * Dispatching of an event is executed via the {@code dispatchEvent} method. This method is responsible for
 * identifying the correct event handler(s) based on the event type. Upon finding the relevant handlers, it
 * invokes their associated handling method(s).
 * </p>
 *
 * Note: The handling method to be invoked is assumed to be {@code onOrderPlaced}. Developers may want to
 * adjust this based on their particular system's necessities.
 *
 * @see GenericSubscribeEvent
 * @see GenericSubscribeEventHandler
 */
public class EventDispatcher {
    private final Map<Class<? extends GenericSubscribeEvent>, List<GenericSubscribeEventHandler>> eventHandlers = new HashMap<>();

}
