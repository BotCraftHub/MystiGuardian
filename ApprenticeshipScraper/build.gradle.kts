import java.util.*

buildscript { repositories { mavenCentral() } }

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

dependencies {
    // Logging
    implementation(libs.bundles.logging)

    // Lombok (Compile-only, Annotation processor)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Google Sheets API
    implementation(libs.bundles.google)

    // HTTP Client and Web Scraping
    implementation(libs.bundles.http.scraping)

    // JDA (for Discord integration - embeds)
    implementation(libs.jda)

    // JetBrains Annotations
    implementation(libs.jetbrains.annotations)
}

tasks.jar {
    val manifestClasspath = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    manifest {
        attributes(
            "Implementation-Title" to "ApprenticeshipScraper",
            "Implementation-Version" to "1.0-SNAPSHOT",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to Date(),
            "Built-JDK" to System.getProperty("java.version"),
            "Built-Gradle" to gradle.gradleVersion,
            "Class-Path" to manifestClasspath)
    }
}

tasks.shadowJar {
    archiveBaseName.set("ApprenticeshipScraper")
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes(
            "Implementation-Title" to "ApprenticeshipScraper",
            "Implementation-Version" to "1.0-SNAPSHOT",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to Date(),
            "Built-JDK" to System.getProperty("java.version"),
            "Built-Gradle" to gradle.gradleVersion)
    }
}
