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
    id("nu.studer.jooq")
}

var jConfig: JConfig? = null

if (file("./config.json").exists()) {
    jConfig = JConfig.build()
}

val dataSource =
    if (jConfig != null) if (jConfig!!.contains("dataSource")) jConfig!!["dataSource"] else null
    else null

dependencies {
    // JavaCord and related dependencies
    implementation("org.javacord:javacord:3.8.0")
    implementation("org.javacord:javacord-core:3.8.0")
    implementation("io.github.realyusufismail:jconfig:1.1.1")
    implementation("io.github.classgraph:classgraph:4.8.161")
    implementation("net.fellbaum:jemoji:1.3.2")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("ch.qos.logback:logback-core:1.4.11")
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
    implementation("com.google.guava:guava:32.1.3-jre")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.9.3"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

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

sourceSets { main { java { srcDir("src/main/jooq") } } }