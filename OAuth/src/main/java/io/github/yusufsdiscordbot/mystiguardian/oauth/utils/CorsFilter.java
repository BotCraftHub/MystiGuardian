package io.github.yusufsdiscordbot.mystiguardian.oauth.utils;

import java.util.HashMap;
import java.util.Objects;

import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import spark.Request;
import spark.Response;
import spark.Spark;

public class CorsFilter {

    private static final HashMap<String, String> corsHeaders = new HashMap<>();

    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Content-Length, Accept, Origin");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    public static void applyCorsHeaders(Request request, Response response) {
        String origin = request.headers("Origin");
        response.header("Access-Control-Allow-Origin", Objects.requireNonNullElse(origin, "*"));
        response.header("Vary", "Origin");

        corsHeaders.forEach((key, value) -> {
            response.header(key, value);
            MystiGuardianUtils.discordAuthLogger.info("CORS Header: " + key + " = " + value); // Logging for debugging
        });

        if ("OPTIONS".equalsIgnoreCase(request.requestMethod())) {
            Spark.halt(200, "Preflight");
        }
    }
}
