package com.autozone.tests.e2e.cucumber;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.autozone.tests.e2e.support.DriverFactory;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import java.time.Duration;
import java.util.Properties;

public class CucumberHooks {
    @Before
    public void beforeScenario() {
    WebDriver driver = DriverFactory.createChromeDriver();
    CucumberScenarioContext.setDriver(driver);

    Properties props = new Properties();
    try {
        props.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
    } catch (Exception e) {
        throw new RuntimeException("No se pudo cargar config.properties", e);
    }

    driver.get("http://localhost:5173/login");
    driver.findElement(By.cssSelector("[data-testid='login-email-input']")).sendKeys(props.getProperty("test.username"));
    driver.findElement(By.cssSelector("[data-testid='login-password-input']")).sendKeys(props.getProperty("test.password"));
    driver.findElement(By.cssSelector("[data-testid='login-submit-button']")).click();

    new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
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
