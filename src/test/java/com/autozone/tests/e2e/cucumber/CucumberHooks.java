package com.autozone.tests.e2e.cucumber;

import org.openqa.selenium.WebDriver;
import com.autozone.tests.e2e.support.DriverFactory;
import com.autozone.tests.e2e.support.E2eTestCredentials;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class CucumberHooks {

    @Before
    public void beforeScenario(Scenario scenario) {
        WebDriver driver = DriverFactory.createChromeDriver();
        CucumberScenarioContext.setDriver(driver);

        if (scenario.getSourceTagNames().contains("@noAutoLogin")) {
            return;
        }

        CucumberScenarioContext.getLoginBot().openLogin();
        CucumberScenarioContext.getLoginBot().loginAs(
                E2eTestCredentials.validUsername(),
                E2eTestCredentials.validPassword()
        );
        CucumberScenarioContext.getLoginBot().waitUntilLoggedIn();
    }

    @After
    public void afterScenario() {
        WebDriver driver = null;
        try {
            driver = CucumberScenarioContext.getDriver();
        } catch (IllegalStateException ignored) {
        }
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            CucumberScenarioContext.clear();
        }
    }
}