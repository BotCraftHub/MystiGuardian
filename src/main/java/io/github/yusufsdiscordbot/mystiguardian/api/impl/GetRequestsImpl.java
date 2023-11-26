package io.github.yusufsdiscordbot.mystiguardian.api.impl;

import io.github.yusufsdiscordbot.mystiguardian.api.reuqestis.GetRequests;
import spark.Spark;

public class GetRequestsImpl implements GetRequests {
    private final Spark spark;
    private final String endpoint;

    private String[] headers;
    private int statusCode;
    private String body;
    private boolean jwtRequired;

    public GetRequestsImpl(Spark spark, String endpoint) {
        this.spark = spark;
        this.endpoint = endpoint;
    }

    @Override
    public GetRequests setHeaders(String... headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public GetRequests setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @Override
    public GetRequests setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public GetRequests setJWTRequired(boolean required) {
        this.jwtRequired = required;
        return this;
    }

    @Override
    public void listen() {
        spark.get(endpoint, (request, response) -> {
            if (jwtRequired) {
                if (request.headers("Authorization") == null) {
                    response.status(401);
                    return "Unauthorized";
                }
            }

            if (headers != null) {
                for (String header : headers) {
                    String[] headerSplit = header.split(":");
                    response.header(headerSplit[0], headerSplit[1]);
                }
            }

            response.status(statusCode);
            return body;
        });
    }
}
