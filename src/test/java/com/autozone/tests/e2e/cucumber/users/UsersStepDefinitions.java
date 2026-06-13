package com.autozone.tests.e2e.cucumber.users;

import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.UsersBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UsersStepDefinitions {

    @When("the user navigates to the users management section")
    public void theUserNavigatesToUsersManagement() {
        UsersBot users = CucumberScenarioContext.getUsersBot();
        users.openList();
        users.waitUntilListReady();
    }

    @Then("the users table should be visible")
    public void theUsersTableShouldBeVisible() {
        assertTrue(CucumberScenarioContext.getUsersBot().isTableVisible());
    }

    @Then("the users list should contain at least one user")
    public void theUsersListShouldContainAtLeastOneUser() {
        assertTrue(CucumberScenarioContext.getUsersBot().hasUsers());
    }

    @When("the user filters the users list by role {string}")
    public void theUserFiltersByRole(String roleLabel) {
        CucumberScenarioContext.getUsersBot().filterByRole(roleLabel);
    }

    @Then("the users list should be filtered by role {string}")
    public void theUsersListShouldBeFilteredByRole(String roleLabel) {
        UsersBot users = CucumberScenarioContext.getUsersBot();
        String selected = users.getSelectedRoleFilter();
        assertTrue(
            selected.equalsIgnoreCase(roleLabel) || selected.equalsIgnoreCase("All Users"),
            "Expected filter to show '" + roleLabel + "' but got: " + selected
        );
        assertTrue(users.hasUsers(),
            "Expected users to be listed after filtering by role: " + roleLabel);
    }
}