package com.autozone.tests.e2e.cucumber.services;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.ServiceIdBot;
import com.autozone.tests.e2e.bots.ServicesBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ServicesStepDefinitions {

    @Given("the user opens the services list page")
    public void theUserOpensTheServicesListPage() {
        CucumberScenarioContext.getServicesBot().openList();
        //CucumberScenarioContext.getServicesBot().waitUntilListReady();
        //porque no funciona console log

    }

    @Then("the services page layout should be correctly displayed")
    public void theServicesPageLayoutShouldBeCorrectlyDisplayed() {

        ServicesBot servicesBot = CucumberScenarioContext.getServicesBot();

        assertTrue(servicesBot.isListTitleVisible(), "Expected services title to be visible");
        assertTrue(servicesBot.isListMetaVisible(), "Expected services meta information to be visible");
        assertTrue(servicesBot.isAddServiceButtonVisible(), "Expected add service button to be visible");
        assertTrue(servicesBot.isAddServiceCardVisible(), "Expected add service card to be visible");
        assertTrue(servicesBot.isSearchVisible(), "Expected search input to be visible");
    }
    
    @And("services should be listed")
    public void servicesShouldBeListed() {
        List<String> serviceIds =
                CucumberScenarioContext.getServicesBot().getListedServiceIds();

        assertTrue(
                !serviceIds.isEmpty(),
                "Expected at least one service card in list"
        );
    }
    
    @And("the service details should be visible when a service is clicked")
    public void theServiceDetailsShouldBeVisibleWhenAServiceIsClicked() {
        List<String> serviceIds =
                CucumberScenarioContext.getServicesBot().getListedServiceIds();

        CucumberScenarioContext.getServicesBot().openServiceDetails(serviceIds.get(0));
        
    }

    @Given("the user opens the services list page to search for a service")
    public void theUserOpensTheServicesListPageToSearch() {
        CucumberScenarioContext.getServicesBot().openList();
        CucumberScenarioContext.getServicesBot().waitUntilListReady();
    }

    @When("the user searches for {string}")
    public void theUserSearchesFor(String text) {
        CucumberScenarioContext.getServicesBot().searchService(text);
    }

    @Then("the services displayed should contain {string}")
    public void theServicesDisplayedShouldContain(String query) {
        assertTrue(
                CucumberScenarioContext.getServicesBot().displayedServicesMatchQuery(query),
                "Expected every displayed service name to contain: " + query
        );
    }

    @Then("the service {string} should be displayed in the list")
    public void theServiceShouldBeDisplayed(String name) {
        assertTrue(
                CucumberScenarioContext.getServicesBot().isServiceListed(name),
                "Expected service to be visible: " + name
        );
    }

    @Given("the user opens the service details page for {int}")
    public void theUserOpensTheServiceDetailsPage(int serviceId) {
        CucumberScenarioContext.getServiceIdBot().openService(String.valueOf(serviceId));
        CucumberScenarioContext.getServiceIdBot().waitUntilFeaturesReady();
    }

    @Then("the service details page layout should be correctly displayed")
    public void theServiceDetailsPageLayoutShouldBeCorrectlyDisplayed() {
        ServiceIdBot serviceIdBot = CucumberScenarioContext.getServiceIdBot();

        assertTrue(serviceIdBot.isPageVisible(), "Expected service details page to be visible");
        assertTrue(!serviceIdBot.isNotFoundVisible(), "Expected not found message to not be visible");
        assertTrue(serviceIdBot.isEditButtonVisible(), "Expected edit button to be visible");
        assertTrue(serviceIdBot.isDeleteButtonVisible(), "Expected delete button to be visible");
        assertTrue(serviceIdBot.isAddFeatureButtonVisible(), "Expected add feature button to be visible");
    }

    @And("the service features should be listed")
    public void theServiceFeaturesShouldBeListed() {
        assertTrue(CucumberScenarioContext.getServiceIdBot().hasFeatures(), "Expected features to be visible");
    }

    @And("the last releases section should be displayed")
    public void theLastReleasesSectionShouldBeDisplayed() {
        assertTrue(CucumberScenarioContext.getServiceIdBot().isLastReleasesSectionVisible(), "Expected last releases section to be visible");
    }
    
    @And("the feature details should be visible when a feature is clicked")
    public void theFeatureDetailsShouldBeVisibleWhenAFeatureIsClicked() {
        List<Integer> featureIds = CucumberScenarioContext.getServiceIdBot().getFeatureIds();

        if (CucumberScenarioContext.getServiceIdBot().hasFeatures()) {
                CucumberScenarioContext.getServiceIdBot().openFeature(featureIds.get(0));
        }
    }    

    @Given("the user opens the service details page for {int} invalid service")
    public void theUserOpensTheServiceDetailsPageForInvalidService(int serviceId) {
        CucumberScenarioContext.getServiceIdBot().openService(String.valueOf(serviceId));
    }

    @Then("the service not found message should be displayed")
    public void theServiceNotFoundMessageShouldBeDisplayed() {
        assertTrue(
                CucumberScenarioContext.getServiceIdBot().isNotFoundVisible(),
                "Expected service not found message to be visible"
        );
    }

    @Given("the user opens an existing service")
    public void theUserOpensAnExistingService() {

        ServicesBot servicesBot =
                CucumberScenarioContext.getServicesBot();

        servicesBot.openList();
        servicesBot.waitUntilListReady();

        List<String> serviceIds =
                servicesBot.getListedServiceIds();

        assertTrue(
                !serviceIds.isEmpty(),
                "Expected at least one service"
        );

        servicesBot.openServiceDetails(serviceIds.get(0));
    }

    @When("the user deletes the service")
    public void theUserDeletesTheService() {
        CucumberScenarioContext
                .getServiceIdBot()
                .confirmDeleteService();
    }

    @Then("the user should be redirected to the services list page")
    public void theUserShouldBeRedirectedToTheServicesListPage() {

        assertTrue(
                CucumberScenarioContext
                        .getServiceIdBot()
                        .waitUntilServicesListPage(),
                "Expected to be redirected to services list page"
        );
    }

    @When("the user cancels the service deletion")
    public void theUserCancelsTheServiceDeletion() {
        CucumberScenarioContext
                .getServiceIdBot()
                .cancelDeleteService();
    }

    @Then("the service should not be deleted")
    public void theServiceShouldNotBeDeleted() {
        assertTrue(
                CucumberScenarioContext
                        .getServiceIdBot()
                        .isStillOnServicePage(),
                "Expected to remain on the service details page"
        );
    }

    @And("the user should remain on the service details page")
    public void theUserShouldRemainOnTheServiceDetailsPage() {
        assertTrue(
                CucumberScenarioContext
                        .getServiceIdBot()
                        .isStillOnServicePage(),
                "Expected to remain on the service details page"
        );
    }

    @When("the user opens the create service modal")
    public void theUserOpensTheCreateServiceModal() {
        CucumberScenarioContext.getServicesBot().openCreateServiceModal();
    }

    @Then("the create service form is displayed")
    public void theCreateServiceFormIsDisplayed() {
        assertTrue(
                CucumberScenarioContext.getServicesBot().isCreateServiceFormVisible(),
                "Expected create service form to be visible"
        );
    }
    @And("the user is on the home page")
    public void userIsOnHomePage() {

        WebDriver driver = CucumberScenarioContext.getDriver();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='sidebar']")
        ));
    }

    @And("the user enters service name {string}")
    public void theUserEntersServiceName(String name) {
        CucumberScenarioContext.getServicesBot().enterServiceName(name);
    }

    @And("the user enters service description {string}")
    public void theUserEntersServiceDescription(String description) {
        CucumberScenarioContext.getServicesBot().enterServiceDescription(description);
    }

    @And("the user enters URL name {string}")
    public void theUserEntersUrlName(String urlName) {
        CucumberScenarioContext.getServicesBot().enterUrlName(urlName);
    }

    @And("the user enters URL {string}")
    public void theUserEntersUrl(String url) {
        CucumberScenarioContext.getServicesBot().enterUrl(url);
    }

    @And("the user saves the service")
    public void theUserSavesTheService() {
        CucumberScenarioContext.getServicesBot().submitCreateService();
    }

    @Then("the service system displays {string}")
    public void theServiceSystemDisplays(String message) {
        assertTrue(
            CucumberScenarioContext.getServicesBot().waitForSystemMessage(message),
            "Expected system message: " + message
        );
    }

    @And("the user leaves service description empty")
    public void theUserLeavesServiceDescriptionEmpty() {
        CucumberScenarioContext.getServicesBot().enterServiceDescription("");
    }

    @Then("the service description default message is shown")
    public void validateDefaultDescription() {
        CucumberScenarioContext.getServicesBot()
            .openServiceByName("Payment Gateway");

        String pageText = CucumberScenarioContext.getDriver()
            .findElement(By.tagName("body"))
            .getText();

        assertTrue(pageText.contains("No description provided for Payment Gateway"));
    }

    @When("the user leaves the service name empty")
    public void theUserLeavesTheServiceNameEmpty() {
        CucumberScenarioContext.getServicesBot().enterServiceName("");
    }

    @Then("the service {string} should not be displayed in the list")
    public void theServiceShouldNotBeDisplayedInTheList(String name) {
        boolean exists = CucumberScenarioContext.getServicesBot()
                .isServiceCurrentlyListed(name);

        assertTrue(
            !exists,
            "Expected service NOT to be visible: " + name
        );
    }
    
}
