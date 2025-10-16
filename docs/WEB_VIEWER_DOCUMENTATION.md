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

### **1. Configure Web Service in config.json**

Add the web service configuration to your `config.json` file:

```json
{
  "token": "YOUR_DISCORD_BOT_TOKEN_HERE",
  "ownerId": "YOUR_DISCORD_USER_ID",
  // ... other config ...
  
  "webService": {
    "port": 8080,
    "baseUrl": "https://your-domain.com"
  }
}
```

**Important:** Replace `https://your-domain.com` with your actual public domain or IP address. This is what users will use to access the apprenticeship viewer.

### **2. Configuration Options**

**For Local Testing:**
```json
"webService": {
  "port": 8080,
  "baseUrl": "http://localhost:8080"
}
```
⚠️ **Warning:** `localhost` URLs only work on your machine. Other users won't be able to access the links!

**For Production with Domain:**
```json
"webService": {
  "port": 8080,
  "baseUrl": "https://yourdomain.com"
}
```

**For Production with Public IP:**
```json
"webService": {
  "port": 8080,
  "baseUrl": "http://123.45.67.89:8080"
}
```

### **3. Making Your Bot Accessible to Others**

If you want other users to access the apprenticeship viewer, you need to ensure your bot is accessible from the internet:

**Option A: Use a Domain Name**
- Register a domain (e.g., from Namecheap, GoDaddy, etc.)
- Point the domain to your server's IP address
- Use `https://yourdomain.com` as your base URL
- Configure SSL/TLS for HTTPS (recommended)

**Option B: Use Your Public IP**
- Find your public IP address
- Forward port 8080 on your router to your bot's machine
- Use `http://YOUR_PUBLIC_IP:8080` as your base URL

**Option C: Use a Tunneling Service (For Testing)**
- Use ngrok or similar service:
  ```bash
  ngrok http 8080
  ```
- Use the provided ngrok URL as your base URL (e.g., `https://abc123.ngrok.io`)

### **3.1 How to Find Your Base URL**

Your base URL depends on where and how your bot is hosted. Here's how to determine it for different scenarios:

#### **Hosted on a Server/VPS (like Kinetic Hosting, DigitalOcean, AWS, etc.)**

1. **Check your hosting control panel** for:
   - Your server's **IP address** (e.g., `123.45.67.89`)
   - Your **assigned domain** (if provided by the host)
   - **SSH connection details** (usually shows the IP or domain)

2. **Log into your server** and run:
   ```bash
   curl ifconfig.me
   ```
   This will show your public IP address.

3. **Your base URL will be one of:**
   - If you have a domain: `https://yourdomain.com` or `http://yourdomain.com`
   - If using IP only: `http://YOUR_IP:8080` (e.g., `http://123.45.67.89:8080`)
   - If host provides subdomain: `https://yourbot.kinetichosting.com`

#### **Common Hosting Providers Examples**

**Kinetic Hosting / Similar Game Hosting:**
- Usually provides: An IP address like `123.45.67.89` and port ranges
- Your base URL: `http://123.45.67.89:8080`
- You may need to request port 8080 be opened (contact their support)
- Some provide subdomains like: `yourserver.host.com`

**DigitalOcean / Linode / Vultr:**
- Provides: A static IP address
- Your base URL: `http://YOUR_DROPLET_IP:8080`
- Example: `http://159.65.123.45:8080`

**AWS EC2:**
- Provides: Public IPv4 address or Elastic IP
- Your base URL: `http://ec2-XX-XX-XX-XX.compute.amazonaws.com`
- Or: `http://YOUR_ELASTIC_IP:8080`

**Heroku:**
- Provides: Automatic subdomain
- Your base URL: `https://your-app-name.herokuapp.com`
- Note: Port is handled automatically (use 443/80, not 8080)

**Localhost / Home Computer:**
- If testing locally only: `http://localhost:8080`
- If exposing to internet: `http://YOUR_HOME_IP:8080` (requires port forwarding)

#### **How to Test Your Base URL**

1. **Start your bot** with the configured base URL
2. **From another device or network**, open your browser and go to:
   ```
   http://YOUR_BASE_URL/apprenticeships?token=test
   ```
3. **Expected results:**
   - ✅ Success: You see an "Access Denied" page (this is good! It means the server is reachable)
   - ❌ Failed: Connection timeout or "can't reach this page" (server not accessible)

#### **Common Issues and Solutions**

**Issue: "Connection refused" or "Can't reach server"**
- Solution: Check firewall rules, ensure port 8080 is open
- Contact hosting support to open port 8080
- Check if your hosting requires specific port ranges

**Issue: "This site can't provide a secure connection" (ERR_SSL_PROTOCOL_ERROR)**
- Solution: You used `https://` but the server doesn't have SSL
- Change to `http://` in config.json
- Or set up SSL certificate (recommended for production)

**Issue: "localhost" in URL when users try to access**
- Solution: You forgot to change the config! Update `config.json` with your actual IP/domain

#### **Quick Reference: Config Examples by Hosting Type**

**Kinetic Hosting or similar:**
```json
"webService": {
  "port": 8080,
  "baseUrl": "http://YOUR_SERVER_IP:8080"
}
```

**With Custom Domain:**
```json
"webService": {
  "port": 8080,
  "baseUrl": "https://bot.yourdomain.com"
}
```

**VPS with Direct IP:**
```json
"webService": {
  "port": 8080,
  "baseUrl": "http://159.89.123.45:8080"
}
```

**Using ngrok (temporary testing):**
```json
"webService": {
  "port": 8080,
  "baseUrl": "https://abc123def.ngrok.io"
}
```

#### **Pro Tip: Using a Custom Domain with Your Hosting**

Many hosting providers allow you to:
1. Buy a domain (e.g., `mybot.com` for ~$10/year)
2. Point an A record to your server's IP
3. Use a clean URL like `https://mybot.com` instead of `http://123.45.67.89:8080`

This is much more professional and easier for users to remember!

### **4. Web Service Auto-Initialization**

The web service is now automatically initialized when the bot starts. No manual code changes needed! Just configure it in `config.json` and restart your bot.

### **Old Manual Method (Deprecated)**

The following manual initialization is no longer needed:
```java
// ❌ OLD WAY - Don't use this anymore
ApprenticeshipWebService.initialize(8080, "http://your-domain:8080");
```

The bot now reads the configuration from `config.json` and initializes automatically.

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
