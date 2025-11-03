import java.util.*

buildscript { repositories { mavenCentral() } }

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

tasks.jar {
    val manifestClasspath = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
    manifest {
        attributes(
            "Implementation-Title" to "Annotations",
            "Implementation-Version" to "1.0-SNAPSHOT",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to Date(),
            "Built-JDK" to System.getProperty("java.version"),
            "Built-Gradle" to gradle.gradleVersion,
            "Class-Path" to manifestClasspath)
    }
}

tasks.shadowJar {
    archiveBaseName.set("Annotations")
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes(
            "Implementation-Title" to "Annotations",
            "Implementation-Version" to "1.0-SNAPSHOT",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to Date(),
            "Built-JDK" to System.getProperty("java.version"),
            "Built-Gradle" to gradle.gradleVersion)
    }
}
