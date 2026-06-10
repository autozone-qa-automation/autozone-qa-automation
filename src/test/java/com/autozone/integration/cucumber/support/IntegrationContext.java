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
}
