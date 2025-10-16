/*
 * Copyright 2024 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.yusufsdiscordbot.mystiguardian.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApprenticeshipWebService {
    private static ApprenticeshipWebService instance;
    private final Map<String, TokenInfo> accessTokens = new ConcurrentHashMap<>();
    private final int port;
    private final String baseUrl;
    private HttpServer server;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ApprenticeshipWebService(int port, String baseUrl) {
        this.port = port;
        this.baseUrl = baseUrl;
    }

    public static synchronized ApprenticeshipWebService getInstance() {
        if (instance == null) {
            // Default configuration - can be overridden via config
            instance = new ApprenticeshipWebService(8080, "http://localhost:8080");
        }
        return instance;
    }

    public static synchronized void initialize(int port, String baseUrl) {
        instance = new ApprenticeshipWebService(port, baseUrl);
        instance.start();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/apprenticeships", new ApprenticeshipHandler());
            server.createContext("/api/apprenticeships", new ApprenticeshipApiHandler());
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            logger.info("Apprenticeship web service started on port {}", port);

            // Start cleanup task for expired tokens
            startTokenCleanupTask();
        } catch (IOException e) {
            logger.error("Failed to start web service", e);
        }
    }

    public String generateAccessToken() {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(24, ChronoUnit.HOURS);
        accessTokens.put(token, new TokenInfo(expiry));
        logger.debug("Generated access token: {} (expires: {})", token, expiry);
        return token;
    }

    private boolean isValidToken(String token) {
        TokenInfo info = accessTokens.get(token);
        if (info == null) {
            return false;
        }
        if (Instant.now().isAfter(info.expiry)) {
            accessTokens.remove(token);
            return false;
        }
        return true;
    }

    private void startTokenCleanupTask() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Instant now = Instant.now();
                accessTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expiry));
            }
        }, 60000, 60000); // Run every minute
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private class ApprenticeshipHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String token = extractToken(query);

            if (token == null || !isValidToken(token)) {
                sendResponse(exchange, 403, generateErrorPage("Access Denied", "Invalid or expired token."));
                return;
            }

            String html = generateHtmlPage();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            sendResponse(exchange, 200, html);
        }

        private String extractToken(String query) {
            if (query == null) return null;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals("token")) {
                    return pair[1];
                }
            }
            return null;
        }
    }

    private class ApprenticeshipApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String token = extractToken(query);

            if (token == null || !isValidToken(token)) {
                sendJsonResponse(exchange, 403, Map.of("error", "Invalid or expired token"));
                return;
            }

            try {
                List<Map<String, Object>> jobs = fetchAllJobs();
                sendJsonResponse(exchange, 200, Map.of("jobs", jobs, "count", jobs.size()));
            } catch (Exception e) {
                logger.error("Error fetching jobs", e);
                sendJsonResponse(exchange, 500, Map.of("error", "Failed to fetch jobs"));
            }
        }

        private String extractToken(String query) {
            if (query == null) return null;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals("token")) {
                    return pair[1];
                }
            }
            return null;
        }

        private List<Map<String, Object>> fetchAllJobs() throws Exception {
            var jobManager = MystiGuardianConfig.getJobSpreadsheetManager();
            if (jobManager == null) {
                logger.warn("Job spreadsheet manager not initialized");
                return Collections.emptyList();
            }

            try {
                return jobManager.getAllJobsForWeb();
            } catch (IOException e) {
                logger.error("Error fetching jobs from spreadsheet", e);
                throw new Exception("Failed to fetch jobs", e);
            }
        }
    }

    private String generateHtmlPage() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Available Apprenticeships</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        .container { max-width: 1200px; margin: 0 auto; }
        header {
            background: white;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            margin-bottom: 30px;
        }
        h1 { color: #333; margin-bottom: 10px; font-size: 2.5em; }
        .subtitle { color: #666; font-size: 1.1em; }
        .filters {
            background: white;
            padding: 20px;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 15px;
        }
        .filter-group { display: flex; flex-direction: column; }
        label { font-weight: 600; color: #555; margin-bottom: 5px; font-size: 0.9em; }
        input, select {
            padding: 10px;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            font-size: 1em;
            transition: border-color 0.3s;
        }
        input:focus, select:focus { outline: none; border-color: #667eea; }
        .stats {
            background: white;
            padding: 15px 20px;
            border-radius: 10px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .stat-item { display: flex; align-items: center; gap: 10px; }
        .stat-number { font-size: 2em; font-weight: bold; color: #667eea; }
        .stat-label { color: #666; font-size: 0.9em; }
        .jobs-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .job-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s, box-shadow 0.3s;
            cursor: pointer;
            position: relative;
            overflow: hidden;
        }
        .job-card:hover { transform: translateY(-5px); box-shadow: 0 15px 30px rgba(0,0,0,0.2); }
        .job-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
            background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
        }
        .job-source {
            position: absolute;
            top: 15px;
            right: 15px;
            background: #f0f0f0;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 0.75em;
            color: #666;
        }
        .job-title {
            color: #333;
            font-size: 1.3em;
            font-weight: bold;
            margin-bottom: 10px;
            padding-right: 80px;
        }
        .job-company { color: #667eea; font-size: 1.1em; margin-bottom: 15px; font-weight: 600; }
        .job-details { display: flex; flex-direction: column; gap: 10px; margin-bottom: 15px; }
        .job-detail { display: flex; align-items: center; gap: 8px; color: #666; font-size: 0.9em; }
        .job-categories { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 15px; }
        .category-tag {
            background: #f0f0ff;
            color: #667eea;
            padding: 5px 12px;
            border-radius: 15px;
            font-size: 0.85em;
            font-weight: 500;
        }
        .job-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 15px;
            border-top: 1px solid #e0e0e0;
        }
        .time-left { font-size: 0.9em; color: #666; }
        .time-left.urgent { color: #e74c3c; font-weight: bold; }
        .apply-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 10px 20px;
            border-radius: 25px;
            text-decoration: none;
            font-weight: 600;
            transition: transform 0.2s;
            display: inline-block;
        }
        .apply-btn:hover { transform: scale(1.05); }
        .loading { text-align: center; padding: 50px; color: white; font-size: 1.2em; }
        .no-results { text-align: center; padding: 50px; background: white; border-radius: 15px; color: #666; }
        .emoji { font-size: 1.2em; }
        @media (max-width: 768px) {
            .jobs-grid { grid-template-columns: 1fr; }
            h1 { font-size: 1.8em; }
        }
    </style>
</head>
<body>
    <div class="container">
        <header>
            <h1>🎓 Available Apprenticeships</h1>
            <p class="subtitle">Find your perfect degree apprenticeship opportunity</p>
        </header>
        <div class="filters">
            <div class="filter-group">
                <label for="search">🔍 Search</label>
                <input type="text" id="search" placeholder="Search by title or company...">
            </div>
            <div class="filter-group">
                <label for="category">📚 Category</label>
                <select id="category"><option value="">All Categories</option></select>
            </div>
            <div class="filter-group">
                <label for="location">📍 Location</label>
                <input type="text" id="location" placeholder="Filter by location...">
            </div>
            <div class="filter-group">
                <label for="sort">📊 Sort By</label>
                <select id="sort">
                    <option value="closing">Closing Date (Soonest)</option>
                    <option value="posted">Recently Posted</option>
                    <option value="company">Company A-Z</option>
                    <option value="title">Title A-Z</option>
                </select>
            </div>
        </div>
        <div class="stats">
            <div class="stat-item">
                <div class="stat-number" id="totalJobs">0</div>
                <div class="stat-label">Total Opportunities</div>
            </div>
            <div class="stat-item">
                <div class="stat-number" id="shownJobs">0</div>
                <div class="stat-label">Shown</div>
            </div>
        </div>
        <div id="jobsContainer" class="jobs-grid">
            <div class="loading">Loading apprenticeships...</div>
        </div>
    </div>
    <script>
        let allJobs = [];
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');
        
        async function fetchJobs() {
            try {
                const response = await fetch(`/api/apprenticeships?token=${token}`);
                if (!response.ok) throw new Error('Failed to fetch jobs');
                const data = await response.json();
                allJobs = data.jobs || [];
                populateCategories();
                displayJobs();
            } catch (error) {
                console.error('Error fetching jobs:', error);
                document.getElementById('jobsContainer').innerHTML = 
                    '<div class="no-results">❌ Failed to load apprenticeships. Please try refreshing the page.</div>';
            }
        }
        
        function populateCategories() {
            const categories = new Set();
            allJobs.forEach(job => {
                if (job.categories && Array.isArray(job.categories)) {
                    job.categories.forEach(cat => categories.add(cat));
                }
            });
            const select = document.getElementById('category');
            Array.from(categories).sort().forEach(cat => {
                const option = document.createElement('option');
                option.value = cat;
                option.textContent = cat.split('-').map(word => 
                    word.charAt(0).toUpperCase() + word.slice(1)
                ).join(' ');
                select.appendChild(option);
            });
        }
        
        function displayJobs() {
            const searchTerm = document.getElementById('search').value.toLowerCase();
            const categoryFilter = document.getElementById('category').value;
            const locationFilter = document.getElementById('location').value.toLowerCase();
            const sortBy = document.getElementById('sort').value;
            
            let filtered = allJobs.filter(job => {
                const matchesSearch = !searchTerm || 
                    job.title.toLowerCase().includes(searchTerm) ||
                    job.companyName.toLowerCase().includes(searchTerm);
                const matchesCategory = !categoryFilter ||
                    (job.categories && job.categories.includes(categoryFilter));
                const matchesLocation = !locationFilter ||
                    job.location.toLowerCase().includes(locationFilter);
                return matchesSearch && matchesCategory && matchesLocation;
            });
            
            filtered.sort((a, b) => {
                switch(sortBy) {
                    case 'closing': return new Date(a.closingDate) - new Date(b.closingDate);
                    case 'posted': return new Date(b.createdAtDate || b.openingDate) - new Date(a.createdAtDate || a.openingDate);
                    case 'company': return a.companyName.localeCompare(b.companyName);
                    case 'title': return a.title.localeCompare(b.title);
                    default: return 0;
                }
            });
            
            document.getElementById('totalJobs').textContent = allJobs.length;
            document.getElementById('shownJobs').textContent = filtered.length;
            
            const container = document.getElementById('jobsContainer');
            if (filtered.length === 0) {
                container.innerHTML = '<div class="no-results">😕 No apprenticeships match your filters. Try adjusting your search.</div>';
                return;
            }
            
            container.innerHTML = filtered.map(job => {
                const daysLeft = job.closingDate ? 
                    Math.ceil((new Date(job.closingDate) - new Date()) / (1000 * 60 * 60 * 24)) : null;
                const urgentClass = daysLeft && daysLeft <= 7 ? 'urgent' : '';
                const timeLeftText = daysLeft ? (daysLeft > 0 ? `${daysLeft} days left` : 'Expired') : 'No deadline';
                const categories = job.categories && job.categories.length > 0 ?
                    `<div class="job-categories">
                        ${job.categories.slice(0, 3).map(cat => 
                            `<span class="category-tag">${cat.split('-').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(' ')}</span>`
                        ).join('')}
                        ${job.categories.length > 3 ? `<span class="category-tag">+${job.categories.length - 3} more</span>` : ''}
                    </div>` : '';
                
                return `
                    <div class="job-card">
                        <div class="job-source">${job.source || 'Unknown'}</div>
                        <div class="job-title">${job.title}</div>
                        <div class="job-company">${job.companyName}</div>
                        <div class="job-details">
                            <div class="job-detail"><span class="emoji">📍</span><span>${job.location}</span></div>
                            <div class="job-detail"><span class="emoji">💰</span><span>${job.salary || 'Not specified'}</span></div>
                            ${job.closingDate ? `<div class="job-detail"><span class="emoji">⏰</span><span>Closes: ${new Date(job.closingDate).toLocaleDateString()}</span></div>` : ''}
                        </div>
                        ${categories}
                        <div class="job-footer">
                            <div class="time-left ${urgentClass}">⌛ ${timeLeftText}</div>
                            <a href="${job.url}" target="_blank" class="apply-btn">Apply Now</a>
                        </div>
                    </div>
                `;
            }).join('');
        }
        
        document.getElementById('search').addEventListener('input', displayJobs);
        document.getElementById('category').addEventListener('change', displayJobs);
        document.getElementById('location').addEventListener('input', displayJobs);
        document.getElementById('sort').addEventListener('change', displayJobs);
        fetchJobs();
    </script>
</body>
</html>
        """;
    }

    private String generateErrorPage(String title, String message) {
        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>%s</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
        }
        .error-container {
            background: white;
            padding: 50px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            text-align: center;
            max-width: 500px;
        }
        h1 { color: #e74c3c; }
        p { color: #666; font-size: 1.1em; }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>%s</h1>
        <p>%s</p>
    </div>
</body>
</html>
        """, title, title, message);
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Map<String, Object> data) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        String json = objectMapper.writeValueAsString(data);
        sendResponse(exchange, statusCode, json);
    }

    private static class TokenInfo {
        final Instant expiry;
        TokenInfo(Instant expiry) {
            this.expiry = expiry;
        }
    }
}
