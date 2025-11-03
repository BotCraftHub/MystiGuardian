# Setup Instructions

## Prerequisites
- PostgreSQL database running
- Database credentials configured in config.json
- Java 21+ installed
- Gradle wrapper available

## Step-by-Step Setup

### 1. Update Dependencies
The Flyway dependencies have already been added to `gradle/libs.versions.toml`:
- flyway-core: 10.21.0
- flyway-database-postgresql: 10.21.0

### 2. Database Configuration
Ensure your `config.json` or environment has database credentials:
```json
{
  "dataSource": {
    "host": "localhost",
    "port": "5432",
    "name": "mystiguardian",
    "user": "your_user",
    "password": "your_password"
  }
}
```

### 3. First Run - Let Flyway Create Tables
When you start the bot for the first time:
1. Flyway will automatically run migrations
2. All tables will be created from the SQL migration files
3. The `stored_files` table will be created
4. Migration history will be tracked in `flyway_schema_history`

### 4. Generate JOOQ Classes
After the tables exist in the database, generate JOOQ classes:

```bash
# Set your database credentials as Gradle properties
./gradlew :DiscordBot:generateJooq \
  -PdataSourceUrl=jdbc:postgresql://localhost:5432/mystiguardian \
  -PdataSourceUser=your_user \
  -PdataSourcePassword=your_password
```

Or add to `gradle.properties`:
```properties
dataSourceUrl=jdbc:postgresql://localhost:5432/mystiguardian
dataSourceUser=your_user
dataSourcePassword=your_password
```

Then run:
```bash
./gradlew :DiscordBot:generateJooq
```

### 5. Rebuild the Project
```bash
./gradlew :DiscordBot:build
```

### 6. Run the Bot
```bash
./gradlew :DiscordBot:run
# or
java -jar build/libs/MystiGuardian-*.jar
```

## Verification

### Check Migrations Ran
Look for these log messages:
```
[INFO] Running database migrations...
[INFO] Database migrations completed. X migration(s) executed.
```

### Verify Tables Exist
Connect to PostgreSQL and check:
```sql
-- Check migration history
SELECT * FROM flyway_schema_history;

-- Check stored_files table exists
\d stored_files

-- Or
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
```

### Test Commands in Discord
1. `/uploadfile name:test file:[some file] description:Test file`
2. `/listfiles`
3. `/getfile name:test`
4. `/deletefile name:test`

## Troubleshooting

### "Cannot resolve symbol 'STORED_FILES'"
**Cause**: JOOQ classes not yet generated  
**Solution**: Run `./gradlew :DiscordBot:generateJooq` after tables are created

### "Cannot resolve method 'migrate' in 'Object'"
**Cause**: IDE type inference issue with Flyway  
**Solution**: This is an IDE warning only. The code compiles and runs correctly. The explicit type `Flyway flyway` resolves this.

### "Table already exists" error
**Cause**: Tables exist but Flyway history doesn't  
**Solution**: Flyway's `baselineOnMigrate(true)` handles this automatically

### Connection refused
**Cause**: PostgreSQL not running or wrong credentials  
**Solution**: 
1. Start PostgreSQL: `pg_ctl start` or `brew services start postgresql`
2. Verify credentials in config.json
3. Check database exists: `psql -l`

### Migration checksum failed
**Cause**: Migration file was modified after execution  
**Solution**: Never modify executed migrations. Create a new V3 migration instead.

## Migration Files Location
```
DiscordBot/src/main/resources/db/migration/
├── V1__Initial_schema.sql          # All base tables
└── V2__Add_stored_files_table.sql  # File management tables
```

## Expected JOOQ Output
After generation, you should see:
```
DiscordBot/src/main/jooq/io/github/yusufsdiscordbot/mystiguardian/db/
├── Tables.java
├── tables/
│   └── records/
│       ├── StoredFilesRecord.java  # New record class
│       ├── WarnsRecord.java
│       └── ... (other records)
```

## Development Workflow

### Adding New Features with Database Changes
1. Create new migration: `V3__Add_feature.sql`
2. Write SQL DDL statements
3. Restart bot (migrations run automatically)
4. Regenerate JOOQ: `./gradlew :DiscordBot:generateJooq`
5. Write Java code using new JOOQ classes
6. Build and test

## Production Deployment

1. **Backup database** before deploying
2. Stop the bot
3. Deploy new code with migration files
4. Start bot - Flyway will run new migrations
5. Verify migrations succeeded in logs
6. Test new features

## Rollback Strategy

If a migration fails in production:
1. Fix the issue
2. Create a new migration (V4, V5, etc.) with the fix
3. **Never** delete or modify executed migrations
4. Flyway will skip already-executed migrations and run new ones

## Support

If you encounter issues:
1. Check logs for Flyway messages
2. Verify database connectivity
3. Ensure migration files are in correct location
4. Check `flyway_schema_history` table for migration status
5. Review JOOQ generation logs

## Summary

✅ Flyway migrations in `db/migration/`  
✅ Automatic schema management  
✅ JOOQ code generation from database  
✅ File management commands ready  
✅ Full documentation provided  

Once you run the bot and generate JOOQ classes, everything will be fully functional!

