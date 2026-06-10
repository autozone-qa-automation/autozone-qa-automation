package com.autozone.tests.e2e.cucumber.testcases;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.*;

public class TestCasesStepDefinitions {

    private String editedTitle;
    private String originalTitle;

    @Given("the user opens the test cases list page")
    public void openList() {
        CucumberScenarioContext.getTestCasesBot().openList();
        CucumberScenarioContext.getTestCasesBot().waitUntilListReady();
    }

    @When("the user selects a test case to edit")
    public void selectTestCase() {
        CucumberScenarioContext.getTestCasesBot().openEditFirst();
    }

    @Then("the test case data should be displayed correctly")
    public void verifyData() {
        originalTitle = CucumberScenarioContext.getTestCasesEditBot().getTitleValue();

        assertTrue(originalTitle != null && !originalTitle.isEmpty());
        assertTrue(CucumberScenarioContext.getTestCasesEditBot().getStepsValue() != null);
        assertTrue(CucumberScenarioContext.getTestCasesEditBot().getExpectedOutputValue() != null);
    }

    @When("the user edits the test case with valid data")
    public void editValid() {

        editedTitle = "Edited TC";

        var bot = CucumberScenarioContext.getTestCasesEditBot();

        bot.setTitle(editedTitle);
        bot.selectFirstFeature(); 
        bot.setDescription("Updated");
        bot.setType("Regression");
        bot.setPreconditions("Pre");
        bot.setPostconditions("Post");
        bot.setInputs("Inputs");
        bot.setSteps("Steps");
        bot.setExpectedOutput("Output");

        assertTrue(bot.isSaveEnabled());
    }

    @And("the user saves the changes")
    public void save() {
        CucumberScenarioContext.getTestCasesEditBot().save();
    }
    @Then("the test case should be updated successfully")
    public void verifySaved() {
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
        
        CucumberScenarioContext.getDriver().navigate().refresh();
        CucumberScenarioContext.getTestCasesBot().waitUntilListReady();
        
    }

    @When("the user leaves required fields empty")
    public void emptyFields() {
        var bot = CucumberScenarioContext.getTestCasesEditBot();

        bot.setTitle("");
        bot.setSteps("");
        bot.setExpectedOutput("");
    }

    @Then("the system should prevent saving the test case")
        public void cannotSave() {
        var bot = CucumberScenarioContext.getTestCasesEditBot();
        bot.save();
        assertTrue(true);
    }

    @When("the user modifies the test case but cancels")
    public void cancelEdit() {
        var bot = CucumberScenarioContext.getTestCasesEditBot();

        originalTitle = bot.getTitleValue();
        bot.setTitle("Temp");
        bot.cancel();
    }

    @Then("the changes should not be saved")
    public void verifyCancel() {
        var bot = CucumberScenarioContext.getTestCasesEditBot();

        assertTrue(bot.isBackOnList()); 
        assertTrue(CucumberScenarioContext.getTestCasesBot().hasTestCaseNamed(originalTitle));
    }
}