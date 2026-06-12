package com.autozone.tests.e2e.cucumber.releases;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.ReleasesBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReleaseDeleteStepDefinitions {

    private String releaseTitle;

    @Given("the user opens the releases page")
    public void theUserOpensTheReleasesPage() {
        ReleasesBot releasesBot = CucumberScenarioContext.getReleasesBot();
        releasesBot.openList();
        releasesBot.waitUntilListReady();
    }

    @When("the user filters releases by draft status")
    public void theUserFiltersReleasesByDraftStatus() {
        CucumberScenarioContext.getReleasesBot().filterByDraft();
    }

    @When("the user opens the first draft release")
    public void theUserOpensTheFirstDraftRelease() {
        releaseTitle = CucumberScenarioContext.getReleasesBot().openFirstRelease();
    }

    @When("the user clicks the delete release button")
    public void theUserClicksTheDeleteReleaseButton() {
        CucumberScenarioContext.getReleaseDeleteBot().clickDeleteButton();
    }

    @When("the user confirms the release deletion")
    public void theUserConfirmsTheReleaseDeletion() {
        CucumberScenarioContext.getReleaseDeleteBot().confirmDelete();
    }

    @Then("the release deletion success message should be displayed")
    public void theReleaseDeletionSuccessMessageShouldBeDisplayed() {
        assertTrue(CucumberScenarioContext.getReleaseDeleteBot().waitForSuccessNotification(),
                "Expected a success notification after deleting the release");
    }

    @Then("the deleted release should no longer be listed")
    public void theDeletedReleaseShouldNoLongerBeListed() {
        assertTrue(CucumberScenarioContext.getReleasesBot().waitUntilReleaseDisappears(releaseTitle),
                "Expected the release to no longer be listed: " + releaseTitle);
    }
}
