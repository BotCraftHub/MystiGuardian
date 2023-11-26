plugins {
    id("java")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":DiscordBot"))
    implementation("org.javacord:javacord:3.8.0")
    implementation("io.github.realyusufismail:jconfig:1.1.1")

    // API
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")

    // Lombok (Compile-only, Annotation processor)
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    // Database
    implementation("org.jooq:jooq:3.18.7")
    implementation("org.jooq:jooq-meta:3.18.7")
    implementation("org.jooq:jooq-codegen:3.18.7")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
}

configurations { all { exclude(group = "org.slf4j", module = "slf4j-log4j12") } }

tasks {
    shadowJar {
        archiveClassifier.set("")
        manifest { attributes["Main-Class"] = "io.github.yusufsdiscordbot.mystiguardian.OAuth" }
    }
}
