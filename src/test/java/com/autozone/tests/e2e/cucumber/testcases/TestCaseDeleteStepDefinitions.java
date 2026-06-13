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

    private void captureCount() {
        initialTestCaseCount = bot().getTestCasesCount();
    }

    @Given("the user is on the test cases page")
    public void theUserIsOnTheTestCasesPage() {
        CucumberScenarioContext.getTestCasesBot().openList();
        CucumberScenarioContext.getTestCasesBot().waitUntilListReady();
    }

    @And("there is at least one test case on the board")
    public void thereIsAtLeastOneTestCaseOnTheBoard() {
        captureCount();
        assertTrue(initialTestCaseCount > 0, "Expected at least one test case on the board");
    }

    @When("the user clicks delete on a test case")
    public void theUserClicksDeleteOnATestCase() {
        captureCount();
        CucumberScenarioContext.getTestCasesBot().clickFirstViewButton();
        bot().clickDeleteButton();
    }

    @Then("a confirmation message to delete the test case should be displayed")
    public void aConfirmationMessageToDeleteTheTestCaseShouldBeDisplayed() {
        assertTrue(bot().isConfirmModalVisible(), "Expected delete confirmation modal to be visible");
    }

    @When("the user confirms the deletion")
    public void theUserConfirmsTheDeletion() {
        bot().clickConfirmDelete();
    }

    @When("the user cancels the deletion")
    public void theUserCancelsTheDeletion() {
        bot().clickCancelDelete();
    }

    @Then("the test case should be deleted successfully")
    public void theTestCaseShouldBeDeletedSuccessfully() {
        assertTrue(bot().waitForConfirmModalClosed(), "Expected the confirm modal to close");
    }

    @And("the test case should no longer appear on the board")
    public void theTestCaseShouldNoLongerAppearOnTheBoard() {
        int currentCount = bot().getTestCasesCount();
        assertTrue(currentCount < initialTestCaseCount,
                "Expected test case count to decrease. Before: " + initialTestCaseCount + ", After: " + currentCount);
    }

    @And("a success message should be shown")
    public void aSuccessMessageShouldBeShown() {
        assertTrue(
                bot().waitForSuccessNotification(),
                "Expected success notification for test case deletion"
        );
    }

    @Then("the deletion operation should be cancelled")
    public void theDeletionOperationShouldBeCancelled() {
        assertTrue(bot().waitForConfirmModalClosed(),
                "Expected the confirm modal to close after cancellation");
    }

    @And("the test case should continue to be visible on the board")
    public void theTestCaseShouldContinueToBeVisibleOnTheBoard() {
        int currentCount = bot().getTestCasesCount();
        assertTrue(currentCount == initialTestCaseCount,
                "Expected test case count to remain unchanged. Before: " + initialTestCaseCount + ", After: " + currentCount);
    }
}