import java.util.*

buildscript { repositories { mavenCentral() } }

plugins { id("java") }

dependencies {
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("ch.qos.logback:logback-core:1.5.13")
    implementation("org.slf4j:slf4j-api:2.0.7")

    // Lombok (Compile-only, Annotation processor)
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // Google Sheets API
    implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.20.0")

    // HTTP Client and Web Scraping
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("org.jsoup:jsoup:1.18.1")

    // JDA (for Discord integration - embeds)
    implementation("net.dv8tion:JDA:6.0.0")

    // JetBrains Annotations
    implementation("org.jetbrains:annotations:24.0.0")
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

