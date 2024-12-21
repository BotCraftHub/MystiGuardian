import java.util.*
import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

buildscript {
    repositories { mavenCentral() }
    dependencies { classpath("org.postgresql:postgresql:42.6.0") }
}

plugins {
    id("java")
    id("nu.studer.jooq")
}

dependencies {
    // JavaCord and related dependencies
    implementation("net.dv8tion:JDA:5.2.1")
    implementation("io.github.realyusufismail:jconfig:1.1.2")
    implementation("io.github.classgraph:classgraph:4.8.171")
    implementation("net.fellbaum:jemoji:1.4.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("ch.qos.logback:logback-core:1.5.13")
    implementation("uk.org.lidalia:sysout-over-slf4j:1.0.2")

    // Lombok (Compile-only, Annotation processor) and Annotations
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    compileOnly(project(":Annotations"))
    annotationProcessor(project(":Annotations"))

    // Lombok (Test-only, Annotation processor)
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // jOOQ and PostgreSQL
    implementation("org.jooq:jooq:3.19.8")
    implementation("org.jooq:jooq-meta:3.19.8")
    implementation("org.jooq:jooq-codegen:3.19.7")
    implementation("org.postgresql:postgresql:42.7.3")

    // jOOQ Generator with PostgreSQL
    jooqGenerator("org.postgresql:postgresql:42.7.3")

    // Database Connection Pool
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Google
    implementation("com.google.guava:guava:33.3.0-jre")
    implementation("com.google.apis:google-api-services-sheets:v4-rev614-1.18.0-rc")
    implementation("com.google.api-client:google-api-client:2.7.1")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.37.0")

    // (Querying API Requests)
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("com.github.serpapi:google-search-results-java:2.0.3:sources")

    // Oshi (System Information)
    implementation("com.github.oshi:oshi-core:6.6.1")
}

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

jooq {
    version.set("3.19.8")
    edition.set(JooqEdition.OSS)
    configurations {
        create("jooqGenerator") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"

                    val dataSourceUrl: String? =
                        if (project.findProperty("dataSourceUrl") != null)
                            project.property("dataSourceUrl") as String
                        else null
                    val dataSourceUser: String? =
                        if (project.findProperty("dataSourceUser") != null)
                            project.property("dataSourceUser") as String
                        else null
                    val dataSourcePassword: String? =
                        if (project.findProperty("dataSourcePassword") != null)
                            project.property("dataSourcePassword") as String
                        else null

                    if (dataSourceUrl != null &&
                        dataSourceUser != null &&
                        dataSourcePassword != null) {
                        url = dataSourceUrl
                        user = dataSourceUser
                        password = dataSourcePassword
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
                        packageName = "io.github.yusufsdiscordbot.mystiguardian.db"
                        directory = "src/main/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

sourceSets { main { java { srcDir("src/main/jooq") } } }

tasks.jar {
    val manifestClasspath = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    manifest {
        attributes(
            "Implementation-Title" to "DiscordBot",
            "Implementation-Version" to "1.0-SNAPSHOT",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to Date(),
            "Built-JDK" to System.getProperty("java.version"),
            "Built-Gradle" to gradle.gradleVersion,
            "Class-Path" to manifestClasspath)
    }
}
