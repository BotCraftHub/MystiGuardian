# Database Migration to Flyway - Complete

## ✅ Migration Complete

The MystiGuardian project has successfully migrated from programmatic database table creation to **Flyway-based SQL migrations**.

## What Changed

### Before (Old System)
- ❌ `DatabaseTables.java` - Created tables programmatically in Java
- ❌ `HandleDataBaseTables.java` - Managed table creation process
- ❌ `DatabaseTableBuilder` and related builder classes - Java-based schema definition
- ❌ No version control for database schema
- ❌ No migration history tracking

### After (New System)
- ✅ **Flyway** manages all database migrations
- ✅ **SQL migration files** in `src/main/resources/db/migration/`
- ✅ **Version-controlled schema** changes
- ✅ **Automatic migration** on bot startup
- ✅ **Migration history** tracked in `flyway_schema_history` table

## Current State

### Active Components

#### 1. MystiGuardianDatabase.java
**Status:** ✅ Updated to use Flyway

```java
Flyway flyway = Flyway.configure()
    .dataSource(ds)
    .locations("classpath:db/migration")
    .baselineOnMigrate(true)
    .load();

int migrationsExecuted = flyway.migrate().migrationsExecuted;
```

**Features:**
- Initializes Flyway on startup
- Runs pending migrations automatically
- Logs migration execution
- Uses existing HikariCP data source

#### 2. Migration Files

**V1__Initial_schema.sql** ✅
- All base tables (reload_audit, warns, time_out, kick, ban, etc.)
- Proper constraints and indexes
- PostgreSQL-specific syntax

**V2__Add_stored_files_table.sql** ✅
- File management table
- Unique constraints
- Performance indexes

#### 3. Dependencies

**gradle/libs.versions.toml** ✅
```toml
[versions]
flyway = "10.21.0"

[libraries]
flyway-core = { module = "org.flywaydb:flyway-core", version.ref = "flyway" }
flyway-database-postgresql = { module = "org.flywaydb:flyway-database-postgresql", version.ref = "flyway" }

[bundles]
database = [..., "flyway-core", "flyway-database-postgresql"]
```

### Deprecated Components

The following classes are now **deprecated** and marked for removal:

#### 1. DatabaseTables.java
**Status:** ⚠️ Deprecated (forRemoval = true)
- No longer called by any active code
- Kept for reference only
- Will be removed in a future version

#### 2. HandleDataBaseTables.java
**Status:** ⚠️ Deprecated (forRemoval = true)
- No longer used
- Kept for reference only
- Will be removed in a future version

#### 3. Builder Classes (database.builder package)
**Status:** ⚠️ Deprecated (forRemoval = true)
- `DatabaseTableBuilder`
- `DatabaseColumnBuilder`
- `DatabaseTableBuilderImpl`
- `DatabaseColumnBuilderImpl`
- `DatabaseColumnBuilderRecord`

All marked as deprecated with removal planned.

## How It Works Now

### On Bot Startup

1. **Database Connection**
   ```
   [INFO] Attempting to establish database connection...
   [INFO] Database connection established successfully.
   ```

2. **Flyway Initialization**
   ```
   [INFO] Running database migrations...
   ```

3. **Migration Execution**
   - Flyway checks `flyway_schema_history` table
   - Identifies pending migrations
   - Executes them in order (V1, V2, ...)
   - Records execution in history table

4. **Completion**
   ```
   [INFO] Database migrations completed. X migration(s) executed.
   [INFO] Database tables initialized successfully.
   ```

### Migration Flow Diagram

```
Bot Startup
    ↓
MystiGuardianDatabase constructor
    ↓
HikariCP connection pool initialized
    ↓
Flyway.configure()
    ↓
flyway.migrate()
    ↓
Check flyway_schema_history
    ↓
Execute pending migrations (V1, V2, ...)
    ↓
Update flyway_schema_history
    ↓
Bot ready with up-to-date schema
```

## Benefits Realized

### 1. Version Control ✅
- All schema changes in Git
- Easy to review changes in PRs
- Clear history of what changed when

### 2. Reproducibility ✅
- Any developer can recreate schema
- CI/CD can set up test databases
- Production deployments are predictable

### 3. Team Collaboration ✅
- Multiple developers can work independently
- Merge conflicts are clear and visible
- No more "it works on my machine"

### 4. Audit Trail ✅
- `flyway_schema_history` shows:
  - Which migrations ran
  - When they ran
  - Whether they succeeded
  - Migration checksums for integrity

### 5. Safety ✅
- Migrations are immutable (can't change executed ones)
- Checksums prevent tampering
- Failed migrations won't corrupt state
- Easy to understand what went wrong

## Database Schema

### Current Tables (After Migrations)

From **V1__Initial_schema.sql:**
- `reload_audit` - Bot reload tracking
- `warns` - User warnings
- `amount_of_warns` - Warning counts
- `time_out` - Timeout records
- `amount_of_time_outs` - Timeout counts
- `kick` - Kick records
- `amount_of_kicks` - Kick counts
- `ban` - Ban records
- `amount_of_bans` - Ban counts
- `soft_ban` - Soft ban records
- `oauth` - OAuth tokens
- `audit_channel` - Audit log channels

From **V2__Add_stored_files_table.sql:**
- `stored_files` - File management system
  - With indexes on guild_id and file_name
  - Unique constraint on (guild_id, file_name)

### Flyway System Table
- `flyway_schema_history` - Migration tracking (created by Flyway)

## Adding New Migrations

### Step-by-Step Process

1. **Create migration file**
   ```bash
   touch src/main/resources/db/migration/V3__Description_of_change.sql
   ```

2. **Write SQL**
   ```sql
   -- V3__Add_tags_table.sql
   CREATE TABLE IF NOT EXISTS tags (
       id BIGSERIAL PRIMARY KEY,
       guild_id VARCHAR(256) NOT NULL,
       tag_name VARCHAR(256) NOT NULL,
       tag_content TEXT NOT NULL,
       created_at TIMESTAMP NOT NULL,
       UNIQUE (guild_id, tag_name)
   );
   
   CREATE INDEX idx_tags_guild_id ON tags(guild_id);
   ```

3. **Test locally**
   - Start bot
   - Verify migration executes
   - Check table exists

4. **Commit and deploy**
   - Migration runs automatically on next bot start
   - No manual intervention needed

### Naming Convention

```
V{VERSION}__{DESCRIPTION}.sql
```

Examples:
- `V1__Initial_schema.sql`
- `V2__Add_stored_files_table.sql`
- `V3__Add_tags_table.sql`
- `V4__Add_column_to_users.sql`

**Rules:**
- Version must be numeric and sequential
- Two underscores between version and description
- Use snake_case for description
- Use descriptive names

## Verification

### Check Migration Status

**Via Database:**
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

Expected result:
```
installed_rank | version | description              | success
---------------+---------+--------------------------+---------
1              | 1       | Initial schema           | t
2              | 2       | Add stored files table   | t
```

**Via Logs:**
```
[INFO] Database migrations completed. 2 migration(s) executed.
```

### Verify Tables Exist

```sql
\dt
```

Should show all tables from migrations.

## Troubleshooting

### Migration Fails

**Symptom:** Error during migration execution

**Solution:**
1. Check error message in logs
2. Fix SQL in a NEW migration file (V3, V4, etc.)
3. Never modify executed migrations
4. Restart bot to apply fix

### Checksum Mismatch

**Symptom:** "Validate failed: Migration checksum mismatch"

**Cause:** An executed migration file was modified

**Solution:**
1. Don't modify executed migrations
2. If absolutely necessary, manually update checksum in flyway_schema_history
3. Better: Create a new migration with the fix

### Tables Already Exist

**Symptom:** "Table already exists" error

**Cause:** Tables exist but Flyway history doesn't

**Solution:**
- Flyway's `baselineOnMigrate(true)` handles this automatically
- Creates baseline entry for existing database
- Future migrations run normally

## Best Practices

### ✅ DO

- Create new migrations for schema changes
- Test migrations locally before committing
- Use descriptive migration names
- Keep migrations small and focused
- Use `IF NOT EXISTS` for safety
- Add indexes for performance
- Document complex migrations

### ❌ DON'T

- Modify executed migration files
- Delete migration files
- Skip version numbers
- Put multiple unrelated changes in one migration
- Use database-specific syntax without good reason
- Forget to test migrations

## Removal Plan

The deprecated classes will be removed in version **0.1.0**:

### Phase 1: Current (0.0.8)
- ✅ Mark classes as deprecated
- ✅ Add `@Deprecated(forRemoval = true)`
- ✅ Update documentation
- ✅ Ensure no active code uses them

### Phase 2: Next Minor Release (0.0.9)
- Keep deprecated classes
- Monitor for any issues
- Document removal in CHANGELOG

### Phase 3: Major Release (0.1.0)
- Delete deprecated classes:
  - `DatabaseTables.java`
  - `HandleDataBaseTables.java`
  - All `database.builder` classes
- Update documentation
- Celebrate clean codebase! 🎉

## Summary

✅ **Migration is complete and working**
- Flyway is integrated and active
- All schema defined in SQL migrations
- Old Java-based system deprecated
- New migrations follow SQL approach
- Documentation updated
- Best practices documented

🚀 **Ready for Production**
- Schema changes are version-controlled
- Deployments are automated
- History is tracked
- Team collaboration improved

📚 **Well Documented**
- This migration guide
- Flyway usage guide
- Setup instructions
- Troubleshooting help

The migration to Flyway is **100% complete and production-ready**! 🎉

