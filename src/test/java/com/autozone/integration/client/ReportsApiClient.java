package com.autozone.integration.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/** HTTP client for the (read-only) {@code /api/v1/reports} resource, backed by {@link HttpClient}. */
public class ReportsApiClient {

  private static final String REPORTS_PATH = "/api/v1/reports";

  private final String baseUrl;
  private final HttpClient httpClient;

  public ReportsApiClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClient.newHttpClient();
  }

  /** {@code GET /api/v1/reports}, returning the JSON release/service/feature/test-case hierarchy. */
  public ApiResponse getReports(String token) throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + REPORTS_PATH))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/json")
            .GET()
            .build();
    return execute(request);
  }

  /**
   * {@code GET /api/v1/reports/export}.
   *
   * <p>{@code releaseIdsQuery} controls how the {@code releaseIds} query parameter is sent:
   *
   * <ul>
   *   <li>{@code null} - the parameter is omitted entirely
   *   <li>{@code ""} - sent as {@code ?releaseIds=} (present but empty)
   *   <li>any other value - sent as-is, e.g. {@code "1,2,3"}
   * </ul>
   */
  public ApiResponse exportReportsCsv(String token, String releaseIdsQuery) throws IOException, InterruptedException {
    String path = REPORTS_PATH + "/export";
    if (releaseIdsQuery != null) {
      path += "?releaseIds=" + releaseIdsQuery;
    }

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", "Bearer " + token)
            .header("Accept", "text/csv, */*")
            .GET()
            .build();
    return execute(request);
  }

  private ApiResponse execute(HttpRequest request) throws IOException, InterruptedException {
    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    String contentType = response.headers().firstValue("Content-Type").orElse(null);
    return new ApiResponse(response.statusCode(), contentType, response.body());
  }
}
