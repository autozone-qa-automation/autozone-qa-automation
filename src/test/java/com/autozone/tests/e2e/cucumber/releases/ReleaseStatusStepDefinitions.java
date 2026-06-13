package com.autozone.tests.e2e.cucumber.releases;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.ReleaseStatusBot;
import com.autozone.tests.e2e.bots.ReleasesBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReleaseStatusStepDefinitions {

    private String releaseTitle;

    @Given("the user opens a draft release")
    public void theUserOpensADraftRelease() {
        ReleasesBot releasesBot = CucumberScenarioContext.getReleasesBot();
        releasesBot.openList();
        releasesBot.waitUntilListReady();
        releasesBot.filterByDraft();
        releaseTitle = releasesBot.openFirstRelease();
    }

    @When("the user clicks the update release button")
    public void theUserClicksTheUpdateReleaseButton() {
        CucumberScenarioContext.getReleaseStatusBot().clickUpdateButton();
    }

    @And("the user changes the release status to {string}")
    public void theUserChangesTheReleaseStatusTo(String status) {
        CucumberScenarioContext.getReleaseStatusBot().selectStatus(status);
    }

    @And("the user submits the status update")
    public void theUserSubmitsTheStatusUpdate() {
        ReleaseStatusBot bot = CucumberScenarioContext.getReleaseStatusBot();
        bot.clickModalUpdate();
        bot.confirmUpdate();
    }

    @Then("the release status update success message should be displayed")
    public void theReleaseStatusUpdateSuccessMessageShouldBeDisplayed() {
        assertTrue(
                CucumberScenarioContext.getReleaseStatusBot().waitForSuccessNotification(),
                "Expected a success notification after updating the release status"
        );
    }

    @And("the user navigates back to the releases page")
    public void theUserNavigatesBackToTheReleasesPage() {
        ReleasesBot releasesBot = CucumberScenarioContext.getReleasesBot();
        releasesBot.openList();
        releasesBot.waitUntilListReady();
    }

    @And("the user filters releases by {string}")
    public void theUserFiltersReleasesBy(String status) {
        CucumberScenarioContext.getReleasesBot().filterByStatus(status);
    }

    @Then("the updated release should be visible in the list")
    public void theUpdatedReleaseShouldBeVisibleInTheList() {
        assertTrue(
                CucumberScenarioContext.getReleasesBot().isReleaseListed(releaseTitle),
                "Expected the updated release to be visible in the list: " + releaseTitle
        );
    }
}
