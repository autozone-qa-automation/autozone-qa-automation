package com.autozone.integration.client;

/** Plain holder for an HTTP response returned by {@link ServicesApiClient}. */
public class ApiResponse {

  private final int statusCode;
  private final String contentType;
  private final String body;

  public ApiResponse(int statusCode, String contentType, String body) {
    this.statusCode = statusCode;
    this.contentType = contentType;
    this.body = body == null ? "" : body;
  }

  public int getStatusCode() {
    return statusCode;
  }

  /** Value of the {@code Content-Type} response header, or {@code null} if absent. */
  public String getContentType() {
    return contentType;
  }

  /** Raw response body, or an empty string for no-content responses. */
  public String getBody() {
    return body;
  }

  public boolean hasBody() {
    return !body.isBlank();
  }
}
