package com.autozone.tests.e2e.cucumber.testcases;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.TestCaseDeleteBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TestCaseDeleteStepDefinitions {

    private int initialTestCaseCount;

    private TestCaseDeleteBot bot() {
        return CucumberScenarioContext.getTestCaseDeleteBot();
    }

    @Given("the user is on the test cases page")
    public void theUserIsOnTheTestCasesPage() {
        CucumberScenarioContext.getTestCasesBot().openList();
        CucumberScenarioContext.getTestCasesBot().waitUntilListReady();
    }

    @And("there is at least one test case on the board")
    public void thereIsAtLeastOneTestCaseOnTheBoard() {
        initialTestCaseCount = bot().getTestCasesCount();
        assertTrue(initialTestCaseCount > 0, "Expected at least one test case on the board");
    }

    @When("the user clicks the delete button for a specific test case")
    public void theUserClicksTheDeleteButtonForASpecificTestCase() {
        bot().clickDeleteButton();
    }

    @Then("a confirmation message to delete the test case should be displayed")
    public void aConfirmationMessageToDeleteTheTestCaseShouldBeDisplayed() {
        assertTrue(bot().isConfirmModalVisible(), "Expected delete confirmation modal to be visible");
    }

    @When("the user clicks the {string} option on the modal")
    public void theUserClicksTheOptionOnTheModal(String buttonName) {
        switch (buttonName.toLowerCase()) {
            case "confirm delete" -> bot().clickConfirmDelete();
            case "cancel" -> bot().clickCancelDelete();
            default -> throw new IllegalArgumentException("Unknown button: " + buttonName);
        }
    }

    @Then("the test case should be deleted successfully")
    public void theTestCaseShouldBeDeletedSuccessfully() {
        assertTrue(bot().isConfirmModalClosed(), "Expected the confirm modal to close");
    }

    @And("the test case should no longer appear on the board")
    public void theTestCaseShouldNoLongerAppearOnTheBoard() {
        int currentCount = bot().getTestCasesCount();
        assertTrue(currentCount < initialTestCaseCount, "Expected test case count to decrease");
    }

    @And("a deletion confirmation message should be shown")
    public void aDeletionConfirmationMessageShouldBeShown() {
        assertTrue(
                bot().waitForSuccessNotification(),
                "Expected success notification for test case deletion"
        );
    }

    @Then("the deletion operation should be cancelled")
    public void theDeletionOperationShouldBeCancelled() {
        assertTrue(bot().isConfirmModalClosed(), "Expected the confirm modal to close after cancellation");
    }

    @And("the test case should remain registered in the system")
    public void theTestCaseShouldRemainRegisteredInTheSystem() {
        assertTrue(bot().isTestCaseBoardVisible(), "Expected test case board to be visible");
    }

    @And("the test case should continue to be visible on the board")
    public void theTestCaseShouldContinueToBeVisibleOnTheBoard() {
        int currentCount = bot().getTestCasesCount();
        assertTrue(currentCount == initialTestCaseCount, "Expected test case count to remain unchanged");
    }

    @When("the user attempts to delete a test case using an invalid or outdated reference")
    public void theUserAttemptsToDeleteATestCaseUsingAnInvalidOrOutdatedReference() {
        bot().triggerInvalidDelete();
    }

    @Then("a message should indicate that the test case does not exist or was already deleted")
    public void aMessageShouldIndicateThatTheTestCaseDoesNotExistOrWasAlreadyDeleted() {
        assertTrue(
                bot().waitForErrorNotification(),
                "Expected error notification for non-existent test case"
        );
    }

    @And("no records should be deleted")
    public void noRecordsShouldBeDeleted() {
        int currentCount = bot().getTestCasesCount();
        assertTrue(currentCount == initialTestCaseCount, "Expected test case count to remain unchanged");
    }

    @And("the user should remain on the test cases page")
    public void theUserShouldRemainOnTheTestCasesPage() {
        assertTrue(bot().isTestCaseBoardVisible(), "Expected to remain on the test cases page");
    }

    @When("the user successfully deletes an existing test case")
    public void theUserSuccessfullyDeletesAnExistingTestCase() {
        bot().clickDeleteButton();
        bot().clickConfirmDelete();
    }

    @Then("the test cases board should update automatically")
    public void theTestCasesBoardShouldUpdateAutomatically() {
        assertTrue(bot().isTestCaseBoardVisible(), "Expected test case board to update and be visible");
    }

    @And("the test cases list should update correctly without reloading the page")
    public void theTestCasesListShouldUpdateCorrectlyWithoutReloadingThePage() {
        int currentCount = bot().getTestCasesCount();
        assertTrue(currentCount < initialTestCaseCount, "Expected test case list to update instantly");
    }
}