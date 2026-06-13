package com.autozone.integration.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/** HTTP client for the {@code /api/v1/features} resource, backed by {@link HttpClient}. */
public class FeaturesApiClient {

  private static final String FEATURES_PATH = "/api/v1/features";

  private final String baseUrl;
  private final HttpClient httpClient;

  public FeaturesApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient();
  }

  /** {@code GET /api/v1/features} */
  public ApiResponse getAllFeatures(String token) throws IOException, InterruptedException {
    return get(FEATURES_PATH, token);
  }

  /** {@code GET /api/v1/features/{id}} */
  public ApiResponse getFeatureById(String token, long id) throws IOException, InterruptedException {
    return get(FEATURES_PATH + "/" + id, token);
  }

  /** {@code GET /api/v1/features/filtered/{serviceId}} */
  public ApiResponse getFeaturesByService(String token, long serviceId) throws IOException, InterruptedException {
    return get(FEATURES_PATH + "/filtered/" + serviceId, token);
  }

  /** {@code POST /api/v1/features} */
  public ApiResponse createFeature(String token, String featureJson) throws IOException, InterruptedException {
    return send("POST", FEATURES_PATH, token, featureJson);
  }

  /** {@code PUT /api/v1/features/{id}} */
  public ApiResponse updateFeature(String token, long id, String featureJson)
      throws IOException, InterruptedException {
    return send("PUT", FEATURES_PATH + "/" + id, token, featureJson);
  }

  /** {@code PUT /api/v1/features/{id}/deactivate} */
  public ApiResponse deactivateFeature(String token, long id) throws IOException, InterruptedException {
    return send("PUT", FEATURES_PATH + "/" + id + "/deactivate", token, null);
  }

  private ApiResponse get(String path, String token) throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/json")
            .GET()
            .build();
    return execute(request);
  }

  private ApiResponse send(String method, String path, String token, String jsonBody)
      throws IOException, InterruptedException {
    BodyPublisher body = jsonBody == null ? BodyPublishers.noBody() : BodyPublishers.ofString(jsonBody);

    HttpRequest.Builder builder =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/json")
            .method(method, body);

    if (jsonBody != null) {
      builder.header("Content-Type", "application/json");
    }

    return execute(builder.build());
  }

  private ApiResponse execute(HttpRequest request) throws IOException, InterruptedException {
    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    String contentType = response.headers().firstValue("Content-Type").orElse(null);
    return new ApiResponse(response.statusCode(), contentType, response.body());
  }
}
