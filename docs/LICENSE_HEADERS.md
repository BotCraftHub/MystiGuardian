# License Header Management with Spotless

This project uses **Spotless** to automatically manage Apache License 2.0 headers on all Java source files.

## Automatic License Headers

All Java files in the project automatically include this header:

```java
/*
 * Copyright 2025 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
```

## Usage

### Check if files need license headers

```bash
./gradlew spotlessCheck
```

This will check if all files have proper formatting and license headers.

### Automatically add/fix license headers

```bash
./gradlew spotlessApply
```

This will:
- ✅ Add Apache License headers to files missing them
- ✅ Format Java code according to Google Java Style
- ✅ Fix trailing whitespace and line endings
- ✅ Format Kotlin Gradle files

### Before Committing

**Always run Spotless before committing:**

```bash
./gradlew spotlessApply
git add -A
git commit -m "your commit message"
```

Or check first:

```bash
./gradlew spotlessCheck
# If it passes, you're good to commit
# If it fails, run spotlessApply
```

## CI/CD Integration

GitHub Actions automatically runs `spotlessCheck` on all pull requests. If it fails, you must run `spotlessApply` locally and push the changes.

## Configuration

The Spotless configuration is in the root `build.gradle.kts`:

```kotlin
spotless {
    java {
        target("**/*.java")
        targetExclude("src/main/jooq/**/*.java")
        googleJavaFormat()
        
        licenseHeader(
            """/*
 * Copyright 2025 RealYusufIsmail.
 * ...
 */ """
        )
    }
}
```

### Excluded Files

The following are excluded from license headers:
- ✅ Generated JOOQ code (`src/main/jooq/**/*.java`)
- ✅ Build directories
- ✅ Gradle wrapper files

## Troubleshooting

### "License header violation" error

If you see this error:
```
> Task :spotlessCheck FAILED
src/main/java/YourFile.java:1: License header violation
```

**Solution**: Run `./gradlew spotlessApply` to automatically fix it.

### Spotless fails on generated code

If Spotless tries to format generated code (like JOOQ classes):

1. Check that `targetExclude("src/main/jooq/**/*.java")` is in the config
2. Add more exclusions if needed in `build.gradle.kts`

### Wrong year in copyright

The copyright year is set to **2025** (as configured). If you need to update it:

1. Edit `build.gradle.kts` 
2. Change the year in `licenseHeader()`
3. Run `./gradlew spotlessApply` to update all files

## IDE Integration

### IntelliJ IDEA

Spotless can be integrated with IntelliJ:

1. Install the "Spotless Gradle" plugin (optional)
2. Or set up a "Save Actions" plugin to run `spotlessApply` on save

### Pre-commit Hook (Optional)

Create `.git/hooks/pre-commit`:

```bash
#!/bin/bash
./gradlew spotlessCheck
if [ $? -ne 0 ]; then
    echo "❌ Spotless check failed! Run './gradlew spotlessApply' to fix."
    exit 1
fi
```

Make it executable:
```bash
chmod +x .git/hooks/pre-commit
```

## Why License Headers Matter

License headers protect the project by:

- ⚖️ Establishing copyright ownership
- 📜 Declaring the license terms (Apache 2.0)
- 🛡️ Providing legal protection
- 🔍 Making it clear this is open-source with conditions

**All contributors must agree to the Apache License 2.0** when contributing code (see [CONTRIBUTING.md](../CONTRIBUTING.md)).

## Related Documentation

- [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines
- [LICENSE](../LICENSE) - Full Apache License 2.0 text
- [NOTICE](../NOTICE) - Required legal notices
- [SECURITY.md](../SECURITY.md) - Security and license policy

---

**Copyright 2024-2025 RealYusufIsmail - Licensed under Apache License 2.0**

