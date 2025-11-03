# Quick Start: File Management Commands

## Commands at a Glance

### Upload a File
```
/uploadfile name:tips file:[attach file] description:Interview tips
```

### Get a File
```
/getfile name:tips
```

### List All Files
```
/listfiles
```

### Delete a File
```
/deletefile name:tips
```

## Common Workflows

### Storing Tips/Guides
1. Prepare your file (PDF, DOC, TXT, etc.)
2. Use `/uploadfile` with a memorable name
3. Add a description so others know what it contains
4. Share the name with your server members

### Retrieving Stored Content
1. Use `/listfiles` to see what's available
2. Use `/getfile name:filename` to get the specific file
3. Click the download link in the embed

### Managing Files
- **View all files**: `/listfiles`
- **Update description**: Delete and re-upload (or use database directly)
- **Remove old files**: `/deletefile name:filename` (if you uploaded it or have Manage Messages)

## Permission Summary

| Command | Who Can Use | Notes |
|---------|-------------|-------|
| `/uploadfile` | Everyone | Max 8MB per file |
| `/getfile` | Everyone | Public access to all stored files |
| `/listfiles` | Everyone | Shows all files in the server |
| `/deletefile` | File uploader OR users with Manage Messages | Permission check enforced |

## Tips & Best Practices

1. **Use descriptive names**: `interview-tips` not `file1`
2. **Add descriptions**: Help others understand the file's content
3. **Keep names unique**: Each file name must be unique per server
4. **Check existing files**: Use `/listfiles` before uploading to avoid duplicates
5. **Reasonable file sizes**: While 8MB is the limit, smaller is better
6. **Organize with prefixes**: `guide-`, `tip-`, `template-` etc.

## Example Use Cases

### Interview Preparation Server
```
/uploadfile name:interview-questions file:questions.pdf description:Common technical interview questions
/uploadfile name:resume-template file:template.docx description:ATS-friendly resume template
/uploadfile name:salary-negotiation file:tips.pdf description:Guide to salary negotiation
```

### Study Group
```
/uploadfile name:math-formulas file:formulas.pdf description:All calculus formulas
/uploadfile name:study-schedule file:schedule.xlsx description:Exam preparation timeline
/uploadfile name:past-papers file:papers.pdf description:Last 5 years exam papers
```

### Development Team
```
/uploadfile name:code-style file:style-guide.pdf description:Team coding standards
/uploadfile name:api-docs file:api.pdf description:Internal API documentation
/uploadfile name:deployment-checklist file:checklist.md description:Pre-deployment steps
```

## Error Messages & Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| "File Already Exists" | Name is taken | Use a different name or delete existing |
| "File Too Large" | Over 8MB | Compress or split the file |
| "Missing required options" | Forgot name or file | Provide both name and file parameters |
| "Permission Denied" | Not uploader/no Manage Messages | Ask an admin or the original uploader |
| "File Not Found" | Wrong name or deleted | Check `/listfiles` for correct name |

## Quick Reference

**File Requirements:**
- Max size: 8MB
- Any file type supported by Discord
- Unique name per server required

**Storage:**
- Files stored on Discord CDN
- Permanent URLs (don't expire)
- Metadata in database

**Access:**
- Public within the server
- Anyone can view/download
- Only specific users can delete

## Support

For issues or questions:
1. Check `/listfiles` to verify file exists
2. Ensure you have proper permissions
3. Check file size is under 8MB
4. Contact server admins for help

