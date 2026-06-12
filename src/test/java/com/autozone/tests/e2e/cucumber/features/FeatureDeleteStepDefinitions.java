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
        org.openqa.selenium.WebDriver driver = CucumberScenarioContext.getDriver();
         boolean redirected = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(15))
                 .until(d -> {
                     String url = d.getCurrentUrl();
                     return url.endsWith("/features") || url.endsWith("/features/");
                 });
         assertTrue(redirected, "Expected to be redirected to the features list page");
    }
    @Then("the modal is closed and the user is kept on the feature details page")
    public void theModalIsClosedAndTheUserIsKeptOnTheFeatureDetailsPage() {
        FeatureBot featureBot = CucumberScenarioContext.getFeatureBot();
        assertTrue(featureBot.isPageVisible(), "Expected to still be on the feature details page");
    }

}
