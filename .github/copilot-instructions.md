# GitHub Copilot Instructions for MystiGuardian

## Project Overview
MystiGuardian is a Discord bot focused on **apprenticeship opportunities** scraping and management. The project scrapes apprenticeship listings from multiple sources and posts them to Discord channels.

## Key Terminology
- **Apprenticeships** (not jobs) - The primary focus is on degree apprenticeships and similar opportunities
- Use `Apprenticeship` interface/class names, not `Job`
- Use `ApprenticeshipSpreadsheetManager` not `JobSpreadsheetManager`
- Use `HigherinApprenticeship` for Higher In apprenticeships
- Use `FindAnApprenticeshipJob` for GOV.UK apprenticeships (this name is kept as-is)

## Project Structure

### Modules
1. **DiscordBot** - Main Discord bot module with JDA integration
2. **ApprenticeshipScraper** - Scraping and Google Sheets management
3. **OAuth** - OAuth authentication and web service
4. **Annotations** - Custom annotations for the project

### Key Packages

#### ApprenticeshipScraper Module
- `io.github.yusufsdiscordbot.mystiguardian.api`
  - `ApprenticeshipScraper.java` - Web scraping logic for Higher In and GOV.UK
  - `ApprenticeshipSpreadsheetManager.java` - Google Sheets integration and Discord posting
  
- `io.github.yusufsdiscordbot.mystiguardian.api.job`
  - `Apprenticeship.java` - Interface for apprenticeship objects
  - `ApprenticeshipSource.java` - Enum for apprenticeship sources (RMA, GOV_UK)
  - `HigherinApprenticeship.java` - Implementation for Higher In apprenticeships
  - `FindAnApprenticeshipJob.java` - Implementation for GOV.UK apprenticeships

#### DiscordBot Module
- `io.github.yusufsdiscordbot.mystiguardian`
  - Main bot configuration and initialization
  - Event system for new apprenticeship announcements
  - Discord commands (admin, moderation, miscellaneous)
  
- `io.github.yusufsdiscordbot.mystiguardian.event`
  - Event dispatcher and handlers
  - `NewDAEvent` - Triggered when new apprenticeships are found

## Coding Conventions

### Language & Framework
- **Java 21+** with virtual threads support
- **Gradle** with Kotlin DSL for build configuration
- **JDA** (Java Discord API) for Discord integration
- **Google Sheets API** for spreadsheet management
- **Lombok** for boilerplate reduction

### Style Guidelines
- Use **Lombok annotations** (@Getter, @Setter, @Slf4j, etc.)
- Use **records** for immutable data classes where appropriate
- Use **var** for local variable type inference when type is obvious
- Use **SLF4J** for logging (`logger.info()`, `logger.error()`, etc.)
- Follow Google Java Style Guide (enforced by Spotless)

### Error Handling
- Always log errors with context using SLF4J
- Use try-catch blocks for I/O operations
- Handle rate limits and API failures gracefully

### Database
- **PostgreSQL** with JOOQ for type-safe queries
- Database configuration in `DataSourceConfig`
- Use `MystiGuardianDatabase` for database operations

## Apprenticeship Scraping

### Sources
1. **Higher In** (`RATE_MY_APPRENTICESHIP`)
   - Scrapes from `higherin.com`
   - Supports multiple tech, business, engineering categories
   - Returns `HigherinApprenticeship` objects

2. **GOV.UK Find an Apprenticeship** (`GOV_UK`)
   - Scrapes from `findapprenticeship.service.gov.uk`
   - Returns `FindAnApprenticeshipJob` objects

### Google Sheets Integration
- Apprenticeships are stored in Google Sheets
- Sheet structure: ID, Title, Company, Location, Categories, Salary, Opening Date, Closing Date, URL, Source
- New apprenticeships are detected by comparing with existing sheet data
- Duplicate detection uses apprenticeship ID

### Discord Posting
- New apprenticeships are posted to configured Discord channels
- Embeds show apprenticeship details with category tags
- Role pinging based on category mappings in `config.json`
- Batch posting (10 embeds per message) with rate limit handling

## Configuration

### config.json Structure
- `token` - Discord bot token
- `prefix` - Command prefix (default: `/`)
- `ownerId` - Bot owner Discord ID
- `discordAuth` - OAuth configuration
- `da` (Digital Apprenticeships) - Scraping configuration
  - `channelIds` - Discord channel IDs to post to
  - `spreadsheetId` - Google Sheets ID
  - `categoryGroups` - Category to role mappings
- `rolesToPing` - List of role IDs to ping for new apprenticeships

## Testing
- Unit tests in `src/test/java`
- Use JUnit 5
- Mock external APIs where appropriate

## Common Tasks

### Adding a New Apprenticeship Source
1. Create a new class implementing `Apprenticeship` interface
2. Add source enum to `ApprenticeshipSource`
3. Implement scraping logic in `ApprenticeshipScraper`
4. Update sheet format in `ApprenticeshipSpreadsheetManager.convertJobsToRows()`

### Adding a New Discord Command
1. Create class implementing `ISlashCommand`
2. Add `@SlashCommand` annotation
3. Implement `onSlashCommand()` method
4. Command auto-registers via `AutoSlashAdder`

### Modifying Scraping Categories
- Update `HIGHERIN_CATEGORIES` list in `ApprenticeshipScraper`
- Categories are used to filter relevant apprenticeships
- Each category corresponds to a URL path on Higher In

## Important Notes
- Always use "apprenticeship" terminology in code, comments, and logs
- The project focuses on **degree apprenticeships** (Level 6+)
- Respect rate limits when scraping (500ms delay between requests)
- Use virtual threads for concurrent operations when available (Java 21+)
- The bot runs as a long-lived service with scheduled tasks for scraping

