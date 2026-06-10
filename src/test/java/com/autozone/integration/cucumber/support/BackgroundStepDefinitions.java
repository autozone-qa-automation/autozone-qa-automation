package com.autozone.integration.cucumber.support;

import com.autozone.integration.client.ServicesApiClient;
import com.autozone.integration.config.IntegrationConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.IOException;

/**
 * Shared "background" steps (base URL, login, token) and generic response assertions used by
 * every {@code @integration} feature.
 *
 * <p>These steps are defined exactly once here so that step text shared across feature files
 * (e.g. "the response status code should be {int}") does not collide with per-resource step
 * definition classes and trigger a {@code DuplicateStepDefinitionException}.
 */
public class BackgroundStepDefinitions {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Given("the API base URL is configured")
  public void theApiBaseUrlIsConfigured() {
    assertTrue(!IntegrationConfig.getBaseUrl().isBlank(), "Expected a non-blank API base URL");
  }

  @Given("the login endpoint is configured")
  public void theLoginEndpointIsConfigured() {
    assertTrue(!IntegrationConfig.getLoginPath().isBlank(), "Expected a non-blank login path");
  }

  @Given("valid ADMIN credentials are configured")
  public void validAdminCredentialsAreConfigured() {
    assertTrue(!IntegrationConfig.getUsername().isBlank(), "Expected a non-blank ADMIN username");
    assertTrue(!IntegrationConfig.getPassword().isBlank(), "Expected a non-blank ADMIN password");
  }

  @Given("the client requests an access token")
  public void theClientRequestsAnAccessToken() throws IOException, InterruptedException {
    ServicesApiClient client = new ServicesApiClient(IntegrationConfig.getBaseUrl());
    String token =
        client.loginAndGetToken(
            IntegrationConfig.getLoginPath(), IntegrationConfig.getUsername(), IntegrationConfig.getPassword());
    assertTrue(token != null && !token.isBlank(), "Expected a non-blank access token");
    IntegrationContext.setToken(token);
  }

  @Then("the response status code should be {int}")
  public void theResponseStatusCodeShouldBe(int expectedStatus) {
    assertEquals(
        expectedStatus,
        IntegrationContext.getLastResponse().getStatusCode(),
        "Unexpected status code. Body: " + IntegrationContext.getLastResponse().getBody());
  }

  @Then("the response content type should be {string}")
  public void theResponseContentTypeShouldBe(String expectedType) {
    String contentType = IntegrationContext.getLastResponse().getContentType();
    assertTrue(
        contentType != null && contentType.toLowerCase().contains(expectedType.toLowerCase()),
        "Expected content type to contain '" + expectedType + "' but was '" + contentType + "'");
  }

  @Then("the response body should be a JSON array")
  public void theResponseBodyShouldBeAJsonArray() throws IOException {
    JsonNode body = readTree(IntegrationContext.getLastResponse().getBody());
    assertTrue(body.isArray(), "Expected a JSON array but got: " + IntegrationContext.getLastResponse().getBody());
  }

  @Then("the response body should be empty")
  public void theResponseBodyShouldBeEmpty() {
    assertTrue(
        !IntegrationContext.getLastResponse().hasBody(),
        "Expected an empty response body but got: " + IntegrationContext.getLastResponse().getBody());
  }

  @Then("the response body should contain {string}")
  public void theResponseBodyShouldContain(String expectedSubstring) {
    String body = IntegrationContext.getLastResponse().getBody();
    assertTrue(
        body.contains(expectedSubstring),
        "Expected response body to contain '" + expectedSubstring + "' but was: " + body);
  }

  private JsonNode readTree(String json) throws IOException {
    return objectMapper.readTree(json);
  }

  private void assertEquals(Object expected, Object actual, String message) {
    if (!java.util.Objects.equals(expected, actual)) {
      throw new AssertionError(message + " - expected: <" + expected + "> but was: <" + actual + ">");
    }
  }

  private void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }
}
