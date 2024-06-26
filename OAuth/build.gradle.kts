import java.util.*

plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":DiscordBot"))
    implementation("org.javacord:javacord:3.8.0")
    implementation("io.github.realyusufismail:jconfig:1.1.2")

    // API
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78")

    // Lombok (Compile-only, Annotation processor)
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    // Database
    implementation("org.jooq:jooq:3.19.8")
    implementation("org.jooq:jooq-meta:3.19.8")
    implementation("org.jooq:jooq-codegen:3.19.7")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Testing (JUnit 5) and Mocking
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.javacord:javacord:3.8.0")
    testImplementation("org.javacord:javacord-core:3.8.0")
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
