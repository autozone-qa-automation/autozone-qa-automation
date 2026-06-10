package com.autozone.tests.e2e.cucumber.features;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.FeatureBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FeatureDeleteStepDefinitions {

    @When("the user opens the delete feature modal")
    public void theUserOpensTheDeleteFeatureModal() {
        FeatureBot featureBot = CucumberScenarioContext.getFeatureBot();
        featureBot.openDeleteModal();
        assertTrue(featureBot.isDeleteModalVisible(), "Expected delete feature modal to be visible");
    }
    @And("the user confirms the feature deletion")
    public void theUserConfirmsTheFeatureDeletion() {
        CucumberScenarioContext.getFeatureBot().confirmDelete();
    }
    @And("the user clicks Cancel")
    public void theUserClicksCancel() {
        CucumberScenarioContext.getFeatureBot().cancelDelete();
    }
    @Then("the user is redirected to the features page")
    public void theUserIsRedirectedToTheFeaturesPage() {
        FeatureBot featureBot = CucumberScenarioContext.getFeatureBot();
        assertTrue(featureBot.isPageVisible(), "Expected feature details page to be visible");
    }
}
