/*
 * Copyright 2025 RealYusufIsmail.
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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Processes the {@link SlashEventBus} annotation.
 *
 * <p>This processor validates that annotated classes:
 *
 * <ul>
 *   <li>Are not abstract
 *   <li>Implement the ISlashCommand interface
 *   <li>Have a public no-args constructor (or no explicit constructor)
 * </ul>
 */
@SupportedAnnotationTypes("io.github.yusufsdiscordbot.mystiguardian.event.bus.SlashEventBus")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class SlashEventBusProcessor extends AbstractProcessor {

    /**
     * Default constructor for the annotation processor.
     */
    public SlashEventBusProcessor() {
        super();
    }

    private static final String ISLASH_COMMAND_INTERFACE =
            "io.github.yusufsdiscordbot.mystiguardian.slash.ISlashCommand";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(SlashEventBus.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                processingEnv
                        .getMessager()
                        .printMessage(
                                Diagnostic.Kind.ERROR, "@SlashEventBus can only be applied to classes", element);
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            processingEnv
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.NOTE,
                            "Processing @SlashEventBus on " + typeElement.getQualifiedName());

            validateClass(typeElement);
        }
        return true;
    }

    private void validateClass(TypeElement typeElement) {
        // Check if class is abstract
        if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
            processingEnv
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.ERROR,
                            "Class annotated with @SlashEventBus cannot be abstract. "
                                    + "Slash commands must be instantiable.",
                            typeElement);
        }

        // Check if class implements ISlashCommand
        if (!implementsInterface(typeElement, ISLASH_COMMAND_INTERFACE)) {
            processingEnv
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.ERROR,
                            "Class annotated with @SlashEventBus must implement ISlashCommand interface. "
                                    + "Found: "
                                    + typeElement.getQualifiedName(),
                            typeElement);
        }

        // Check for valid constructor
        if (!hasValidConstructor(typeElement)) {
            processingEnv
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.ERROR,
                            "Class annotated with @SlashEventBus must have a public no-args constructor. "
                                    + "Either provide one explicitly or remove all constructors to use the default.",
                            typeElement);
        }

        // Check if class is public
        if (!typeElement.getModifiers().contains(Modifier.PUBLIC)) {
            processingEnv
                    .getMessager()
                    .printMessage(
                            Diagnostic.Kind.WARNING,
                            "Class annotated with @SlashEventBus should be public for proper instantiation. "
                                    + "Non-public classes may not be accessible by the command registry.",
                            typeElement);
        }
    }

    private boolean implementsInterface(TypeElement typeElement, String interfaceName) {
        // Check direct interfaces
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            Element interfaceElement = processingEnv.getTypeUtils().asElement(interfaceType);
            if (interfaceElement instanceof TypeElement interfaceTypeElement) {
                if (interfaceTypeElement.getQualifiedName().toString().equals(interfaceName)) {
                    return true;
                }
            }
        }

        // Check superclass
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass != null) {
            Element superElement = processingEnv.getTypeUtils().asElement(superclass);
            if (superElement instanceof TypeElement superTypeElement) {
                // Don't check Object class
                if (!superTypeElement.getQualifiedName().toString().equals("java.lang.Object")) {
                    return implementsInterface(superTypeElement, interfaceName);
                }
            }
        }

        return false;
    }

    private boolean hasValidConstructor(TypeElement typeElement) {
        boolean hasConstructor = false;
        boolean hasValidNoArgsConstructor = false;

        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                hasConstructor = true;
                ExecutableElement constructor = (ExecutableElement) enclosedElement;

                // Check if it's a no-args constructor
                if (constructor.getParameters().isEmpty()) {
                    // Check if it's public
                    if (constructor.getModifiers().contains(Modifier.PUBLIC)) {
                        hasValidNoArgsConstructor = true;
                    }
                }
            }
        }

        // If no constructor is defined, Java provides a default public no-args constructor
        return !hasConstructor || hasValidNoArgsConstructor;
    }
}
