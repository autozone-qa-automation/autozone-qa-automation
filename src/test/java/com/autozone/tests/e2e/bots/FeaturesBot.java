package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FeaturesBot extends BaseBot {

    private static final String FEATURES_PATH = "/features";

    private static final By TITLE = By.cssSelector("[data-testid='features-page-container'] h1");
    private static final By BREADCRUMB = By.cssSelector("[data-testid='title-header-breadcrumbs']");
    private static final By SERVICE_SELECTOR = By.cssSelector("[data-testid='features-service-selector']");
    private static final By FEATURES_LIST = By.cssSelector("[data-testid='features-list']");
    private static final By EMPTY_STATE = By.cssSelector("[data-testid='features-empty-message']");
    private static final By FEATURE_ITEMS = By.cssSelector("[data-testid^='feature-item-']");
    private static final By SERVICE_OPTIONS = By.cssSelector("[data-combobox-option]");

    public FeaturesBot(WebDriver driver) {
        super(driver);
    }

    public void openFeatures() {
        openPath(FEATURES_PATH);
    }

    public void waitUntilListReady() {
        waitForPresence(FEATURES_LIST);
    }

    public boolean isTitleVisible() {
        return waitForPresence(TITLE).isDisplayed();
    }

    public String getTitle() {
        return waitForPresence(TITLE).getText();
    }

    public boolean isBreadcrumbVisible() {
        return waitForPresence(BREADCRUMB).isDisplayed();
    }

    public boolean isServiceSelectorVisible() {
        return findElements(SERVICE_SELECTOR).size() > 0;
    }

    public boolean isListVisible() {
        return findElements(FEATURES_LIST).size() > 0;
    }

    public boolean isEmptyStateVisible() {
        try {
            wait.until(driver -> !driver.findElements(EMPTY_STATE).isEmpty());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasFeatures() {
        return findElements(FEATURE_ITEMS).size() > 0;
    }

    public List<String> getFeatureIds() {
        return findElements(FEATURE_ITEMS).stream()
            .map(e -> e.getAttribute("data-testid"))
            .filter(id -> id != null && id.startsWith("feature-item-"))
            .map(id -> id.replace("feature-item-", ""))
            .collect(Collectors.toList());
    }

    public String getFeatureName(String featureId) {
        return waitForPresence(
            By.cssSelector("[data-testid='feature-name-" + featureId + "']")
        ).getText();
    }

    public String getFeatureDescription(String featureId) {
        return waitForPresence(
            By.cssSelector("[data-testid='feature-description-" + featureId + "']")
        ).getText();
    }

    public boolean isViewButtonVisible(String featureId) {
        return findElements(
            By.cssSelector("[data-testid='feature-view-button-" + featureId + "']")
        ).size() > 0;
    }

    public void clickView(String featureId) {
        waitForPresence(
            By.cssSelector("[data-testid='feature-view-button-" + featureId + "']")
        ).click();

        wait.until(driver ->
            driver.getCurrentUrl().contains(FEATURES_PATH + "/" + featureId)
        );
    }

    public void openServiceSelector() {
        waitForPresence(SERVICE_SELECTOR).click();
    }

    /** Select a service by its exact display name in the dropdown. */
    public void selectService(String serviceName) {
        waitForPresence(SERVICE_SELECTOR).click();

        wait.until(driver -> !driver.findElements(SERVICE_OPTIONS).isEmpty());

        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        findElements(SERVICE_OPTIONS).stream()
            .filter(o -> o.getText().trim().equals(serviceName))
            .findFirst()
            .ifPresent(WebElement::click);

        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Select a service by its value attribute in the dropdown (used when the
     * service name is dynamic and unknown at test-design time).
     */
    public void selectServiceByValue(String serviceId) {
        waitForPresence(SERVICE_SELECTOR).click();

        wait.until(driver -> !driver.findElements(SERVICE_OPTIONS).isEmpty());

        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        findElements(SERVICE_OPTIONS).stream()
            .filter(o -> serviceId.equals(o.getAttribute("data-value"))
                      || serviceId.equals(o.getAttribute("value")))
            .findFirst()
            .ifPresent(WebElement::click);

        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public String getSelectorValue() {
        WebElement selector = waitForPresence(SERVICE_SELECTOR);
        WebElement target = selector.getTagName().equalsIgnoreCase("input")
                ? selector
                : selector.findElements(By.tagName("input")).stream().findFirst().orElse(selector);
        String value = target.getDomProperty("value");
        return value == null ? "" : value;
    }

    public boolean hasErrorText(String text) {
        return findElements(By.xpath("//*[contains(text(), '" + text + "')]")).size() > 0;
    }

    public void openFeatureById(String featureId) {
        openPath("/features/" + featureId);
    }
}
