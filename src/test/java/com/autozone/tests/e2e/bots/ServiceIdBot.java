package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ServiceIdBot extends BaseBot {

    private static final String SERVICES_PATH = "/services/";
    private static final String FEATURES_PATH = "/features/";
    private static final String FEATURE_OPEN_BUTTON_PREFIX = "service-id-feature-open-button-";
    private static final String FEATURE_DELETE_BUTTON_PREFIX = "service-id-feature-delete-button-";

    private static final By SERVICE_ID_PAGE = By.cssSelector("[data-testid='service-id-page']");
    private static final By LOADING_STATE = By.cssSelector("[data-testid='service-id-loading-state']");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-testid='service-id-error-message']");
    private static final By NOT_FOUND_MESSAGE = By.cssSelector("[data-testid='service-id-not-found-message']");

    private static final By EDIT_BUTTON = By.cssSelector("[data-testid='service-id-edit-button']");
    private static final By DELETE_BUTTON = By.cssSelector("[data-testid='service-id-delete-button']");
    private static final By DELETE_CONFIRM_BUTTON = By.cssSelector("[data-testid='delete-service-confirm-btn']");
    private static final By DELETE_CANCEL_BUTTON =By.cssSelector("[data-testid='delete-service-cancel-btn']");
    private static final By ADD_FEATURE_BUTTON = By.cssSelector("[data-testid='service-id-add-feature-button']");

    private static final By FEATURE_OPEN_BUTTONS = By.cssSelector("[data-testid^='service-id-feature-open-button-']");
    private static final By FEATURES_EMPTY_MESSAGE = By.cssSelector("[data-testid='service-id-features-empty-message']");

    private static final By LAST_RELEASES_EMPTY_MESSAGE = By.cssSelector("[data-testid='service-id-last-releases-section']");

    public ServiceIdBot(WebDriver driver) {
        super(driver);
    }

    public void openService(String serviceId) {
        openPath(SERVICES_PATH + serviceId);
    }

    public boolean isPageVisible() {
        return findElements(SERVICE_ID_PAGE).size() > 0;
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

    public boolean isEditButtonVisible() {
        return findElements(EDIT_BUTTON).size() > 0;
    }

    public boolean isDeleteButtonVisible() {
        return findElements(DELETE_BUTTON).size() > 0;
    }

    public void confirmDeleteService() {
        waitForPresence(DELETE_BUTTON).click();
        waitForPresence(DELETE_CONFIRM_BUTTON).click();
    }

    public void cancelDeleteService() {
        waitForPresence(DELETE_BUTTON).click();
        waitForPresence(DELETE_CANCEL_BUTTON).click();
    }

    public boolean isStillOnServicePage() {
    return wait.until(driver ->
            driver.getCurrentUrl().contains("/services/")
    );
}

    public boolean isAddFeatureButtonVisible() {
        return findElements(ADD_FEATURE_BUTTON).size() > 0;
    }

    public void waitUntilFeaturesReady() {
        wait.until(driver ->
                driver.findElements(FEATURE_OPEN_BUTTONS).size() > 0 
        );
    }

    // returns list of feature ids as integers
    public List<Integer> getFeatureIds() {
        return findElements(FEATURE_OPEN_BUTTONS)
                .stream()
                .map(e -> e.getAttribute("data-testid"))
                .filter(id -> id != null && id.startsWith(FEATURE_OPEN_BUTTON_PREFIX))
                .map(id -> id.replace(FEATURE_OPEN_BUTTON_PREFIX, ""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public void openFeature(int featureId) {
        WebElement btn = waitForPresence(
                By.cssSelector("[data-testid='" + FEATURE_OPEN_BUTTON_PREFIX + featureId + "']")
        );

        btn.click();

        wait.until(driver ->
                driver.getCurrentUrl().endsWith(FEATURES_PATH + featureId)
        );

    }

    public void deleteFeature(int featureId) {
        WebElement btn = waitForPresence(
                By.cssSelector("[data-testid='" + FEATURE_DELETE_BUTTON_PREFIX + featureId + "']")
        );

        btn.click();
    }

    public boolean hasFeatures() {
        return findElements(FEATURE_OPEN_BUTTONS).size() > 0;
    }

    public boolean isFeaturesEmptyMessageVisible() {
        return findElements(FEATURES_EMPTY_MESSAGE).size() > 0;
    }

    public boolean isLastReleasesSectionVisible() {
        return waitForPresence(LAST_RELEASES_EMPTY_MESSAGE).isDisplayed();
    }

    public boolean waitUntilServicesListPage() {
        return wait.until(driver ->
                driver.getCurrentUrl().endsWith("/services")
        );
    }
}