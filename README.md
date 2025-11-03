<div align="center">
    <img src="logo.png" alt="logo" width="50%" height="50%">
</div>

# MystiGuardian

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Code of Conduct](https://img.shields.io/badge/Code%20of%20Conduct-Contributor%20Covenant-purple.svg)](CODE_OF_CONDUCT.md)
[![Security](https://img.shields.io/badge/Security-Policy-red.svg)](SECURITY.md)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

MystiGuardian - Your server's mystical protector and entertainment extraordinaire. Uniting moderation with fun, it
ensures a secure and delightful Discord experience.

> **⚠️ License Notice**: This project is licensed under Apache License 2.0. Any use, modification, or distribution must comply with the license terms. See [LICENSE](LICENSE) and [NOTICE](NOTICE) for details.

## ✨ Features

### 🎓 Apprenticeship Scraping
Automatically scrapes and posts degree apprenticeship opportunities to Discord channels:

- **Higher In (Rate My Apprenticeship)** - Scrapes 150+ categories including:
  - Technology (Computer Science, Cyber Security, Software Engineering, AI, etc.)
  - Business & Finance (Accounting, Banking, Management Consulting, etc.)
  - Engineering (Mechanical, Civil, Electrical, Aerospace, etc.)
  - And many more across all industries

- **GOV.UK Find an Apprenticeship** - Comprehensive coverage of all 15 official route categories:
  - Agriculture, environmental and animal care
  - Business and administration
  - Care services
  - Catering and hospitality
  - Construction and the built environment
  - Creative and design
  - Digital
  - Education and early years
  - Engineering and manufacturing
  - Hair and beauty
  - Health and science
  - Legal, finance and accounting
  - Protective services
  - Sales, marketing and procurement
  - Transport and logistics

#### Key Features:
- ✅ Automated scraping with configurable scheduling
- ✅ Duplicate detection - only new apprenticeships are posted
- ✅ Google Sheets integration for data persistence
- ✅ Rich Discord embeds with all relevant information
- ✅ Category-based role pinging
- ✅ Batch posting with rate limiting
- ✅ Robust error handling - errors in one category don't stop others

### 🛡️ Moderation & Security
- Server protection and moderation tools
- User management and permissions
- Secure data handling with encryption

### 🎮 Entertainment & Utility
- Fun commands and interactions
- Server utilities and management
- YouTube integration for content updates

## 🏗️ Architecture

### Module Structure
- **DiscordBot** - Main bot with JDA integration and commands
- **ApprenticeshipScraper** - Web scraping and Google Sheets management
  - `HigherinScraper` - Specialized scraper for Higher In
  - `FindAnApprenticeshipScraper` - Specialized scraper for GOV.UK
  - `ApprenticeshipScraper` - Facade coordinating both scrapers
- **OAuth** - OAuth authentication and web service
- **Annotations** - Custom annotations for the project

### Technology Stack
- **Java 21+** with virtual threads support
- **JDA (Java Discord API)** for Discord integration
- **OkHttp** for efficient HTTP requests with connection pooling
- **Jsoup** for HTML parsing (GOV.UK)
- **Jackson** for JSON processing (Higher In)
- **Google Sheets API** for data persistence
- **PostgreSQL** with JOOQ for database operations
- **Gradle** with Kotlin DSL and Version Catalog

## 📋 Configuration

The following is required in your `config.json` file:

```json
{
  "token": "",
  "ownerId": "",
  "githubToken": "",
  "daConfig": {
    "discordChannelId": "",
    "guildId": "",
    "spreadsheetId": ""
  },
  "youtube": {
    "apiKey": "",
    "channelId": "",
    "discordChannelId": "",
    "guildId": ""
  },
  "dataSource": {
    "user": "",
    "password": "",
    "driver": "org.postgresql.Driver",
    "port": "",
    "name": "postgres",
    "host": "",
    "url": ""
  },
  "discord-auth": {
    "clientId": "",
    "clientSecret": ""
  },
  "tripAdvisor": {
    "apiKey": ""
  },
  "log": {
    "logGuildId": "",
    "logChannelId": ""
  }
}
```

### Encryption Keys
You will also need a public and private key for the bot to use for encryption. These should be placed in your home directory under the names `public.key` and `private.key` respectively.

### Google Sheets Setup
For apprenticeship scraping, you'll need:
1. A Google Cloud project with Sheets API enabled
2. Service account credentials
3. A Google Sheet with appropriate permissions for the service account
4. The spreadsheet ID configured in `daConfig.spreadsheetId`

## 🚀 Recent Updates (v0.0.9)

### Externalized Category Configuration & Reorganization
- Moved hardcoded category and route lists into dedicated configuration classes for easier maintenance:
  - `HigherinCategories` - organizes Higher In category slugs by sector
  - `GovUkRoutes` - maps GOV.UK route names to official IDs
- Reorganized packages to improve clarity and separation of concerns (scraper, categories, apprenticeship models, manager, config)

### Maintainability & Documentation
- Cleaner code structure: specialized scrapers and a small facade `ApprenticeshipScraper`
- Improved Javadoc and documentation for category configuration classes
- Immutable collections used for category/route definitions

### Backwards-compatible Enhancements
- No breaking changes to spreadsheet format or Discord posting
- Scrapers now read categories from the new configuration classes, making updates easier without touching scraper logic

See [CHANGELOG.md](CHANGELOG.md) for the full version history.

## 📚 Documentation

- [VERSION_MANAGEMENT.md](docs/VERSION_MANAGEMENT.md) - Complete guide to Gradle Version Catalog
- [VERSION_CATALOG_QUICK_REFERENCE.md](docs/VERSION_CATALOG_QUICK_REFERENCE.md) - Quick reference for dependency management
- [CHANGELOG.md](CHANGELOG.md) - Detailed version history
- [.github/copilot-instructions.md](.github/copilot-instructions.md) - Project conventions and guidelines

## 🛠️ Building & Running

### Prerequisites
- Java 21 or higher
- PostgreSQL database
- Google Cloud project with Sheets API
- Discord bot token

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew run
```

Or use the shadow JAR:
```bash
java -jar build/libs/MystiGuardian-0.0.9.jar
```

## 🔒 Security & Legal

### License
**Copyright 2024 RealYusufIsmail**

This project is licensed under the **Apache License 2.0**. This means:

- ✅ **You CAN**: Use, modify, and distribute this code (with conditions)
- ✅ **You CAN**: Use this for commercial purposes
- ✅ **You CAN**: Distribute modified versions
- ❌ **You MUST**: Include the original copyright notice
- ❌ **You MUST**: Include a copy of the Apache License 2.0
- ❌ **You MUST**: State significant changes made
- ❌ **You CANNOT**: Use trademarks without permission
- ❌ **You CANNOT**: Hold the author liable

**Important Files:**
- [LICENSE](LICENSE) - Full Apache License 2.0 text
- [NOTICE](NOTICE) - Required attribution and third-party licenses
- [SECURITY.md](SECURITY.md) - Security policy and vulnerability reporting

### Copyright Protection

**⚠️ WARNING: Unauthorized use without proper attribution is copyright infringement and may result in legal action.**

All source code files include copyright headers. If you use this code:
1. **Retain all copyright notices** from source files
2. **Include LICENSE and NOTICE files** in distributions
3. **Document changes** you make to the code
4. **Provide attribution** to the original project

Example attribution:
```
Based on MystiGuardian by RealYusufIsmail
https://github.com/RealYusufIsmail/MystiGuardian
Licensed under Apache License 2.0
```

### Security

- 🔐 **Never commit sensitive data** - `config.json`, `service-account.json`, tokens, passwords
- 🛡️ **Report vulnerabilities responsibly** - See [SECURITY.md](SECURITY.md)
- 🔑 **Rotate credentials regularly** - Discord tokens, database passwords, API keys
- 📋 **Follow security best practices** - Use environment variables in production
- ⚠️ **Keep dependencies updated** - Run `./gradlew dependencyUpdates` regularly

**Found a security issue?** Contact the maintainer privately - do NOT open public issues for security vulnerabilities.

### Community Standards

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you agree to:

- 🤝 Be respectful and inclusive
- 💡 Provide constructive feedback
- 🎯 Focus on what's best for the community
- ⚖️ Respect intellectual property rights
- 🚫 Not engage in harassment or inappropriate behavior

**Violations of the license or code of conduct will result in removal from the community and may result in legal action.**

## 📄 License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

## 🤝 Contributing

**Contributions are welcome!** Please read our guidelines first:

- 📖 [CONTRIBUTING.md](CONTRIBUTING.md) - How to contribute
- 📋 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) - Community standards
- 🔒 [SECURITY.md](SECURITY.md) - Security policy

### Quick Start for Contributors

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes** following our code standards
4. **Format your code**: `./gradlew spotlessApply`
5. **Test your changes**: `./gradlew test`
6. **Commit with clear messages**: `git commit -m "feat: Add amazing feature"`
7. **Push to your fork**: `git push origin feature/amazing-feature`
8. **Open a Pull Request**

### Code Standards

This project uses:
- **Spotless** for code formatting (Google Java Style)
- **Gradle Version Catalog** for dependency management
- **Semantic Versioning** for releases
- **Keep a Changelog** format for version history

Please ensure your code follows the project conventions before submitting PRs.
