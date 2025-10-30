# Quick Reference: Version Catalog Usage

## File Location
`gradle/libs.versions.toml`

## Common Patterns

### Single Dependencies
```kotlin
implementation(libs.jda)
implementation(libs.okhttp)
implementation(libs.lombok)
compileOnly(libs.lombok)
annotationProcessor(libs.lombok)
```

### Bundles
```kotlin
implementation(libs.bundles.logging)     // logback-classic, logback-core, slf4j-api
implementation(libs.bundles.database)    // jOOQ, PostgreSQL, HikariCP
implementation(libs.bundles.google)      // All Google API dependencies
implementation(libs.bundles.http.scraping) // OkHttp, Jackson, Jsoup
testImplementation(libs.bundles.testing) // JUnit, Mockito
```

### Plugins
```kotlin
plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.shadow)
    alias(libs.plugins.jooq)
}
```

## Naming Convention

### In libs.versions.toml → In build.gradle.kts
- `jda` → `libs.jda`
- `google-sheets-api` → `libs.google.sheets.api`
- `sysout-over-slf4j` → `libs.sysout.over.slf4j`
- `java-jwt` → `libs.java.jwt`

**Rule:** Hyphens (-) become dots (.)

## Update a Version
1. Edit `gradle/libs.versions.toml`
2. Find version in `[versions]` section
3. Change number
4. Sync Gradle

Example:
```toml
[versions]
jda = "6.0.0"  # Change to "6.1.0"
```

## Add New Dependency
1. Add version:
```toml
[versions]
newlib = "1.0.0"
```

2. Add library:
```toml
[libraries]
newlib = { module = "com.example:newlib", version.ref = "newlib" }
```

3. Use it:
```kotlin
implementation(libs.newlib)
```

## Current Key Versions
- Java: 21
- JDA: 6.0.0
- Lombok: 1.18.34
- jOOQ: 3.19.8
- PostgreSQL: 42.7.3
- Logback: 1.5.13

See `gradle/libs.versions.toml` for complete list.

