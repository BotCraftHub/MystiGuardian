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
package io.github.yusufsdiscordbot.mystiguardian.oauth.utils;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.util.HashMap;
import java.util.Objects;
import spark.Request;
import spark.Response;
import spark.Spark;

public class CorsFilter {

    private static final HashMap<String, String> corsHeaders = new HashMap<>();

    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
        corsHeaders.put(
                "Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    public static void applyCorsHeaders(Request request, Response response) {
        String origin = request.headers("Origin");
        response.header("Access-Control-Allow-Origin", Objects.requireNonNullElse(origin, "*"));
        response.header("Vary", "Origin");

        corsHeaders.forEach(
                (key, value) -> {
                    response.header(key, value);
                    MystiGuardianUtils.discordAuthLogger.info(
                            "CORS Header: " + key + " = " + value); // Logging for debugging
                });

        if ("OPTIONS".equalsIgnoreCase(request.requestMethod())) {
            Spark.halt(200, "Preflight");
        }
    }
}
