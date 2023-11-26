package io.github.yusufsdiscordbot.mystiguardian.api.reuqestis;

public interface GetRequests {

    /**
     * Sets any headers that should be sent with the response.
     *
     * @param headers The headers to set.
     * @return The [GetRequests] instance.
     */
    GetRequests setHeaders(String... headers);

    /**
     * Sets the response status code.
     *
     * @param statusCode The status code to set.
     * @return The [GetRequests] instance.
     */
    GetRequests setStatusCode(int statusCode);

    /**
     * Sets the response body.
     *
     * @param body The body to set.
     * @return The [GetRequests] instance.
     */
    GetRequests setBody(String body);

    /**
     * Whether JWT is required or not.
     *
     * @param required Whether JWT is required or not.
     * @return The [GetRequests] instance.
     */
    GetRequests setJWTRequired(boolean required);


    /**
     * Listens to GET requests.
     */
    void listen();
}
