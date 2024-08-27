import groovy.json.JsonSlurper
import org.gradle.api.internal.artifacts.dsl.dependencies.DependenciesExtensionModule.module

plugins {
    id("java")
    id("com.diffplug.spotless") version "6.22.0"
    id("nu.studer.jooq") version "8.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    idea
}

group = "io.github.yusufsdiscordbot"
version = "0.0.4"


allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    configurations {
        all { exclude(group = "org.slf4j", module = "slf4j-log4j12") }
    }
}

// Use the root project to store shared properties
val rootProject = project.rootProject

val configFile = file("config.json")

if (file("config.json").exists()) {
    val configJson = JsonSlurper().parseText(configFile.readText()) as Map<*, *>

    val dataSource = configJson["dataSource"] as? Map<*, *>

    if (dataSource != null) {
        rootProject.extra["dataSourceUrl"] = dataSource["url"] ?: ""
        rootProject.extra["dataSourceUser"] = dataSource["user"] ?: ""
        rootProject.extra["dataSourcePassword"] = dataSource["password"] ?: ""
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":OAuth", "shadow"))
}


subprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "java")

    tasks.test {
        useJUnitPlatform()
        jvmArgs("--enable-preview")
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    java.sourceCompatibility = JavaVersion.VERSION_21

    java.targetCompatibility = JavaVersion.VERSION_21


    spotless {
        kotlinGradle {
            target("**/*.gradle.kts")
            ktfmt("0.42").dropboxStyle()
            trimTrailingWhitespace()
            indentWithSpaces()
            endWithNewline()
        }

        java {
            target("**/*.java")
            targetExclude("src/main/jooq/**/*.java")
            googleJavaFormat()

            trimTrailingWhitespace()
            endWithNewline()

            indentWithTabs(2)
            indentWithSpaces(4)


            licenseHeader(
                """/*
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
 */ """)
        }
    }
}


tasks.withType<JavaCompile> { options.compilerArgs.add("--enable-preview") }

tasks.withType<JavaExec> { jvmArgs("--enable-preview") }

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}