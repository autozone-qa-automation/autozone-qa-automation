package com.autozone.integration.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Resolves configuration for the API integration test suite.
 *
 * <p>Each setting is resolved in the following order, the first match wins:
 *
 * <ol>
 *   <li>JVM system property (e.g. {@code -Dapi.base.url=...})
 *   <li>Environment variable (e.g. {@code API_BASE_URL})
 *   <li>{@code env.properties} on the test classpath
 *   <li>built-in default (only for {@code api.base.url} / {@code api.login.path})
 * </ol>
 */
public final class IntegrationConfig {

  private static final String ENV_PROPERTIES_FILE = "env.properties";

  private static final String DEFAULT_BASE_URL = "http://localhost:8080";
  private static final String DEFAULT_LOGIN_PATH = "/api/v1/authentify";

  private static final Properties FILE_PROPERTIES = loadFileProperties();

  private IntegrationConfig() {}

  /** Base URL of the backend API, with any trailing slash removed. */
  public static String getBaseUrl() {
    String value = resolve("api.base.url", "API_BASE_URL", DEFAULT_BASE_URL);
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }

  /** Path of the login/authentication endpoint. */
  public static String getLoginPath() {
    return resolve("api.login.path", "API_LOGIN_PATH", DEFAULT_LOGIN_PATH);
  }

  /** Email of the seeded ADMIN account used to authenticate the test suite. */
  public static String getUsername() {
    return requireValue("api.username", "API_USERNAME");
  }

  /** Plaintext password of the seeded ADMIN account. */
  public static String getPassword() {
    return requireValue("api.password", "API_PASSWORD");
  }

  private static String requireValue(String systemProperty, String envVar) {
    String value = resolve(systemProperty, envVar, null);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(
          "Missing required configuration '"
              + systemProperty
              + "'. Set it via -D"
              + systemProperty
              + ", the "
              + envVar
              + " environment variable, or "
              + ENV_PROPERTIES_FILE
              + ".");
    }
    return value;
  }

  private static String resolve(String systemProperty, String envVar, String defaultValue) {
    String value = System.getProperty(systemProperty);
    if (value != null && !value.isBlank()) {
      return value;
    }

    value = System.getenv(envVar);
    if (value != null && !value.isBlank()) {
      return value;
    }

    value = FILE_PROPERTIES.getProperty(systemProperty);
    if (value != null && !value.isBlank()) {
      return value;
    }

    return defaultValue;
  }

  private static Properties loadFileProperties() {
    Properties properties = new Properties();
    try (InputStream input =
        IntegrationConfig.class.getClassLoader().getResourceAsStream(ENV_PROPERTIES_FILE)) {
      if (input != null) {
        properties.load(input);
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to load " + ENV_PROPERTIES_FILE, e);
    }
    return properties;
  }
}
