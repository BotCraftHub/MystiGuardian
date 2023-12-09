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
package io.github.yusufsdiscordbot.mystiguardian.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;
import org.jetbrains.annotations.NotNull;

public class LoggerColorScheme extends ForegroundCompositeConverterBase<ILoggingEvent> {
    @Override
    protected String getForegroundColorCode(@NotNull ILoggingEvent event) {
        return switch (event.getLevel().levelInt) {
            case Level.DEBUG_INT -> ANSIConstants.BLUE_FG;
            case Level.INFO_INT -> ANSIConstants.GREEN_FG;
            case Level.WARN_INT -> ANSIConstants.YELLOW_FG;
            case Level.ERROR_INT -> ANSIConstants.RED_FG;
            case Level.TRACE_INT -> ANSIConstants.CYAN_FG;
            default -> ANSIConstants.DEFAULT_FG;
        };
    }
}
