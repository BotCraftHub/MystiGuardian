# GitHub Actions Workflows

This directory contains all GitHub Actions workflows for the MystiGuardian project. These workflows automate various tasks including building, testing, security checks, and releases.

## Table of Contents

1. [Release Workflow](#release-workflow-releaseyml) - Automated releases on merge to main
2. [Java CI Workflow](#java-ci-workflow-gradleyml) - Build and test
3. [Basic Checks Workflow](#basic-checks-workflow-basic-checksyml) - Code formatting and unit tests
4. [Security Checks Workflow](#security-checks-workflow-securityyml) - Secret scanning and license compliance
5. [Dependency Review Workflow](#dependency-review-workflow-dependency-reviewyml) - Review dependencies in PRs
6. [Greetings Workflow](#greetings-workflow-greetingsyml) - Welcome first-time contributors
7. [Update Copyright Year Workflow](#update-copyright-year-workflow-update-copyright-yearyml) - Annual copyright updates

---

## Release Workflow (`release.yml`)

### Purpose
Automatically creates a GitHub release when changes are merged to the `main` branch.

### Trigger
- **Event**: Push to `main` branch
- **When**: After merging from `develop` (or any other branch) to `main`

### What It Does

1. **Extracts Version**
   - Reads the version from `build.gradle.kts` (e.g., `version = "0.0.9"`)
   - Uses this version for the release tag (e.g., `v0.0.9`)

2. **Checks for Existing Release**
   - Verifies if a release with this version tag already exists
   - Skips release creation if the version already exists (prevents duplicates)

3. **Extracts Changelog**
   - Automatically extracts the changelog section for the current version from `CHANGELOG.md`
   - Uses the content between `## [X.X.X]` markers
   - Includes this in the release notes

4. **Builds the Project**
   - Runs `./gradlew clean shadowJar`
   - Creates the fat JAR with all dependencies

5. **Creates GitHub Release**
   - Creates a new release with tag `vX.X.X`
   - Attaches the shadow JAR (`MystiGuardian-X.X.X.jar`)
   - Includes the extracted changelog as release notes
   - Marks as non-draft, non-prerelease

### Workflow Process

```
develop branch
    └─> (merge PR to main)
           └─> main branch
                 └─> Workflow triggers
                       ├─> Extract version
                       ├─> Check if tag exists
                       ├─> Extract changelog
                       ├─> Build shadow JAR
                       └─> Create GitHub Release
```

### Requirements

- **Permissions**: The workflow has `contents: write` permission to create releases
- **GitHub Token**: Automatically provided by GitHub Actions (`GITHUB_TOKEN`)
- **Java 21**: Uses Temurin JDK 21
- **Gradle**: Uses Gradle wrapper with caching

### Version Management

To create a new release:

1. Update the version in `build.gradle.kts`:
   ```kotlin
   version = "0.0.10"
   ```

2. Update `CHANGELOG.md` with the new version:
   ```markdown
   ## [0.0.10] - DD/MM/YYYY
   
   ### Added
   - New feature 1
   
   ### Fixed
   - Bug fix 1
   ```

3. Merge your changes from `develop` to `main`

4. The workflow automatically:
   - Detects version `0.0.10`
   - Creates tag `v0.0.10`
   - Extracts changelog for `0.0.10`
   - Builds `MystiGuardian-0.0.10.jar`
   - Creates GitHub Release with all the above

### Preventing Duplicate Releases

If you push to `main` again with the same version:
- The workflow runs but detects the tag already exists
- Skips release creation
- Logs: "Release vX.X.X already exists. Skipping release creation."

To create a new release, increment the version in `build.gradle.kts`.

### Manual Release Creation

If you need to create a release manually:

1. Go to: https://github.com/YOUR_USERNAME/MystiGuardian/releases/new
2. Choose a tag (e.g., `v0.0.9`)
3. Fill in the release notes
4. Attach the JAR from `build/libs/`
5. Publish release

However, the automated workflow is recommended for consistency.

### Troubleshooting

**Workflow doesn't trigger:**
- Ensure you pushed to `main` (not `develop` or another branch)
- Check: https://github.com/YOUR_USERNAME/MystiGuardian/actions

**Release creation fails:**
- Check the Actions logs for errors
- Verify `CHANGELOG.md` has the correct version section
- Ensure `build.gradle.kts` has a valid version

**Wrong version detected:**
- Check `build.gradle.kts` for the `version = "X.X.X"` line
- Ensure it's not commented out or malformed

**JAR not attached:**
- Verify the shadow JAR builds successfully
- Check the file name matches: `MystiGuardian-X.X.X.jar`
- Ensure it's in `build/libs/` directory

### Best Practices

1. **Semantic Versioning**: Use `MAJOR.MINOR.PATCH` (e.g., `0.0.9`)
2. **Update Version Before Merging**: Always update `version` in `build.gradle.kts` before merging to `main`
3. **Keep CHANGELOG Updated**: Document all changes in `CHANGELOG.md` following [Keep a Changelog](https://keepachangelog.com/) format
4. **One Release Per Version**: Don't push to `main` multiple times with the same version
5. **Test on Develop**: Thoroughly test on `develop` before merging to `main`

### Example Workflow Run

```
✓ Checkout code
✓ Set up JDK 21
✓ Grant execute permission for gradlew
✓ Extract version from build.gradle.kts
  → Detected version: 0.0.9
✓ Check if tag already exists
  → Tag v0.0.9 does not exist
✓ Extract changelog for current version
  → Changelog extracted successfully
✓ Build shadow JAR
  → BUILD SUCCESSFUL in 1m 23s
✓ Create GitHub Release
  → Created release v0.0.9
  → Attached MystiGuardian-0.0.9.jar
  → Release notes from CHANGELOG.md
```

### Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [softprops/action-gh-release](https://github.com/softprops/action-gh-release)
- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)

---

## Java CI Workflow (`gradle.yml`)

### Purpose
Main CI/CD workflow that builds and tests the project on every push and pull request.

### Trigger
- **Push** to `main` or `develop` branches
- **Pull requests** targeting `main` or `develop`

### What It Does

1. **Validates Gradle wrapper** - Security check for Gradle wrapper
2. **Sets up Java 21** - Temurin distribution with Gradle caching
3. **Builds the project** - Runs `./gradlew build` with parallel execution
4. **Generates dependency graph** - Submits dependency information to GitHub
5. **Uploads artifacts** - Saves build artifacts for download

### Key Features
- ✅ Parallel build execution for speed
- ✅ Gradle build scan generation
- ✅ Dependency graph submission for insights
- ✅ Artifact upload (JARs, reports)

---

## Basic Checks Workflow (`basic-checks.yml`)

### Purpose
Runs code quality checks including formatting and unit tests.

### Trigger
- **Push** to `main` or `develop` branches
- **Pull requests** targeting `main` or `develop`

### Jobs

#### 1. Code Formatting (Spotless)
- Validates code formatting using Spotless
- Ensures consistent code style across the project
- Runs: `./gradlew spotlessCheck`

#### 2. Unit Tests
- Runs all unit tests
- Generates test reports
- Runs: `./gradlew test`

### Key Features
- ✅ Gradle wrapper validation
- ✅ Parallel execution
- ✅ Gradle caching for faster runs

---

## Security Checks Workflow (`security.yml`)

### Purpose
Performs security scanning including secret detection and license compliance.

### Trigger
- **Push** to `main` or `develop` branches
- **Pull requests** targeting `main` or `develop`
- **Scheduled**: Weekly on Mondays at 00:00 UTC

### Jobs

#### 1. Secret Scanning (TruffleHog)
- Scans repository for exposed secrets (API keys, tokens, passwords)
- Uses TruffleHog OSS for detection
- Only reports verified secrets to reduce false positives

#### 2. License Compliance
- Checks dependency licenses for compliance
- Validates Gradle wrapper
- Ensures project follows license requirements

### Key Features
- ✅ Automated secret detection
- ✅ Weekly scheduled scans
- ✅ License compliance checking
- ✅ Security event reporting

---

## Dependency Review Workflow (`dependency-review.yml`)

### Purpose
Reviews dependencies changed in pull requests to identify vulnerabilities.

### Trigger
- **Pull requests** targeting `main` or `develop`

### What It Does

1. **Scans dependency changes** - Identifies new/updated dependencies in PR
2. **Checks for vulnerabilities** - Looks up known security issues
3. **Fails on moderate+ severity** - Blocks PRs with vulnerable dependencies
4. **Comments on PR** - Adds summary of findings to the PR

### Configuration
- **Fail threshold**: `moderate` severity or higher
- **PR comments**: Always posted
- **Required check**: Can block merging

### Key Features
- ✅ Prevents vulnerable dependencies from being merged
- ✅ Automatic PR comments with findings
- ✅ Configurable severity threshold

---

## Greetings Workflow (`greetings.yml`)

### Purpose
Welcomes and provides guidance to first-time contributors.

### Trigger
- **First issue** opened by a new contributor
- **First pull request** opened by a new contributor

### What It Does

1. **Detects first-time contributors** - Identifies if this is their first interaction
2. **Posts welcome message** - Friendly greeting with guidance
3. **Provides guidelines** - Explains what information to include

### Message Content
- 👋 Welcome greeting
- 📋 Checklist of what to include in issues
- 🔍 What to expect from maintainers

### Key Features
- ✅ Friendly onboarding experience
- ✅ Clear contribution guidelines
- ✅ Reduces incomplete issues/PRs

---

## Update Copyright Year Workflow (`update-copyright-year.yml`)

### Purpose
Automatically updates copyright year in source files at the start of each year.

### Trigger
- **Scheduled**: January 1st at 00:00 UTC every year (`0 0 1 1 *`)
- **Manual**: Can be triggered via workflow_dispatch for testing

### What It Does

1. **Calculates years** - Determines current year and previous year
2. **Updates build.gradle.kts** - Replaces copyright year in license headers
3. **Creates pull request** - Opens PR with the copyright updates
4. **Assigns reviewers** - Notifies maintainers for review

### Example Update
```kotlin
// Before (January 1, 2026)
Copyright 2025 RealYusufIsmail

// After
Copyright 2026 RealYusufIsmail
```

### Key Features
- ✅ Fully automated annual updates
- ✅ Creates PR for review (doesn't auto-merge)
- ✅ Can be manually triggered for testing
- ✅ Updates license headers in build files

---

## Workflow Status Overview

| Workflow | Runs On | Purpose | Required Check |
|----------|---------|---------|----------------|
| **Release** | Push to `main` | Create GitHub releases | No |
| **Java CI** | Push/PR to `main`/`develop` | Build and test | Yes |
| **Basic Checks** | Push/PR to `main`/`develop` | Format & unit tests | Yes |
| **Security** | Push/PR + Weekly | Security scanning | Yes |
| **Dependency Review** | PRs to `main`/`develop` | Review new dependencies | Yes |
| **Greetings** | First issue/PR | Welcome contributors | No |
| **Copyright Update** | January 1st yearly | Update copyright year | No |

---

## Best Practices

### For Contributors

1. **Before submitting PR**:
   - Run `./gradlew spotlessApply` to format code
   - Run `./gradlew test` to ensure tests pass
   - Run `./gradlew build` to verify build succeeds

2. **Dependency updates**:
   - Check dependency-review workflow results
   - Don't add dependencies with known vulnerabilities
   - Update `gradle/libs.versions.toml` for version changes

3. **Security**:
   - Never commit secrets or API keys
   - Use environment variables or GitHub Secrets
   - Check TruffleHog results if secret scanning fails

### For Maintainers

1. **Release process**:
   - Work on `develop` branch
   - Update version in `build.gradle.kts`
   - Update `CHANGELOG.md` with version entry
   - Merge to `main` to trigger release workflow

2. **Security reviews**:
   - Monitor weekly security scan results
   - Review dependency-review findings in PRs
   - Update vulnerable dependencies promptly

3. **CI/CD maintenance**:
   - Keep workflow actions up-to-date (Dependabot helps)
   - Review and adjust fail thresholds as needed
   - Monitor workflow execution times

---

## Troubleshooting

### Workflow Failed - What to Check

**Release workflow fails**:
- Verify version in `build.gradle.kts` is correct
- Check if tag already exists
- Ensure `CHANGELOG.md` has entry for the version

**Build fails**:
- Check Gradle build logs
- Verify Java 21 compatibility
- Look for compilation errors

**Spotless check fails**:
- Run `./gradlew spotlessApply` locally
- Commit formatted code
- Push changes

**Security scan fails**:
- Review TruffleHog findings
- Remove or rotate exposed secrets
- Update `.gitignore` if needed

**Dependency review fails**:
- Check which dependency has vulnerabilities
- Update to patched version
- Or justify and request override

### Viewing Workflow Runs

1. Go to: `https://github.com/YOUR_USERNAME/MystiGuardian/actions`
2. Click on workflow name to see all runs
3. Click on specific run to see detailed logs
4. Check each job for errors

### Manual Workflow Triggers

Some workflows support manual triggering:

```bash
# Via GitHub UI: Actions tab → Select workflow → Run workflow button
```

Or using GitHub CLI:
```bash
# Trigger copyright update workflow
gh workflow run update-copyright-year.yml

# Trigger release workflow (if configured for workflow_dispatch)
gh workflow run release.yml
```

---

## Workflow Maintenance

### Updating Actions

Workflows use GitHub Actions from the marketplace. Keep them updated:

1. **Dependabot**: Automatically creates PRs for action updates
2. **Manual check**: Look for warnings about outdated actions
3. **Version pinning**: Use `@v5` for major version, or `@v5.1.0` for specific version

### Adding New Workflows

1. Create `.yml` file in `.github/workflows/`
2. Define trigger (`on:` section)
3. Define jobs and steps
4. Test with a draft PR
5. Update this README

### Modifying Existing Workflows

1. Make changes to `.yml` file
2. Test thoroughly (consider using `pull_request` trigger first)
3. Update this README if behavior changes
4. Merge to `main` when validated

---

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Actions](https://github.com/gradle/actions)
- [Dependency Review Action](https://github.com/actions/dependency-review-action)
- [TruffleHog](https://github.com/trufflesecurity/trufflehog)
- [Semantic Versioning](https://semver.org/)
- [Keep a Changelog](https://keepachangelog.com/)

