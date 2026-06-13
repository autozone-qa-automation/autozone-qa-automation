package com.autozone.integration.cucumber.releases;

import java.io.IOException;
import java.util.UUID;

import com.autozone.integration.client.ReleasesApiClient;
import com.autozone.integration.config.IntegrationConfig;
import com.autozone.integration.cucumber.support.IntegrationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for the {@code @releases} integration feature.
 *
 * <p>The shared "background" steps (base URL, login, token), the "an existing service from the
 * services list" fixture and generic GET/DELETE/response assertions live in {@link
 * com.autozone.integration.cucumber.support.BackgroundStepDefinitions}. This class holds the
 * steps unique to {@code releases.feature}.
 */
public class ReleasesIntegrationStepDefinitions {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private ReleasesApiClient apiClient;

  // created release state
  private long createdReleaseId;
  private String createdReleaseName;
  private String createdReleaseDescription;
  private String createdReleaseVersion;
  private String createdReleaseStatus;
  private String createdReleaseTags;

  @Before
  public void setUpClient() {
    apiClient = new ReleasesApiClient(IntegrationConfig.getBaseUrl());
  }

  // ---------------------------------------------------------------------
  // Shared shape assertion
  // ---------------------------------------------------------------------

  @Then("each release in the response should have a \"releaseId\", a \"releaseName\", a \"releaseDescription\", a \"releaseVersion\", a \"releaseStatus\", \"releaseTags\", a \"releaseCreationDate\" and a \"releaseIsActive\" flag")
  public void eachReleaseShouldHaveExpectedFields() throws IOException {
    JsonNode releases = readTree(IntegrationContext.getLastResponse().getBody());
    for (JsonNode release : releases) {
      assertTrue(release.has("releaseId"), "Release is missing 'releaseId': " + release);
      assertTrue(release.has("releaseName"), "Release is missing 'releaseName': " + release);
      assertTrue(release.has("releaseDescription"), "Release is missing 'releaseDescription': " + release);
      assertTrue(release.has("releaseVersion"), "Release is missing 'releaseVersion': " + release);
      assertTrue(release.has("releaseStatus"), "Release is missing 'releaseStatus': " + release);
      assertTrue(release.has("releaseTags"), "Release is missing 'releaseTags': " + release);
      assertTrue(release.has("releaseCreationDate"), "Release is missing 'releaseCreationDate': " + release);
      assertTrue(release.has("releaseIsActive"), "Release is missing 'releaseIsActive': " + release);
    }
  }

  // ---------------------------------------------------------------------
  // Create / read / filter lifecycle
  // ---------------------------------------------------------------------

  @When("the client creates a new Draft release with a unique name")
  public void theClientCreatesANewDraftReleaseWithAUniqueName() throws IOException, InterruptedException {
    createdReleaseName = "QA Integration Release " + UUID.randomUUID();
    createdReleaseDescription = "Created by the Releases API integration suite";
    createdReleaseVersion = "1.0.0";
    createdReleaseStatus = "Draft";
    createdReleaseTags = "qa,integration";

    IntegrationContext.setLastResponse(
        apiClient.createRelease(
            IntegrationContext.getToken(),
            buildReleaseJson(
                createdReleaseName,
                createdReleaseDescription,
                createdReleaseVersion,
                createdReleaseStatus,
                createdReleaseTags)));
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    if (node.has("releaseId")) {
        createdReleaseId = node.get("releaseId").asLong();
    }
  }

  @When("the client creates a new Draft release with the tag {string}")
  public void theClientCreatesANewDraftReleaseWithTheTag(String tag) throws IOException, InterruptedException {
    createdReleaseName = "QA Tagged Release " + UUID.randomUUID();
    createdReleaseDescription = "Created by the Releases API integration suite for tag filtering";
    createdReleaseVersion = "1.0.0";
    createdReleaseStatus = "Draft";
    createdReleaseTags = tag;

    IntegrationContext.setLastResponse(
        apiClient.createRelease(
            IntegrationContext.getToken(),
            buildReleaseJson(
                createdReleaseName,
                createdReleaseDescription,
                createdReleaseVersion,
                createdReleaseStatus,
                createdReleaseTags)));

    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    createdReleaseId = node.get("releaseId").asLong();
  }

  @When("the client creates a new release with a unique name and status {string}")
  public void theClientCreatesANewReleaseWithAUniqueNameAndStatus(String status) throws IOException, InterruptedException {
    createdReleaseName = "QA Status Release " + UUID.randomUUID();
    createdReleaseDescription = "Created by the Releases API integration suite for status transitions";
    createdReleaseVersion = "1.0.0";
    createdReleaseStatus = status;
    createdReleaseTags = "qa,status";

    IntegrationContext.setLastResponse(
        apiClient.createRelease(
            IntegrationContext.getToken(),
            buildReleaseJson(
                createdReleaseName,
                createdReleaseDescription,
                createdReleaseVersion,
                createdReleaseStatus,
                createdReleaseTags)));
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    if (node.has("releaseId")) {
        createdReleaseId = node.get("releaseId").asLong();
    }
  }

  @When("the client creates a release with a null release name")
  public void theClientCreatesAReleaseWithANullReleaseName() throws IOException, InterruptedException {
    ObjectNode body = objectMapper.createObjectNode();
    body.putNull("releaseName");
    body.put("releaseDescription", "Release with no name");
    body.put("releaseVersion", "1.0.0");
    body.put("releaseStatus", "Draft");
    body.put("idService", IntegrationContext.getExistingServiceId());

    ArrayNode tags = body.putArray("releaseTags");
    tags.add("qa");

    IntegrationContext.setLastResponse(
        apiClient.createRelease(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @Then("the response should echo the created release name, description, version, status and tags")
  public void theResponseShouldEchoTheCreatedReleaseNameDescriptionVersionStatusAndTags() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdReleaseName, node.get("releaseName").asText(), "Response did not echo the created release name");
    assertEquals(createdReleaseDescription, node.get("releaseDescription").asText(), "Response did not echo the created release description");
    assertEquals(createdReleaseVersion, node.get("releaseVersion").asText(), "Response did not echo the created release version");
    assertEquals(createdReleaseStatus, node.get("releaseStatus").asText(), "Response did not echo the created release status");

    JsonNode tags = node.get("releaseTags");
    assertTrue(tags != null && tags.isArray(), "Response is missing a 'releaseTags' array: " + node);
    StringBuilder joined = new StringBuilder();
    for (JsonNode tag : tags) {
      if (joined.length() > 0) {
        joined.append(",");
      }
      joined.append(tag.asText());
    }
    assertEquals(createdReleaseTags, joined.toString(), "Response did not echo the created release tags");
  }

  @Then("the response should be active and include a generated release id")
  public void theResponseShouldBeActiveAndIncludeAGeneratedReleaseId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertTrue(node.get("releaseIsActive").asBoolean(), "Release should be active");

    JsonNode id = node.get("releaseId");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated release id: " + node);
    createdReleaseId = id.asLong();
  }

  @When("the client retrieves the created release by id")
  public void theClientRetrievesTheCreatedReleaseById() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getReleaseById(IntegrationContext.getToken(), createdReleaseId));
  }

  @Then("the response body should match the created release data")
  public void theResponseBodyShouldMatchTheCreatedReleaseData() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(createdReleaseId, node.get("releaseId").asLong(), "Release id does not match");
    assertEquals(createdReleaseName, node.get("releaseName").asText(), "Release name does not match");
    assertEquals(createdReleaseDescription, node.get("releaseDescription").asText(), "Release description does not match");
    assertEquals(createdReleaseVersion, node.get("releaseVersion").asText(), "Release version does not match");
    assertEquals(createdReleaseStatus, node.get("releaseStatus").asText(), "Release status does not match");
  }

  @When("the client lists releases filtered by status {string}")
  public void theClientListsReleasesFilteredByStatus(String status) throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getReleases(IntegrationContext.getToken(), status, null));
  }

  @When("the client lists releases filtered by tag {string}")
  public void theClientListsReleasesFilteredByTag(String tag) throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.getReleases(IntegrationContext.getToken(), null, tag));
  }

  @Then("the response should include the created release")
  public void theResponseShouldIncludeTheCreatedRelease() throws IOException {
    JsonNode releases = readTree(IntegrationContext.getLastResponse().getBody());
    boolean found = false;
    for (JsonNode release : releases) {
      if (release.has("releaseId") && release.get("releaseId").asLong() == createdReleaseId) {
        found = true;
        break;
      }
    }
    assertTrue(found, "Expected release id " + createdReleaseId + " in filtered list: " + releases);
  }

  // ---------------------------------------------------------------------
  // Status transitions and deletion
  // ---------------------------------------------------------------------

  @When("the client transitions the created release to status {string}")
  public void theClientTransitionsTheCreatedReleaseToStatus(String status) throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        apiClient.updateReleaseStatus(IntegrationContext.getToken(), createdReleaseId, status));
  }

  @When("the client transitions the release with id {long} to status {string}")
  public void theClientTransitionsTheReleaseWithIdToStatus(long id, String status) throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.updateReleaseStatus(IntegrationContext.getToken(), id, status));
  }

  @When("the client deletes the created release")
  public void theClientDeletesTheCreatedRelease() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(apiClient.deleteRelease(IntegrationContext.getToken(), createdReleaseId));
  }

  // ---------------------------------------------------------------------
  // Generic field equality assertion
  // ---------------------------------------------------------------------

  @Then("the response {string} should equal {string}")
  public void theResponseFieldShouldEqual(String fieldName, String expectedValue) throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    assertEquals(expectedValue, node.get(fieldName).asText(), "Response field '" + fieldName + "' does not match");
  }

  // ---------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------

  private String buildReleaseJson(String name, String description, String version, String status, String tags)
      throws IOException {
    ObjectNode body = objectMapper.createObjectNode();
    body.put("releaseName", name);
    body.put("releaseDescription", description);
    body.put("releaseVersion", version);
    body.put("releaseStatus", status);
    body.put("releaseServiceId", IntegrationContext.getExistingServiceId());
    body.put("releaseCreationDate", java.time.LocalDate.now().toString());

    ArrayNode tagsArray = body.putArray("releaseTags");
    for (String tag : tags.split(",")) {
      tagsArray.add(tag);
    }

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
