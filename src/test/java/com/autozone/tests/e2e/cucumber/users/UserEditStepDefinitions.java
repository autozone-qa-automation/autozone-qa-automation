package com.autozone.tests.e2e.cucumber.users;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.UserEditBot;
import com.autozone.tests.e2e.bots.UsersBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserEditStepDefinitions {

    private String originalFirstUserRow;
    private String originalName;
    private String originalLastName;

    @Given("the user opens the users list page")
    public void theUserOpensTheUsersListPage() {
        UsersBot usersBot = CucumberScenarioContext.getUsersBot();
        usersBot.openList();
        usersBot.waitUntilListReady();

        originalFirstUserRow = usersBot.getFirstUserRowText();
    }

    @Given("the user opens the edit modal for the first user")
    public void theUserOpensTheEditModalForTheFirstUser() {
        CucumberScenarioContext.getUsersBot().openEditForFirstUser();

        UserEditBot userEditBot = CucumberScenarioContext.getUserEditBot();
        assertTrue(userEditBot.isModalVisible(),
                "Expected the user edit modal to be visible");

        originalName = userEditBot.getNameValue();
        originalLastName = userEditBot.getLastNameValue();
    }

    @When("the user updates the user name to {string}")
    public void theUserUpdatesTheUserNameTo(String name) {
        CucumberScenarioContext.getUserEditBot().setName(name);
    }

    @When("the user updates the user last name to {string}")
    public void theUserUpdatesTheUserLastNameTo(String lastName) {
        CucumberScenarioContext.getUserEditBot().setLastName(lastName);
    }

    @When("the user clears the user name field")
    public void theUserClearsTheUserNameField() {
        CucumberScenarioContext.getUserEditBot().clearName();
    }

    @When("the user saves the user changes")
    public void theUserSavesTheUserChanges() {
        CucumberScenarioContext.getUserEditBot().clickSave();
    }

    @Then("the user success message should be displayed")
    public void theUserSuccessMessageShouldBeDisplayed() {
        assertTrue(CucumberScenarioContext.getUserEditBot().waitForSuccessNotification(),
                "Expected a success notification after saving the user");
    }

    @Then("the user restores the original name and last name")
    public void theUserRestoresTheOriginalNameAndLastName() {
        UsersBot usersBot = CucumberScenarioContext.getUsersBot();
        UserEditBot userEditBot = CucumberScenarioContext.getUserEditBot();

        usersBot.openEditForFirstUser();
        assertTrue(userEditBot.isModalVisible(), "Expected the user edit modal to be visible");

        userEditBot.setName(originalName);
        userEditBot.setLastName(originalLastName);
        userEditBot.clickSave();

        assertTrue(userEditBot.waitForSuccessNotification(),
                "Expected a success notification after restoring the user");
    }

    @Then("the user update should not be processed")
    public void theUserUpdateShouldNotBeProcessed() {
        UserEditBot userEditBot = CucumberScenarioContext.getUserEditBot();
        UsersBot usersBot = CucumberScenarioContext.getUsersBot();

        assertFalse(userEditBot.hasSuccessNotification(),
                "Expected no success notification when user update is invalid");
        assertTrue(userEditBot.isModalVisible(),
                "Expected the edit modal to remain open when validation fails");
        assertTrue(usersBot.getFirstUserRowText().equals(originalFirstUserRow),
                "Expected the users list to remain unchanged when update is invalid");
    }
}
