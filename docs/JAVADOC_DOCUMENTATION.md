# JavaDoc Documentation Guide

## Completed Documentation (17 files)

### ApprenticeshipScraper Module
- ✅ Core interfaces and models (Apprenticeship, ApprenticeshipSource, HigherinApprenticeship, FindAnApprenticeshipJob)
- ✅ Scrapers (HigherinScraper, FindAnApprenticeshipScraper, ApprenticeshipScraper)
- ✅ Configuration (DAConfig, JobCategoryGroup)

### DiscordBot Module
- ✅ Events (NewDAEvent, EventDispatcher, GenericSubscribeEvent)
- ✅ Commands (PingCommand, ShutdownCommand)
- ✅ Interfaces (ISlashCommand, SlashEventBus)

### OAuth Module
- ✅ OAuth.java - OAuth2 service

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

## Standards

### Use
- ✅ "apprenticeship" (not "job")
- ✅ Higher In, GOV.UK (full names)
- ✅ `@param`, `@return`, `@see` tags
- ✅ HTML tags: `<p>`, `<ul>`, `<li>`, `<b>`

### Avoid
- ❌ Redundant comments when JavaDoc exists
- ❌ Obvious descriptions ("getter for X")
- ❌ Repeating method signature in words

## Generate JavaDoc
```bash
./gradlew javadoc
open build/docs/javadoc/index.html
```

---
*Last Updated: November 2, 2025*

