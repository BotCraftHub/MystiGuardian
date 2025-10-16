# Apprenticeship Web Viewer Feature

## Overview
A new feature that allows users to view available apprenticeships through a beautiful web interface instead of relying on the Excel spreadsheet. Users can run a Discord command to get a temporary, secure link to view and filter all available apprenticeships.

---

## 🎯 Features

### **Discord Command**
- **Command**: `/view-apprenticeships`
- **Description**: Generates a temporary, secure link to view apprenticeships
- **Access**: Private (ephemeral) - only visible to the user who runs it
- **Expiry**: Links expire after 24 hours for security

### **Web Interface Features**
✅ **Real-time Search** - Search by job title or company name  
✅ **Category Filtering** - Filter by specific job categories  
✅ **Location Filtering** - Find jobs in specific locations  
✅ **Smart Sorting** - Sort by closing date, posted date, company, or title  
✅ **Urgency Indicators** - Shows days left with urgent warnings for < 7 days  
✅ **Statistics Dashboard** - See total opportunities and filtered results  
✅ **Responsive Design** - Works on desktop, tablet, and mobile  
✅ **Beautiful UI** - Modern gradient design with smooth animations  
✅ **Auto-filters** - Only shows jobs that haven't expired  

---

## 📸 What Users See

### **Discord Command Response**
When a user runs `/view-apprenticeships`, they receive:
```
🎓 View Available Apprenticeships

Click the link below to view all available apprenticeships in an interactive web interface!

🔗 Access Link
[Click here to view apprenticeships](http://your-domain:8080/apprenticeships?token=xxx)

⏰ Link Expires
This link will expire in 24 hours

✨ Features
• Filter by category
• Sort by closing date
• Search by company or title
• View all details in one place

This link is private and only visible to you
```

### **Web Interface**
Users see a beautiful purple gradient page with:
- **Header**: "Available Apprenticeships" with subtitle
- **Filters Section**: 4 filter options (Search, Category, Location, Sort)
- **Stats**: Live count of total jobs and filtered results
- **Job Cards**: Each card shows:
  - Job title and company (with 🎓 emoji)
  - Location (📍), Salary (💰), Closing date (⏰)
  - Category tags
  - Time left indicator (urgent in red if < 7 days)
  - "Apply Now" button

---

## 🔧 How It Works

### **Architecture**

```
User runs command
    ↓
ViewApprenticeshipsCommand generates secure token
    ↓
Token stored in memory with 24h expiry
    ↓
User clicks link in Discord
    ↓
ApprenticeshipWebService validates token
    ↓
Fetches jobs from JobSpreadsheetManager
    ↓
Displays interactive HTML page
```

### **Security**
- **Unique Tokens**: Each link has a unique UUID token
- **Time-Limited**: Tokens expire after 24 hours
- **Automatic Cleanup**: Expired tokens removed every minute
- **In-Memory Storage**: Tokens not persisted to disk

### **Data Flow**
1. Web service fetches jobs from your Google Spreadsheet
2. Filters out expired jobs (closing date passed)
3. Returns data as JSON to web interface
4. JavaScript handles filtering/sorting client-side

---

## 🚀 Setup Instructions

### **1. Add Web Service Initialization**

Add to your main bot startup code (where you initialize other services):

```java
import io.github.yusufsdiscordbot.mystiguardian.web.ApprenticeshipWebService;

// In your main bot startup method:
public void startBot() {
    // ... existing initialization code ...
    
    // Initialize the web service
    // Port: 8080 (or your preferred port)
    // Base URL: Your server's public URL
    ApprenticeshipWebService.initialize(8080, "http://your-domain:8080");
    
    // ... rest of initialization ...
}
```

### **2. Configuration Options**

**For Local Testing:**
```java
ApprenticeshipWebService.initialize(8080, "http://localhost:8080");
```

**For Production:**
```java
ApprenticeshipWebService.initialize(8080, "https://yourdomain.com");
```

### **3. Port Forwarding (if needed)**

If running locally and want to share links:
- Open port 8080 on your router
- Or use a service like ngrok:
  ```bash
  ngrok http 8080
  ```
  Then use the ngrok URL as your base URL

### **4. Test the Feature**

1. Start your bot
2. Run `/view-apprenticeships` in Discord
3. Click the generated link
4. You should see the web interface with all apprenticeships

---

## 📊 Technical Details

### **Files Created**

1. **ViewApprenticeshipsCommand.java**
   - Discord slash command handler
   - Generates access tokens
   - Sends ephemeral message with link

2. **ApprenticeshipWebService.java**
   - HTTP server (built-in Java HttpServer)
   - Token management and validation
   - Serves HTML/CSS/JavaScript
   - REST API endpoint for job data

3. **JobSpreadsheetManager.java** (updated)
   - New method: `getAllJobsForWeb()`
   - Fetches all jobs from spreadsheet
   - Filters out expired jobs
   - Returns data as Map for JSON serialization

### **Endpoints**

- `GET /apprenticeships?token={token}` - HTML page
- `GET /api/apprenticeships?token={token}` - JSON API

### **Performance**

- **Threading**: Uses thread pool (10 threads) for concurrent requests
- **Caching**: Jobs fetched on-demand from spreadsheet
- **Memory**: Minimal - only stores active tokens
- **Scalability**: Can handle multiple simultaneous users

---

## 🎨 Customization

### **Change Colors**

Edit the gradient in `generateHtmlPage()`:
```css
background: linear-gradient(135deg, #YOUR_COLOR_1 0%, #YOUR_COLOR_2 100%);
```

### **Change Port**

```java
ApprenticeshipWebService.initialize(YOUR_PORT, "http://your-domain:YOUR_PORT");
```

### **Change Token Expiry**

In `ApprenticeshipWebService.java`:
```java
Instant expiry = Instant.now().plus(24, ChronoUnit.HOURS); // Change 24 to desired hours
```

---

## 🔒 Security Considerations

✅ **Tokens expire automatically**  
✅ **Each user gets unique token**  
✅ **No authentication credentials in URLs**  
✅ **Tokens can't be guessed (UUID)**  
✅ **Expired tokens automatically cleaned up**  
✅ **No data stored on client side**  

---

## 🐛 Troubleshooting

### **"Failed to generate access link"**
- Check if web service is initialized
- Verify port is not already in use
- Check logs for startup errors

### **"Access Denied" on web page**
- Token may have expired (24 hours)
- Run command again to get new link
- Check system clock is correct

### **Jobs not loading**
- Verify JobSpreadsheetManager is initialized
- Check Google Sheets API access
- Look for errors in console logs

### **Port already in use**
```
Error binding to port 8080
```
- Change port in initialization
- Or stop process using port 8080

---

## 📈 Future Enhancements

Possible improvements:
- **Email notifications** when new jobs posted
- **Save favorites** feature
- **Export to PDF** functionality
- **Analytics** on popular categories
- **Job alerts** based on criteria
- **Company profiles** with ratings
- **Application tracking**

---

## 🎉 Benefits Over Excel Spreadsheet

| Feature | Excel | Web Viewer |
|---------|-------|------------|
| Mobile friendly | ❌ | ✅ |
| Real-time search | ❌ | ✅ |
| No download needed | ❌ | ✅ |
| Urgency indicators | ❌ | ✅ |
| Auto-filters expired | ❌ | ✅ |
| Beautiful UI | ❌ | ✅ |
| Category filtering | Basic | Advanced |
| Sort options | Manual | Automatic |

---

## 📝 Example Usage

```
User: /view-apprenticeships
Bot: [Ephemeral Message]
     🎓 View Available Apprenticeships
     
     🔗 Access Link
     [Click here to view apprenticeships]
     
     ⏰ Link Expires: This link will expire in 24 hours
     
     ✨ Features
     • Filter by category
     • Sort by closing date
     • Search by company or title
     • View all details in one place

User: [Clicks link]
Browser: [Opens beautiful web interface]
         Shows 47 apprenticeships
         User can filter, search, and apply
```

---

## ✅ Checklist for Deployment

- [ ] Add web service initialization to bot startup
- [ ] Configure correct port and base URL
- [ ] Test locally with `localhost`
- [ ] Set up port forwarding or reverse proxy for production
- [ ] Test token expiry functionality
- [ ] Verify data loading from spreadsheet
- [ ] Test on mobile devices
- [ ] Monitor server logs for errors
- [ ] Document URL for your team

---

## 🆘 Support

If you encounter issues:
1. Check console logs for errors
2. Verify web service is initialized
3. Test with `curl http://localhost:8080/apprenticeships?token=test`
4. Ensure Google Sheets API is working
5. Check network/firewall settings

---

**Ready to use! Your users can now view apprenticeships in a beautiful, modern web interface! 🚀**

