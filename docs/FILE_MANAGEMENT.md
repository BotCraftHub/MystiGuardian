# File Management System

The file management system allows users to upload, store, and retrieve files (such as tips, documents, guides, etc.) directly through Discord. Files are stored using Discord's CDN and metadata is tracked in the database.

## Features

- **Upload files** from Discord with custom names and descriptions
- **Retrieve files** by name with formatted embed responses
- **List all files** in a server with pagination
- **Delete files** with permission controls
- **Automatic duplicate detection** - prevents overwriting existing files
- **Size validation** - enforces Discord's 8MB limit
- **Persistent storage** - Files stored on Discord CDN with database metadata

## Commands

### `/uploadfile`
Upload and store a file for later retrieval.

**Options:**
- `name` (required): A unique identifier for the file
- `file` (required): The file to upload
- `description` (optional): A description of the file's contents

**Example:**
```
/uploadfile name:interview-tips file:tips.pdf description:Common interview questions and answers
```

**Permissions:** Everyone
**Max file size:** 8MB

### `/getfile`
Retrieve a stored file by its name.

**Options:**
- `name` (required): The name of the file to retrieve

**Example:**
```
/getfile name:interview-tips
```

**Permissions:** Everyone

### `/listfiles`
Display all files stored in the current server.

**Example:**
```
/listfiles
```

**Permissions:** Everyone
**Note:** Shows up to 25 files per message (Discord embed limit)

### `/deletefile`
Delete a stored file.

**Options:**
- `name` (required): The name of the file to delete

**Example:**
```
/deletefile name:interview-tips
```

**Permissions:** 
- File uploader (can delete their own files)
- Users with "Manage Messages" permission (can delete any file)

## Database Schema

The `stored_files` table stores metadata about uploaded files:

| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL | Primary key |
| guild_id | VARCHAR(256) | Discord server ID |
| file_name | VARCHAR(256) | Unique file identifier per guild |
| file_type | VARCHAR(256) | File extension |
| description | VARCHAR(512) | Optional file description |
| file_url | VARCHAR(512) | Discord CDN URL |
| uploaded_by | VARCHAR(256) | User ID of uploader |
| uploaded_at | TIMESTAMP | Upload timestamp |

**Indexes:**
- Primary key on `id`
- Unique constraint on `(guild_id, file_name)`
- Index on `guild_id` for faster lookups
- Index on `file_name` for search optimization

## Implementation Details

### File Storage
Files are stored using Discord's CDN which provides:
- Permanent URLs that don't expire
- Global CDN distribution
- Automatic image optimization
- No additional storage costs

### Migration
The database table is created via Flyway migration `V2__Add_stored_files_table.sql`.

### Database Handler
`MystiGuardianDatabaseHandler.StoredFiles` provides methods:
- `storeFile()` - Insert new file record
- `getFile()` - Retrieve file by name and guild
- `getAllFiles()` - List all files for a guild
- `deleteFile()` - Remove file record
- `updateDescription()` - Update file description
- `fileExists()` - Check if file name is taken

## Use Cases

1. **Tips & Guides**: Store interview tips, study guides, resource lists
2. **Documentation**: Server rules, bot documentation, FAQs
3. **Templates**: Resume templates, cover letter examples
4. **Resources**: Useful PDFs, spreadsheets, images
5. **Reference Materials**: Code snippets, cheat sheets, diagrams

## Error Handling

The system handles:
- Duplicate file names
- Missing required parameters
- File size limits
- Permission validation
- Database errors
- Network failures

## Future Enhancements

Potential improvements:
- File categories/tags for organization
- Search functionality
- File versioning
- Usage statistics
- Bulk upload/download
- File expiration dates
- Admin-only files
- File sharing across servers

