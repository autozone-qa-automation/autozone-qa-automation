package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.autozone.tests.e2e.E2eConfig;
import com.autozone.tests.e2e.support.WaitSupport;

public class BaseBot {
    
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BaseBot(WebDriver driver) {
        this(driver, WaitSupport.createWait(driver));
    }

    protected BaseBot(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    protected void openPath(String path) {
        driver.get(E2eConfig.baseUrl() + path);
    }

    protected WebElement waitForPresence(By locator) {
        return WaitSupport.waitForPresence(wait, locator);
    }

    protected List<WebElement> waitForAllPresent(By locator) {
        return WaitSupport.waitForAllPresent(wait, locator);
    }

    protected boolean waitForText(By locator, String text) {
        return WaitSupport.waitForText(wait, locator, text);
    }

    protected boolean waitForUrl(String url) {
        return WaitSupport.waitForUrl(wait, url);
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    protected String currentUrl() {
        return driver.getCurrentUrl();
    }

}
