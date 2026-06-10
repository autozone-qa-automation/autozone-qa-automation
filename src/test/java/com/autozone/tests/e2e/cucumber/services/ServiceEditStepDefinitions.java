package com.autozone.tests.e2e.cucumber.services;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.ServiceEditBot;
import com.autozone.tests.e2e.bots.ServiceIdBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ServiceEditStepDefinitions {

    private String originalName;
    private String originalDescription;

    @Given("the user navigates to service details page for {int}")
    public void theUserNavigatesToServiceDetailsPage(int serviceId) {
        CucumberScenarioContext.getServiceIdBot().openService(String.valueOf(serviceId));
        CucumberScenarioContext.getServiceIdBot().waitUntilFeaturesReady();
    }

    @When("the user opens the service edit modal")
    public void theUserOpensTheServiceEditModal() {
        ServiceEditBot bot = CucumberScenarioContext.getServiceEditBot();
        originalName = CucumberScenarioContext.getServiceEditBot() != null
                ? "" : "";

        bot.openEditModal();

        assertTrue(bot.isModalVisible(), "Expected the service edit modal to be visible");

        originalName = bot.getNameValue();
        originalDescription = bot.getDescriptionValue();
    }

    @And("the user updates the service name to {string}")
    public void theUserUpdatesTheServiceNameTo(String name) {
        CucumberScenarioContext.getServiceEditBot().setName(name);
    }

    @And("the user updates the service description to {string}")
    public void theUserUpdatesTheServiceDescriptionTo(String description) {
        CucumberScenarioContext.getServiceEditBot().setDescription(description);
    }

    @And("the user clears the service name field")
    public void theUserClearsTheServiceNameField() {
        CucumberScenarioContext.getServiceEditBot().clearName();
    }

    @When("the user saves the service changes")
    public void theUserSavesTheServiceChanges() {
        CucumberScenarioContext.getServiceEditBot().clickSave();
    }

    @When("the user cancels the service edit")
    public void theUserCancelsTheServiceEdit() {
        CucumberScenarioContext.getServiceEditBot().clickCancel();
    }

    @Then("the service edit success message should be displayed")
    public void theServiceEditSuccessMessageShouldBeDisplayed() {
        assertTrue(
                CucumberScenarioContext.getServiceEditBot().waitForSuccessNotification(),
                "Expected a success notification after saving the service"
        );
    }

    @Then("the service edit modal should be closed")
    public void theServiceEditModalShouldBeClosed() {
        assertTrue(
                CucumberScenarioContext.getServiceEditBot().waitForModalToClose(),
                "Expected the service edit modal to be closed"
        );
    }

    @Then("the service edit should not be processed")
    public void theServiceEditShouldNotBeProcessed() {
        ServiceEditBot bot = CucumberScenarioContext.getServiceEditBot();

        assertFalse(
                bot.hasSuccessNotification(),
                "Expected no success notification when service update is invalid"
        );
        assertTrue(
                bot.isModalVisible(),
                "Expected the edit modal to remain open when validation fails"
        );
        assertTrue(
                bot.hasNameRequiredError(),
                "Expected a validation error for the name field"
        );
    }

    @And("the user restores the original service name and description")
    public void theUserRestoresTheOriginalServiceNameAndDescription() {
        ServiceEditBot bot = CucumberScenarioContext.getServiceEditBot();

        bot.openEditModal();
        assertTrue(bot.isModalVisible(), "Expected the service edit modal to be visible");

        bot.setName(originalName);
        bot.setDescription(originalDescription);
        bot.clickSave();

        assertTrue(
                bot.waitForSuccessNotification(),
                "Expected a success notification after restoring the service"
        );
    }

    @Then("the service edit modal should remain open")
    public void theServiceEditModalShouldRemainOpen() {
        assertTrue(
                CucumberScenarioContext.getServiceEditBot().isModalVisible(),
                "Expected the service edit modal to still be visible"
        );
    }

    @And("the user updates the service url nombre {int} to {string}")
    public void theUserUpdatesTheServiceUrlNombreTo(int index, String nombre) {
        CucumberScenarioContext.getServiceEditBot().setUrlNombre(index, nombre);
    }

    @And("the user updates the service url {int} to {string}")
    public void theUserUpdatesTheServiceUrlTo(int index, String url) {
        CucumberScenarioContext.getServiceEditBot().setUrlValue(index, url);
    }
}
