package com.autozone.tests.e2e.cucumber.users;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.HomeBot;
import com.autozone.tests.e2e.bots.UsersBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UsersStepDefinitions {

    @When("the user opens the users list page")
    public void theUserOpensTheUsersListPage() {
        HomeBot home = CucumberScenarioContext.getHomeBot();
        UsersBot users = CucumberScenarioContext.getUsersBot();

        // Navegación directa a /users — evita fragilidad del popover
        home.navigateToUserManagement();
        users.waitUntilUsersListReady();
    }

    @Then("the users page layout should be correctly displayed")
    public void layout() {
        UsersBot users = CucumberScenarioContext.getUsersBot();
        assertTrue(users.isTitleVisible());
        assertTrue(users.isTableVisible());
    }

    @Then("users should be listed")
    public void usersListed() {
        assertTrue(CucumberScenarioContext.getUsersBot().hasUsers());
    }
}