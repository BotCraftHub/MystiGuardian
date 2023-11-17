import io.github.realyusufismail.jconfig.JConfig
import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath("org.postgresql:postgresql:42.6.0")
        classpath("io.github.realyusufismail:jconfig:1.1.1")
    }
}

plugins {
    id("java")
    id("com.diffplug.spotless") version "6.22.0"
    id("nu.studer.jooq") version "8.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "io.github.yusufsdiscordbot"

version = "1.0-SNAPSHOT"

var jConfig: JConfig? = null

if (file("config.json").exists()) {
    jConfig = JConfig.build()
}

val dataSource =
    if (jConfig != null) if (jConfig!!.contains("dataSource")) jConfig!!["dataSource"] else null
    else null

repositories { mavenCentral() }

dependencies {
    // JavaCord and related dependencies
    implementation("org.javacord:javacord:3.8.0")
    implementation("org.javacord:javacord-core:3.8.0")
    implementation("io.github.realyusufismail:jconfig:1.1.1")
    implementation("io.github.classgraph:classgraph:4.8.161")
    implementation("net.fellbaum:jemoji:1.3.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("ch.qos.logback:logback-core:1.4.7")
    implementation("uk.org.lidalia:sysout-over-slf4j:1.0.2")

    // Lombok (Compile-only, Annotation processor)
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // jOOQ and PostgreSQL
    implementation("org.jooq:jooq:3.18.7")
    implementation("org.jooq:jooq-meta:3.18.7")
    implementation("org.jooq:jooq-codegen:3.18.7")
    implementation("org.postgresql:postgresql:42.6.0")

    // jOOQ Generator with PostgreSQL
    jooqGenerator("org.postgresql:postgresql:42.6.0")

    // Database Connection Pool
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Google Guava
    implementation("com.google.guava:guava:31.1-jre")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

configurations {
    all { exclude(group = "org.slf4j", module = "slf4j-log4j12") }
    compileOnly { extendsFrom(configurations.annotationProcessor.get()) }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
}

java {
    withSourcesJar()
    withJavadocJar()
}

java.sourceCompatibility = JavaVersion.VERSION_19

java.targetCompatibility = JavaVersion.VERSION_19

/*
tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-preview")
}
 */

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
        palantirJavaFormat()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
        licenseHeader(
            """/*
 * Copyright 2023 RealYusufIsmail.
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

jooq {
    version.set("3.18.7")
    edition.set(JooqEdition.OSS)
    configurations {
        create("jooqGenerator") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"

                    if (dataSource != null) {
                        url = dataSource.get("url").asText() ?: ""
                        user = dataSource.get("user").asText() ?: ""
                        password = dataSource.get("password").asText() ?: ""
                    }
                }

                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = ".*"
                        excludes = ""
                        forcedTypes.addAll(
                            listOf(
                                ForcedType().apply {
                                    name = "varchar"
                                    includeExpression = ".*"
                                    includeTypes = "JSONB?"
                                },
                                ForcedType().apply {
                                    name = "OFFSETDATETIME"
                                    includeExpression = ".*"
                                    includeTypes = "TIMESTAMP"
                                }))
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "io.github.yusufsdiscordbot.mystigurdian.db"
                        directory = "src/main/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest {
            attributes["Main-Class"] = "io.github.yusufsdiscordbot.mystiguardian.MystiGuardian"
        }
    }
}

sourceSets { main { java { srcDir("src/main/jooq") } } }
