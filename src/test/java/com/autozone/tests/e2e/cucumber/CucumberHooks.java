package com.autozone.tests.e2e.cucumber;

import org.openqa.selenium.WebDriver;

import com.autozone.tests.e2e.support.DriverFactory;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class CucumberHooks {
    @Before
    public void beforeScenario() {
        WebDriver driver = DriverFactory.createChromeDriver();
        CucumberScenarioContext.setDriver(driver);
        
    }

    @After
    public void afterScenario() {
        WebDriver driver = null;
        try {
            driver = CucumberScenarioContext.getDriver();
        } catch (IllegalStateException ignored) {
            // No driver was set, so we can ignore this exception
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
