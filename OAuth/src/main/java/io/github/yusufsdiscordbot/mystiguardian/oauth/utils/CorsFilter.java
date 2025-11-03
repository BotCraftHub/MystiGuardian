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
package io.github.yusufsdiscordbot.mystiguardian.oauth.utils;

import java.util.HashMap;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Utility class for handling Cross-Origin Resource Sharing (CORS) headers in HTTP responses.
 *
 * <p>This filter applies necessary CORS headers to allow cross-origin requests from web clients.
 * It supports configurable origins and standard CORS methods and headers.
 */
@Slf4j
public class CorsFilter {

    /**
     * Private constructor to prevent instantiation.
     */
    private CorsFilter() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final HashMap<String, String> corsHeaders = new HashMap<>();

    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
        corsHeaders.put(
                "Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    /**
     * Applies CORS headers to the HTTP response based on the request origin.
     *
     * @param request the incoming HTTP request
     * @param response the HTTP response to which CORS headers will be added
     */
    public static void applyCorsHeaders(Request request, Response response) {
        String origin = request.headers("Origin");
        response.header("Access-Control-Allow-Origin", Objects.requireNonNullElse(origin, "*"));
        response.header("Vary", "Origin");

        corsHeaders.forEach(
                (key, value) -> {
                    response.header(key, value);
                    logger.debug("{}{}", "CORS Header: " + key + " = ", value);
                });

        if ("OPTIONS".equalsIgnoreCase(request.requestMethod())) {
            Spark.halt(200, "Preflight");
        }
    }
}
