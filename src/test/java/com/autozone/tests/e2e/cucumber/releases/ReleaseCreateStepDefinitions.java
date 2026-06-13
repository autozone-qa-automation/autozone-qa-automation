package com.autozone.tests.e2e.cucumber.releases;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.ReleaseCreateBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReleaseCreateStepDefinitions {

    private ReleaseCreateBot bot() {
        return CucumberScenarioContext.getReleaseCreateBot();
    }

    @Given("the user is on the releases page")
    public void theUserIsOnTheReleasesPage() {
        CucumberScenarioContext.getReleasesBot().openList();
        CucumberScenarioContext.getReleasesBot().waitUntilListReady();
    }

    @When("the user opens the create release modal")
    public void theUserOpensTheCreateReleaseModal() {
        bot().openCreateModal();
        assertTrue(bot().isModalVisible(), "Expected create release modal to be visible");
    }

    @And("the user leaves the release name empty")
    public void theUserLeavesTheReleaseNameEmpty() {
        bot().clearReleaseName();
    }

    @And("the user leaves the release version empty")
    public void theUserLeavesTheReleaseVersionEmpty() {
        bot().clearReleaseVersion();
    }

    @And("the user clicks the create release submit button")
    public void theUserClicksTheCreateReleaseSubmitButton() {
        bot().clickSubmit();
    }

    @Then("validation errors should be shown for the required fields")
    public void validationErrorsShouldBeShownForTheRequiredFields() {
        assertTrue(bot().hasAnyValidationError(), "Expected validation errors to be displayed");
        assertTrue(bot().hasNameValidationError(), "Expected name validation error");
        assertTrue(bot().hasVersionValidationError(), "Expected version validation error");
    }

    @And("the user enters the release name {string}")
    public void theUserEntersTheReleaseName(String name) {
        bot().enterReleaseName(name);
    }

    @And("the user enters the objective {string}")
    public void theUserEntersTheObjective(String objective) {
        bot().enterObjective(objective);
    }

    @And("the user enters the release version {string}")
    public void theUserEntersTheReleaseVersion(String version) {
        bot().enterReleaseVersion(version);
    }

    @And("the user selects the status {string}")
    public void theUserSelectsTheStatus(String status) {
        switch (status.toLowerCase()) {
            case "draft" -> bot().clickStatusDraft();
            case "progress" -> bot().clickStatusProgress();
            case "active" -> bot().clickStatusActive();
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        }
    }

    @And("the user selects a service")
    public void theUserSelectsAService() {
        bot().selectFirstService();
    }

    @And("the user selects a feature")
    public void theUserSelectsAFeature() {
        bot().selectFirstFeature();
    }

    @And("the user adds a tag {string}")
    public void theUserAddsATag(String tag) {
        bot().addTag(tag);
    }

    @Then("the release should be created successfully")
    public void theReleaseShouldBeCreatedSuccessfully() {
        assertTrue(
                bot().waitForSuccessNotification(),
                "Expected success notification: 'Release created successfully'"
        );
    }

    @And("the create release modal should close")
    public void theCreateReleaseModalShouldClose() {
        assertTrue(bot().waitForModalToClose(), "Expected the create release modal to close");
    }

    @Then("the features field should be disabled")
    public void theFeaturesFieldShouldBeDisabled() {
        assertTrue(bot().isFeaturesDisabled(), "Expected features field to be disabled");
    }

    @And("the features placeholder should indicate service selection is required")
    public void theFeaturesPlaceholderShouldIndicateServiceSelectionIsRequired() {
        String placeholder = bot().getFeaturesPlaceholder();
        assertTrue(
                placeholder != null && placeholder.contains("Requires prior service selection"),
                "Expected placeholder to indicate service selection is required, but got: " + placeholder
        );
    }

    @Then("the features field should be enabled")
    public void theFeaturesFieldShouldBeEnabled() {
        assertFalse(bot().isFeaturesDisabled(), "Expected features field to be enabled after service selection");
    }

    @Then("the status should default to {string}")
    public void theStatusShouldDefaultTo(String expectedStatus) {
        switch (expectedStatus.toLowerCase()) {
            case "draft" -> assertTrue(bot().isStatusDraftSelected(), "Expected Draft status to be selected by default");
            default -> throw new IllegalArgumentException("Unknown default status: " + expectedStatus);
        }
    }

    @When("the user clicks on the {string} status")
    public void theUserClicksOnTheStatus(String status) {
        theUserSelectsTheStatus(status);
    }

    @Then("the {string} status should be selected")
    public void theStatusShouldBeSelected(String status) {
        boolean isSelected = switch (status.toLowerCase()) {
            case "draft" -> bot().isStatusDraftSelected();
            case "progress" -> bot().isStatusProgressSelected();
            case "active" -> bot().isStatusActiveSelected();
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
        assertTrue(isSelected, "Expected '" + status + "' status to be selected");
    }

    @When("the user changes to a different service")
    public void theUserChangesToADifferentService() {
        bot().selectDifferentService();
    }

    @Then("the previously selected features should be cleared")
    public void thePreviouslySelectedFeaturesShouldBeCleared() {
        assertFalse(
                bot().hasAnyFeatureSelected(),
                "Expected previously selected features to be cleared after service change"
        );
    }
}
