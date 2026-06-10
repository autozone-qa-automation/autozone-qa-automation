package com.autozone.integration.cucumber.testcases;

import com.autozone.integration.client.ApiResponse;
import com.autozone.integration.client.FeaturesApiClient;
import com.autozone.integration.client.ServicesApiClient;
import com.autozone.integration.client.TestCasesApiClient;
import com.autozone.integration.config.IntegrationConfig;
import com.autozone.integration.cucumber.support.IntegrationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Step definitions for the {@code @test-cases} integration feature.
 *
 * <p>The shared "background" steps (base URL, login, token) and generic response assertions
 * live in {@link com.autozone.integration.cucumber.support.BackgroundStepDefinitions}. This
 * class holds the steps for {@code test-cases.feature}.
 */
public class TestCasesIntegrationStepDefinitions {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private TestCasesApiClient apiClient;
  private FeaturesApiClient featuresApiClient;
  private ServicesApiClient servicesApiClient;

  // an existing feature used to attach test cases to
  private long existingFeatureId;
  private String existingFeatureName;
  private Long fallbackCreatedFeatureId;
  private Long fallbackCreatedServiceId;

  // created test case(s) state
  private long createdTestCaseId;
  private String createdTestCaseTitle;
  private String createdTestCaseSteps;
  private String createdTestCaseExpectedOutput;

  private long secondTestCaseId;
  private String secondTestCaseTitle;

  private final List<Long> createdTestCaseIds = new ArrayList<>();

  @Before
  public void setUpClients() {
    String baseUrl = IntegrationConfig.getBaseUrl();
    apiClient = new TestCasesApiClient(baseUrl);
    featuresApiClient = new FeaturesApiClient(baseUrl);
    servicesApiClient = new ServicesApiClient(baseUrl);
  }

  @After
  public void cleanUpCreatedTestCases() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    for (Long id : createdTestCaseIds) {
      apiClient.deactivateTestCase(token, id);
    }
    createdTestCaseIds.clear();

    if (fallbackCreatedFeatureId != null) {
      featuresApiClient.deactivateFeature(token, fallbackCreatedFeatureId);
      fallbackCreatedFeatureId = null;
    }
    if (fallbackCreatedServiceId != null) {
      servicesApiClient.deleteService(token, fallbackCreatedServiceId);
      fallbackCreatedServiceId = null;
    }
  }

  // ---------------------------------------------------------------------
  // Shared shape assertions
  // ---------------------------------------------------------------------

  @Then("the response {string} should be null")
  public void theResponseFieldShouldBeNull(String fieldName) throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    JsonNode field = node.get(fieldName);
    assertTrue(field == null || field.isNull(), "Expected '" + fieldName + "' to be null but was: " + field);
  }

  @Then("each test case in the response should have an \"id\", a \"title\", a \"relatedFeature\", a \"type\", \"steps\", \"expectedOutput\" and an \"active\" flag")
  public void eachTestCaseShouldHaveExpectedFields() throws IOException {
    JsonNode testCases = readTree(IntegrationContext.getLastResponse().getBody());
    for (JsonNode testCase : testCases) {
      assertTrue(testCase.has("id"), "Test case is missing 'id': " + testCase);
      assertTrue(testCase.has("title"), "Test case is missing 'title': " + testCase);
      assertTrue(testCase.has("relatedFeature"), "Test case is missing 'relatedFeature': " + testCase);
      assertTrue(testCase.has("type"), "Test case is missing 'type': " + testCase);
      assertTrue(testCase.has("steps"), "Test case is missing 'steps': " + testCase);
      assertTrue(testCase.has("expectedOutput"), "Test case is missing 'expectedOutput': " + testCase);
      assertTrue(testCase.has("active"), "Test case is missing 'active': " + testCase);
    }
  }

  // ---------------------------------------------------------------------
  // an existing feature for test cases
  // ---------------------------------------------------------------------

  @Given("an existing feature for test cases")
  public void anExistingFeatureForTestCases() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    ApiResponse response = featuresApiClient.getAllFeatures(token);
    assertEquals(200, response.getStatusCode(), "Could not list features. Body: " + response.getBody());

    JsonNode features = readTree(response.getBody());
    if (features.size() > 0) {
      JsonNode first = features.get(0);
      existingFeatureId = first.get("id").asLong();
      existingFeatureName = first.get("featureName").asText();
      return;
    }

    // No features exist yet - create a service and a feature so the scenario has data to work with.
    ApiResponse servicesResponse = servicesApiClient.getServices(token);
    assertEquals(200, servicesResponse.getStatusCode(), "Could not list services. Body: " + servicesResponse.getBody());

    long serviceId;
    JsonNode services = readTree(servicesResponse.getBody());
    if (services.size() > 0) {
      serviceId = services.get(0).get("id").asLong();
    } else {
      String serviceName = "QA Fallback Service " + UUID.randomUUID();
      ObjectNode serviceBody = objectMapper.createObjectNode();
      serviceBody.put("name", serviceName);
      serviceBody.put("description", "Fallback service for Test Cases integration tests");
      serviceBody.put("isActive", true);
      serviceBody.putArray("urls");

      ApiResponse createdService = servicesApiClient.createService(token, objectMapper.writeValueAsString(serviceBody));
      assertEquals(201, createdService.getStatusCode(), "Could not create fallback service. Body: " + createdService.getBody());
      JsonNode serviceNode = readTree(createdService.getBody());
      serviceId = serviceNode.get("id").asLong();
      fallbackCreatedServiceId = serviceId;
    }

    String featureName = "QA Fallback Feature " + UUID.randomUUID();
    ObjectNode featureBody = objectMapper.createObjectNode();
    featureBody.put("featureName", featureName);
    featureBody.put("featureDescription", "Fallback feature for Test Cases integration tests");
    featureBody.put("idService", serviceId);

    ApiResponse createdFeature = featuresApiClient.createFeature(token, objectMapper.writeValueAsString(featureBody));
    assertEquals(201, createdFeature.getStatusCode(), "Could not create fallback feature. Body: " + createdFeature.getBody());
    JsonNode featureNode = readTree(createdFeature.getBody());
    existingFeatureId = featureNode.get("id").asLong();
    existingFeatureName = featureName;
    fallbackCreatedFeatureId = existingFeatureId;
  }

  // ---------------------------------------------------------------------
  // Create / read / list-by-feature / update lifecycle
  // ---------------------------------------------------------------------

  @When("the client creates a new test case with a unique title for that feature")
  public void theClientCreatesANewTestCaseWithAUniqueTitleForThatFeature() throws IOException, InterruptedException {
    createdTestCaseTitle = "QA Integration Test Case " + UUID.randomUUID();
    createdTestCaseSteps = "1. Open the application. 2. Perform the action under test.";
    createdTestCaseExpectedOutput = "The expected result is observed.";

    IntegrationContext.setLastResponse(
        apiClient.createTestCase(
            IntegrationContext.getToken(),
            buildTestCaseJson(createdTestCaseTitle, createdTestCaseSteps, createdTestCaseExpectedOutput, "REGRESSION", existingFeatureId, null)));
  }

  @Then("the response should echo the created test case title, type, steps, expected output and related feature")
  public void theResponseShouldEchoTheCreatedTestCaseFields() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdTestCaseTitle, node.get("title").asText(), "Response did not echo the created test case title");
    assertEquals("REGRESSION", node.get("type").asText(), "Response did not echo the created test case type");
    assertEquals(createdTestCaseSteps, node.get("steps").asText(), "Response did not echo the created test case steps");
    assertEquals(createdTestCaseExpectedOutput, node.get("expectedOutput").asText(), "Response did not echo the created test case expected output");
    assertEquals(existingFeatureId, node.get("relatedFeature").asLong(), "Response did not echo the related feature id");
  }

  @Then("the response should be active and include a generated test case id")
  public void theResponseShouldBeActiveAndIncludeAGeneratedTestCaseId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertTrue(node.get("active").asBoolean(), "Expected the created test case to be active: " + node);

    JsonNode id = node.get("id");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated id: " + node);
    createdTestCaseId = id.asLong();
    createdTestCaseIds.add(createdTestCaseId);
  }

  @When("the client retrieves the created test case by id")
  public void theClientRetrievesTheCreatedTestCaseById() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getTestCaseById(IntegrationContext.getToken(), createdTestCaseId));
  }

  @Then("the response body should match the created test case data")
  public void theResponseBodyShouldMatchTheCreatedTestCaseData() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdTestCaseId, node.get("id").asLong(), "Test case id does not match");
    assertEquals(createdTestCaseTitle, node.get("title").asText(), "Test case title does not match");
    assertEquals(existingFeatureId, node.get("relatedFeature").asLong(), "Test case relatedFeature does not match");
    assertEquals("REGRESSION", node.get("type").asText(), "Test case type does not match");
    assertEquals(createdTestCaseSteps, node.get("steps").asText(), "Test case steps do not match");
    assertEquals(createdTestCaseExpectedOutput, node.get("expectedOutput").asText(), "Test case expectedOutput does not match");
    assertTrue(node.get("active").asBoolean(), "Expected the test case to be active");
  }

  @When("the client lists test cases by that feature")
  public void theClientListsTestCasesByThatFeature() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getTestCasesByFeature(IntegrationContext.getToken(), existingFeatureId));
  }

  @Then("the response should include the created test case")
  public void theResponseShouldIncludeTheCreatedTestCase() throws IOException {
    JsonNode testCases = readTree(IntegrationContext.getLastResponse().getBody());
    boolean found = false;
    for (JsonNode testCase : testCases) {
      if (testCase.has("id") && testCase.get("id").asLong() == createdTestCaseId) {
        found = true;
        break;
      }
    }
    assertTrue(found, "Expected test case id " + createdTestCaseId + " in list: " + testCases);
  }

  @When("the client updates the created test case with a new title, steps and expected output")
  public void theClientUpdatesTheCreatedTestCaseWithANewTitleStepsAndExpectedOutput() throws IOException, InterruptedException {
    createdTestCaseTitle = createdTestCaseTitle + " (Updated)";
    createdTestCaseSteps = createdTestCaseSteps + " 3. Verify the updated behavior.";
    createdTestCaseExpectedOutput = createdTestCaseExpectedOutput + " The update is reflected.";

    IntegrationContext.setLastResponse(
        apiClient.updateTestCase(
            IntegrationContext.getToken(),
            createdTestCaseId,
            buildTestCaseJson(createdTestCaseTitle, createdTestCaseSteps, createdTestCaseExpectedOutput, "REGRESSION", existingFeatureId, createdTestCaseId)));
  }

  @Then("the response should reflect the updated test case title, steps and expected output")
  public void theResponseShouldReflectTheUpdatedTestCaseFields() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdTestCaseTitle, node.get("title").asText(), "Test case title was not updated");
    assertEquals(createdTestCaseSteps, node.get("steps").asText(), "Test case steps were not updated");
    assertEquals(createdTestCaseExpectedOutput, node.get("expectedOutput").asText(), "Test case expectedOutput was not updated");
  }

  // ---------------------------------------------------------------------
  // featureName population on getAllTestCases
  // ---------------------------------------------------------------------

  @Given("the client has created a new test case for that feature")
  public void theClientHasCreatedANewTestCaseForThatFeature() throws IOException, InterruptedException {
    createdTestCaseTitle = "QA Integration Test Case " + UUID.randomUUID();
    createdTestCaseSteps = "1. Open the application. 2. Perform the action under test.";
    createdTestCaseExpectedOutput = "The expected result is observed.";

    ApiResponse response = apiClient.createTestCase(
        IntegrationContext.getToken(),
        buildTestCaseJson(createdTestCaseTitle, createdTestCaseSteps, createdTestCaseExpectedOutput, "REGRESSION", existingFeatureId, null));
    assertEquals(201, response.getStatusCode(), "Could not create test case. Body: " + response.getBody());

    JsonNode node = readTree(response.getBody());
    createdTestCaseId = node.get("id").asLong();
    createdTestCaseIds.add(createdTestCaseId);
  }

  @Then("the response should include the created test case with its feature name populated")
  public void theResponseShouldIncludeTheCreatedTestCaseWithItsFeatureNamePopulated() throws IOException {
    JsonNode testCases = readTree(IntegrationContext.getLastResponse().getBody());
    boolean found = false;
    for (JsonNode testCase : testCases) {
      if (testCase.has("id") && testCase.get("id").asLong() == createdTestCaseId) {
        found = true;
        assertEquals(existingFeatureName, testCase.get("featureName").asText(), "Expected the test case 'featureName' to be populated");
        break;
      }
    }
    assertTrue(found, "Expected test case id " + createdTestCaseId + " in list: " + testCases);
  }

  // ---------------------------------------------------------------------
  // Validation
  // ---------------------------------------------------------------------

  @When("the client creates a test case with a null title for that feature")
  public void theClientCreatesATestCaseWithANullTitleForThatFeature() throws IOException, InterruptedException {
    ObjectNode body = objectMapper.createObjectNode();
    body.putNull("title");
    body.put("steps", "1. Open the application.");
    body.put("expectedOutput", "Something happens.");
    body.put("type", "REGRESSION");
    body.put("relatedFeature", existingFeatureId);

    IntegrationContext.setLastResponse(apiClient.createTestCase(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @When("the client creates another test case using the same title for that feature")
  public void theClientCreatesAnotherTestCaseUsingTheSameTitleForThatFeature() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        apiClient.createTestCase(
            IntegrationContext.getToken(),
            buildTestCaseJson(createdTestCaseTitle, "1. Different steps.", "A different expected output.", "REGRESSION", existingFeatureId, null)));
  }

  // ---------------------------------------------------------------------
  // Update id mismatch -> 400
  // ---------------------------------------------------------------------

  @When("the client updates the created test case using a different id in the body")
  public void theClientUpdatesTheCreatedTestCaseUsingADifferentIdInTheBody() throws IOException, InterruptedException {
    long mismatchedId = createdTestCaseId + 1;
    IntegrationContext.setLastResponse(
        apiClient.updateTestCase(
            IntegrationContext.getToken(),
            createdTestCaseId,
            buildTestCaseJson(createdTestCaseTitle, createdTestCaseSteps, createdTestCaseExpectedOutput, "REGRESSION", existingFeatureId, mismatchedId)));
  }

  // ---------------------------------------------------------------------
  // Update duplicate title -> 409
  // ---------------------------------------------------------------------

  @Given("the client has created two test cases with distinct titles for that feature")
  public void theClientHasCreatedTwoTestCasesWithDistinctTitlesForThatFeature() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    createdTestCaseTitle = "QA Test Case A " + UUID.randomUUID();
    createdTestCaseSteps = "1. Steps for test case A.";
    createdTestCaseExpectedOutput = "Expected output for test case A.";

    ApiResponse firstResponse = apiClient.createTestCase(
        token,
        buildTestCaseJson(createdTestCaseTitle, createdTestCaseSteps, createdTestCaseExpectedOutput, "REGRESSION", existingFeatureId, null));
    assertEquals(201, firstResponse.getStatusCode(), "Could not create first test case. Body: " + firstResponse.getBody());
    JsonNode firstNode = readTree(firstResponse.getBody());
    createdTestCaseId = firstNode.get("id").asLong();
    createdTestCaseIds.add(createdTestCaseId);

    secondTestCaseTitle = "QA Test Case B " + UUID.randomUUID();
    ApiResponse secondResponse = apiClient.createTestCase(
        token,
        buildTestCaseJson(secondTestCaseTitle, "1. Steps for test case B.", "Expected output for test case B.", "REGRESSION", existingFeatureId, null));
    assertEquals(201, secondResponse.getStatusCode(), "Could not create second test case. Body: " + secondResponse.getBody());
    JsonNode secondNode = readTree(secondResponse.getBody());
    secondTestCaseId = secondNode.get("id").asLong();
    createdTestCaseIds.add(secondTestCaseId);
  }

  @When("the client updates the first test case using the second test case's title")
  public void theClientUpdatesTheFirstTestCaseUsingTheSecondTestCaseSTitle() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        apiClient.updateTestCase(
            IntegrationContext.getToken(),
            createdTestCaseId,
            buildTestCaseJson(secondTestCaseTitle, createdTestCaseSteps, createdTestCaseExpectedOutput, "REGRESSION", existingFeatureId, createdTestCaseId)));
  }

  // ---------------------------------------------------------------------
  // Deactivate -> 204, then 404 on get
  // ---------------------------------------------------------------------

  @When("the client deactivates the created test case")
  public void theClientDeactivatesTheCreatedTestCase() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.deactivateTestCase(IntegrationContext.getToken(), createdTestCaseId));
  }

  @When("the client deactivates the test case with id {long}")
  public void theClientDeactivatesTheTestCaseWithId(long id) throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.deactivateTestCase(IntegrationContext.getToken(), id));
  }

  // ---------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------

  private String buildTestCaseJson(
      String title, String steps, String expectedOutput, String type, long featureId, Long id) throws IOException {
    ObjectNode body = objectMapper.createObjectNode();
    if (id != null) {
      body.put("id", id);
    }
    body.put("title", title);
    body.put("steps", steps);
    body.put("expectedOutput", expectedOutput);
    body.put("type", type);
    body.put("featureId", featureId);
    return objectMapper.writeValueAsString(body);
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
