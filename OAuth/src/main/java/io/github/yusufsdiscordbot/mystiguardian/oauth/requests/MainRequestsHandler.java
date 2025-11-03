/*
 * Copyright 2025 RealYusufIsmail.
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

import io.github.yusufsdiscordbot.mystiguardian.oauth.requests.database.DatabaseGetRequests;
import io.github.yusufsdiscordbot.mystiguardian.oauth.utils.CorsFilter;
import spark.Spark;

/**
 * Main handler that initializes all request handlers for the OAuth web service.
 *
 * <p>This class coordinates the setup of:
 * <ul>
 *   <li>GET request handlers
 *   <li>POST request handlers
 *   <li>PUT request handlers
 *   <li>Database GET request handlers
 *   <li>Apprenticeship request handlers
 *   <li>CORS filters
 * </ul>
 */
public class MainRequestsHandler {

    /**
     * Constructs a new MainRequestsHandler and initializes all endpoint handlers.
     */
    public MainRequestsHandler() {
        // needed for cors
        Spark.before(CorsFilter::applyCorsHeaders);

        new GetRequestsHandler();
        new PostRequestsHandler();
        new PutRequestsHandler();
        new DatabaseGetRequests();
        new ApprenticeshipRequestsHandler();
    }
}
