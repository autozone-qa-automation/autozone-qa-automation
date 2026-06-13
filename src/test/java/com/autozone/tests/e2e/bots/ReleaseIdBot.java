package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ReleaseIdBot extends BaseBot {

    private static final String RELEASES_PATH = "/releases/";
    private static final String SERVICES_PATH = "/services/";

    private static final By RELEASE_ID_PAGE = By.cssSelector("[data-testid='release-id-page']");
    private static final By LOADING_STATE = By.cssSelector("[data-testid='release-id-loading-state']");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-testid='release-id-error-message']");
    private static final By NOT_FOUND_MESSAGE = By.cssSelector("[data-testid='release-id-not-found-message']");

    private static final By ASSOCIATED_SERVICE_LINK = By.cssSelector("[data-testid='release-id-associated-service']");

    public ReleaseIdBot(WebDriver driver) {
        super(driver);
    }

    public void openRelease(String releaseId) {
        openPath(RELEASES_PATH + releaseId);
    }

    public boolean isPageVisible() {
        return findElements(RELEASE_ID_PAGE).size() > 0;
    }

    public boolean isLoadingVisible() {
        return findElements(LOADING_STATE).size() > 0;
    }

    public boolean isErrorVisible() {
        return waitForPresence(ERROR_MESSAGE).isDisplayed();
    }

    public boolean isNotFoundVisible() {
        return findElements(NOT_FOUND_MESSAGE).size() > 0;
    }

    public boolean isAssociatedServiceVisible() {
        return findElements(ASSOCIATED_SERVICE_LINK).size() > 0;
    }

    public void clickAssociatedService() {
        WebElement link = waitForPresence(ASSOCIATED_SERVICE_LINK);
        link.click();

        wait.until(driver -> 
                driver.getCurrentUrl().contains(SERVICES_PATH)
        );
    }
}