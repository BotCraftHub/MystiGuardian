# Automatic Copyright Year Update Workflow

## Overview

This workflow automatically updates the copyright year in all project files every January 1st at 00:00 UTC.

## Workflow File

📁 **Location:** `.github/workflows/update-copyright-year.yml`

## How It Works

### Schedule
- **Runs:** January 1st at 00:00 UTC every year
- **Can be triggered manually:** Yes, via GitHub Actions UI

### What It Does

1. **Calculates Years**
   - Gets current year (e.g., 2026)
   - Gets previous year (e.g., 2025)

2. **Updates build.gradle.kts**
   - Finds the Spotless license header configuration
   - Updates: `Copyright 2025` → `Copyright 2026`

3. **Updates Documentation**
   - Scans all `.md` files (README, SECURITY, CONTRIBUTING, etc.)
   - Updates copyright notices: `Copyright 2025 RealYusufIsmail` → `Copyright 2026 RealYusufIsmail`
   - Updates the `NOTICE` file

4. **Runs Spotless**
   - Executes: `./gradlew spotlessApply --no-daemon`
   - This updates the license header in **all Java files** automatically

5. **Creates Pull Request**
   - Branch name: `chore/update-copyright-2026`
   - Title: `🗓️ Update copyright year to 2026`
   - Labels: `chore`, `automated`, `copyright`
   - Assigns to repository owner
   - Includes detailed description of changes

### Files That Get Updated

| File Type | Example | How Updated |
|-----------|---------|-------------|
| `build.gradle.kts` | Root build file | `sed` replacement |
| `*.java` | All Java source files | Spotless (`./gradlew spotlessApply`) |
| `*.md` | Documentation files | `sed` replacement |
| `NOTICE` | Legal notices | `sed` replacement |

## Testing the Workflow

### Option 1: Manual Trigger (Recommended for Testing)

1. Go to your repository on GitHub
2. Click **Actions** tab
3. Select **Update Copyright Year** workflow
4. Click **Run workflow** button
5. Select branch (usually `main`)
6. Click **Run workflow**

This will run the workflow immediately, creating a test PR.

### Option 2: Test Locally

You can simulate what the workflow does:

```bash
# Navigate to your project
cd "/Users/yusufismail/local github/MystiGuardian"

# Calculate years
CURRENT_YEAR=$(date +%Y)
PREVIOUS_YEAR=$((CURRENT_YEAR - 1))
echo "Testing update from $PREVIOUS_YEAR to $CURRENT_YEAR"

# Update build.gradle.kts (on macOS, sed requires .bak extension)
sed -i.bak "s/Copyright $PREVIOUS_YEAR RealYusufIsmail/Copyright $CURRENT_YEAR RealYusufIsmail/g" build.gradle.kts
rm -f build.gradle.kts.bak

# Update documentation files
find . -name "*.md" -type f -not -path "*/node_modules/*" -not -path "*/build/*" | while read -r file; do
    if grep -q "Copyright.*$PREVIOUS_YEAR.*RealYusufIsmail" "$file"; then
        sed -i.bak "s/Copyright.*$PREVIOUS_YEAR.*RealYusufIsmail/Copyright $CURRENT_YEAR RealYusufIsmail/g" "$file"
        rm -f "$file.bak"
        echo "Updated: $file"
    fi
done

# Update NOTICE
if [ -f "NOTICE" ]; then
    sed -i.bak "s/Copyright $PREVIOUS_YEAR RealYusufIsmail/Copyright $CURRENT_YEAR RealYusufIsmail/g" NOTICE
    rm -f NOTICE.bak
fi

# Run Spotless
./gradlew spotlessApply --no-daemon

# Check what changed
git status
git diff

# If you want to undo (don't commit yet!)
git restore .
```

## Expected Pull Request

When the workflow runs successfully, it creates a PR that looks like this:

### Title
```
🗓️ Update copyright year to 2026
```

### Body
```markdown
## 🗓️ Automatic Copyright Year Update

This PR automatically updates the copyright year from **2025** to **2026**.

### Changes Made

- ✅ Updated copyright year in `build.gradle.kts` Spotless configuration
- ✅ Ran `./gradlew spotlessApply` to update all Java file headers
- ✅ Updated copyright year in documentation files
- ✅ Updated NOTICE file

### Files Modified

- `build.gradle.kts` - Spotless license header configuration
- `**/*.java` - All Java source files (via Spotless)
- `**/*.md` - Documentation files
- `NOTICE` - Legal notices

### Review Checklist

- [ ] Verify copyright year is correct (2026)
- [ ] Check that Java files have updated headers
- [ ] Confirm documentation reflects new year
- [ ] Run tests to ensure no functionality was affected
```

### Labels
- `chore`
- `automated`
- `copyright`

### Assignee
Repository owner (you)

## What You Need to Do

### When the PR is Created (January 1st)

1. **Review the PR**
   - Check that the year was updated correctly
   - Verify a few Java files to ensure headers are correct
   - Review documentation changes

2. **Run Tests (Optional)**
   - The workflow doesn't break functionality, but you can test if desired
   - GitHub Actions will run your normal CI/CD on the PR

3. **Merge the PR**
   - Once satisfied, merge the PR
   - The copyright year is now updated for the entire year!

### If Something Goes Wrong

If the workflow fails or creates an incorrect PR:

1. **Close the PR** - The branch will be deleted automatically
2. **Fix the issue** in the workflow file
3. **Trigger manually** to test the fix
4. **Wait until next January 1st** or trigger manually when ready

## Maintenance

### Updating the Workflow

If you need to modify the workflow:

1. Edit `.github/workflows/update-copyright-year.yml`
2. Test by triggering manually
3. Commit and push changes

### Changing Copyright Format

If you want to change the copyright format (e.g., use a range like "2024-2026"):

1. Update `build.gradle.kts` Spotless configuration
2. Update the workflow's `sed` commands to match new format
3. Run Spotless manually: `./gradlew spotlessApply`

## Troubleshooting

### Workflow Doesn't Run on January 1st

**Possible causes:**
- GitHub Actions scheduled workflows can be delayed
- Repository might be inactive (workflows are disabled after 60 days of inactivity)

**Solution:**
- Trigger manually from Actions tab
- Check repository settings → Actions → Allow workflows

### PR Not Created

**Possible causes:**
- No files needed updating (year already correct)
- Workflow lacks permissions

**Solution:**
- Check workflow run logs in Actions tab
- Verify `GITHUB_TOKEN` has `contents: write` and `pull-requests: write` permissions

### Merge Conflicts

**Possible cause:**
- Someone manually updated some files but not others

**Solution:**
- Resolve conflicts manually
- Or close PR and re-run workflow after fixing conflicts

### Wrong Year Updated

**Possible cause:**
- Workflow ran at wrong time or year calculation failed

**Solution:**
- Close the incorrect PR
- Fix the workflow if needed
- Run manually with correct parameters

## Benefits

✅ **Automated** - No manual work required
✅ **Consistent** - All files updated at once
✅ **Auditable** - Changes reviewed via PR
✅ **Reliable** - Runs every year automatically
✅ **Transparent** - Clear PR description with all changes
✅ **Flexible** - Can be triggered manually anytime

## Related Documentation

- [LICENSE_HEADERS.md](LICENSE_HEADERS.md) - License header management with Spotless
- [SECURITY_IMPLEMENTATION.md](SECURITY_IMPLEMENTATION.md) - Overall security setup
- [CONTRIBUTING.md](../CONTRIBUTING.md) - Contribution guidelines

---

**Copyright 2024-2025 RealYusufIsmail - Licensed under Apache License 2.0**

