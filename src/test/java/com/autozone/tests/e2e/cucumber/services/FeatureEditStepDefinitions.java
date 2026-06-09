package com.autozone.tests.e2e.cucumber.services;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.FeatureBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FeatureEditStepDefinitions {

    private String originalFeatureName;
    private String originalFeatureDescription;

    @Given("the user opens the feature details page for {int}")
    public void theUserOpensTheFeatureDetailsPage(int featureId) {
        FeatureBot featureBot = CucumberScenarioContext.getFeatureBot();
        featureBot.openFeature(featureId);
        featureBot.waitUntilPageReady();

        originalFeatureName = featureBot.getFeatureTitle();
        originalFeatureDescription = featureBot.getFeatureDescription();

        assertTrue(featureBot.isPageVisible(), "Expected feature details page to be visible");
    }

    @When("the user opens the edit feature modal")
    public void theUserOpensTheEditFeatureModal() {
        FeatureBot featureBot = CucumberScenarioContext.getFeatureBot();
        featureBot.openEditModal();
        assertTrue(featureBot.isEditModalVisible(), "Expected edit feature modal to be visible");
    }

    @When("the user updates the feature name to {string}")
    public void theUserUpdatesTheFeatureNameTo(String name) {
        CucumberScenarioContext.getFeatureBot().setFeatureName(name);
    }

    @When("the user updates the feature description to {string}")
    public void theUserUpdatesTheFeatureDescriptionTo(String description) {
        CucumberScenarioContext.getFeatureBot().setFeatureDescription(description);
    }

    @When("the user clears the feature name field")
    public void theUserClearsTheFeatureNameField() {
        CucumberScenarioContext.getFeatureBot().clearFeatureName();
    }

    @When("the user clicks save changes")
    public void theUserClicksSaveChanges() {
        CucumberScenarioContext.getFeatureBot().clickSaveFeature();
    }

    @Then("the feature success message should be displayed")
    public void theFeatureSuccessMessageShouldBeDisplayed() {
        assertTrue(CucumberScenarioContext.getFeatureBot().waitForSuccessNotification(),
                "Expected a success notification after saving the feature");
    }

    @Then("the feature detail title should show {string}")
    public void theFeatureDetailTitleShouldShow(String title) {
        assertTrue(CucumberScenarioContext.getFeatureBot().waitForFeatureTitle(title),
                "Expected feature title to update to: " + title);
    }

    @Then("the feature description should show {string}")
    public void theFeatureDescriptionShouldShow(String description) {
        assertTrue(CucumberScenarioContext.getFeatureBot().waitForFeatureDescription(description),
                "Expected feature description to update to: " + description);
    }

    @Then("the save feature button should be disabled")
    public void theSaveFeatureButtonShouldBeDisabled() {
        assertFalse(CucumberScenarioContext.getFeatureBot().isSaveButtonEnabled(),
                "Expected save feature button to be disabled when name is empty");
    }

    @Then("the feature update should not be processed")
    public void theFeatureUpdateShouldNotBeProcessed() {
        FeatureBot featureBot = CucumberScenarioContext.getFeatureBot();

        assertFalse(featureBot.hasSuccessNotification(),
                "Expected no success notification when feature update is invalid");
        assertTrue(featureBot.getFeatureTitle().equals(originalFeatureName),
                "Expected feature title to remain unchanged when update is invalid");
        assertTrue(featureBot.getFeatureDescription().equals(originalFeatureDescription),
                "Expected feature description to remain unchanged when update is invalid");
    }
}
