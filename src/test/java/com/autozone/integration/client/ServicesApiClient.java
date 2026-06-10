package com.autozone.integration.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.LinkedHashMap;
import java.util.Map;

/** HTTP client for the {@code /api/v1/services} resource, backed by {@link HttpClient}. */
public class ServicesApiClient {

  private static final String SERVICES_PATH = "/api/v1/services";

  private final String baseUrl;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  public ServicesApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Logs in with the given credentials and returns the JWT from the {@code token} field of the
   * response body.
   *
   * @throws IllegalStateException if the login does not return 200/JSON or has no token field
   */
  public String loginAndGetToken(String loginPath, String mail, String password)
      throws IOException, InterruptedException {
    Map<String, String> credentials = new LinkedHashMap<>();
    credentials.put("mail", mail);
    credentials.put("password", password);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + loginPath))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(BodyPublishers.ofString(objectMapper.writeValueAsString(credentials)))
            .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new IllegalStateException(
          "Login failed: expected status 200 but got "
              + response.statusCode()
              + ". Body: "
              + response.body());
    }

    String contentType = response.headers().firstValue("Content-Type").orElse("");
    if (!contentType.toLowerCase().contains("application/json")) {
      throw new IllegalStateException(
          "Login response had unexpected content type '" + contentType + "'");
    }

    JsonNode root = objectMapper.readTree(response.body());
    JsonNode token = root.get("token");
    if (token == null || token.isNull()) {
      throw new IllegalStateException(
          "Login response did not contain a 'token' field. Body: " + response.body());
    }

    return token.asText();
  }

  /** {@code GET /api/v1/services} */
  public ApiResponse getServices(String token) throws IOException, InterruptedException {
    return get(SERVICES_PATH, token);
  }

  /** {@code GET /api/v1/services/{id}} */
  public ApiResponse getServiceById(String token, long id) throws IOException, InterruptedException {
    return get(SERVICES_PATH + "/" + id, token);
  }

  /** {@code GET /api/v1/services/test/{id}} */
  public ApiResponse getTestService(String token, long id) throws IOException, InterruptedException {
    return get(SERVICES_PATH + "/test/" + id, token);
  }

  /** {@code POST /api/v1/services} */
  public ApiResponse createService(String token, String serviceJson)
      throws IOException, InterruptedException {
    return send("POST", SERVICES_PATH, token, serviceJson);
  }

  /** {@code PUT /api/v1/services/{id}} */
  public ApiResponse updateService(String token, long id, String serviceJson)
      throws IOException, InterruptedException {
    return send("PUT", SERVICES_PATH + "/" + id, token, serviceJson);
  }

  /** {@code DELETE /api/v1/services/{id}} */
  public ApiResponse deleteService(String token, long id) throws IOException, InterruptedException {
    return send("DELETE", SERVICES_PATH + "/" + id, token, null);
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
