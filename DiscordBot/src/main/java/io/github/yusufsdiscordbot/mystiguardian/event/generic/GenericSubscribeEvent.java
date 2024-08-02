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
 * A representation of a generic subscription event in an Event-Driven Architecture system.
 *
 * <p>An object of a type implementing this interface serves as the catalyst for a series of actions
 * or updates the state in an Event-Driven Architecture system upon a subscription event. The
 * specifics of which actions should be taken or how the system state should be updated are
 * determined by the associated {@code GenericSubscribeEventHandler}.
 *
 * <p>This is particularly useful in scenarios where the specifics of the subscription event don't
 * affect the event's effect in the system - instead, all subscription events can be treated in the
 * same manner.
 */
public interface GenericSubscribeEvent {}
