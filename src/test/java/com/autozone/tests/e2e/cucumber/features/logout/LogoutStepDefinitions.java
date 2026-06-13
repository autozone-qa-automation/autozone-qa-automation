package com.autozone.tests.e2e.cucumber.features.logout;

import com.autozone.tests.e2e.bots.LogoutBot;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LogoutStepDefinitions {

    private LogoutBot logoutBot;

    // IMPORTANTE: Deja aquí tu constructor original donde inicializas el logoutBot
    // Ejemplo:
    // public LogoutStepDefinitions(TestContext context) {
    // this.logoutBot = new LogoutBot(context.getDriver());
    // }

    @When("^the user logs out from the application$")
    public void theUserLogsOutFromTheApplication() {
        logoutBot.logout();
    }

    @Then("^the user session must be closed completely$")
    public void theUserSessionMustBeClosedCompletely() {
        Assert.assertTrue(logoutBot.hasNoAuthToken(), "El token de autenticación no se eliminó.");
    }

    @And("^the user is redirected to the login screen$")
    public void theUserIsRedirectedToTheLoginScreen() {
        Assert.assertTrue(logoutBot.isOnLoginPage(), "No se redirigió correctamente al Login.");
    }

    @And("^the user cannot access protected routes without a new session$")
    public void theUserCannotAccessProtectedRoutesWithoutANewSession() {
        logoutBot.openProtectedPage();
        Assert.assertTrue(logoutBot.isOnLoginPage(), "Se permitió el acceso a ruta protegida sin sesión.");
    }
}