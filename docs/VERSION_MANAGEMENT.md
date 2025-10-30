# Version Management Guide

## Overview
This project uses Gradle Version Catalogs for centralized dependency version management across all modules. This prevents version conflicts and makes it easy to update dependencies consistently.

## Version Catalog Location
The version catalog is located at: `gradle/libs.versions.toml`

## Structure

### 1. Versions Section
All dependency versions are defined once in the `[versions]` section:

```toml
[versions]
jda = "6.0.0"
lombok = "1.18.34"
logback = "1.5.13"
# ... etc
```

### 2. Libraries Section
Individual dependencies reference versions using `version.ref`:

```toml
[libraries]
jda = { module = "net.dv8tion:JDA", version.ref = "jda" }
lombok = { module = "org.projectlombok:lombok", version.ref = "lombok" }
```

### 3. Bundles Section
Related dependencies can be grouped into bundles:

```toml
[bundles]
logging = ["logback-classic", "logback-core", "slf4j-api"]
database = ["jooq", "jooq-meta", "jooq-codegen", "postgresql", "hikaricp"]
```

### 4. Plugins Section
Build plugins are also managed centrally:

```toml
[plugins]
spotless = { id = "com.diffplug.spotless", version.ref = "spotless" }
shadow = { id = "com.github.johnrengelman.shadow", version.ref = "shadowPlugin" }
```

## Usage in build.gradle.kts Files

### Using Individual Libraries
```kotlin
dependencies {
    implementation(libs.jda)
    compileOnly(libs.lombok)
    implementation(libs.okhttp)
}
```

### Using Bundles
```kotlin
dependencies {
    implementation(libs.bundles.logging)
    implementation(libs.bundles.database)
    implementation(libs.bundles.google)
}
```

### Using Plugins
```kotlin
plugins {
    id("java")
    alias(libs.plugins.spotless)
    alias(libs.plugins.shadow)
}
```

## Naming Convention

### In libs.versions.toml
- Use **hyphens** for multi-word keys: `google-sheets-api`, `sysout-over-slf4j`
- Version refs use camelCase: `version.ref = "googleSheetsApi"`

### In build.gradle.kts
- Access with **dots**: `libs.google.sheets.api`
- Hyphens become dots: `google-sheets-api` → `libs.google.sheets.api`
- Underscores also become dots: `sysout_over_slf4j` → `libs.sysout.over.slf4j`

## Benefits

### 1. Single Source of Truth
All versions are defined in one place (`libs.versions.toml`), making it easy to:
- See all dependencies and their versions at a glance
- Update a version once and have it apply everywhere
- Prevent version conflicts between modules

### 2. Type-Safe Accessors
Gradle generates type-safe accessors, providing:
- IDE auto-completion
- Compile-time validation
- Refactoring support

### 3. Centralized Plugin Management
Build plugins are also versioned centrally, ensuring consistency across:
- Root project
- All submodules

### 4. Easy Maintenance
To update a dependency:
1. Edit the version in `libs.versions.toml`
2. Sync Gradle
3. All modules automatically use the new version

## Module-Specific Dependencies

### DiscordBot Module
- JDA and Discord-related dependencies
- Database (jOOQ, PostgreSQL, HikariCP)
- Google APIs
- Logging
- System monitoring (Oshi)

### ApprenticeshipScraper Module
- Google Sheets API
- Web scraping (OkHttp, Jsoup, Jackson)
- Logging
- JDA (for Discord embeds)

### OAuth Module
- Web framework (Spark)
- Authentication (JWT, Bouncycastle)
- Database
- Testing frameworks

### Annotations Module
- No external dependencies (only standard library)

## Updating Dependencies

### To Update a Single Dependency
1. Open `gradle/libs.versions.toml`
2. Find the version in the `[versions]` section
3. Update the version number
4. Save and sync Gradle

Example:
```toml
[versions]
jda = "6.0.0"  # Change to "6.1.0"
```

### To Add a New Dependency
1. Add the version in `[versions]`:
```toml
[versions]
newLib = "1.0.0"
```

2. Add the library in `[libraries]`:
```toml
[libraries]
new-lib = { module = "com.example:new-lib", version.ref = "newLib" }
```

3. Use in build.gradle.kts:
```kotlin
dependencies {
    implementation(libs.new.lib)
}
```

### To Create a New Bundle
1. Add to `[bundles]` section:
```toml
[bundles]
my-bundle = ["lib1", "lib2", "lib3"]
```

2. Use in build.gradle.kts:
```kotlin
dependencies {
    implementation(libs.bundles.my.bundle)
}
```

## Troubleshooting

### IDE Not Recognizing libs
1. Click "Sync Gradle" in your IDE
2. Invalidate caches and restart IDE if needed
3. Ensure `gradle/libs.versions.toml` exists and is properly formatted

### Version Conflict
If you see a version conflict:
1. Check all modules are using the version catalog
2. Search for any hardcoded versions (e.g., `"1.0.0"` strings)
3. Update the centralized version in `libs.versions.toml`

### Build Fails After Update
1. Check the changelog of the updated dependency
2. Look for breaking changes
3. Update code if necessary
4. Ensure all related dependencies are compatible

## Best Practices

1. **Always use the version catalog** - Never hardcode versions in build.gradle.kts files
2. **Group related dependencies** - Use bundles for dependencies that are always used together
3. **Keep versions up-to-date** - Regularly check for updates to dependencies
4. **Test after updates** - Always run tests after updating dependencies
5. **Document breaking changes** - Add notes in CHANGELOG.md when updating major versions

## Migration from Hardcoded Versions

If you find hardcoded versions in build.gradle.kts files:
1. Extract the version to `libs.versions.toml`
2. Replace the hardcoded dependency with the catalog reference
3. Sync Gradle and verify the build works
4. Commit the changes

Example:
```kotlin
// Before
implementation("net.dv8tion:JDA:6.0.0")

// After
implementation(libs.jda)
```

## Current Managed Dependencies

### Core
- Java 21
- JDA 6.0.0
- Lombok 1.18.34

### Database
- jOOQ 3.19.8
- PostgreSQL 42.7.3
- HikariCP 5.1.0

### Logging
- Logback 1.5.13
- SLF4J 2.0.7

### Google APIs
- Sheets API v4-rev20220927-2.0.0
- API Client 2.2.0
- OAuth Client 1.34.1
- Auth Library 1.20.0

### Build Plugins
- Spotless 6.22.0
- jOOQ Plugin 8.1
- Shadow Plugin 8.1.1

For a complete list, see `gradle/libs.versions.toml`.

