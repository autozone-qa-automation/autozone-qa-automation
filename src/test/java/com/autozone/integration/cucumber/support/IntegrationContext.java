package com.autozone.integration.cucumber.support;

import com.autozone.integration.client.ApiResponse;

/**
 * Mutable, process-wide state shared across the integration step definition classes.
 *
 * <p>Cucumber instantiates each step definition class separately, so a single field cannot be
 * shared between e.g. {@code BackgroundStepDefinitions} and a resource-specific class. This
 * holder lets the access token obtained in the Background and the most recent {@link
 * ApiResponse} be read and written from any step definition class.
 */
public final class IntegrationContext {

  private static String token;
  private static ApiResponse lastResponse;
  private static long existingServiceId;
  private static String existingServiceName;
  private static Long fallbackCreatedServiceId;

  private IntegrationContext() {}

  public static String getToken() {
    return token;
  }

  public static void setToken(String value) {
    token = value;
  }

  public static ApiResponse getLastResponse() {
    return lastResponse;
  }

  public static void setLastResponse(ApiResponse value) {
    lastResponse = value;
  }

  /** The id of the service resolved by the "an existing service from the services list" step. */
  public static long getExistingServiceId() {
    return existingServiceId;
  }

  public static void setExistingServiceId(long value) {
    existingServiceId = value;
  }

  /** The name of the service resolved by the "an existing service from the services list" step. */
  public static String getExistingServiceName() {
    return existingServiceName;
  }

  public static void setExistingServiceName(String value) {
    existingServiceName = value;
  }

  /**
   * The id of a service created as a fallback by "an existing service from the services list"
   * (when no service existed yet), or {@code null} if an existing service was reused.
   */
  public static Long getFallbackCreatedServiceId() {
    return fallbackCreatedServiceId;
  }

  public static void setFallbackCreatedServiceId(Long value) {
    fallbackCreatedServiceId = value;
  }
}
