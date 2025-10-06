import java.util.*

plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":DiscordBot"))
    compileOnly(project(":Annotations"))
    annotationProcessor(project(":Annotations"))

    implementation("net.dv8tion:JDA:6.0.0")
    implementation("io.github.realyusufismail:jconfig:1.1.2")

    // API
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78")

    // Lombok (Compile-only, Annotation processor)
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    // Lombok (Test-only, Annotation processor)
    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // Database
    implementation("org.jooq:jooq:3.19.8")
    implementation("org.jooq:jooq-meta:3.19.8")
    implementation("org.jooq:jooq-codegen:3.19.7")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Testing (JUnit 5) and Mocking
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testImplementation("org.mockito:mockito-core:5.13.0")
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
