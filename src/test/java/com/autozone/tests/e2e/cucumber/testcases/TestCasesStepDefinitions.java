package com.autozone.tests.e2e.cucumber.testcases;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.TestCasesBot;
import com.autozone.tests.e2e.bots.TestCaseModalBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

//import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TestCasesStepDefinitions {

    @Given("the user opens the test cases list page")
    public void theUserOpensTheTestCasesListPage() {

        CucumberScenarioContext
                .getTestCasesBot()
                .openList();
    }

    @Then("test cases should be listed")
    public void testCasesShouldBeListed() {

        TestCasesBot bot =
                CucumberScenarioContext.getTestCasesBot();

        assertTrue(
                bot.hasViewButtons(),
                "Expected test cases to be visible"
        );
    }

    @When("the user clicks the first view button")
    public void theUserClicksTheFirstViewButton() {

        CucumberScenarioContext
                .getTestCasesBot()
                .clickFirstViewButton();
    }

    @Then("the test case modal should be visible")
    public void theTestCaseModalShouldBeVisible() {

        TestCaseModalBot modalBot =
                CucumberScenarioContext.getTestCaseModalBot();

        assertTrue(
                modalBot.isVisible(),
                "Expected testcase modal to be visible"
        );
    }

    @Then("the empty test cases message should be displayed")
    public void theEmptyTestCasesMessageShouldBeDisplayed() {

        TestCasesBot bot =
                CucumberScenarioContext.getTestCasesBot();

        assertTrue(
                bot.isEmptyMessageVisible(),
                "Expected empty message to be visible"
        );

        assertFalse(
                bot.hasViewButtons(),
                "Expected no View buttons"
        );
    }
}