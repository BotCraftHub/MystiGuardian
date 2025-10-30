import java.util.*

plugins {
    id("java")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":DiscordBot"))
    compileOnly(project(":Annotations"))
    annotationProcessor(project(":Annotations"))
    implementation(project(":ApprenticeshipScraper"))

    implementation(libs.jda)
    implementation(libs.jconfig)

    // API
    implementation(libs.okhttp)
    implementation(libs.spark.core)
    implementation(libs.java.jwt)
    implementation(libs.bouncycastle)

    // Lombok (Compile-only, Annotation processor)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Lombok (Test-only, Annotation processor)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // Database
    implementation(libs.bundles.database)

    // Testing (JUnit 5) and Mocking
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.testing)
}

configurations { all { exclude(group = "org.slf4j", module = "slf4j-log4j12") } }

tasks {
    shadowJar {
        archiveBaseName.set("MystiGuardian")
        manifest {
            attributes(
                "Main-Class" to "io.github.yusufsdiscordbot.mystiguardian.MystiGuardian",
                "Implementation-Title" to "MystiGuardian",
                "Implementation-Version" to "1.0.0",
                "Built-By" to System.getProperty("user.name"),
                "Built-Date" to Date(),
                "Built-JDK" to System.getProperty("java.version"),
                "Built-Gradle" to gradle.gradleVersion)
        }
    }
}
