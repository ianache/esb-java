package com.crossnetcorp.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A processor that checks for a valid OAuth2 token.
 *
 * @param <A> The type of the payload.
 */
public class HttpUtils {
    private static final Logger logger = LogManager.getLogger(HttpUtils.class);

    HttpClient client;

    public HttpUtils() {
        this.client = HttpClient.newHttpClient();
    }

    public HttpResponse<String> post(String url, String payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString())) // Define el método GET
                .build();
            HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Status: {}, Body: {}", result.statusCode(), result.body());
            return result;
        } catch(Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }
    
}
