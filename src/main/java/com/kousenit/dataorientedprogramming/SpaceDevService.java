package com.kousenit.dataorientedprogramming;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.kousenit.dataorientedprogramming.SpaceDevRecords.*;

/**
 * Fetches active space station expeditions from the Launch Library 2 API.
 * Returns a Result (sealed interface) that the caller pattern-matches on.
 */
public class SpaceDevService {
    private static final String BASE_URL = "https://ll.thespacedevs.com";
    private static final String EXPEDITIONS_PATH = "/2.3.0/expeditions/?is_active=true&mode=detailed";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    enum StatusCategory {
        SUCCESS, RATE_LIMITED, CLIENT_ERROR, SERVER_ERROR;

        static StatusCategory of(int statusCode) {
            return switch (statusCode) {
                case 200 -> SUCCESS;
                case 429 -> RATE_LIMITED;
                default -> statusCode >= 500 ? SERVER_ERROR : CLIENT_ERROR;
            };
        }
    }

    public Result fetchExpeditions() {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + EXPEDITIONS_PATH))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response =
                    HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            return switch (StatusCategory.of(response.statusCode())) {
                case SUCCESS -> {
                    var expeditionResponse = objectMapper.readValue(
                            response.body(), ExpeditionResponse.class);
                    yield new Result.Success(expeditionResponse.results());
                }
                case RATE_LIMITED -> new Result.RateLimited(
                        response.headers().firstValue("Retry-After").orElse("unknown"));
                case CLIENT_ERROR ->
                        new Result.ClientError(response.statusCode(), response.body());
                case SERVER_ERROR ->
                        new Result.ServerError(response.statusCode());
            };
        } catch (IOException e) {
            return new Result.NetworkError(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Result.NetworkError(e.getMessage());
        }
    }
}
