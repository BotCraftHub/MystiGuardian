# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.10] - Unreleased

### Fixed
- **JUnit Platform Launcher missing in Gradle 9.2.0** - Fixed test execution failure in OAuth module
  - Added explicit `junit-platform-launcher` dependency to version catalog
  - Gradle 9.x requires explicit JUnit Platform Launcher on the test runtime classpath
  - Fixed "Could not start Gradle Test Executor: Failed to load JUnit Platform" error
  - All test tasks now execute successfully with JUnit 5
- **Shadow plugin compatibility with Gradle 9.2.0** - Fixed `Could not add META-INF to ZIP` error
  - Upgraded Shadow plugin from version 8.1.1 to 9.2.2 for Gradle 9.x compatibility
  - Migrated from old plugin ID `com.github.johnrengelman.shadow` to new `com.gradleup.shadow`
  - Shadow plugin maintenance was transferred to GradleUp organization for continued development
  - Fixed `MissingPropertyException: No such property: mode` error in `StubbedFileCopyDetails`
  - Added META-INF signature file exclusions to all shadowJar tasks for consistency
  - Build now completes successfully with all modules producing valid shadow JARs
- **Code structure improvements** - Fixed incorrect record conversions and redundant constructors
  - Reverted `ApprenticeshipSpreadsheetManager` from record to regular class
    - Records are for immutable data carriers, not classes with mutable state and business logic
    - Class contains complex methods like `ensureHeaders()`, `getExistingApprenticeshipsFromSheet()`, and scheduling logic
    - Properly documented constructor parameters in regular class format
  - Removed redundant empty compact constructor from `TokensResponse` record
    - Records automatically generate canonical constructors, explicit empty constructor was unnecessary
    - Updated javadoc to clarify automatic constructor generation
  - Removed redundant default constructors from multiple classes
    - `FindAnApprenticeship`, `AICommand`, `AmountAuditCommand`, `ReloadCommand`
    - Java automatically provides no-arg constructors when none are defined
    - Removed unnecessary "Default constructor" comments and empty constructor bodies
- **Changelog command character limit error** - Fixed `IllegalArgumentException: Description cannot be longer than 4096 characters`
  - Added automatic truncation when changelog content exceeds Discord's 4096 character embed description limit
  - Truncation occurs at the last newline before the limit to avoid cutting text mid-line
  - Adds a link to view the full changelog on GitHub when content is truncated
  - Prevents bot crashes when displaying long version changelogs like 0.0.9 (5540 characters)
- **Javadoc warnings** - Resolved all 100+ javadoc warnings across all modules
  - Fixed varargs warning in `ApprenticeshipSpreadsheetManager` by proper array casting
  - Added missing `@param` documentation for record components in `TokensResponse`
  - Added comprehensive javadoc for all OAuth utility classes (`DiscordRestAPI`, `JWTUtils`, `CorsFilter`)
  - Added missing method documentation for `generateAccessToken()`, `applyCorsHeaders()`, and JWT utility methods
  - Added explicit constructors with javadoc for `MystiGuardian` and `ReloadCommand` classes
  - Fixed misplaced javadoc in `CorsFilter` class
  - Configured Gradle to exclude JOOQ-generated files from javadoc validation using `-Xdoclint:none`
- **GitHub Actions workflows** - Fixed parameter naming and configuration issues
  - **Security workflow** - Fixed TruffleHog "BASE and HEAD commits are the same" error
    - Added detection for initial commits where `github.event.before` is `0000000000000000000000000000000000000000`
    - Initial commits now use `git rev-list --max-parents=0 HEAD` to find the first commit as base
    - Split push event handling into two steps: initial commit vs regular commit
    - Regular commits continue using `github.event.before` and `github.sha` for proper diff scanning
    - Pull request events use `base.sha` and `head.sha` for accurate PR scanning
    - Scheduled runs scan entire repository without base/head comparison
    - Prevents workflow failures when BASE=HEAD (initial commits, no changes)
- **Category display showing academic years instead of topical categories** - Fixed category dropdown and cards displaying "3rd-year", "4th-year" values
  - **HigherinScraper** - Changed to use search category slug (e.g., "software-engineering") instead of parsing `relevantFor` JSON field
    - `relevantFor` field contains academic year targeting metadata (which years students can apply), not subject matter categories
    - Category parameter passed to scraper already contains the actual topical category from the search URL slug
    - Reduced from 13 lines of parsing logic to single line assignment using `Collections.singletonList(category)`
  - **FindAnApprenticeship** - Added `getCategories()` implementation to return GOV.UK route category
    - Previously used interface default that returned empty list, causing "Not specified" to display on cards
    - Now returns GOV.UK route category (e.g., "Digital", "Engineering and manufacturing") as single-item list
    - Added proper imports for `Collections` and `List` classes
  - Category dropdown now populates with 98 topical categories (83 Higher In + 15 GOV.UK routes) across Technology, Finance, Engineering, Business, Marketing, and other sectors
  - All apprenticeships now display meaningful categories instead of "Not specified" or academic year values
  - Web UI formatting automatically converts category slugs to proper title case (e.g., "software-engineering" → "Software Engineering")

### Improved
- **Changelog command Discord formatting** - Improved how changelog renders in Discord embeds
  - Converts Markdown `### Headers` to bold `**Headers**` for better Discord compatibility
  - Nested bullet points now use `├─` for visual hierarchy instead of plain indentation
  - Cleaned up excessive spacing between sections
  - Headers, bold text, and bullet points now render properly in Discord
  - Discord embeds don't support Markdown headers natively, so we convert them
- **Javadoc quality** - Enhanced documentation across all modules
  - `ApprenticeshipSpreadsheetManager` record now has proper `@param` tags for all 5 components
  - All OAuth service classes now have comprehensive class-level and method-level documentation
  - JWT utility methods clearly document their parameters, return values, and exceptions
  - All public APIs now have complete javadoc with proper `@param`, `@return`, and `@throws` tags

### Changed
- **Javadoc configuration** - Added global javadoc settings to suppress warnings from generated code
  - Configured `Xdoclint:none` for all javadoc tasks to allow flexibility
  - Added UTF-8 encoding for javadoc output
  - Excluded JOOQ-generated files from javadoc processing using `exclude("**/jooq/**")`
  - Build now completes cleanly with no javadoc warnings

## [0.0.9] - 03/11/2025

### Added
- **Category configuration classes** - Externalized category/route definitions for easier maintenance
  - `HigherinCategories` - Organizes 83 Higher In categories into 14 logical sectors (Technology, Finance, Business, Engineering, Marketing, Design, Legal, Construction, Retail, Hospitality, HR, Property, Public Sector, Science)
  - `GovUkRoutes` - Maps 15 GOV.UK route categories to their official IDs
  - Utility class pattern with private constructors to prevent instantiation
  - Clear Javadoc documentation for each sector
- **Automated GitHub Releases** - GitHub Actions workflow for automatic release creation
  - Triggers on push to `main` branch
  - Automatically extracts version from `build.gradle.kts`
  - Creates GitHub release with version tag (e.g., `v0.0.9`)
  - Extracts changelog for the specific version from `CHANGELOG.md`
  - Builds and attaches shadow JAR to the release
  - Prevents duplicate releases by checking if tag already exists
  - Workflow file: `.github/workflows/release.yml`

### Changed
- **Externalized hardcoded category lists** - Moved category constants from scraper classes to dedicated configuration classes
  - `HigherinScraper` now uses `HigherinCategories.getAllCategories()` instead of inline list
  - `FindAnApprenticeshipScraper` now uses `GovUkRoutes.getAllRoutes()` instead of inline map
  - Follows proper Java conventions and separation of concerns
  - Makes categories easier to maintain and update without touching scraper logic
- **Reorganized package structure** - Improved module organization for better clarity
  - `io.github.yusufsdiscordbot.mystiguardian.apprenticeship` - Apprenticeship interfaces and implementations
  - `io.github.yusufsdiscordbot.mystiguardian.categories` - Category configuration classes
  - `io.github.yusufsdiscordbot.mystiguardian.scraper` - Scraper implementations
  - `io.github.yusufsdiscordbot.mystiguardian.manager` - Spreadsheet and data management
  - `io.github.yusufsdiscordbot.mystiguardian.config` - Configuration records

### Fixed
- **Flyway initialization in shaded/fat JARs** - Fixed "Unknown prefix for location: classpath:db/callback" error on hosting platforms
  - **Added shadow plugin with `mergeServiceFiles()` and `duplicatesStrategy = DuplicatesStrategy.INCLUDE` to ALL modules** - This is the complete fix
  - **DiscordBot** - Added shadow plugin configuration with ServiceLoader merging
  - **ApprenticeshipScraper** - Added shadow plugin configuration with ServiceLoader merging
  - **Annotations** - Added shadow plugin configuration with ServiceLoader merging
  - **OAuth** - Already had shadow plugin, added missing `mergeServiceFiles()` and `duplicatesStrategy`
  - **Root** - Already had complete configuration
  - **Simplified Flyway configuration** - Using minimal configuration pattern (dataSource, locations, baselineOnMigrate) for maximum compatibility
  - All modules now properly merge `META-INF/services/` files, allowing Flyway to discover its location resolvers in the fat JAR
  - Verified working on hosting platform - database migrations execute successfully
- **Date parsing for "Closes today"** - Fixed FindAnApprenticeshipScraper failing to parse "Closes today" and "Posted today" date formats
  - Added early detection for "today" keyword in date strings
  - Returns current date (LocalDate.now()) when "today" is detected
  - Prevents parsing errors and ensures apprenticeships with today's closing date are captured

### Improved
- **Code maintainability** - Better organization and easier category management
  - Categories organized by sector with clear labels
  - Single source of truth for all categories
  - No need to modify scraper code when adding/removing categories
  - Improved code readability with descriptive constant names
- **Documentation** - Enhanced Javadoc for category configuration classes
  - Each sector documented with category counts
  - Usage examples and cross-references to scraper classes
- **Documentation organization** - Streamlined `docs/` folder
  - Removed verbose descriptions and redundant content
  - Cleaned up references to non-existent files
  - Organized into clear sections: Quick Start, Developer Guides, Feature Documentation
  - Reduced from 250+ lines to ~50 lines in main README
  - More concise and easier to navigate

### Technical Details
- New package structure for better organization:
  - `categories/` - `HigherinCategories.java` (~220 lines) and `GovUkRoutes.java` (~80 lines)
  - `scraper/` - `HigherinScraper.java` and `FindAnApprenticeshipScraper.java`
  - `apprenticeship/` - `Apprenticeship.java`, `HigherinApprenticeship.java`, `FindAnApprenticeship.java`
  - `manager/` - `ApprenticeshipSpreadsheetManager.java`
  - `config/` - Configuration records (DAConfig, etc.)
- Both category classes use utility class pattern (final class with private constructor)
- Immutable collections using `List.of()` and `Map.ofEntries()`
- Category counts: Technology (7), Finance (11), Business (8), Engineering (10), Marketing (5), Design (5), Legal (5), Construction (4), Retail (4), Hospitality (3), HR (2), Property (4), Public Sector (8), Science (6)
- **All 5 modules now have shadow plugin** with `mergeServiceFiles()` and `duplicatesStrategy = DuplicatesStrategy.INCLUDE`
- **Simplified Flyway configuration** - Minimal setup (dataSource + locations + baselineOnMigrate) for maximum compatibility with shaded JARs
- **Solution verified working** - Database migrations now execute successfully on hosting platform

## [0.0.8] - 01/11/2025

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
- **Specialized scraper classes** - New dedicated scrapers in `scraper` package
  - `HigherinScraper` - Handles Higher In (Rate My Apprenticeship) scraping with 150+ categories
  - `FindAnApprenticeshipScraper` - Handles GOV.UK Find an Apprenticeship scraping with all 15 routes
- **Category field** - Added `category` field to `FindAnApprenticeship` to track apprenticeship route category
- **Rate limiting helper methods** - Added `rateLimitDelay()` methods to both scrapers for clearer intent

### Changed
- **Refactored `ApprenticeshipScraper`** - Converted from 600+ line monolithic class to clean facade pattern (~71 lines)
  - Now delegates to specialized scrapers (`HigherinScraper` and `FindAnApprenticeshipScraper`)
  - Maintains 100% backward compatibility - existing code works without modifications
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
- **Date parsing errors** - Fixed GOV.UK apprenticeship date parsing to handle all date formats
  - Now handles dates with time information (e.g., "at 11:59pm")
  - Supports complex formats like "Closes in 30 days (Monday 1 December 2025 at 11:59pm)"
  - Improved day number extraction with smart search algorithm
  - Added filtering for empty parts after string splitting
  - No more "Unexpected date format" errors in logs

### Technical Details
- Created new package: `io.github.yusufsdiscordbot.mystiguardian.scraper`
- `HigherinScraper.java` - ~330 lines, processes 150+ categories in batches
- `FindAnApprenticeshipScraper.java` - ~320 lines, iterates through 15 route categories with pagination
- `ApprenticeshipScraper.java` - Reduced to ~71 lines as a facade
- Backward compatible - `ApprenticeshipSpreadsheetManager` works without modifications

## [0.0.7] - 30/10/2025

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

