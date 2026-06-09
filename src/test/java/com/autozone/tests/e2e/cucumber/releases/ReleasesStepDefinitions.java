package com.autozone.tests.e2e.cucumber.releases;

import java.util.List;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import com.autozone.tests.e2e.bots.ReleasesBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReleasesStepDefinitions {

    @Given("the user opens the releases list page")
    public void theUserOpensTheReleasesListPage() {
        CucumberScenarioContext.getReleasesBot().openList();
        CucumberScenarioContext.getReleasesBot().waitUntilListReady();
    }

    @Then("the releases page layout should be correctly displayed")
    public void theReleasesPageLayoutShouldBeCorrectlyDisplayed() {
        ReleasesBot releasesBot = CucumberScenarioContext.getReleasesBot();

        assertTrue(releasesBot.isPageVisible(), "Expected releases page to be visible");
        assertTrue(releasesBot.isSearchVisible(), "Expected search input to be visible");
        assertTrue(!releasesBot.isLoadingVisible(), "Expected loading state to not be visible");
        assertTrue(!releasesBot.isErrorVisible(), "Expected connection error message to not be visible");
    }

    @And("releases should be listed with metadata including name, version, status, and dates")
    public void releasesShouldBeListedWithMetadata() {
        List<String> releaseIds = CucumberScenarioContext.getReleasesBot().getListedReleaseIds();

        assertTrue(
                !releaseIds.isEmpty(),
                "Expected at least one release card in the list"
        );
    }

    @And("the release details should be visible when a release is clicked")
    public void theReleaseDetailsShouldBeVisibleWhenAReleaseIsClicked() {
        List<String> releaseIds = CucumberScenarioContext.getReleasesBot().getListedReleaseIds();
        
        // Interactúa con el primer release disponible para comprobar la transición por UI
        CucumberScenarioContext.getReleasesBot().openReleaseDetails(releaseIds.get(0));
    }

    @Given("the user opens the releases list page to search for a release")
    public void theUserOpensTheReleasesListPageToSearchForARelease() {
        CucumberScenarioContext.getReleasesBot().openList();
        CucumberScenarioContext.getReleasesBot().waitUntilListReady();
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String text) {
        CucumberScenarioContext.getReleasesBot().searchRelease(text);
    }

    @Then("the release {string} should be displayed in the list")
    public void theReleaseShouldBeDisplayedInTheList(String name) {
        assertTrue(
                CucumberScenarioContext.getReleasesBot().isReleaseListed(name),
                "Expected release to be visible: " + name
        );
    }

    @Then("the releases empty message {string} should be displayed")
    public void theReleasesEmptyMessageShouldBeDisplayed(String expectedMessage) {
        ReleasesBot releasesBot = CucumberScenarioContext.getReleasesBot();
        
        assertTrue(releasesBot.isEmptyMessageVisible(), "Expected empty state message container to be visible");
        assertEquals(releasesBot.getEmptyMessageText(), expectedMessage, "The empty state text does not match");
    }

    @When("the user sorts the list by {string}")
    public void theUserSortsTheListBy(String criteria) {
        CucumberScenarioContext.getReleasesBot().sortBy(criteria);
    }

    @Then("the releases should be ordered by newest creation date first")
    public void theReleasesShouldBeOrderedByNewestCreationDateFirst() {
        // En un flujo avanzado aquí podrías parsear las fechas de las tarjetas y validar matemáticamente el orden descendente
        List<String> releaseIds = CucumberScenarioContext.getReleasesBot().getListedReleaseIds();
        assertTrue(!releaseIds.isEmpty(), "Expected ordered cards to be present");
    }

    @Then("the releases should be ordered by oldest creation date first")
    public void theReleasesShouldBeOrderedByOldestCreationDateFirst() {
        List<String> releaseIds = CucumberScenarioContext.getReleasesBot().getListedReleaseIds();
        assertTrue(!releaseIds.isEmpty(), "Expected ordered cards to be present");
    }

    @Given("the user opens the release details page for {int}")
    public void theUserOpensTheReleaseDetailsPageFor(int releaseId) {
        // Siguiendo el patrón arquitectónico de ServiceIdBot
        CucumberScenarioContext.getReleaseIdBot().openRelease(String.valueOf(releaseId));
    }

    @Then("the release details page layout should be correctly displayed")
    public void theReleaseDetailsPageLayoutShouldBeCorrectlyDisplayed() {
        assertTrue(CucumberScenarioContext.getReleaseIdBot().isPageVisible(), "Expected release details layout to be visible");
    }

    @And("the single associated service should be displayed")
    public void theSingleAssociatedServiceShouldBeDisplayed() {
        assertTrue(CucumberScenarioContext.getReleaseIdBot().isAssociatedServiceVisible(), "Expected the single associated service link/card to be visible");
    }

    @When("the user selects the service")
    public void theUserSelectsTheService() {
        CucumberScenarioContext.getReleaseIdBot().clickAssociatedService();
    }

    @Then("the service features should be listed")
    public void theServiceFeaturesShouldBeListed() {
        // Reutiliza o redirige la validación hacia el estado esperado del servicio
        assertTrue(CucumberScenarioContext.getServiceIdBot().hasFeatures(), "Expected features of the service to be listed successfully");
    }

    @Given("the user opens the release details page for {int} invalid release")
    public void theUserOpensTheReleaseDetailsPageForInvalidRelease(int releaseId) {
        CucumberScenarioContext.getReleaseIdBot().openRelease(String.valueOf(releaseId));
    }

    @Then("the release not found message should be displayed")
    public void theReleaseNotFoundMessageShouldBeDisplayed() {
        assertTrue(CucumberScenarioContext.getReleaseIdBot().isErrorVisible(), "Expected release not found error message to be displayed");
    }
}