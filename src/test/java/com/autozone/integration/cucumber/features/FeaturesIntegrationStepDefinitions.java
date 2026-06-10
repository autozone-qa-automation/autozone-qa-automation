package com.autozone.integration.cucumber.features;

import com.autozone.integration.client.ApiResponse;
import com.autozone.integration.client.FeaturesApiClient;
import com.autozone.integration.client.ServicesApiClient;
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
 * Step definitions for the {@code @features} integration feature.
 *
 * <p>The shared "background" steps (base URL, login, token) and generic response assertions
 * live in {@link com.autozone.integration.cucumber.support.BackgroundStepDefinitions}. This
 * class holds the steps for {@code features.feature}.
 */
public class FeaturesIntegrationStepDefinitions {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private FeaturesApiClient apiClient;
  private ServicesApiClient servicesApiClient;

  // an existing service used to attach features to
  private long existingServiceId;
  private String existingServiceName;
  private Long fallbackCreatedServiceId;

  // created feature(s) state
  private long createdFeatureId;
  private String createdFeatureName;
  private String createdFeatureDescription;

  private long secondFeatureId;
  private String secondFeatureName;

  private final List<Long> createdFeatureIds = new ArrayList<>();

  @Before
  public void setUpClients() {
    String baseUrl = IntegrationConfig.getBaseUrl();
    apiClient = new FeaturesApiClient(baseUrl);
    servicesApiClient = new ServicesApiClient(baseUrl);
  }

  @After
  public void cleanUpCreatedFeatures() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    for (Long id : createdFeatureIds) {
      apiClient.deactivateFeature(token, id);
    }
    createdFeatureIds.clear();

    if (fallbackCreatedServiceId != null) {
      servicesApiClient.deleteService(token, fallbackCreatedServiceId);
      fallbackCreatedServiceId = null;
    }
  }

  // ---------------------------------------------------------------------
  // Generic GET dispatcher
  // ---------------------------------------------------------------------

  @When("the client sends a GET request to {string}")
  public void theClientSendsAGetRequestTo(String path) throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    if ("/api/v1/features".equals(path)) {
      IntegrationContext.setLastResponse(apiClient.getAllFeatures(token));
      return;
    }
    if (path.startsWith("/api/v1/features/filtered/")) {
      long serviceId = Long.parseLong(path.substring("/api/v1/features/filtered/".length()));
      IntegrationContext.setLastResponse(apiClient.getFeaturesByService(token, serviceId));
      return;
    }
    if (path.startsWith("/api/v1/features/")) {
      long id = Long.parseLong(path.substring("/api/v1/features/".length()));
      IntegrationContext.setLastResponse(apiClient.getFeatureById(token, id));
      return;
    }
    throw new IllegalArgumentException("Unsupported path for generic GET step: " + path);
  }

  // ---------------------------------------------------------------------
  // Shared shape assertions
  // ---------------------------------------------------------------------

  @Then("each feature in the response should have an \"id\", a \"featureName\", a \"featureDescription\" and an \"idService\"")
  public void eachFeatureShouldHaveExpectedFields() throws IOException {
    JsonNode features = readTree(IntegrationContext.getLastResponse().getBody());
    for (JsonNode feature : features) {
      assertTrue(feature.has("id"), "Feature is missing 'id': " + feature);
      assertTrue(feature.has("featureName"), "Feature is missing 'featureName': " + feature);
      assertTrue(feature.has("featureDescription"), "Feature is missing 'featureDescription': " + feature);
      assertTrue(feature.has("idService"), "Feature is missing 'idService': " + feature);
    }
  }

  // ---------------------------------------------------------------------
  // an existing service from the services list
  // ---------------------------------------------------------------------

  @Given("an existing service from the services list")
  public void anExistingServiceFromTheServicesList() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    ApiResponse response = servicesApiClient.getServices(token);
    assertEquals(200, response.getStatusCode(), "Could not list services. Body: " + response.getBody());

    JsonNode services = readTree(response.getBody());
    if (services.size() > 0) {
      JsonNode first = services.get(0);
      existingServiceId = first.get("id").asLong();
      existingServiceName = first.get("name").asText();
      return;
    }

    String name = "QA Fallback Service " + UUID.randomUUID();
    ObjectNode body = objectMapper.createObjectNode();
    body.put("name", name);
    body.put("description", "Fallback service for Features integration tests");
    body.put("isActive", true);
    body.putArray("urls");

    ApiResponse created = servicesApiClient.createService(token, objectMapper.writeValueAsString(body));
    assertEquals(201, created.getStatusCode(), "Could not create fallback service. Body: " + created.getBody());

    JsonNode node = readTree(created.getBody());
    existingServiceId = node.get("id").asLong();
    existingServiceName = name;
    fallbackCreatedServiceId = existingServiceId;
  }

  // ---------------------------------------------------------------------
  // Create / read / filter / update lifecycle
  // ---------------------------------------------------------------------

  @When("the client creates a new feature with a unique name for that service")
  public void theClientCreatesANewFeatureWithAUniqueNameForThatService() throws IOException, InterruptedException {
    createdFeatureName = "QA Integration Feature " + UUID.randomUUID();
    createdFeatureDescription = "Created by the Features API integration suite";

    IntegrationContext.setLastResponse(
        apiClient.createFeature(
            IntegrationContext.getToken(), buildFeatureJson(createdFeatureName, createdFeatureDescription, existingServiceId)));
  }

  @Then("the response should echo the created feature name, description and service id")
  public void theResponseShouldEchoTheCreatedFeatureNameDescriptionAndServiceId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdFeatureName, node.get("featureName").asText(), "Response did not echo the created feature name");
    assertEquals(createdFeatureDescription, node.get("featureDescription").asText(), "Response did not echo the created feature description");
    assertEquals(existingServiceId, node.get("idService").asLong(), "Response did not echo the service id");
  }

  @Then("the response should include the service name and a generated feature id")
  public void theResponseShouldIncludeTheServiceNameAndAGeneratedFeatureId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(existingServiceName, node.get("serviceName").asText(), "Response did not include the expected service name");

    JsonNode id = node.get("id");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated id: " + node);
    createdFeatureId = id.asLong();
    createdFeatureIds.add(createdFeatureId);
  }

  @When("the client retrieves the created feature by id")
  public void theClientRetrievesTheCreatedFeatureById() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getFeatureById(IntegrationContext.getToken(), createdFeatureId));
  }

  @Then("the response body should match the created feature data")
  public void theResponseBodyShouldMatchTheCreatedFeatureData() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdFeatureId, node.get("id").asLong(), "Feature id does not match");
    assertEquals(createdFeatureName, node.get("featureName").asText(), "Feature name does not match");
    assertEquals(createdFeatureDescription, node.get("featureDescription").asText(), "Feature description does not match");
    assertEquals(existingServiceId, node.get("idService").asLong(), "Feature idService does not match");
    assertEquals(existingServiceName, node.get("serviceName").asText(), "Feature serviceName does not match");
  }

  @When("the client lists features filtered by that service id")
  public void theClientListsFeaturesFilteredByThatServiceId() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getFeaturesByService(IntegrationContext.getToken(), existingServiceId));
  }

  @Then("the response should include the created feature")
  public void theResponseShouldIncludeTheCreatedFeature() throws IOException {
    JsonNode features = readTree(IntegrationContext.getLastResponse().getBody());
    boolean found = false;
    for (JsonNode feature : features) {
      if (feature.has("id") && feature.get("id").asLong() == createdFeatureId) {
        found = true;
        break;
      }
    }
    assertTrue(found, "Expected feature id " + createdFeatureId + " in filtered list: " + features);
  }

  @When("the client updates the created feature with a new name and description")
  public void theClientUpdatesTheCreatedFeatureWithANewNameAndDescription() throws IOException, InterruptedException {
    createdFeatureName = createdFeatureName + " (Updated)";
    createdFeatureDescription = "Updated by the Features API integration suite";

    IntegrationContext.setLastResponse(
        apiClient.updateFeature(
            IntegrationContext.getToken(), createdFeatureId, buildFeatureJson(createdFeatureName, createdFeatureDescription, existingServiceId)));
  }

  @Then("the response should reflect the updated feature name and description")
  public void theResponseShouldReflectTheUpdatedFeatureNameAndDescription() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdFeatureName, node.get("featureName").asText(), "Feature name was not updated");
    assertEquals(createdFeatureDescription, node.get("featureDescription").asText(), "Feature description was not updated");
  }

  @Then("the feature's service id should remain unchanged")
  public void theFeatureSServiceIdShouldRemainUnchanged() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(existingServiceId, node.get("idService").asLong(), "Feature idService should not change on update");
  }

  // ---------------------------------------------------------------------
  // Duplicate name on update -> 500 (known quirk)
  // ---------------------------------------------------------------------

  @Given("the client has created two features with distinct names for that service")
  public void theClientHasCreatedTwoFeaturesWithDistinctNamesForThatService() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    createdFeatureName = "QA Duplicate Name Feature A " + UUID.randomUUID();
    createdFeatureDescription = "First feature for duplicate-name update test";
    ApiResponse firstResponse = apiClient.createFeature(token, buildFeatureJson(createdFeatureName, createdFeatureDescription, existingServiceId));
    assertEquals(201, firstResponse.getStatusCode(), "Could not create first feature. Body: " + firstResponse.getBody());
    JsonNode firstNode = readTree(firstResponse.getBody());
    createdFeatureId = firstNode.get("id").asLong();
    createdFeatureIds.add(createdFeatureId);

    secondFeatureName = "QA Duplicate Name Feature B " + UUID.randomUUID();
    ApiResponse secondResponse = apiClient.createFeature(token, buildFeatureJson(secondFeatureName, "Second feature for duplicate-name update test", existingServiceId));
    assertEquals(201, secondResponse.getStatusCode(), "Could not create second feature. Body: " + secondResponse.getBody());
    JsonNode secondNode = readTree(secondResponse.getBody());
    secondFeatureId = secondNode.get("id").asLong();
    createdFeatureIds.add(secondFeatureId);
  }

  @When("the client updates the first feature using the second feature's name")
  public void theClientUpdatesTheFirstFeatureUsingTheSecondFeatureSName() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        apiClient.updateFeature(
            IntegrationContext.getToken(), createdFeatureId, buildFeatureJson(secondFeatureName, createdFeatureDescription, existingServiceId)));
  }

  // ---------------------------------------------------------------------
  // Deactivate -> 200 empty body (known quirk), then 404 on get
  // ---------------------------------------------------------------------

  @Given("the client has created a new feature for that service")
  public void theClientHasCreatedANewFeatureForThatService() throws IOException, InterruptedException {
    createdFeatureName = "QA Deactivate Feature " + UUID.randomUUID();
    createdFeatureDescription = "Created for the deactivate scenario";
    ApiResponse response = apiClient.createFeature(IntegrationContext.getToken(), buildFeatureJson(createdFeatureName, createdFeatureDescription, existingServiceId));
    assertEquals(201, response.getStatusCode(), "Could not create feature. Body: " + response.getBody());
    JsonNode node = readTree(response.getBody());
    createdFeatureId = node.get("id").asLong();
    createdFeatureIds.add(createdFeatureId);
  }

  @When("the client deactivates the created feature")
  public void theClientDeactivatesTheCreatedFeature() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.deactivateFeature(IntegrationContext.getToken(), createdFeatureId));
  }

  @When("the client deactivates the feature with id {long}")
  public void theClientDeactivatesTheFeatureWithId(long id) throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.deactivateFeature(IntegrationContext.getToken(), id));
  }

  // ---------------------------------------------------------------------
  // Validation
  // ---------------------------------------------------------------------

  @When("the client creates a feature with a null feature name for that service")
  public void theClientCreatesAFeatureWithANullFeatureNameForThatService() throws IOException, InterruptedException {
    ObjectNode body = objectMapper.createObjectNode();
    body.putNull("featureName");
    body.put("featureDescription", "Feature with no name");
    body.put("idService", existingServiceId);

    IntegrationContext.setLastResponse(apiClient.createFeature(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @When("the client creates a feature for service id {long}")
  public void theClientCreatesAFeatureForServiceId(long serviceId) throws IOException, InterruptedException {
    String name = "QA Feature For Missing Service " + UUID.randomUUID();
    IntegrationContext.setLastResponse(
        apiClient.createFeature(IntegrationContext.getToken(), buildFeatureJson(name, "Feature pointing at a non-existent service", serviceId)));
  }

  // ---------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------

  private String buildFeatureJson(String featureName, String featureDescription, long idService) throws IOException {
    ObjectNode body = objectMapper.createObjectNode();
    body.put("featureName", featureName);
    body.put("featureDescription", featureDescription);
    body.put("idService", idService);
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
