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
package io.github.yusufsdiscordbot.mystiguardian.event.generic;

/**
 * Defines a contract for the handler of generic subscription events.
 *
 * <p>
 * An implementer of this interface acts as the receiver for an event-based subscription mechanism. A class
 * implementing this interface should define how it handles the occurrence of a subscription event in an
 * Event-Driven Architecture system.
 * </p>
 *
 * <p>
 * When a generic subscription event takes place, the handler provided by the implementing object
 * is triggered according to the specifications laid out by the implementer.
 * </p>
 *
 * <p>
 * This is particularly useful in scenarios where the specifics of the subscription event don't affect
 * how the system should react, and allows the system's behavior to be adjusted dynamically at runtime
 * via different implementations of this interface.
 * </p>
 */
@FunctionalInterface
public interface GenericSubscribeEventHandler {
    void onGenericEvent(GenericSubscribeEvent event);
}
