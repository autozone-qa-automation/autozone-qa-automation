package com.autozone.integration.cucumber.support;

import com.autozone.integration.client.ApiResponse;
import com.autozone.integration.client.FeaturesApiClient;
import com.autozone.integration.client.ReleasesApiClient;
import com.autozone.integration.client.ReportsApiClient;
import com.autozone.integration.client.ServicesApiClient;
import com.autozone.integration.client.TestCasesApiClient;
import com.autozone.integration.config.IntegrationConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared "background" steps (base URL, login, token) and generic response assertions used by
 * every {@code @integration} feature.
 *
 * <p>These steps are defined exactly once here so that step text shared across feature files
 * (e.g. "the response status code should be {int}", "the client sends a GET request to
 * {string}") does not collide with per-resource step definition classes and trigger a {@code
 * DuplicateStepDefinitionException}.
 */
public class BackgroundStepDefinitions {

  private static final Pattern SERVICE_BY_ID_PATH = Pattern.compile("^/api/v1/services/(\\d+)$");
  private static final Pattern TEST_SERVICE_PATH = Pattern.compile("^/api/v1/services/test/(\\d+)$");
  private static final Pattern FEATURES_BY_SERVICE_PATH = Pattern.compile("^/api/v1/features/filtered/(\\d+)$");
  private static final Pattern FEATURE_BY_ID_PATH = Pattern.compile("^/api/v1/features/(\\d+)$");
  private static final Pattern TEST_CASES_BY_FEATURE_PATH = Pattern.compile("^/api/v1/test-cases/feature/(\\d+)$");
  private static final Pattern TEST_CASE_BY_ID_PATH = Pattern.compile("^/api/v1/test-cases/(\\d+)$");
  private static final Pattern RELEASE_BY_ID_PATH = Pattern.compile("^/api/v1/releases/(\\d+)$");

  private final ObjectMapper objectMapper = new ObjectMapper();

  private ServicesApiClient servicesApiClient;
  private FeaturesApiClient featuresApiClient;
  private TestCasesApiClient testCasesApiClient;
  private ReleasesApiClient releasesApiClient;
  private ReportsApiClient reportsApiClient;

  @Before
  public void setUpClients() {
    String baseUrl = IntegrationConfig.getBaseUrl();
    servicesApiClient = new ServicesApiClient(baseUrl);
    featuresApiClient = new FeaturesApiClient(baseUrl);
    testCasesApiClient = new TestCasesApiClient(baseUrl);
    releasesApiClient = new ReleasesApiClient(baseUrl);
    reportsApiClient = new ReportsApiClient(baseUrl);
  }

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

  // ---------------------------------------------------------------------
  // Shared "an existing service" fixture, used by Services, Features and Releases scenarios
  // ---------------------------------------------------------------------

  @Given("an existing service from the services list")
  public void anExistingServiceFromTheServicesList() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    ApiResponse response = servicesApiClient.getServices(token);
    assertEquals(200, response.getStatusCode(), "Could not list services. Body: " + response.getBody());

    JsonNode services = readTree(response.getBody());
    if (services.size() > 0) {
      JsonNode first = services.get(0);
      IntegrationContext.setExistingServiceId(first.get("id").asLong());
      IntegrationContext.setExistingServiceName(first.get("name").asText());
      return;
    }

    // No services exist yet - create one so the scenario has data to work with.
    String name = "QA-FALLBACK-SVC-1";
    ObjectNode body = objectMapper.createObjectNode();
    body.put("name", name);
    body.put("description", "Fallback service for integration tests");
    body.put("isActive", true);

    ArrayNode urls = body.putArray("urls");
    ObjectNode url = urls.addObject();
    url.put("nombre", "QA");
    url.put("url", "https://qa.example.com");

    ApiResponse created = servicesApiClient.createService(token, objectMapper.writeValueAsString(body));
    assertEquals(201, created.getStatusCode(), "Could not create fallback service. Body: " + created.getBody());

    JsonNode node = readTree(created.getBody());
    IntegrationContext.setExistingServiceId(node.get("id").asLong());
    IntegrationContext.setExistingServiceName(name);
    IntegrationContext.setFallbackCreatedServiceId(IntegrationContext.getExistingServiceId());
  }

  @After(order = 10)
  public void cleanUpFallbackCreatedService() throws IOException, InterruptedException {
    Long fallbackServiceId = IntegrationContext.getFallbackCreatedServiceId();
    if (fallbackServiceId != null) {
      servicesApiClient.deleteService(IntegrationContext.getToken(), fallbackServiceId);
      IntegrationContext.setFallbackCreatedServiceId(null);
    }
  }

  // ---------------------------------------------------------------------
  // Generic GET/DELETE dispatchers shared across resource feature files
  // ---------------------------------------------------------------------

  @When("the client sends a GET request to {string}")
  public void theClientSendsAGetRequestTo(String path) throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();

    if ("/api/v1/services".equals(path)) {
      IntegrationContext.setLastResponse(servicesApiClient.getServices(token));
      return;
    }
    Matcher testServiceMatcher = TEST_SERVICE_PATH.matcher(path);
    if (testServiceMatcher.matches()) {
      IntegrationContext.setLastResponse(
          servicesApiClient.getTestService(token, Long.parseLong(testServiceMatcher.group(1))));
      return;
    }
    Matcher serviceByIdMatcher = SERVICE_BY_ID_PATH.matcher(path);
    if (serviceByIdMatcher.matches()) {
      IntegrationContext.setLastResponse(
          servicesApiClient.getServiceById(token, Long.parseLong(serviceByIdMatcher.group(1))));
      return;
    }

    if ("/api/v1/features".equals(path)) {
      IntegrationContext.setLastResponse(featuresApiClient.getAllFeatures(token));
      return;
    }
    Matcher featuresByServiceMatcher = FEATURES_BY_SERVICE_PATH.matcher(path);
    if (featuresByServiceMatcher.matches()) {
      IntegrationContext.setLastResponse(
          featuresApiClient.getFeaturesByService(token, Long.parseLong(featuresByServiceMatcher.group(1))));
      return;
    }
    Matcher featureByIdMatcher = FEATURE_BY_ID_PATH.matcher(path);
    if (featureByIdMatcher.matches()) {
      IntegrationContext.setLastResponse(
          featuresApiClient.getFeatureById(token, Long.parseLong(featureByIdMatcher.group(1))));
      return;
    }

    if ("/api/v1/test-cases".equals(path)) {
      IntegrationContext.setLastResponse(testCasesApiClient.getAllTestCases(token));
      return;
    }
    Matcher testCasesByFeatureMatcher = TEST_CASES_BY_FEATURE_PATH.matcher(path);
    if (testCasesByFeatureMatcher.matches()) {
      IntegrationContext.setLastResponse(
          testCasesApiClient.getTestCasesByFeature(token, Long.parseLong(testCasesByFeatureMatcher.group(1))));
      return;
    }
    Matcher testCaseByIdMatcher = TEST_CASE_BY_ID_PATH.matcher(path);
    if (testCaseByIdMatcher.matches()) {
      IntegrationContext.setLastResponse(
          testCasesApiClient.getTestCaseById(token, Long.parseLong(testCaseByIdMatcher.group(1))));
      return;
    }

    if ("/api/v1/releases".equals(path)) {
      IntegrationContext.setLastResponse(releasesApiClient.getReleases(token, null, null));
      return;
    }
    if ("/api/v1/releases/last".equals(path)) {
      IntegrationContext.setLastResponse(releasesApiClient.getLastReleases(token, null));
      return;
    }
    Matcher releaseByIdMatcher = RELEASE_BY_ID_PATH.matcher(path);
    if (releaseByIdMatcher.matches()) {
      IntegrationContext.setLastResponse(
          releasesApiClient.getReleaseById(token, Long.parseLong(releaseByIdMatcher.group(1))));
      return;
    }

    if ("/api/v1/reports".equals(path)) {
      IntegrationContext.setLastResponse(reportsApiClient.getReports(token));
      return;
    }

    throw new IllegalArgumentException("Unsupported path for generic GET step: " + path);
  }

  @When("the client sends a DELETE request to {string}")
  public void theClientSendsADeleteRequestTo(String path) throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();

    Matcher releaseByIdMatcher = RELEASE_BY_ID_PATH.matcher(path);
    if (releaseByIdMatcher.matches()) {
      IntegrationContext.setLastResponse(
          releasesApiClient.deleteRelease(token, Long.parseLong(releaseByIdMatcher.group(1))));
      return;
    }

    throw new IllegalArgumentException("Unsupported path for generic DELETE step: " + path);
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
