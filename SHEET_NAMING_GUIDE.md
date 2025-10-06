# Spreadsheet Year-Based Naming Guide

## Overview
The JobSpreadsheetManager now automatically organizes jobs by academic/recruitment year using a September-based year boundary.

## How It Works

### Academic Year Calculation
- **January - August**: Uses the current calendar year
- **September - December**: Uses the next calendar year

### Examples

| Current Date | Sheet Name | Explanation |
|-------------|------------|-------------|
| January 2025 | Jobs 2025 | Still in the 2025 recruitment cycle |
| August 2025 | Jobs 2025 | Still in the 2025 recruitment cycle |
| September 2025 | Jobs 2026 | New recruitment cycle begins |
| October 2025 | Jobs 2026 | 2026 recruitment cycle |
| December 2025 | Jobs 2026 | 2026 recruitment cycle |
| January 2026 | Jobs 2026 | 2026 recruitment cycle continues |
| September 2026 | Jobs 2027 | New 2027 recruitment cycle begins |

## Current Status (October 2025)
- **Active Sheet**: "Jobs 2026"
- All new jobs will be saved to the "Jobs 2026" sheet
- The sheet was automatically created when the manager initialized

## Benefits

1. **Automatic Organization**: Jobs are automatically sorted by recruitment year
2. **Historical Tracking**: Previous years' jobs remain in their respective sheets
3. **Clean Separation**: Each recruitment cycle gets its own sheet
4. **No Manual Intervention**: The year transition happens automatically in September

## Technical Details

- The year calculation is handled by `getAcademicYear()` method
- Sheet names follow the pattern: "Jobs {YEAR}"
- Each year's sheet has the same column structure with headers
- Job ID deduplication only checks within the current year's sheet

## Migration Note

If you had a previous "Jobs" sheet without a year, it will remain untouched. The new year-based sheets are created separately. You may want to manually rename old sheets to match the year-based naming convention if needed.

