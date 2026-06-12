package com.autozone.tests.e2e.cucumber.login;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;
import com.autozone.tests.e2e.support.E2eTestCredentials;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginStepDefinitions {

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        CucumberScenarioContext.getLoginBot().openLoginForm();
    }

    @When("the user enters a valid username")
    public void theUserEntersAValidUsername() {
        CucumberScenarioContext.getLoginBot().enterUsername(E2eTestCredentials.validUsername());
    }

    @And("the user enters a valid password")
    public void theUserEntersAValidPassword() {
        CucumberScenarioContext.getLoginBot().enterPassword(E2eTestCredentials.validPassword());
    }

    @And("the user enters an invalid password")
    public void theUserEntersAnInvalidPassword() {
        CucumberScenarioContext.getLoginBot().enterPassword(E2eTestCredentials.invalidPassword());
    }

    @When("the user enters a non-existent username")
    public void theUserEntersANonExistentUsername() {
        CucumberScenarioContext.getLoginBot().enterUsername(E2eTestCredentials.unregisteredUsername());
    }

    @When("the user enters an invalid email format")
    public void theUserEntersAnInvalidEmailFormat() {
        CucumberScenarioContext.getLoginBot().enterUsername(E2eTestCredentials.invalidEmailFormat());
    }

    @When("the user enters an inactive account username")
    public void theUserEntersAnInactiveAccountUsername() {
        CucumberScenarioContext.getLoginBot().enterUsername(E2eTestCredentials.inactiveUsername());
    }

    @And("the user enters the inactive account password")
    public void theUserEntersTheInactiveAccountPassword() {
        CucumberScenarioContext.getLoginBot().enterPassword(E2eTestCredentials.inactivePassword());
    }

    @And("the user enters any password")
    public void theUserEntersAnyPassword() {
        CucumberScenarioContext.getLoginBot().enterPassword(E2eTestCredentials.anyPassword());
    }

    @When("the user leaves the username field empty")
    public void theUserLeavesTheUsernameFieldEmpty() {
        CucumberScenarioContext.getLoginBot().clearUsername();
    }

    @And("the user leaves the password field empty")
    public void theUserLeavesThePasswordFieldEmpty() {
        CucumberScenarioContext.getLoginBot().clearPassword();
    }

    @And("the user clicks the {string} button")
    public void theUserClicksTheButton(String buttonName) {
        if ("Log Out".equalsIgnoreCase(buttonName)) {
            CucumberScenarioContext.getLoginBot().logout();
            return;
        }

        CucumberScenarioContext.getLoginBot().submit();
    }

    @Then("an active session is created for the user")
    public void anActiveSessionIsCreatedForTheUser() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().waitUntilLoggedIn(),
                "Expected an auth token and a non-login URL after successful login"
        );
    }

    @And("the system redirects the user to the dashboard or main page")
    public void theSystemRedirectsTheUserToTheDashboardOrMainPage() {
        assertFalse(
                CucumberScenarioContext.getLoginBot().isOnLoginPage(),
                "Expected the user to leave the login page after successful login"
        );
    }

    @And("no error messages are displayed")
    public void noErrorMessagesAreDisplayed() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasNoErrorMessages(),
                "Expected no login errors or validation messages"
        );
    }

    @Then("no session is created")
    public void noSessionIsCreated() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasNoAuthToken(),
                "Expected no auth token to be stored"
        );
    }

    @And("the user remains on the login page")
    public void theUserRemainsOnTheLoginPage() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().isOnLoginPage(),
                "Expected the user to remain on the login page"
        );
    }

    @And("the system displays {string}")
    public void theSystemDisplays(String message) {
        assertTrue(
                CucumberScenarioContext.getLoginBot().isLoginErrorVisible(message),
                "Expected login error message: " + message
        );
    }

    @And("the system displays an invalid credentials message")
    public void theSystemDisplaysAnInvalidCredentialsMessage() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasInvalidCredentialsMessage(),
                "Expected an invalid credentials message"
        );
    }

    @And("the system displays an inactive account message")
    public void theSystemDisplaysAnInactiveAccountMessage() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasInactiveAccountMessage(),
                "Expected an inactive account message"
        );
    }

    @Then("the login request is not submitted to the server")
    public void theLoginRequestIsNotSubmittedToTheServer() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasNoAuthToken(),
                "Expected no session when validation blocks login"
        );
    }

    @And("the system displays {string} validation messages")
    public void theSystemDisplaysValidationMessages(String message) {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasRequiredFieldValidationMessages(),
                "Expected required field validation messages"
        );
    }

    @Then("the login process is not completed")
    public void theLoginProcessIsNotCompleted() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasNoAuthToken(),
                "Expected the login process to remain incomplete"
        );
    }

    @And("the system displays a message indicating the username is required")
    public void theSystemDisplaysAMessageIndicatingTheUsernameIsRequired() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasUsernameRequiredValidationMessage(),
                "Expected username validation to be visible"
        );
    }

    @And("the system displays a message indicating the password is required")
    public void theSystemDisplaysAMessageIndicatingThePasswordIsRequired() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasPasswordRequiredValidationMessage(),
                "Expected password validation to be visible"
        );
    }

    @Given("the user logs in successfully")
    public void theUserLogsInSuccessfully() {
        CucumberScenarioContext.getLoginBot().openLoginForm();
        CucumberScenarioContext.getLoginBot().loginAs(
                E2eTestCredentials.validUsername(),
                E2eTestCredentials.validPassword()
        );
        CucumberScenarioContext.getLoginBot().waitUntilLoggedIn();
    }

    @When("the authenticated user opens the login page")
    public void theAuthenticatedUserOpensTheLoginPage() {
        CucumberScenarioContext.getLoginBot().openLogin();
        CucumberScenarioContext.getLoginBot().waitUntilLoggedIn();
    }

    @Given("the user has no active session")
    public void theUserHasNoActiveSession() {
        CucumberScenarioContext.getLoginBot().openLoginForm();
        CucumberScenarioContext.getLoginBot().clearSession();
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasNoAuthToken(),
                "Expected no active session before opening a protected route"
        );
    }

    @When("the user opens a protected page")
    public void theUserOpensAProtectedPage() {
        CucumberScenarioContext.getLoginBot().openProtectedPage();
        CucumberScenarioContext.getLoginBot().waitUntilLoginPage();
    }

    @And("the user attempts to return to the previous page using the browser back button")
    public void theUserAttemptsToReturnToThePreviousPageUsingTheBrowserBackButton() {
        CucumberScenarioContext.getLoginBot().navigateBack();
        CucumberScenarioContext.getLoginBot().waitUntilLoginPage();
    }

    @Then("the session is terminated")
    public void theSessionIsTerminated() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().hasNoAuthToken(),
                "Expected auth token to be removed after logout"
        );
    }

    @And("the system redirects the user to the login page")
    public void theSystemRedirectsTheUserToTheLoginPage() {
        assertTrue(
                CucumberScenarioContext.getLoginBot().isOnLoginPage(),
                "Expected the user to be redirected to the login page"
        );
    }

    @And("access to the dashboard is denied until the user logs in again")
    public void accessToTheDashboardIsDeniedUntilTheUserLogsInAgain() {
        CucumberScenarioContext.getLoginBot().openHome();
        CucumberScenarioContext.getLoginBot().waitUntilLoginPage();
        assertTrue(
                CucumberScenarioContext.getLoginBot().isOnLoginPage(),
                "Expected protected pages to redirect to login without a session"
        );
    }
}
