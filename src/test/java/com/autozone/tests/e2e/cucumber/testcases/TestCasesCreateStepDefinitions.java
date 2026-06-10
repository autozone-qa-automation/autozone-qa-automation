package com.autozone.tests.e2e.cucumber.testcases;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TestCasesCreateStepDefinitions {

    private String createdTitle;
    private String invalidTitle;

    @When("the user opens the create test case modal")
    public void openCreateTestCaseModal() {
        CucumberScenarioContext.getTestCasesBot().openCreateModal();
        assertTrue(CucumberScenarioContext.getTestCasesCreateBot().isModalVisible());
    }

    @And("the user fills the create test case form with valid data")
    public void fillCreateFormWithValidData() {
        createdTitle = "E2E Created TC " + System.currentTimeMillis();
        CucumberScenarioContext.getTestCasesCreateBot().fillValidForm(createdTitle);
    }

    @And("the user submits the create test case form")
    public void submitCreateForm() {
        CucumberScenarioContext.getTestCasesCreateBot().create();
    }

    @Then("the test case should be created successfully")
    public void verifyTestCaseCreatedSuccessfully() {
        assertTrue(CucumberScenarioContext.getTestCasesCreateBot().isSuccessVisible());

        CucumberScenarioContext.getDriver().navigate().refresh();
        CucumberScenarioContext.getTestCasesBot().waitUntilListReady();

        assertTrue(CucumberScenarioContext.getTestCasesBot().hasTestCaseNamed(createdTitle));
    }

    @And("the user leaves required create test case fields empty")
    public void leaveRequiredCreateFieldsEmpty() {
        invalidTitle = "E2E Invalid TC " + System.currentTimeMillis();
        CucumberScenarioContext.getTestCasesCreateBot().fillOnlyTitle(invalidTitle);
    }

    @Then("the system should prevent creating the test case")
    public void verifyCreateValidation() {
        assertTrue(CucumberScenarioContext.getTestCasesCreateBot().isValidationMessageVisible());
        assertTrue(CucumberScenarioContext.getTestCasesCreateBot().isModalVisible());

        CucumberScenarioContext.getTestCasesCreateBot().close();
        CucumberScenarioContext.getDriver().navigate().refresh();
        CucumberScenarioContext.getTestCasesBot().waitUntilListReady();

        assertFalse(CucumberScenarioContext.getTestCasesBot().hasTestCaseNamed(invalidTitle));
    }
}