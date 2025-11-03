# Branching Strategy

## Overview

MystiGuardian follows a **Git Flow** branching strategy with `main` and `develop` branches.

## Branch Structure

```
main (production-ready releases)
  ↑
  └── develop (integration branch for next release)
        ↑
        ├── feature/* (new features)
        ├── bugfix/* (bug fixes)
        ├── hotfix/* (urgent production fixes)
        └── chore/* (maintenance tasks)
```

## Main Branches

### `main`
- **Purpose**: Production-ready code
- **Protection**: Branch protection enabled
- **Merges from**: `develop` (via PR) or `hotfix/*` (emergencies)
- **Tagged**: All releases are tagged here (e.g., `v0.0.9`)
- **Status**: Should always be stable and deployable

### `develop`
- **Purpose**: Integration branch for the next release
- **Protection**: Branch protection enabled
- **Merges from**: `feature/*`, `bugfix/*`, `chore/*` branches
- **Merges to**: `main` (when ready for release)
- **Status**: Should be stable but may contain unreleased features

## Supporting Branches

### Feature Branches (`feature/*`)
```bash
# Create from develop
git checkout develop
git pull origin develop
git checkout -b feature/apprenticeship-notifications

# Work on feature...
git add .
git commit -m "feat: Add notification system for new apprenticeships"

# Push and create PR to develop
git push origin feature/apprenticeship-notifications
```

**Naming convention**: `feature/short-description`
- `feature/discord-embed-improvements`
- `feature/new-scraper-source`
- `feature/database-migration`

**Lifecycle**:
1. Branch from: `develop`
2. Merge back to: `develop`
3. Delete after merge: Yes

### Bugfix Branches (`bugfix/*`)
```bash
# Create from develop
git checkout develop
git pull origin develop
git checkout -b bugfix/scraper-rate-limit

# Fix the bug...
git add .
git commit -m "fix: Correctly handle rate limiting in Higher In scraper"

# Push and create PR to develop
git push origin bugfix/scraper-rate-limit
```

**Naming convention**: `bugfix/short-description`
- `bugfix/null-pointer-apprenticeships`
- `bugfix/discord-embed-formatting`
- `bugfix/database-connection-timeout`

**Lifecycle**:
1. Branch from: `develop`
2. Merge back to: `develop`
3. Delete after merge: Yes

### Hotfix Branches (`hotfix/*`)
```bash
# Create from main (for urgent production fixes)
git checkout main
git pull origin main
git checkout -b hotfix/critical-security-issue

# Fix critical issue...
git add .
git commit -m "fix: Patch security vulnerability in OAuth module"

# Push and create PR to BOTH main AND develop
git push origin hotfix/critical-security-issue
```

**Naming convention**: `hotfix/short-description`
- `hotfix/discord-token-leak`
- `hotfix/critical-crash`
- `hotfix/security-patch`

**Lifecycle**:
1. Branch from: `main`
2. Merge back to: **BOTH** `main` AND `develop`
3. Tag on `main` after merge
4. Delete after merge: Yes

### Chore Branches (`chore/*`)
```bash
# Create from develop
git checkout develop
git pull origin develop
git checkout -b chore/update-dependencies

# Make changes...
git add .
git commit -m "chore: Update Gradle dependencies to latest versions"

# Push and create PR to develop
git push origin chore/update-dependencies
```

**Naming convention**: `chore/short-description`
- `chore/update-dependencies`
- `chore/refactor-code-structure`
- `chore/improve-documentation`

**Lifecycle**:
1. Branch from: `develop`
2. Merge back to: `develop`
3. Delete after merge: Yes

## Workflow Examples

### Adding a New Feature

1. **Create feature branch**:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/new-amazing-feature
   ```

2. **Develop the feature**:
   ```bash
   # Make changes, commit frequently
   git add .
   git commit -m "feat: Add initial implementation"
   
   # Keep up to date with develop
   git fetch origin
   git rebase origin/develop
   ```

3. **Push and create PR**:
   ```bash
   git push origin feature/new-amazing-feature
   # Create PR on GitHub: feature/new-amazing-feature → develop
   ```

4. **After PR approval**:
   - PR is merged to `develop`
   - Feature branch is deleted
   - Feature will be included in next release

### Preparing a Release

1. **Ensure develop is stable**:
   ```bash
   # Make sure all tests pass
   ./gradlew build test
   
   # Check for any issues
   ./gradlew spotlessCheck
   ```

2. **Update version number**:
   ```bash
   # Edit build.gradle.kts
   version = "0.0.10" # Increment version
   ```

3. **Update CHANGELOG.md**:
   ```markdown
   ## [0.0.10] - 05/11/2024
   
   ### Added
   - New feature X
   - New feature Y
   
   ### Fixed
   - Bug fix A
   - Bug fix B
   ```

4. **Create release PR**:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b release/v0.0.10
   
   # Commit version and changelog updates
   git add build.gradle.kts CHANGELOG.md
   git commit -m "chore: Prepare release v0.0.10"
   
   git push origin release/v0.0.10
   # Create PR: release/v0.0.10 → main
   ```

5. **After PR is merged to main**:
   ```bash
   # Tag the release
   git checkout main
   git pull origin main
   git tag -a v0.0.10 -m "Release version 0.0.10"
   git push origin v0.0.10
   
   # Merge back to develop
   git checkout develop
   git pull origin develop
   git merge main
   git push origin develop
   ```

6. **GitHub Actions automatically**:
   - Builds the release JAR
   - Creates GitHub Release
   - Attaches JAR file

### Emergency Hotfix

1. **Create hotfix from main**:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b hotfix/critical-bug
   ```

2. **Fix the issue**:
   ```bash
   # Make the fix
   git add .
   git commit -m "fix: Resolve critical production bug"
   
   # Update version (patch increment)
   # Edit build.gradle.kts: 0.0.9 → 0.0.9.1 or 0.0.10
   
   git add build.gradle.kts
   git commit -m "chore: Bump version for hotfix"
   ```

3. **Create PR to main**:
   ```bash
   git push origin hotfix/critical-bug
   # Create PR: hotfix/critical-bug → main
   ```

4. **After merge to main**:
   ```bash
   # Tag the hotfix
   git checkout main
   git pull origin main
   git tag -a v0.0.10 -m "Hotfix: Critical bug resolution"
   git push origin v0.0.10
   
   # IMPORTANT: Merge to develop too!
   git checkout develop
   git pull origin develop
   git merge main
   git push origin develop
   ```

## Branch Protection Rules

### For `main` branch:
- ✅ Require pull request reviews (1 reviewer)
- ✅ Require status checks to pass:
  - `build` (Java CI)
  - `spotless` (Code Formatting)
  - `gradle-test` (Unit Tests)
  - `license-check` (License Compliance)
  - `secret-scanning` (Security)
- ✅ Require branches to be up to date
- ✅ Require signed commits (recommended)
- ✅ Include administrators
- ❌ Allow force pushes: **NEVER**
- ❌ Allow deletions: **NEVER**

### For `develop` branch:
- ✅ Require pull request reviews (1 reviewer)
- ✅ Require status checks to pass:
  - `build` (Java CI)
  - `spotless` (Code Formatting)
  - `gradle-test` (Unit Tests)
- ✅ Require branches to be up to date
- ❌ Allow force pushes: No (except admins in emergencies)
- ❌ Allow deletions: **NEVER**

## Commit Message Conventions

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, no functional changes)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Maintenance tasks (dependencies, build, etc.)
- `ci`: CI/CD changes
- `revert`: Revert a previous commit

### Examples:
```bash
feat(scraper): Add support for GOV.UK apprenticeship scraping

fix(discord): Correctly format embed titles with special characters

docs(readme): Update installation instructions for Java 21

chore(deps): Update JDA to version 5.0.0

ci(workflows): Migrate to latest GitHub Actions versions
```

## GitHub Actions Integration

All workflows now support both `main` and `develop`:

### On Push to `main` or `develop`:
- ✅ Java CI (build and test)
- ✅ Basic Checks (Spotless, tests)
- ✅ Security Checks (secrets, licenses)

### On Pull Requests to `main` or `develop`:
- ✅ Java CI
- ✅ Basic Checks
- ✅ Dependency Review
- ✅ Security Checks

### On Tags (`v*.*.*`):
- ✅ Release workflow (build JAR, create GitHub release)

## Best Practices

### Do:
- ✅ Always branch from `develop` for new work
- ✅ Keep branches short-lived (merge within a week if possible)
- ✅ Regularly sync with `develop` to avoid conflicts
- ✅ Write descriptive commit messages
- ✅ Run tests locally before pushing
- ✅ Run `./gradlew spotlessApply` before committing
- ✅ Keep PRs focused on one feature/fix
- ✅ Update CHANGELOG.md for user-facing changes
- ✅ Request code reviews promptly

### Don't:
- ❌ Commit directly to `main` or `develop`
- ❌ Force push to shared branches
- ❌ Let branches live for months
- ❌ Mix multiple features in one branch
- ❌ Commit sensitive data (tokens, passwords)
- ❌ Commit without running tests
- ❌ Ignore merge conflicts

## Quick Reference

| Task | Command |
|------|---------|
| Start new feature | `git checkout -b feature/my-feature develop` |
| Start bug fix | `git checkout -b bugfix/my-fix develop` |
| Start hotfix | `git checkout -b hotfix/urgent-fix main` |
| Update from develop | `git fetch && git rebase origin/develop` |
| Check status | `git status` |
| Run tests | `./gradlew test` |
| Format code | `./gradlew spotlessApply` |
| Build project | `./gradlew build` |

## Migration to This Strategy

If you're currently working on `main` only:

1. **Create `develop` branch**:
   ```bash
   git checkout main
   git pull origin main
   git checkout -b develop
   git push origin develop
   ```

2. **Set `develop` as default branch** (GitHub Settings → Branches → Default branch)

3. **Apply branch protection rules** to both `main` and `develop`

4. **Update all open PRs** to target `develop` instead of `main`

5. **Inform contributors** about the new branching strategy

## Related Documentation

- [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines
- [COPYRIGHT_AUTOMATION.md](COPYRIGHT_AUTOMATION.md) - Automated copyright updates
- [LICENSE_HEADERS.md](LICENSE_HEADERS.md) - License header management

---

**Copyright 2024-2025 RealYusufIsmail - Licensed under Apache License 2.0**

