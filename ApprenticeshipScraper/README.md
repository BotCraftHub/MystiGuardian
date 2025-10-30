# Apprenticeship Scraper Module

This module handles scraping apprenticeship opportunities from various sources and managing them via Google Sheets.

## Components

### API Package
- **ApprenticeshipScraper**: Main scraper that fetches jobs from multiple sources
  - Higher Education (Rate My Apprenticeship)
  - Find An Apprenticeship (Gov.UK)
  
- **JobSpreadsheetManager**: Manages job data in Google Sheets and sends notifications to Discord

### Job Package
- **Job**: Interface defining the contract for job data
- **HigherinJob**: Implementation for Higher Education apprenticeships
- **FindAnApprenticeshipJob**: Implementation for Gov.UK apprenticeships
- **JobSource**: Enum defining job sources

### Config Package
- **DAConfig**: Configuration for Discord channels and Google Sheets integration
- **JobCategoryGroup**: Categorization of job types for role mapping

## Integration

This module is used by:
- **DiscordBot**: Main bot module that schedules job scraping and sends notifications
- **OAuth**: Web service for displaying available apprenticeships

## Dependencies

- Google Sheets API for data storage
- JDA for Discord integration (embeds)
- OkHttp for HTTP requests
- Jackson for JSON parsing
- Jsoup for HTML parsing

