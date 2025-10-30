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
package io.github.yusufsdiscordbot.mystiguardian.oauth.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yusufsdiscordbot.mystiguardian.MystiGuardianConfig;
import lombok.extern.slf4j.Slf4j;
import spark.Spark;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApprenticeshipRequestsHandler {
    private static final Map<String, TokenInfo> accessTokens = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ApprenticeshipRequestsHandler() {
        setupRoutes();
        startTokenCleanupTask();
    }

    private void setupRoutes() {
        // HTML page endpoint
        Spark.get("/apprenticeships", (req, res) -> {
            String token = req.queryParams("token");

            if (token == null || !isValidToken(token)) {
                res.type("text/html; charset=UTF-8");
                res.status(403);
                return generateErrorPage("Access Denied", "Invalid or expired token.");
            }

            res.type("text/html; charset=UTF-8");
            res.status(200);
            return generateHtmlPage();
        });

        // JSON API endpoint
        Spark.get("/api/apprenticeships", (req, res) -> {
            String token = req.queryParams("token");

            if (token == null || !isValidToken(token)) {
                res.type("application/json");
                res.status(403);
                return objectMapper.writeValueAsString(Map.of("error", "Invalid or expired token"));
            }

            try {
                var apprenticeshipSpreadsheetManager = MystiGuardianConfig.getApprenticeshipSpreadsheetManager();
                if (apprenticeshipSpreadsheetManager == null) {
                    res.status(500);
                    return objectMapper.writeValueAsString(Map.of("error", "Apprenticeship manager not initialized"));
                }

                List<Map<String, Object>> apprenticeships = apprenticeshipSpreadsheetManager.getAllJobsForWeb();
                res.type("application/json");
                res.status(200);
                return objectMapper.writeValueAsString(Map.of("apprenticeships", apprenticeships));
            } catch (Exception e) {
                logger.error("Error fetching apprenticeships", e);
                res.status(500);
                return objectMapper.writeValueAsString(Map.of("error", "Failed to fetch apprenticeships"));
            }
        });
    }

    public static String generateAccessToken() {
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

    private String generateErrorPage(String title, String message) {
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>" + title +
               "</title></head><body><h1>" + title + "</h1><p>" + message + "</p></body></html>";
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
                        * {
                            margin: 0;
                            padding: 0;
                            box-sizing: border-box;
                        }
                        
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            min-height: 100vh;
                            padding: 20px;
                        }
                        
                        .container {
                            max-width: 1200px;
                            margin: 0 auto;
                        }
                        
                        header {
                            text-align: center;
                            color: white;
                            margin-bottom: 40px;
                        }
                        
                        h1 {
                            font-size: 2.5rem;
                            margin-bottom: 10px;
                        }
                        
                        .subtitle {
                            font-size: 1.2rem;
                            opacity: 0.9;
                        }
                        
                        .filters {
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                            margin-bottom: 30px;
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                            gap: 15px;
                        }
                        
                        .filter-group {
                            display: flex;
                            flex-direction: column;
                        }
                        
                        label {
                            font-weight: 600;
                            margin-bottom: 5px;
                            color: #333;
                        }
                        
                        input, select {
                            padding: 10px;
                            border: 2px solid #e0e0e0;
                            border-radius: 5px;
                            font-size: 1rem;
                            transition: border-color 0.3s;
                        }
                        
                        input:focus, select:focus {
                            outline: none;
                            border-color: #667eea;
                        }
                        
                        .stats {
                            background: rgba(255,255,255,0.9);
                            padding: 15px;
                            border-radius: 10px;
                            margin-bottom: 20px;
                            text-align: center;
                            font-size: 1.1rem;
                            font-weight: 600;
                            color: #333;
                        }
                        
                        .jobs-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
                            gap: 20px;
                        }
                        
                        .job-card {
                            background: white;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                            transition: transform 0.3s, box-shadow 0.3s;
                        }
                        
                        .job-card:hover {
                            transform: translateY(-5px);
                            box-shadow: 0 8px 12px rgba(0,0,0,0.15);
                        }
                        
                        .job-title {
                            font-size: 1.3rem;
                            font-weight: 700;
                            color: #667eea;
                            margin-bottom: 10px;
                        }
                        
                        .job-company {
                            font-size: 1.1rem;
                            color: #555;
                            margin-bottom: 15px;
                        }
                        
                        .job-detail {
                            margin: 8px 0;
                            color: #666;
                            display: flex;
                            align-items: center;
                            gap: 5px;
                        }
                        
                        .job-category {
                            display: inline-block;
                            background: #f0f0f0;
                            padding: 5px 10px;
                            border-radius: 5px;
                            font-size: 0.9rem;
                            margin-top: 10px;
                        }
                        
                        .apply-button {
                            display: inline-block;
                            background: #667eea;
                            color: white;
                            padding: 10px 20px;
                            border-radius: 5px;
                            text-decoration: none;
                            margin-top: 15px;
                            transition: background 0.3s;
                        }
                        
                        .apply-button:hover {
                            background: #5568d3;
                        }
                        
                        .urgent {
                            color: #e74c3c;
                            font-weight: 700;
                        }
                        
                        .loading {
                            text-align: center;
                            color: white;
                            font-size: 1.5rem;
                            padding: 40px;
                        }
                        
                        .error {
                            background: #fff;
                            padding: 20px;
                            border-radius: 10px;
                            text-align: center;
                            color: #e74c3c;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <header>
                            <h1>🎓 Available Apprenticeships</h1>
                            <p class="subtitle">Find your perfect opportunity</p>
                        </header>
                        
                        <div class="filters">
                            <div class="filter-group">
                                <label for="search">🔍 Search</label>
                                <input type="text" id="search" placeholder="Search by job title or company...">
                            </div>
                            <div class="filter-group">
                                <label for="category">📋 Category</label>
                                <select id="category">
                                    <option value="">All Categories</option>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label for="location">📍 Location</label>
                                <select id="location">
                                    <option value="">All Locations</option>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label for="sort">🔄 Sort By</label>
                                <select id="sort">
                                    <option value="closing">Closing Date</option>
                                    <option value="posted">Posted Date</option>
                                    <option value="company">Company</option>
                                    <option value="title">Apprenticeship Title</option>
                                </select>
                            </div>
                        </div>
                        
                        <div class="stats" id="stats">Loading...</div>
                        
                        <div class="jobs-grid" id="jobs-grid">
                            <div class="loading">Loading apprenticeships...</div>
                        </div>
                    </div>
                    
                    <script>
                        let allJobs = [];
                        let filteredJobs = [];
                        
                        async function loadJobs() {
                            try {
                                const urlParams = new URLSearchParams(window.location.search);
                                const token = urlParams.get('token');
                                const response = await fetch(`/api/apprenticeships?token=${token}`);
                                const data = await response.json();
                                
                                if (data.error) {
                                    document.getElementById('jobs-grid').innerHTML = `<div class="error">${data.error}</div>`;
                                    return;
                                }
                                
                                allJobs = data.apprenticeships;
                                filteredJobs = allJobs;
                                
                                populateFilters();
                                renderJobs();
                                updateStats();
                            } catch (error) {
                                console.error('Error loading apprenticeships:', error);
                                document.getElementById('jobs-grid').innerHTML = '<div class="error">Failed to load apprenticeships. Please try again later.</div>';
                            }
                        }
                        
                        function populateFilters() {
                            const categories = [...new Set(allJobs.flatMap(job => job.categories || []))].sort();
                            const locations = [...new Set(allJobs.map(job => job.location))].filter(loc => loc).sort();
                            
                            const categorySelect = document.getElementById('category');
                            categories.forEach(cat => {
                                const option = document.createElement('option');
                                option.value = cat;
                                option.textContent = cat;
                                categorySelect.appendChild(option);
                            });
                            
                            const locationSelect = document.getElementById('location');
                            locations.forEach(loc => {
                                const option = document.createElement('option');
                                option.value = loc;
                                option.textContent = loc;
                                locationSelect.appendChild(option);
                            });
                        }
                        
                        function filterJobs() {
                            const searchTerm = document.getElementById('search').value.toLowerCase();
                            const selectedCategory = document.getElementById('category').value;
                            const selectedLocation = document.getElementById('location').value;
                            
                            filteredJobs = allJobs.filter(job => {
                                const matchesSearch = (job.title || '').toLowerCase().includes(searchTerm) || 
                                                    (job.companyName || '').toLowerCase().includes(searchTerm);
                                const matchesCategory = !selectedCategory || (job.categories || []).includes(selectedCategory);
                                const matchesLocation = !selectedLocation || job.location === selectedLocation;
                                
                                return matchesSearch && matchesCategory && matchesLocation;
                            });
                            
                            sortJobs();
                            renderJobs();
                            updateStats();
                        }
                        
                        function sortJobs() {
                            const sortBy = document.getElementById('sort').value;
                            
                            filteredJobs.sort((a, b) => {
                                switch(sortBy) {
                                    case 'closing':
                                        return new Date(a.closingDate || 0) - new Date(b.closingDate || 0);
                                    case 'posted':
                                        return new Date(b.openingDate || 0) - new Date(a.openingDate || 0);
                                    case 'company':
                                        return (a.companyName || '').localeCompare(b.companyName || '');
                                    case 'title':
                                        return (a.title || '').localeCompare(b.title || '');
                                    default:
                                        return 0;
                                }
                            });
                        }
                        
                        function renderJobs() {
                            const grid = document.getElementById('jobs-grid');
                            
                            if (filteredJobs.length === 0) {
                                grid.innerHTML = '<div class="error">No apprenticeships found matching your criteria.</div>';
                                return;
                            }
                            
                            grid.innerHTML = filteredJobs.map(job => {
                                const closingDate = new Date(job.closingDate);
                                const today = new Date();
                                const daysLeft = Math.ceil((closingDate - today) / (1000 * 60 * 60 * 24));
                                const isUrgent = daysLeft < 7;
                                const categoriesDisplay = (job.categories || []).join(', ') || 'Not specified';
                                
                                return `
                                    <div class="job-card">
                                        <div class="job-title">${job.title || 'Untitled'}</div>
                                        <div class="job-company">${job.companyName || 'Company not specified'}</div>
                                        <div class="job-detail">📍 ${job.location || 'Location not specified'}</div>
                                        <div class="job-detail">💰 ${job.salary || 'Not specified'}</div>
                                        <div class="job-detail">⏰ Closes: ${job.closingDate || 'Not specified'}</div>
                                        <div class="job-detail ${isUrgent ? 'urgent' : ''}">
                                            ${daysLeft > 0 ? `${daysLeft} days left` : 'Closing soon!'}
                                        </div>
                                        <div class="job-category">🎓 ${categoriesDisplay}</div>
                                        <a href="${job.url || '#'}" target="_blank" class="apply-button">Apply Now</a>
                                    </div>
                                `;
                            }).join('');
                        }
                        
                        function updateStats() {
                            document.getElementById('stats').textContent = 
                                `Showing ${filteredJobs.length} of ${allJobs.length} opportunities`;
                        }
                        
                        document.getElementById('search').addEventListener('input', filterJobs);
                        document.getElementById('category').addEventListener('change', filterJobs);
                        document.getElementById('location').addEventListener('change', filterJobs);
                        document.getElementById('sort').addEventListener('change', () => {
                            sortJobs();
                            renderJobs();
                        });
                        
                        loadJobs();
                    </script>
                </body>
                </html>
                """;
    }

    private record TokenInfo(Instant expiry) {}
}
