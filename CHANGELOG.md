# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.7] - 30/10/2024

### Changed
- **Renamed classes to focus on apprenticeship terminology** - All job-related classes now use "Apprenticeship" naming
  - `HigherinJob` → `HigherinApprenticeship`
  - Updated all references across the codebase to use apprenticeship terminology
  - Updated log messages from "Job processing" → "Apprenticeship processing"
  - Updated Google Sheets application name to "MystiGuardian Apprenticeship Scraper"

### Added
- **Centralized version management with Gradle Version Catalog** (`gradle/libs.versions.toml`)
  - All dependency versions now managed in one place
  - Created dependency bundles for related libraries (logging, database, google, http-scraping, testing)
  - Prevents version conflicts across modules
  - Type-safe dependency access with IDE autocomplete
- **Comprehensive documentation**
  - `docs/VERSION_MANAGEMENT.md` - Complete guide to version catalog usage
  - `docs/VERSION_CATALOG_QUICK_REFERENCE.md` - Quick reference for common patterns
  - `.github/copilot-instructions.md` - GitHub Copilot configuration with project conventions
- **GitHub Copilot Instructions** - Added detailed project documentation for AI assistance

### Improved
- **Build system** - All Gradle build files now use version catalog for consistency
  - Root `build.gradle.kts` - Centralized plugin versions
  - `DiscordBot/build.gradle.kts` - 30+ dependencies converted to version catalog
  - `ApprenticeshipScraper/build.gradle.kts` - Simplified with bundles
  - `OAuth/build.gradle.kts` - Fully migrated to version catalog
- **Code maintainability** - Cleaner, more consistent codebase with apprenticeship-focused terminology

### Fixed
- Inconsistent terminology between "jobs" and "apprenticeships" across the codebase
- Version conflicts that could occur when dependencies were updated in only some modules

## [0.0.6] - [Previous Release]

### Previous changes...

## [0.0.5] - 29/08/2024

### Fixed user info command.

## [0.0.4] - 29/08/2024

### Fixed some minor text issues with the bot info command.
### Improved performance of the bot.
### Fixed some issues with the serp API. (Currently limited to my server)
### Migrated from Javacord to JDA.
### Added new roll dice and trivia commands.

## [0.0.3] - 19/08/2024

### Fixed issues with oath2.
### Added Youtube notification system (limited to my channel for now).
### Added AI chat system (limited to my server for now).

## [0.0.2] - 16/12/2023

### Improved changelog command.

## [0.0.1] - 15/12/2023

### Added changelog command.
### Improved bot info command.
### With this update, the changes will be documented in this file, and be available to everyone by /changelog command.

