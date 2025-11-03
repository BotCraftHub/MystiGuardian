# JavaDoc Documentation Guide

## Completed Documentation (19 files)

### ApprenticeshipScraper Module
- ✅ Core interfaces and models (Apprenticeship, ApprenticeshipSource, HigherinApprenticeship, FindAnApprenticeship)
- ✅ Scrapers (HigherinScraper, FindAnApprenticeshipScraper, ApprenticeshipScraper)
- ✅ Configuration (DAConfig, JobCategoryGroup, **HigherinCategories**, **GovUkRoutes**)

### DiscordBot Module
- ✅ Events (NewDAEvent, EventDispatcher, GenericSubscribeEvent)
- ✅ Commands (PingCommand, ShutdownCommand)
- ✅ Interfaces (ISlashCommand, SlashEventBus)

### OAuth Module
- ✅ OAuth.java - OAuth2 service

## Architecture Improvements

### Category Configuration Classes
Categories are now stored in dedicated configuration classes for better maintainability:

- **HigherinCategories.java** - 83 Higher In categories organized by 14 sectors
  - Easier to add/remove categories
  - Grouped by sector (Technology, Finance, Business, etc.)
  - Immutable lists using `List.of()`
  - Utility method `getAllCategories()` for flat list

- **GovUkRoutes.java** - 15 GOV.UK route categories with IDs
  - Centralized route ID mapping
  - Easy to update when GOV.UK changes routes
  - Immutable map using `Map.ofEntries()`
  - Utility method `getAllRoutes()` for access

### Benefits
- ✅ Single source of truth for categories
- ✅ Type-safe and compile-time checked
- ✅ Easier to maintain and update
- ✅ Better separation of concerns
- ✅ Follows utility class pattern (private constructor)
- ✅ Comprehensive JavaDoc for each sector

## Quick Templates

### Class
```java
/**
 * Brief description.
 * 
 * <p>Detailed explanation with key features.
 * 
 * @see RelatedClass
 */
public class MyClass {
```

### Method
```java
/**
 * Does something specific.
 * 
 * @param name parameter description
 * @return return value description
 */
public String myMethod(String name) {
```

### Record
```java
/**
 * Data structure description.
 * 
 * @param field1 first field description
 * @param field2 second field description
 */
public record MyRecord(String field1, int field2) {
```

### Utility Class
```java
/**
 * Utility class for X.
 * 
 * <p>Contains static constants and helper methods.
 */
public final class MyUtil {
    private MyUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static final List<String> CONSTANTS = List.of(...);
}
```

## Standards

### Use
- ✅ "apprenticeship" (not "job")
- ✅ Higher In, GOV.UK (full names)
- ✅ `@param`, `@return`, `@see` tags
- ✅ HTML tags: `<p>`, `<ul>`, `<li>`, `<b>`
- ✅ Immutable collections: `List.of()`, `Map.ofEntries()`
- ✅ Utility class pattern for configuration classes

### Avoid
- ❌ Redundant comments when JavaDoc exists
- ❌ Obvious descriptions ("getter for X")
- ❌ Repeating method signature in words
- ❌ Hard-coded constants in scraper classes
- ❌ Mutable collections for configuration

## Generate JavaDoc
```bash
./gradlew javadoc
open build/docs/javadoc/index.html
```

---
*Last Updated: November 2, 2025*

