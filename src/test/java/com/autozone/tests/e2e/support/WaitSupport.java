package com.autozone.tests.e2e.support;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitSupport {
    
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    private WaitSupport() {
    }

    public static WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForPresence (WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static List<WebElement> waitForAllPresent(WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public static boolean waitForText(WebDriverWait wait, By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public static boolean waitForUrl(WebDriverWait wait, String url) {
        return wait.until(ExpectedConditions.urlToBe(url));
    }
}
