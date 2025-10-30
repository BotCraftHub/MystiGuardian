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
    alias(libs.plugins.jooq)
}

dependencies {
    // JDA and related dependencies
    implementation(libs.jda)
    implementation(libs.jconfig)
    implementation(libs.classgraph)
    implementation(libs.jemoji)

    // Logging
    implementation(libs.bundles.logging)
    implementation(libs.sysout.over.slf4j)

    // Lombok (Compile-only, Annotation processor) and Annotations
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(project(":Annotations"))
    annotationProcessor(project(":Annotations"))

    // ApprenticeshipScraper module
    implementation(project(":ApprenticeshipScraper"))

    // Lombok (Test-only, Annotation processor)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // jOOQ and PostgreSQL
    implementation(libs.bundles.database)

    // jOOQ Generator with PostgreSQL
    jooqGenerator(libs.postgresql)

    // Google
    implementation(libs.guava)
    implementation(libs.bundles.google)

    // (Querying API Requests)
    implementation(libs.bundles.http.scraping)
    implementation(libs.serpapi)

    // Oshi (System Information)
    implementation(libs.oshi.core)
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
