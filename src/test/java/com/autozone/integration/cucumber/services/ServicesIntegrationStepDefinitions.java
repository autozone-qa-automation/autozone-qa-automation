package com.autozone.integration.cucumber.services;

import com.autozone.integration.client.ServicesApiClient;
import com.autozone.integration.config.IntegrationConfig;
import com.autozone.integration.cucumber.support.IntegrationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Step definitions for the {@code @services} integration features.
 *
 * <p>The shared "background" steps (base URL, login, token) and generic response assertions
 * live in {@link com.autozone.integration.cucumber.support.BackgroundStepDefinitions}. This
 * class holds the steps for services-list, services-get, services-test-endpoint,
 * services-crud and services-validation.
 */
public class ServicesIntegrationStepDefinitions {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private ServicesApiClient apiClient;

  // services-crud: state for the create/read/update/delete lifecycle
  private String createdServiceName;
  private String createdServiceDescription;
  private long createdServiceId;
  private long createdUrlId;
  private String updatedServiceName;
  private String updatedServiceDescription;

  @Before
  public void setUpClient() {
    apiClient = new ServicesApiClient(IntegrationConfig.getBaseUrl());
  }

  // ---------------------------------------------------------------------
  // services-list.feature
  // ---------------------------------------------------------------------

  @Then("each service in the response should have an \"id\", a \"name\", an \"isActive\" flag and a \"urls\" array")
  public void eachServiceShouldHaveExpectedFields() throws IOException {
    JsonNode services = readTree(IntegrationContext.getLastResponse().getBody());
    for (JsonNode service : services) {
      assertTrue(service.has("id"), "Service is missing 'id': " + service);
      assertTrue(service.has("name"), "Service is missing 'name': " + service);
      assertTrue(service.has("isActive"), "Service is missing 'isActive': " + service);
      assertTrue(
          service.has("urls") && service.get("urls").isArray(),
          "Service is missing a 'urls' array: " + service);
    }
  }

  // ---------------------------------------------------------------------
  // services-get.feature / services-validation.feature
  // ---------------------------------------------------------------------

  @When("the client sends a GET request for that service by id")
  public void theClientSendsAGetRequestForThatServiceById() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        apiClient.getServiceById(IntegrationContext.getToken(), IntegrationContext.getExistingServiceId()));
  }

  @Then("the response body should match the ServicesVO shape")
  public void theResponseBodyShouldMatchTheServicesVOShape() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertTrue(node.has("id"), "Response is missing 'id': " + node);
    assertTrue(node.has("name"), "Response is missing 'name': " + node);
    assertTrue(node.has("isActive"), "Response is missing 'isActive': " + node);
    assertTrue(node.has("urls") && node.get("urls").isArray(), "Response is missing a 'urls' array: " + node);
  }

  @Then("the returned service id should equal the requested id")
  public void theReturnedServiceIdShouldEqualTheRequestedId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(
        IntegrationContext.getExistingServiceId(),
        node.get("id").asLong(),
        "Returned service id does not match the requested id");
  }

  @When("the client creates a service with a null name")
  public void theClientCreatesAServiceWithANullName() throws IOException, InterruptedException {
    ObjectNode body = objectMapper.createObjectNode();
    body.putNull("name");
    body.put("description", "Service with no name");
    body.put("isActive", true);
    body.putArray("urls");

    IntegrationContext.setLastResponse(
        apiClient.createService(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @When("the client creates a service using that existing service's name")
  public void theClientCreatesAServiceUsingThatExistingServiceSName() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        apiClient.createService(
            IntegrationContext.getToken(),
            buildServiceJson(
                IntegrationContext.getExistingServiceName(), "Duplicate name validation test", "QA", "https://qa.example.com")));
  }

  // ---------------------------------------------------------------------
  // services-test-endpoint.feature
  // ---------------------------------------------------------------------

  @Then("the response body should contain exactly 3 urls")
  public void theResponseBodyShouldContainExactly3Urls() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    JsonNode urls = node.get("urls");
    assertTrue(urls != null && urls.isArray(), "Response is missing a 'urls' array: " + node);
    assertEquals(3, urls.size(), "Expected exactly 3 urls but got: " + urls);
  }

  @Then("the urls should include the environments {string}, {string} and {string}")
  public void theUrlsShouldIncludeTheEnvironments(String env1, String env2, String env3) throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    Set<String> names = new HashSet<>();
    for (JsonNode url : node.get("urls")) {
      names.add(url.get("nombre").asText());
    }

    for (String expected : List.of(env1, env2, env3)) {
      assertTrue(names.contains(expected), "Expected urls to include '" + expected + "' but got " + names);
    }
  }

  // ---------------------------------------------------------------------
  // services-crud.feature
  // ---------------------------------------------------------------------

  @When("the client creates a new service with a unique name")
  public void theClientCreatesANewServiceWithAUniqueName() throws IOException, InterruptedException {
    createdServiceName = "QA Integration Service " + UUID.randomUUID();
    createdServiceDescription = "Created by the Services API integration suite";

    IntegrationContext.setLastResponse(
        apiClient.createService(
            IntegrationContext.getToken(),
            buildServiceJson(createdServiceName, createdServiceDescription, "QA", "https://qa.example.com")));
  }

  @Then("the response should echo the created service name")
  public void theResponseShouldEchoTheCreatedServiceName() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdServiceName, node.get("name").asText(), "Response did not echo the created service name");
  }

  @Then("the response should include a generated service id")
  public void theResponseShouldIncludeAGeneratedServiceId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    JsonNode id = node.get("id");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated id: " + node);
    createdServiceId = id.asLong();
  }

  @When("the client retrieves the created service by id")
  public void theClientRetrievesTheCreatedServiceById() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getServiceById(IntegrationContext.getToken(), createdServiceId));
  }

  @Then("the response body should match the created service data")
  public void theResponseBodyShouldMatchTheCreatedServiceData() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdServiceId, node.get("id").asLong(), "Service id does not match");
    assertEquals(createdServiceName, node.get("name").asText(), "Service name does not match");
    assertEquals(createdServiceDescription, node.get("description").asText(), "Service description does not match");
    assertTrue(node.get("isActive").asBoolean(), "Service should be active");

    JsonNode urls = node.get("urls");
    assertEquals(1, urls.size(), "Expected exactly 1 url but got: " + urls);

    JsonNode url = urls.get(0);
    assertEquals("QA", url.get("nombre").asText(), "Url 'nombre' does not match");
    assertEquals("https://qa.example.com", url.get("url").asText(), "Url 'url' does not match");

    JsonNode urlId = url.get("idUrl");
    assertTrue(urlId != null && !urlId.isNull(), "Url is missing a generated 'idUrl': " + url);
    createdUrlId = urlId.asLong();
  }

  @When("the client updates the created service with new name, description and urls")
  public void theClientUpdatesTheCreatedServiceWithNewNameDescriptionAndUrls() throws IOException, InterruptedException {
    updatedServiceName = createdServiceName + " (Updated)";
    updatedServiceDescription = "Updated by the Services API integration suite";

    ObjectNode body = objectMapper.createObjectNode();
    body.put("name", updatedServiceName);
    body.put("description", updatedServiceDescription);
    body.put("isActive", true);

    ArrayNode urls = body.putArray("urls");
    ObjectNode url = urls.addObject();
    url.put("idUrl", createdUrlId);
    url.put("nombre", "QA Updated");
    url.put("url", "https://qa-updated.example.com");

    IntegrationContext.setLastResponse(
        apiClient.updateService(IntegrationContext.getToken(), createdServiceId, objectMapper.writeValueAsString(body)));
  }

  @Then("the response should reflect the updated service data")
  public void theResponseShouldReflectTheUpdatedServiceData() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(updatedServiceName, node.get("name").asText(), "Service name was not updated");
    assertEquals(updatedServiceDescription, node.get("description").asText(), "Service description was not updated");

    boolean foundUpdatedUrl = false;
    for (JsonNode url : node.get("urls")) {
      if ("QA Updated".equals(url.get("nombre").asText())
          && "https://qa-updated.example.com".equals(url.get("url").asText())) {
        foundUpdatedUrl = true;
      }
    }
    assertTrue(foundUpdatedUrl, "Updated url was not found in response: " + node.get("urls"));
  }

  @When("the client deletes the created service")
  public void theClientDeletesTheCreatedService() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.deleteService(IntegrationContext.getToken(), createdServiceId));
  }

  // ---------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------

  private String buildServiceJson(String name, String description, String urlName, String urlValue)
      throws IOException {
    ObjectNode body = objectMapper.createObjectNode();
    body.put("name", name);
    body.put("description", description);
    body.put("isActive", true);

    ArrayNode urls = body.putArray("urls");
    ObjectNode url = urls.addObject();
    url.put("nombre", urlName);
    url.put("url", urlValue);

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
