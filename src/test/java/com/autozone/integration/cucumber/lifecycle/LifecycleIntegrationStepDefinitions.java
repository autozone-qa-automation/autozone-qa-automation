package com.autozone.integration.cucumber.lifecycle;

import com.autozone.integration.client.FeaturesApiClient;
import com.autozone.integration.client.ReleasesApiClient;
import com.autozone.integration.client.ReportsApiClient;
import com.autozone.integration.client.ServicesApiClient;
import com.autozone.integration.client.TestCasesApiClient;
import com.autozone.integration.config.IntegrationConfig;
import com.autozone.integration.cucumber.support.IntegrationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Step definitions for the {@code @lifecycle} integration feature.
 *
 * <p>The shared "background" steps (base URL, login, token) and the generic "the response
 * {string} should equal {string}" / response assertions live in {@link
 * com.autozone.integration.cucumber.support.BackgroundStepDefinitions} and {@link
 * com.autozone.integration.cucumber.releases.ReleasesIntegrationStepDefinitions}. This class
 * holds the steps unique to {@code lifecycle.feature}, exercising the full Service -&gt; Feature
 * -&gt; Test Case -&gt; Release -&gt; Reports chain.
 */
public class LifecycleIntegrationStepDefinitions {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private ServicesApiClient servicesApiClient;
  private FeaturesApiClient featuresApiClient;
  private TestCasesApiClient testCasesApiClient;
  private ReleasesApiClient releasesApiClient;
  private ReportsApiClient reportsApiClient;

  private Long lifecycleServiceId;
  private String lifecycleServiceName;
  private Long lifecycleFeatureId;
  private String lifecycleFeatureName;
  private Long lifecycleTestCaseId;
  private String lifecycleTestCaseTitle;
  private long lifecycleReleaseId;
  private String lifecycleReleaseName;
  private String lifecycleReleaseTag;

  @Before
  public void setUpClients() {
    String baseUrl = IntegrationConfig.getBaseUrl();
    servicesApiClient = new ServicesApiClient(baseUrl);
    featuresApiClient = new FeaturesApiClient(baseUrl);
    testCasesApiClient = new TestCasesApiClient(baseUrl);
    releasesApiClient = new ReleasesApiClient(baseUrl);
    reportsApiClient = new ReportsApiClient(baseUrl);
  }

  @After
  public void cleanUpLifecycleResources() throws IOException, InterruptedException {
    String token = IntegrationContext.getToken();
    if (lifecycleTestCaseId != null) {
      testCasesApiClient.deactivateTestCase(token, lifecycleTestCaseId);
      lifecycleTestCaseId = null;
    }
    if (lifecycleFeatureId != null) {
      featuresApiClient.deactivateFeature(token, lifecycleFeatureId);
      lifecycleFeatureId = null;
    }
    if (lifecycleServiceId != null) {
      servicesApiClient.deleteService(token, lifecycleServiceId);
      lifecycleServiceId = null;
    }
  }

  // ---------------------------------------------------------------------
  // Service -> Feature -> Test Case creation
  // ---------------------------------------------------------------------

  @When("the client creates a new service for the lifecycle scenario")
  public void theClientCreatesANewServiceForTheLifecycleScenario() throws IOException, InterruptedException {
    lifecycleServiceName = "QA Lifecycle Service " + UUID.randomUUID();

    ObjectNode body = objectMapper.createObjectNode();
    body.put("name", lifecycleServiceName);
    body.put("description", "Created by the Lifecycle integration scenario");
    body.put("isActive", true);

    ArrayNode urls = body.putArray("urls");
    ObjectNode url = urls.addObject();
    url.put("nombre", "QA");
    url.put("url", "https://qa.example.com");

    IntegrationContext.setLastResponse(
        servicesApiClient.createService(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @Then("the response should include a generated lifecycle service id")
  public void theResponseShouldIncludeAGeneratedLifecycleServiceId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    JsonNode id = node.get("id");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated service id: " + node);
    lifecycleServiceId = id.asLong();
  }

  @When("the client creates a new feature for the lifecycle service")
  public void theClientCreatesANewFeatureForTheLifecycleService() throws IOException, InterruptedException {
    lifecycleFeatureName = "QA Lifecycle Feature " + UUID.randomUUID();

    ObjectNode body = objectMapper.createObjectNode();
    body.put("featureName", lifecycleFeatureName);
    body.put("featureDescription", "Created by the Lifecycle integration scenario");
    body.put("idService", lifecycleServiceId);

    IntegrationContext.setLastResponse(
        featuresApiClient.createFeature(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @Then("the response should include a generated lifecycle feature id")
  public void theResponseShouldIncludeAGeneratedLifecycleFeatureId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    JsonNode id = node.get("id");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated feature id: " + node);
    lifecycleFeatureId = id.asLong();
  }

  @When("the client creates a new test case for the lifecycle feature")
  public void theClientCreatesANewTestCaseForTheLifecycleFeature() throws IOException, InterruptedException {
    lifecycleTestCaseTitle = "QA Lifecycle Test Case " + UUID.randomUUID();

    ObjectNode body = objectMapper.createObjectNode();
    body.put("title", lifecycleTestCaseTitle);
    body.put("steps", "1. Open the application. 2. Perform the lifecycle action under test.");
    body.put("expectedOutput", "The lifecycle outcome is observed.");
    body.put("type", "REGRESSION");
    body.put("featureId", lifecycleFeatureId);

    IntegrationContext.setLastResponse(
        testCasesApiClient.createTestCase(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @Then("the response should include a generated lifecycle test case id")
  public void theResponseShouldIncludeAGeneratedLifecycleTestCaseId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    JsonNode id = node.get("id");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated test case id: " + node);
    lifecycleTestCaseId = id.asLong();
  }

  // ---------------------------------------------------------------------
  // Release creation and status transitions
  // ---------------------------------------------------------------------

  @When("the client creates a new Draft release for the lifecycle service including the lifecycle feature")
  public void theClientCreatesANewDraftReleaseForTheLifecycleServiceIncludingTheLifecycleFeature()
      throws IOException, InterruptedException {
    lifecycleReleaseName = "QA Lifecycle Release " + UUID.randomUUID();
    lifecycleReleaseTag = "qa-lifecycle-" + UUID.randomUUID();

    ObjectNode body = objectMapper.createObjectNode();
    body.put("releaseName", lifecycleReleaseName);
    body.put("releaseDescription", "Created by the Lifecycle integration scenario");
    body.put("releaseCreationDate", LocalDate.now().toString());
    body.put("releaseVersion", "1.0.0");
    body.put("releaseStatus", "Draft");
    body.put("releaseServiceId", lifecycleServiceId);

    ArrayNode tags = body.putArray("releaseTags");
    tags.add(lifecycleReleaseTag);

    ArrayNode featureIds = body.putArray("releaseFeatureIds");
    featureIds.add(lifecycleFeatureId);

    IntegrationContext.setLastResponse(
        releasesApiClient.createRelease(IntegrationContext.getToken(), objectMapper.writeValueAsString(body)));
  }

  @Then("the response should include a generated lifecycle release id")
  public void theResponseShouldIncludeAGeneratedLifecycleReleaseId() throws IOException {
    JsonNode node = readTree(IntegrationContext.getLastResponse().getBody());
    JsonNode id = node.get("releaseId");
    assertTrue(id != null && !id.isNull() && id.isIntegralNumber(), "Response did not include a generated release id: " + node);
    lifecycleReleaseId = id.asLong();
  }

  @When("the client transitions the lifecycle release to status {string}")
  public void theClientTransitionsTheLifecycleReleaseToStatus(String status) throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        releasesApiClient.updateReleaseStatus(IntegrationContext.getToken(), lifecycleReleaseId, status));
  }

  // ---------------------------------------------------------------------
  // Reports hierarchy and CSV export
  // ---------------------------------------------------------------------

  @When("the client lists reports filtered by the lifecycle release's tag")
  public void theClientListsReportsFilteredByTheLifecycleReleaseSTag() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        reportsApiClient.getReports(IntegrationContext.getToken(), null, lifecycleReleaseTag));
  }

  @Then("the response should include the lifecycle release with the service, feature and test case in its hierarchy")
  public void theResponseShouldIncludeTheLifecycleReleaseWithTheServiceFeatureAndTestCaseInItsHierarchy() throws IOException {
    JsonNode reports = readTree(IntegrationContext.getLastResponse().getBody());

    JsonNode release = null;
    for (JsonNode candidate : reports) {
      if (candidate.has("releaseId") && candidate.get("releaseId").asLong() == lifecycleReleaseId) {
        release = candidate;
        break;
      }
    }
    assertTrue(release != null, "Expected release id " + lifecycleReleaseId + " in reports: " + reports);

    JsonNode services = release.get("services");
    assertTrue(services != null && services.isArray(), "Report release is missing a 'services' array: " + release);

    JsonNode service = null;
    for (JsonNode candidate : services) {
      if (lifecycleServiceName.equals(candidate.get("serviceName").asText())) {
        service = candidate;
        break;
      }
    }
    assertTrue(service != null, "Expected service '" + lifecycleServiceName + "' in report services: " + services);

    JsonNode features = service.get("features");
    assertTrue(features != null && features.isArray(), "Report service is missing a 'features' array: " + service);

    JsonNode feature = null;
    for (JsonNode candidate : features) {
      if (lifecycleFeatureName.equals(candidate.get("featureName").asText())) {
        feature = candidate;
        break;
      }
    }
    assertTrue(feature != null, "Expected feature '" + lifecycleFeatureName + "' in report features: " + features);

    JsonNode testCases = feature.get("testCases");
    assertTrue(testCases != null && testCases.isArray(), "Report feature is missing a 'testCases' array: " + feature);

    boolean foundTestCase = false;
    for (JsonNode candidate : testCases) {
      if (lifecycleTestCaseTitle.equals(candidate.asText())) {
        foundTestCase = true;
        break;
      }
    }
    assertTrue(foundTestCase, "Expected test case '" + lifecycleTestCaseTitle + "' in report test cases: " + testCases);
  }

  @When("the client requests the reports CSV export for the lifecycle release")
  public void theClientRequestsTheReportsCsvExportForTheLifecycleRelease() throws IOException, InterruptedException {
    IntegrationContext.setLastResponse(
        reportsApiClient.exportReportsCsv(IntegrationContext.getToken(), String.valueOf(lifecycleReleaseId)));
  }

  @Then("the exported CSV should contain the lifecycle release, service, feature and test case")
  public void theExportedCsvShouldContainTheLifecycleReleaseServiceFeatureAndTestCase() {
    String body = IntegrationContext.getLastResponse().getBody();
    assertTrue(body.contains(lifecycleReleaseName), "Expected CSV to contain the release name '" + lifecycleReleaseName + "': " + body);
    assertTrue(body.contains(lifecycleServiceName), "Expected CSV to contain the service name '" + lifecycleServiceName + "': " + body);
    assertTrue(body.contains(lifecycleFeatureName), "Expected CSV to contain the feature name '" + lifecycleFeatureName + "': " + body);
    assertTrue(body.contains(lifecycleTestCaseTitle), "Expected CSV to contain the test case title '" + lifecycleTestCaseTitle + "': " + body);
  }

  // ---------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------

  private JsonNode readTree(String json) throws IOException {
    return objectMapper.readTree(json);
  }

  private void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }
}
