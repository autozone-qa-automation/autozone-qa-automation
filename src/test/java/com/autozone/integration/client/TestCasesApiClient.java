package com.autozone.integration.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/** HTTP client for the {@code /api/v1/test-cases} resource, backed by {@link HttpClient}. */
public class TestCasesApiClient {

  private static final String TEST_CASES_PATH = "/api/v1/test-cases";

  private final String baseUrl;
  private final HttpClient httpClient;

  public TestCasesApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient();
  }

  /** {@code GET /api/v1/test-cases} */
  public ApiResponse getAllTestCases(String token) throws IOException, InterruptedException {
    return get(TEST_CASES_PATH, token);
  }

  /** {@code GET /api/v1/test-cases/{id}} */
  public ApiResponse getTestCaseById(String token, long id) throws IOException, InterruptedException {
    return get(TEST_CASES_PATH + "/" + id, token);
  }

  /** {@code GET /api/v1/test-cases/feature/{featureId}} */
  public ApiResponse getTestCasesByFeature(String token, long featureId) throws IOException, InterruptedException {
    return get(TEST_CASES_PATH + "/feature/" + featureId, token);
  }

  /** {@code POST /api/v1/test-cases} */
  public ApiResponse createTestCase(String token, String testCaseJson) throws IOException, InterruptedException {
    return send("POST", TEST_CASES_PATH, token, testCaseJson);
  }

  /** {@code PUT /api/v1/test-cases/{id}} */
  public ApiResponse updateTestCase(String token, long id, String testCaseJson)
      throws IOException, InterruptedException {
    return send("PUT", TEST_CASES_PATH + "/" + id, token, testCaseJson);
  }

  /** {@code PUT /api/v1/test-cases/{id}/deactivate} */
  public ApiResponse deactivateTestCase(String token, long id) throws IOException, InterruptedException {
    return send("PUT", TEST_CASES_PATH + "/" + id + "/deactivate", token, null);
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
