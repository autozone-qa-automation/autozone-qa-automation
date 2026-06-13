package com.autozone.tests.e2e.cucumber.users;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.UserCreateBot;
import com.autozone.tests.e2e.bots.UserDeleteBot;
import com.autozone.tests.e2e.bots.UsersBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserDeleteStepDefinitions {

    private String createdUserFullName;

    @Given("the user creates a new user named {string} for the delete test")
    public void theUserCreatesANewUserNamedForTheDeleteTest(String fullName) {
        String[] parts = fullName.split(" ", 2);
        String name = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "User";

        String unique = String.valueOf(System.currentTimeMillis());
        String email = "qa.delete." + unique + "@autozone-test.com";
        String password = "Test1234!";

        UsersBot usersBot = CucumberScenarioContext.getUsersBot();
        UserCreateBot userCreateBot = CucumberScenarioContext.getUserCreateBot();

        usersBot.clickNewUser();
        assertTrue(userCreateBot.isModalVisible(), "Expected the user create modal to be visible");

        userCreateBot.fillValidForm(name, lastName, email, password);
        userCreateBot.submit();

        assertTrue(userCreateBot.waitForSuccessNotification(),
                "Expected a success notification after creating the user");

        usersBot.waitUntilListReady();
        assertTrue(usersBot.isUserListed(fullName),
                "Expected the newly created user to be listed: " + fullName);

        createdUserFullName = fullName;
    }

    @When("the user opens the delete modal for {string}")
    public void theUserOpensTheDeleteModalFor(String fullName) {
        UsersBot usersBot = CucumberScenarioContext.getUsersBot();
        UserDeleteBot userDeleteBot = CucumberScenarioContext.getUserDeleteBot();

        usersBot.clickDeleteForUser(fullName);

        assertTrue(userDeleteBot.isModalVisible(), "Expected the delete user modal to be visible");
    }

    @And("the user confirms the user deletion")
    public void theUserConfirmsTheUserDeletion() {
        CucumberScenarioContext.getUserDeleteBot().confirmDelete();
    }

    @And("the user clicks Cancel on the delete modal")
    public void theUserClicksCancelOnTheDeleteModal() {
        CucumberScenarioContext.getUserDeleteBot().cancelDelete();
    }

    @Then("the user deletion success message should be displayed")
    public void theUserDeletionSuccessMessageShouldBeDisplayed() {
        assertTrue(CucumberScenarioContext.getUserDeleteBot().waitForSuccessNotification(),
                "Expected a success notification after deleting the user");
    }

    @And("the deleted user should no longer be listed")
    public void theDeletedUserShouldNoLongerBeListed() {
        UsersBot usersBot = CucumberScenarioContext.getUsersBot();
        assertTrue(usersBot.waitUntilUserDisappears(createdUserFullName),
                "Expected the deleted user to no longer be listed: " + createdUserFullName);
    }

    @Then("the delete modal is closed")
    public void theDeleteModalIsClosed() {
        assertTrue(CucumberScenarioContext.getUserDeleteBot().isModalClosed(),
                "Expected the delete user modal to be closed");
    }

    @And("the user {string} should still be listed")
    public void theUserShouldStillBeListed(String fullName) {
        UsersBot usersBot = CucumberScenarioContext.getUsersBot();
        assertTrue(usersBot.isUserListed(fullName),
                "Expected the user to still be listed: " + fullName);
        assertFalse(CucumberScenarioContext.getUserDeleteBot().isModalVisible(),
                "Expected the delete user modal to no longer be visible");
    }
}
