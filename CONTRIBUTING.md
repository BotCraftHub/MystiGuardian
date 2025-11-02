# Contributing to MystiGuardian

Thank you for your interest in contributing to MystiGuardian! This document provides guidelines for contributing to the project.

## 📋 Table of Contents
- [Code of Conduct](#code-of-conduct)
- [License Agreement](#license-agreement)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Code Standards](#code-standards)
- [Submitting Changes](#submitting-changes)
- [Copyright and Attribution](#copyright-and-attribution)

## 📜 Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct (see CODE_OF_CONDUCT.md).

## ⚖️ License Agreement

**IMPORTANT**: By contributing to MystiGuardian, you agree that:

1. Your contributions will be licensed under the **Apache License 2.0**
2. You have the right to submit the contribution under this license
3. You understand and accept that your code becomes part of the open-source project
4. You grant the project maintainers the right to redistribute your contributions

### Copyright Headers

All source files **MUST** include the Apache License 2.0 copyright header:

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

**All new Java files must include this header at the top.**

## 🚀 Getting Started

### Prerequisites

- **Java 21+** (required for virtual threads support)
- **Gradle 8.5+** (use the included Gradle wrapper)
- **PostgreSQL 14+** (for database operations)
- **Git** (version control)
- **IntelliJ IDEA** (recommended IDE)

### Setting Up Development Environment

1. **Fork the repository** on GitHub

2. **Clone your fork**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/MystiGuardian.git
   cd MystiGuardian
   ```

3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/RealYusufIsmail/MystiGuardian.git
   ```

4. **Configure the bot**:
   - Copy `config.json.example` to `config.json`
   - Fill in your Discord bot token and other credentials
   - **NEVER commit `config.json` - it's gitignored for security**

5. **Set up database**:
   - Create a PostgreSQL database
   - Update `config.json` with database credentials
   - Migrations run automatically on bot startup

6. **Build the project**:
   ```bash
   ./gradlew build
   ```

7. **Run tests**:
   ```bash
   ./gradlew test
   ```

## 🔄 Development Workflow

### 1. Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
```

Branch naming conventions:
- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation updates
- `refactor/` - Code refactoring
- `test/` - Test additions/improvements

### 2. Make Your Changes

Follow our coding standards (see below) and make focused, atomic commits.

### 3. Commit Your Changes

Write clear, descriptive commit messages:

```bash
git commit -m "feat: Add new apprenticeship scraper for XYZ

- Implement XYZ scraper class
- Add tests for scraper functionality
- Update documentation
"
```

Commit message format:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting)
- `refactor:` - Code refactoring
- `test:` - Test additions/changes
- `chore:` - Build/tooling changes

### 4. Keep Your Fork Updated

```bash
git fetch upstream
git rebase upstream/main
```

### 5. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

### 6. Create a Pull Request

- Go to your fork on GitHub
- Click "New Pull Request"
- Provide a clear description of your changes
- Reference any related issues

## 📝 Code Standards

### Java Style Guide

We follow the **Google Java Style Guide** with enforcement via Spotless:

```bash
# Check code formatting
./gradlew spotlessCheck

# Auto-format code
./gradlew spotlessApply
```

**Always run `spotlessApply` before committing!**

### Code Conventions

1. **Use Lombok** for boilerplate reduction:
   ```java
   @Getter
   @Setter
   @Slf4j
   public class Example {
       private String field;
   }
   ```

2. **Use records** for immutable data classes:
   ```java
   public record ApprenticeshipData(String title, String company, String url) {}
   ```

3. **Use var** for local variables when type is obvious:
   ```java
   var apprenticeships = scraper.scrapeAll();
   ```

4. **Use SLF4J** for logging:
   ```java
   @Slf4j
   public class Example {
       public void method() {
           logger.info("Starting operation");
           try {
               // code
           } catch (Exception e) {
               logger.error("Operation failed", e);
           }
       }
   }
   ```

5. **Always handle errors gracefully**:
   - Log errors with context
   - Don't expose sensitive data in logs
   - Use try-catch for I/O operations

### Dependency Management

- **Use the Gradle Version Catalog** (`gradle/libs.versions.toml`)
- Never hardcode versions in `build.gradle.kts`
- Document why you're adding a dependency

Example:
```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.jda)
    implementation(libs.okhttp)
}
```

### Database Migrations

- **Use Flyway** for schema changes
- Create new migration files: `V{VERSION}__{Description}.sql`
- Never modify existing migrations
- Test migrations locally before committing

### Testing

- Write unit tests for new features
- Use JUnit 5
- Mock external APIs where appropriate
- Aim for meaningful test coverage (not 100% for sake of it)

### Documentation

1. **JavaDoc** for public APIs:
   ```java
   /**
    * Scrapes apprenticeships from the specified source.
    *
    * @param source the apprenticeship source to scrape
    * @return list of apprenticeships found
    * @throws IOException if scraping fails
    */
   public List<Apprenticeship> scrape(ApprenticeshipSource source) throws IOException {
       // implementation
   }
   ```

2. **Update CHANGELOG.md** for significant changes

3. **Update README.md** if adding user-facing features

## 🔍 Pull Request Guidelines

### Before Submitting

- ✅ Code compiles without errors
- ✅ All tests pass (`./gradlew test`)
- ✅ Code is formatted (`./gradlew spotlessApply`)
- ✅ No new compiler warnings
- ✅ Copyright headers present on new files
- ✅ CHANGELOG.md updated (for significant changes)
- ✅ Documentation updated (if applicable)

### PR Description Should Include

- **What** changes were made
- **Why** the changes are needed
- **How** to test the changes
- **Related issues** (if any)

### Review Process

1. Maintainer will review your PR
2. Address any feedback or requested changes
3. Once approved, PR will be merged
4. Your contribution will be acknowledged in release notes

## 🚫 What NOT to Contribute

Please **DO NOT** submit PRs for:

- ❌ Changes to `config.json` (unless updating example)
- ❌ Generated files (`build/` directories)
- ❌ IDE-specific configuration (`.idea/` files)
- ❌ Credentials or tokens
- ❌ Large binary files
- ❌ Unrelated formatting changes (use `spotlessApply`)
- ❌ Breaking changes without discussion

## 🐛 Reporting Bugs

### Before Reporting

1. Check existing issues on GitHub
2. Verify you're using the latest version
3. Ensure it's not a configuration issue

### Bug Report Should Include

- **Description**: Clear description of the bug
- **Steps to Reproduce**: How to trigger the bug
- **Expected Behavior**: What should happen
- **Actual Behavior**: What actually happens
- **Environment**: Java version, OS, bot version
- **Logs**: Relevant error logs (redact sensitive info!)

## 💡 Suggesting Features

Feature requests are welcome! Please:

1. Check if the feature already exists or is planned
2. Open a GitHub issue with the `enhancement` label
3. Clearly describe the feature and use case
4. Be open to discussion and feedback

## 📞 Getting Help

- **Discord**: Join our Discord server (link in README)
- **GitHub Issues**: For bug reports and feature requests
- **Discussions**: For questions and general discussion

## 🏆 Recognition

Contributors will be acknowledged in:

- Release notes
- CHANGELOG.md
- GitHub contributors page

Thank you for contributing to MystiGuardian! 🎉

---

**Copyright 2024 RealYusufIsmail - Licensed under Apache License 2.0**

