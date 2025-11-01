# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.8] - 01/11/2024

### Added
- **Complete GOV.UK Find an Apprenticeship coverage** - Now scrapes all 15 route categories (previously only Digital)
  - Agriculture, environmental and animal care
  - Business and administration
  - Care services
  - Catering and hospitality
  - Construction and the built environment
  - Creative and design
  - Digital (existing)
  - Education and early years
  - Engineering and manufacturing
  - Hair and beauty
  - Health and science
  - Legal, finance and accounting
  - Protective services
  - Sales, marketing and procurement
  - Transport and logistics
- **Specialized scraper classes** - New dedicated scrapers in `scrapper` package
  - `HigherinScraper` - Handles Higher In (Rate My Apprenticeship) scraping with 150+ categories
  - `FindAnApprenticeshipScraper` - Handles GOV.UK Find an Apprenticeship scraping with all 15 routes
- **Category field** - Added `category` field to `FindAnApprenticeshipJob` to track apprenticeship route category
- **Rate limiting helper methods** - Added `rateLimitDelay()` methods to both scrapers for clearer intent

### Changed
- **Refactored `ApprenticeshipScraper`** - Converted from 600+ line monolithic class to clean facade pattern (~71 lines)
  - Now delegates to specialized scrapers (`HigherinScraper` and `FindAnApprenticeshipScraper`)
  - Maintains 100% backward compatibility - existing code works without modifications
  - Supports dependency injection for testing via constructor overload
- **Improved error handling** - Isolated error handling per source and category
  - Errors in one scraper don't affect the other
  - Errors in one category don't stop other categories from being scraped
  - Consecutive error tracking (max 3 per category before moving on)
  - More granular logging with better context

### Improved
- **Code organization** - Better separation of concerns with specialized classes
  - Single Responsibility Principle applied to scrapers
  - Each scraper is self-contained and focused on one source
  - Easier to maintain, test, and extend
- **Scraping coverage** - Massively expanded GOV.UK apprenticeship coverage (15x increase)
  - Previously scraped only 1 category (Digital)
  - Now scrapes all 15 official route categories
- **Memory management** - Better resource management per scraper
  - Each scraper manages its own HTTP client
  - Independent memory management strategies
  - Periodic GC hints for long-running scraping sessions
- **Rate limiting** - Improved rate limiting implementation
  - 1 second delay between pages
  - 2 seconds delay between categories
  - 500ms delay between Higher In category batches
  - Thread interruption handling for graceful shutdown

### Fixed
- **Busy-waiting warnings** - Eliminated "Thread.sleep() in a loop" IDE warnings
  - Extracted sleep calls into dedicated `rateLimitDelay()` helper methods
  - Clarified intent (rate limiting, not busy-waiting)
  - Consistent interruption handling across scrapers
- **Exception handling** - Cleaned up unused exception declarations
  - Removed unnecessary `IOException` from method signatures
  - Removed unnecessary `InterruptedException` from internal methods

### Technical Details
- Created new package: `io.github.yusufsdiscordbot.mystiguardian.api.scrapper`
- `HigherinScraper.java` - ~330 lines, processes 150+ categories in batches
- `FindAnApprenticeshipScraper.java` - ~320 lines, iterates through 15 route categories with pagination
- `ApprenticeshipScraper.java` - Reduced to ~71 lines as a facade
- Backward compatible - `ApprenticeshipSpreadsheetManager` works without modifications

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

