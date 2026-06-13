package com.autozone.integration.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

/** HTTP client for the {@code /api/v1/releases} resource, backed by {@link HttpClient}. */
public class ReleasesApiClient {

  private static final String RELEASES_PATH = "/api/v1/releases";

  private final String baseUrl;
  private final HttpClient httpClient;

  public ReleasesApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient();
  }

  /**
   * {@code GET /api/v1/releases}, with optional {@code releaseStatus}/{@code releaseTags} query
   * parameters. Either argument may be {@code null} to omit that filter.
   */
  public ApiResponse getReleases(String token, String releaseStatus, String releaseTags)
      throws IOException, InterruptedException {
    StringBuilder path = new StringBuilder(RELEASES_PATH);
    String separator = "?";
    if (releaseStatus != null) {
      path.append(separator).append("releaseStatus=").append(encode(releaseStatus));
      separator = "&";
    }
    if (releaseTags != null) {
      path.append(separator).append("releaseTags=").append(encode(releaseTags));
      separator = "&";
    }
    return get(path.toString(), token);
  }

  /** {@code GET /api/v1/releases/last}, with an optional {@code serviceId} query parameter. */
  public ApiResponse getLastReleases(String token, Long serviceId) throws IOException, InterruptedException {
    String path = RELEASES_PATH + "/last";
    if (serviceId != null) {
      path += "?serviceId=" + serviceId;
    }
    return get(path, token);
  }

  /** {@code GET /api/v1/releases/{id}} */
  public ApiResponse getReleaseById(String token, long id) throws IOException, InterruptedException {
    return get(RELEASES_PATH + "/" + id, token);
  }

  /** {@code POST /api/v1/releases} */
  public ApiResponse createRelease(String token, String releaseJson) throws IOException, InterruptedException {
    return send("POST", RELEASES_PATH, token, releaseJson);
  }

  /**
   * {@code PUT /api/v1/releases/{id}/status/{status}}.
   *
   * <p>{@code status} is sent as-is in the URL path, so an invalid value (not Draft/Progress/
   * Active) can be used to exercise the enum-conversion error path.
   */
  public ApiResponse updateReleaseStatus(String token, long id, String status)
      throws IOException, InterruptedException {
    return send("PUT", RELEASES_PATH + "/" + id + "/status/" + status, token, null);
  }

  /** {@code DELETE /api/v1/releases/{id}} */
  public ApiResponse deleteRelease(String token, long id) throws IOException, InterruptedException {
    return send("DELETE", RELEASES_PATH + "/" + id, token, null);
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

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
