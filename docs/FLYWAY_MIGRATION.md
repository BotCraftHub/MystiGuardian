# Flyway Migration Guide

## Overview

MystiGuardian now uses [Flyway](https://flywaydb.org/) for database migrations instead of the previous `DatabaseTables` approach. This provides better version control, easier collaboration, and more reliable database schema management.

## What Changed

### Before (DatabaseTables)
- Database tables were created programmatically in Java
- Schema changes required code modifications
- No migration history tracking
- Difficult to rollback changes

### After (Flyway)
- Database tables are defined in SQL migration files
- Schema changes are versioned and tracked
- Automatic migration on startup
- Clear migration history in database

## Migration Files

Migration files are located in: `DiscordBot/src/main/resources/db/migration/`

### Naming Convention
```
V{VERSION}__{DESCRIPTION}.sql
```

Example: `V1__Initial_schema.sql`, `V2__Add_stored_files_table.sql`

### Current Migrations

1. **V1__Initial_schema.sql** - Creates all base tables:
   - reload_audit
   - warns & amount_of_warns
   - time_out & amount_of_time_outs
   - kick & amount_of_kicks
   - ban & amount_of_bans
   - soft_ban
   - oauth
   - audit_channel

2. **V2__Add_stored_files_table.sql** - Adds file management:
   - stored_files table
   - Indexes for performance

## Adding New Migrations

When you need to modify the database schema:

1. Create a new migration file in `DiscordBot/src/main/resources/db/migration/`
2. Follow the naming convention: `V{NEXT_VERSION}__{Description}.sql`
3. Write your SQL DDL statements
4. Restart the bot - Flyway will automatically apply new migrations

### Example: Adding a New Column
```sql
-- V3__Add_file_size_column.sql
ALTER TABLE stored_files ADD COLUMN file_size BIGINT;
```

### Example: Creating a New Table
```sql
-- V4__Add_tags_table.sql
CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    guild_id VARCHAR(256) NOT NULL,
    tag_name VARCHAR(256) NOT NULL,
    tag_content TEXT NOT NULL,
    created_by VARCHAR(256) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (guild_id, tag_name)
);

CREATE INDEX idx_tags_guild_id ON tags(guild_id);
```

## Configuration

Flyway is configured in `MystiGuardianDatabase.java`:

```java
val flyway = Flyway.configure()
    .dataSource(ds)
    .locations("classpath:db/migration")
    .baselineOnMigrate(true)
    .load();

int migrationsExecuted = flyway.migrate().migrationsExecuted;
```

### Key Settings
- **dataSource**: Uses the existing HikariCP connection pool
- **locations**: Migration files directory
- **baselineOnMigrate**: Allows Flyway to work with existing databases

## Benefits

1. **Version Control**: Migration files are tracked in Git
2. **Reproducibility**: Easy to recreate database schema
3. **Collaboration**: Multiple developers can create migrations independently
4. **Audit Trail**: `flyway_schema_history` table tracks all migrations
5. **Rollback Support**: Can write down migrations if needed
6. **Testing**: Easy to test migrations in development

## Flyway Schema History

Flyway automatically creates a `flyway_schema_history` table that tracks:
- Installed migrations
- Migration checksums
- Execution time
- Success/failure status

Query it to see migration history:
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

## Best Practices

1. **Never modify executed migrations** - Create new migrations instead
2. **Test migrations locally** before deploying
3. **Use transactions** where possible (most DDL in PostgreSQL is transactional)
4. **Keep migrations small** and focused
5. **Write descriptive migration names**
6. **Include rollback instructions** in comments if needed

## Troubleshooting

### Migration Failed
If a migration fails:
1. Check the error message in logs
2. Fix the SQL in a new migration
3. Flyway will attempt failed migrations again on restart

### Checksum Mismatch
If you accidentally modified an executed migration:
```sql
-- Option 1: Repair the checksum (use cautiously)
DELETE FROM flyway_schema_history WHERE version = 'X';

-- Option 2: Create a new migration with the fix
```

### Baseline Existing Database
For databases that already exist:
```java
flyway.baseline(); // Run once to baseline
flyway.migrate();  // Then apply new migrations
```

## Dependencies

Added to `gradle/libs.versions.toml`:
```toml
[versions]
flyway = "10.21.0"

[libraries]
flyway-core = { module = "org.flywaydb:flyway-core", version.ref = "flyway" }
flyway-database-postgresql = { module = "org.flywaydb:flyway-database-postgresql", version.ref = "flyway" }

[bundles]
database = ["jooq", "jooq-meta", "jooq-codegen", "postgresql", "hikaricp", "flyway-core", "flyway-database-postgresql"]
```

## Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Flyway SQL Migrations](https://flywaydb.org/documentation/concepts/migrations#sql-based-migrations)
- [PostgreSQL DDL](https://www.postgresql.org/docs/current/ddl.html)

## Removed Components

The following are no longer used:
- ❌ `DatabaseTables.java` - Replaced by migration files
- ❌ `HandleDataBaseTables.java` - No longer needed
- ❌ Manual table creation logic - Handled by Flyway

Database handler classes (`MystiGuardianDatabaseHandler`) remain unchanged and continue to work with the schema.

