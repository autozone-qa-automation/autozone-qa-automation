package com.autozone.tests.e2e.cucumber.users;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.UserCreateBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserCreateStepDefinitions {

    private String createdUserName;

    @Given("the user opens the create user form")
    public void theUserOpensTheCreateUserForm() {
        UserCreateBot userCreateBot = CucumberScenarioContext.getUserCreateBot();
        userCreateBot.openCreateForm();
        assertTrue(userCreateBot.isModalVisible(),
                "Expected the user creation form to be visible");
    }

    @When("the user fills the create user form with name {string}, last name {string}, email {string}, password {string}, and role {string}")
    public void theUserFillsTheCreateUserForm(String name, String lastName, String email, String password, String role) {
        createdUserName = name + " " + lastName;
        CucumberScenarioContext.getUserCreateBot().fillForm(name, lastName, email, password, role);
    }

    @When("the user fills the create user form with valid data")
    public void theUserFillsTheCreateUserFormWithValidData() {
        CucumberScenarioContext.getUserCreateBot().fillWithValidData();
    }

    @When("the user sets the email to {string}")
    public void theUserSetsTheEmailTo(String email) {
        CucumberScenarioContext.getUserCreateBot().setEmail(email);
    }

    @When("the user submits the create user form")
    public void theUserSubmitsTheCreateUserForm() {
        CucumberScenarioContext.getUserCreateBot().submit();
    }

    @Then("the user should see a creation success notification")
    public void theUserShouldSeeACreationSuccessNotification() {
        assertTrue(CucumberScenarioContext.getUserCreateBot().waitForSuccessNotification(),
                "Expected a success notification after creating the user");
    }

    @Then("the modal should close")
    public void theModalShouldClose() {
        assertTrue(CucumberScenarioContext.getUserCreateBot().waitForModalToClose(),
                "Expected the modal to close after successful creation");
    }

    @Then("the new user should be reflected in the users list")
    public void theNewUserShouldBeReflectedInTheUsersList() {
        assertTrue(CucumberScenarioContext.getUserCreateBot().isNewUserInList(createdUserName),
                "Expected the new user to appear in the users list");
    }

    @Then("the user should see validation errors for empty required fields")
    public void theUserShouldSeeValidationErrorsForEmptyRequiredFields() {
        assertTrue(CucumberScenarioContext.getUserCreateBot().hasValidationErrors(),
                "Expected validation error messages for empty required fields");
    }

    @Then("the modal should remain open")
    public void theModalShouldRemainOpen() {
        assertTrue(CucumberScenarioContext.getUserCreateBot().isModalVisible(),
                "Expected the modal to remain open when validation fails");
    }

    @Then("the user should see an invalid email format error message")
    public void theUserShouldSeeAnInvalidEmailFormatErrorMessage() {
        assertTrue(CucumberScenarioContext.getUserCreateBot().hasInvalidEmailFormatError(),
                "Expected an invalid email format error message");
    }
}
