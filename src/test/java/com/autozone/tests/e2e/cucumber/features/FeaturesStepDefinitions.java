package com.autozone.tests.e2e.cucumber.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import com.autozone.tests.e2e.bots.FeatureDetailBot;
import com.autozone.tests.e2e.bots.FeaturesBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import org.testng.SkipException;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FeaturesStepDefinitions {

    // ST-FT-01 / ST-FT-06
    @Given("the user opens the features page")
    public void theUserOpensTheFeaturesPage() {
        CucumberScenarioContext.getFeaturesBot().openFeatures();
        CucumberScenarioContext.getFeaturesBot().waitUntilListReady();
    }

    @Then("the features page layout should be correctly displayed")
    public void theFeaturesPageLayoutShouldBeCorrectlyDisplayed() {
        FeaturesBot bot = CucumberScenarioContext.getFeaturesBot();
        assertTrue(bot.isTitleVisible(), "Expected features title to be visible");
        assertEquals(bot.getTitle(), "Features", "Expected page title to be 'Features'");
        assertTrue(bot.isBreadcrumbVisible(), "Expected breadcrumb to be visible");
        assertTrue(bot.isServiceSelectorVisible(), "Expected service selector to be visible");
    }

    @And("features should be listed")
    public void featuresShouldBeListed() {
        FeaturesBot bot = CucumberScenarioContext.getFeaturesBot();
        assertTrue(bot.isListVisible(), "Expected features list to be visible");
        assertTrue(bot.hasFeatures(), "Expected at least one feature to be listed");
    }

    @And("each feature should display its name, description and view button")
    public void eachFeatureShouldDisplayItsInfo() {
        FeaturesBot bot = CucumberScenarioContext.getFeaturesBot();
        List<String> ids = bot.getFeatureIds();
        assertFalse(ids.isEmpty(), "Expected at least one feature");

        String firstId = ids.get(0);
        assertFalse(bot.getFeatureName(firstId).isEmpty(),
            "Expected feature name to be visible");
        assertFalse(bot.getFeatureDescription(firstId).isEmpty(),
            "Expected feature description to be visible");
        assertTrue(bot.isViewButtonVisible(firstId),
            "Expected View button to be visible");
    }

    @And("each feature should display its feature number")
    public void eachFeatureShouldDisplayItsNumber() {
        FeaturesBot bot = CucumberScenarioContext.getFeaturesBot();
        List<String> ids = bot.getFeatureIds();
        assertFalse(ids.isEmpty(), "Expected at least one feature");
        assertTrue(bot.hasFeatureNumber(ids.get(0)),
            "Expected feature number (e.g. F1) to be visible in the first feature");
    }

    @And("a new feature button should be visible")
    public void aNewFeatureButtonShouldBeVisible() {
        assertTrue(CucumberScenarioContext.getFeaturesBot().isNewFeatureButtonVisible(),
            "Expected + New Feature button to be visible");
    }

    // ST-FT-02
    @When("the user selects the service {string} in the selector")
    public void theUserSelectsTheService(String serviceName) {
        CucumberScenarioContext.getFeaturesBot().selectService(serviceName);
    }

    @When("the user selects the empty service in the selector")
    public void theUserSelectsTheEmptyService() {
        boolean found = CucumberScenarioContext.getFeaturesBot().selectFirstEmptyService();
        if (!found) {
            throw new SkipException("No service with empty features found in the DB — skipping ST-FT-02");
        }
    }
    
    @Then("the features empty state should be displayed")
    public void theFeaturesEmptyStateShouldBeDisplayed() {
        FeaturesBot bot = CucumberScenarioContext.getFeaturesBot();

        assertTrue(bot.isEmptyStateVisible(),
            "Expected empty state message when service has no features");
    }

    // ST-FT-03
    @When("the user clicks View on the first feature")
    public void theUserClicksViewOnFirstFeature() {
        FeaturesBot bot = CucumberScenarioContext.getFeaturesBot();
        List<String> ids = bot.getFeatureIds();
        assertFalse(ids.isEmpty(), "Expected at least one feature to click View on");
        bot.clickView(ids.get(0));
    }

    @Then("the feature detail page layout should be correctly displayed")
    public void theFeatureDetailPageLayoutShouldBeCorrectlyDisplayed() {
        FeatureDetailBot bot = CucumberScenarioContext.getFeatureDetailBot();
        bot.waitUntilPageReady();

        assertTrue(bot.isPageVisible(), "Expected feature detail page to be visible");
        assertTrue(bot.isTitleVisible(), "Expected feature title to be visible");
        assertFalse(bot.isTitleFallback(),
            "Expected real feature name, not fallback 'Feature Detail'");
        assertTrue(bot.isBreadcrumbVisible(), "Expected breadcrumb to be visible");
        assertTrue(bot.isDescriptionVisible(), "Expected description to be visible");
        assertFalse(bot.getDescription().isEmpty(), "Expected description to not be empty");
        assertTrue(bot.getCurrentUrl().matches(".*/features/\\d+.*"),
            "Expected URL to match /features/{id}");
    }

    @And("the feature should have linked test cases visible")
    public void theFeatureShouldHaveLinkedTestCasesVisible() {
        FeatureDetailBot bot = CucumberScenarioContext.getFeatureDetailBot();
        bot.openAccordion();
        bot.waitUntilTestCasesVisible();

        assertTrue(bot.hasTestCases(),
            "Expected at least one linked test case to be visible");

        List<String> tcIds = bot.getTestCaseIds();
        String firstTcId = tcIds.get(0);
        assertFalse(bot.getTestCaseIdText(firstTcId).isEmpty(),
            "Expected test case ID to be visible");
        assertFalse(bot.getTestCaseNameText(firstTcId).isEmpty(),
            "Expected test case name to be visible");
    }

    @And("the test cases count should match the rendered items")
    public void theTestCasesCountShouldMatchRenderedItems() {
        FeatureDetailBot bot = CucumberScenarioContext.getFeatureDetailBot();
        int countFromHeader = bot.getTestCasesCount();
        int actualCount = bot.getRenderedTestCasesCount();
        assertEquals(actualCount, countFromHeader,
            "Expected test cases count in header (" + countFromHeader +
            ") to match actual items rendered (" + actualCount + ")");
    }

    // ST-FT-03 / ST-FT-04
    @And("the edit and delete buttons should be visible")
    public void theEditAndDeleteButtonsShouldBeVisible() {
        FeatureDetailBot bot = CucumberScenarioContext.getFeatureDetailBot();
        assertTrue(bot.isEditButtonVisible(), "Expected Edit button to be visible");
        assertTrue(bot.isDeleteButtonVisible(), "Expected Delete button to be visible");
    }

    // ST-FT-04
    @When("the user finds and opens a feature with no test cases")
    public void theUserFindsAndOpensFeatureWithNoTestCases() {
        FeaturesBot featuresBot = CucumberScenarioContext.getFeaturesBot();
        FeatureDetailBot detailBot = CucumberScenarioContext.getFeatureDetailBot();

        List<String> ids = featuresBot.getFeatureIds();
        for (String id : ids) {
            featuresBot.openFeatureById(id);
            detailBot.waitUntilPageReady();
            if (detailBot.hasTestCasesEmptyStateQuick()) return;
            featuresBot.openFeatures();
            featuresBot.waitUntilListReady();
        }
        throw new SkipException("No feature with empty test cases found — skipping ST-FT-04");
    }

    @Then("the feature test cases empty state should be displayed")
    public void theFeatureTestCasesEmptyStateShouldBeDisplayed() {
        FeatureDetailBot bot = CucumberScenarioContext.getFeatureDetailBot();
        bot.waitUntilPageReady();
        assertTrue(bot.isTestCasesEmptyStateVisible(),
            "Expected empty state when feature has no linked test cases");
    }
}
