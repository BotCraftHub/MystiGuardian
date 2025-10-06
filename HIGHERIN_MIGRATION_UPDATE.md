# HigherIn API Migration Update

## Date: October 6, 2025

## Changes Made

HigherIn has migrated their JSON API structure from snake_case to camelCase. The system has been updated to handle the new format.

### JSON Structure Changes

**Old Structure:**
```json
{
  "id": "35439",
  "title": "Job Title",
  "company": {
    "name": "Company Name",
    "small_logo": "logo_url"
  },
  "jobLocations": "Location",
  "salary": "Salary",
  "deadline": "date",
  "url": "url"
}
```

**New Structure:**
```json
{
  "id": 35439,
  "jobId": 35439,
  "jobTitle": "Register Your Interest: Cyber Security Engineering Degree Apprenticeship",
  "jobTypeNames": "Degree Apprenticeship",
  "deadline": null,
  "employmentStartDate": null,
  "url": "https://higherin.com/jobs/35439/...",
  "salary": "£20,000 - £21,999",
  "isPreReg": true,
  "jobLocationNames": "Newport",
  "jobLocationNamesTrimmed": "Newport",
  "relevantFor": "",
  "companyId": 296,
  "companyName": "Airbus",
  "smallLogo": "https://imagekit-production.higherin.com/..."
}
```

### Updated Field Mappings

| Old Field | New Field | Notes |
|-----------|-----------|-------|
| `title` | `jobTitle` | Job title field renamed |
| `company.name` | `companyName` | Company info now at root level |
| `company.small_logo` | `smallLogo` | Logo field flattened and renamed |
| `jobLocations` | `jobLocationNames` | Location field renamed |
| `salary` | `salary` | ✅ Unchanged |
| `deadline` | `deadline` | ✅ Unchanged |
| `url` | `url` | ✅ Unchanged |
| `id` | `id` | ✅ Unchanged |

### Files Modified

1. **ApprenticeshipScraper.java** - Updated `createHigherinJob()` method to use new field names
2. **JobSpreadsheetManager.java** - Fixed spreadsheet column mapping for different job sources
3. **HigherinJob.java** - Improved Discord embed display to always show job title
4. **FindAnApprenticeshipJob.java** - Improved Discord embed display for consistency

### Additional Improvements

- ✅ Fixed missing job titles in Discord embeds
- ✅ Fixed spreadsheet category column confusion between RateMyApprenticeship and GOV.UK jobs
- ✅ Added logging to detect missing job titles or company names
- ✅ Job title now displayed prominently as first field in Discord embeds

## Testing Recommendations

1. Run the bot and verify jobs from HigherIn scrape correctly
2. Check that Discord embeds show job titles properly
3. Verify spreadsheet has correct data in all columns
4. Check that both RateMyApprenticeship and GOV.UK jobs display correctly

