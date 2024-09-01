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
package io.github.yusufsdiscordbot.mystiguardian.event.bus;

import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/** Processes the {@link SlashEventBus} annotation. */
@SupportedAnnotationTypes("io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class SlashEventBusProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(SlashEventBus.class)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + element);
            if (element.getModifiers().contains(Modifier.ABSTRACT)) {
                processingEnv
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR, "Abstract classes cannot be used with @SlashEventBus");
            }
        }
        return true;
    }
}
