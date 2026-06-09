package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FeatureBot extends BaseBot {

    private static final String FEATURE_PATH = "/features/";
    private static final By FEATURE_DETAIL_CONTAINER = By.cssSelector("[data-testid='feature-detail-container']");
    private static final By FEATURE_TITLE = By.cssSelector("[data-testid='feature-detail-title']");
    private static final By FEATURE_DESCRIPTION_TEXT = By.cssSelector("[data-testid='feature-description-text']");
    private static final By EDIT_BUTTON = By.cssSelector("[data-testid='feature-detail-edit-button']");
    private static final By EDIT_MODAL = By.cssSelector("[data-testid='feature-edit-modal']");
    private static final By NAME_INPUT = By.cssSelector("[data-testid='feature-edit-name-input']");
    private static final By DESCRIPTION_INPUT = By.cssSelector("[data-testid='feature-edit-description-input']");
    private static final By SAVE_BUTTON = By.cssSelector("[data-testid='feature-edit-save-button']");
    private static final By SUCCESS_NOTIFICATION = By.xpath("//*[contains(normalize-space(.), 'Feature updated successfully')]");

    public FeatureBot(WebDriver driver) {
        super(driver);
    }

    public void openFeature(int featureId) {
        openPath(FEATURE_PATH + featureId);
    }

    public void waitUntilPageReady() {
        waitForPresence(FEATURE_DETAIL_CONTAINER);
    }

    public boolean isPageVisible() {
        return findElements(FEATURE_DETAIL_CONTAINER).size() > 0;
    }

    public void openEditModal() {
        waitForPresence(EDIT_BUTTON).click();
        waitForPresence(EDIT_MODAL);
    }

    public boolean isEditModalVisible() {
        return findElements(EDIT_MODAL).size() > 0;
    }

    public void setFeatureName(String name) {
        WebElement input = waitForPresence(NAME_INPUT);
        input.clear();
        input.sendKeys(name);
    }

    public void setFeatureDescription(String description) {
        WebElement textarea = waitForPresence(DESCRIPTION_INPUT);
        textarea.clear();
        textarea.sendKeys(description);
    }

    public void clearFeatureName() {
        WebElement input = waitForPresence(NAME_INPUT);
        input.clear();
    }

    public void clickSaveFeature() {
        waitForPresence(SAVE_BUTTON).click();
    }

    public boolean isSaveButtonEnabled() {
        return waitForPresence(SAVE_BUTTON).isEnabled();
    }

    public boolean hasSuccessNotification() {
        return findElements(SUCCESS_NOTIFICATION).size() > 0;
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, "Feature updated successfully");
    }

    public boolean waitForFeatureTitle(String title) {
        return waitForText(FEATURE_TITLE, title);
    }

    public boolean waitForFeatureDescription(String description) {
        return waitForText(FEATURE_DESCRIPTION_TEXT, description);
    }

    public String getFeatureTitle() {
        return waitForPresence(FEATURE_TITLE).getText();
    }

    public String getFeatureDescription() {
        return waitForPresence(FEATURE_DESCRIPTION_TEXT).getText();
    }
}
