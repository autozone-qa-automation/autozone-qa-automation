package com.autozone.tests.e2e.bots;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.autozone.tests.e2e.support.WaitSupport;

public class ReleaseCreateBot extends BaseBot {

    private static final By OPEN_BUTTON = By.cssSelector("[data-testid='release-create-open-btn']");
    private static final By FORM = By.cssSelector("[data-testid='release-create-form']");
    private static final By NAME_INPUT = By.cssSelector("[data-testid='release-name-input']");
    private static final By VERSION_INPUT = By.cssSelector("[data-testid='release-version-input']");
    private static final By SERVICE_SELECT = By.cssSelector("[data-testid='release-service-select']");
    private static final By FEATURES_SELECT = By.cssSelector("[data-testid='release-features-select']");
    private static final By TAGS_INPUT = By.cssSelector("[data-testid='release-tags-input']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='release-create-submit-btn']");
    private static final By OBJECTIVE_TEXTAREA = By.cssSelector("textarea");

    private static final String SUCCESS_MESSAGE = "Release created successfully";
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), '" + SUCCESS_MESSAGE + "')]");

    private static final String VALIDATION_ERROR_NAME = "Minimum 3 characters required";
    private static final String VALIDATION_ERROR_VERSION = "Required format: X.X.X (e.g., 1.0.0)";
    private static final String VALIDATION_ERROR_SERVICE = "Select at least one service";
    private static final String VALIDATION_ERROR_FEATURES = "Select at least one feature";
    private static final String VALIDATION_ERROR_TAGS = "Add at least one tag";

    private static final By STATUS_DRAFT = By.xpath("//label[span[text()='Draft']]");
    private static final By STATUS_PROGRESS = By.xpath("//label[span[text()='Progress']]");
    private static final By STATUS_ACTIVE = By.xpath("//label[span[text()='Active']]");
    private static final By STATUS_RADIO_DRAFT = By.xpath("//input[@type='radio' and @value='Draft']");
    private static final By STATUS_RADIO_PROGRESS = By.xpath("//input[@type='radio' and @value='Progress']");
    private static final By STATUS_RADIO_ACTIVE = By.xpath("//input[@type='radio' and @value='Active']");

    public ReleaseCreateBot(WebDriver driver) {
        super(driver);
    }

    public void openCreateModal() {
        waitForPresence(OPEN_BUTTON).click();
        waitForPresence(FORM);
    }

    public boolean isModalVisible() {
        return findElements(FORM).size() > 0;
    }

    public boolean isFormVisible() {
        return findElements(NAME_INPUT).size() > 0;
    }

    public void enterReleaseName(String name) {
        WebElement root = waitForPresence(NAME_INPUT);
        root.clear();
        root.sendKeys(name);
    }

    public void clearReleaseName() {
        WebElement root = waitForPresence(NAME_INPUT);
        root.clear();
    }

    public void enterReleaseVersion(String version) {
        WebElement root = waitForPresence(VERSION_INPUT);
        root.clear();
        root.sendKeys(version);
    }

    public void clearReleaseVersion() {
        WebElement root = waitForPresence(VERSION_INPUT);
        root.clear();
    }

    public void enterObjective(String objective) {
        WebElement textarea = waitForPresence(OBJECTIVE_TEXTAREA);
        textarea.clear();
        textarea.sendKeys(objective);
    }

    public void selectService(String serviceName) {
        selectBySearch(SERVICE_SELECT, serviceName);
        wait.until(d -> serviceName.equals(waitForPresence(SERVICE_SELECT).getAttribute("value")));
    }

    public void selectFeature(String featureName) {
        selectBySearch(FEATURES_SELECT, featureName);
        wait.until(d -> waitForPresence(FEATURES_SELECT).getAttribute("value") != null);
    }

    private WebElement resolveInput(By containerLocator) {
        WebElement el = waitForPresence(containerLocator);
        if ("input".equals(el.getTagName())) {
            return el;
        }
        List<WebElement> inputs = el.findElements(By.tagName("input"));
        return inputs.isEmpty() ? el : inputs.get(0);
    }

    private void selectFromDropdown(By containerLocator, int optionIndex) {
        WebElement input = resolveInput(containerLocator);
        input.click();

        Actions actions = new Actions(driver);
        actions.pause(Duration.ofMillis(500)).perform();

        for (int i = 0; i < optionIndex; i++) {
            actions.sendKeys(Keys.ARROW_DOWN).perform();
            actions.pause(Duration.ofMillis(150)).perform();
        }
        actions.sendKeys(Keys.ENTER).perform();
    }

    private void selectBySearch(By containerLocator, String text) {
        WebElement input = resolveInput(containerLocator);
        input.click();
        input.clear();
        input.sendKeys(text);

        Actions actions = new Actions(driver);
        actions.pause(Duration.ofMillis(500)).perform();
        actions.sendKeys(Keys.ARROW_DOWN).perform();
        actions.pause(Duration.ofMillis(150)).perform();
        actions.sendKeys(Keys.ENTER).perform();
    }

    public void addTag(String tag) {
        WebElement root = waitForPresence(TAGS_INPUT);
        root.sendKeys(tag);
        root.sendKeys(Keys.ENTER);
    }

    public void clickStatusDraft() {
        waitForPresence(STATUS_DRAFT).click();
    }

    public void clickStatusProgress() {
        waitForPresence(STATUS_PROGRESS).click();
    }

    public void clickStatusActive() {
        waitForPresence(STATUS_ACTIVE).click();
    }

    public boolean isStatusDraftSelected() {
        return waitForPresence(STATUS_RADIO_DRAFT).isSelected();
    }

    public boolean isStatusProgressSelected() {
        return waitForPresence(STATUS_RADIO_PROGRESS).isSelected();
    }

    public boolean isStatusActiveSelected() {
        return waitForPresence(STATUS_RADIO_ACTIVE).isSelected();
    }

    public void clickSubmit() {
        waitForPresence(SUBMIT_BUTTON).click();
    }

    public boolean isFeaturesDisabled() {
        WebElement input = resolveInput(FEATURES_SELECT);
        return input.getAttribute("disabled") != null;
    }

    public String getFeaturesPlaceholder() {
        WebElement input = resolveInput(FEATURES_SELECT);
        String placeholder = input.getAttribute("placeholder");
        return placeholder != null ? placeholder : "";
    }

    public boolean hasNameValidationError() {
        return pageTextContains(VALIDATION_ERROR_NAME);
    }

    public boolean hasVersionValidationError() {
        return pageTextContains(VALIDATION_ERROR_VERSION);
    }

    public boolean hasServiceValidationError() {
        return pageTextContains(VALIDATION_ERROR_SERVICE);
    }

    public boolean hasFeaturesValidationError() {
        return pageTextContains(VALIDATION_ERROR_FEATURES);
    }

    public boolean hasTagsValidationError() {
        return pageTextContains(VALIDATION_ERROR_TAGS);
    }

    public boolean hasAnyValidationError() {
        return hasNameValidationError()
                || hasVersionValidationError()
                || hasServiceValidationError()
                || hasFeaturesValidationError()
                || hasTagsValidationError();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, SUCCESS_MESSAGE);
    }

    public boolean waitForModalToClose() {
        return WaitSupport.waitForAbsence(wait, FORM);
    }

    public void selectFirstService() {
        selectServiceByIndex(1);
    }

    public void selectDifferentService() {
        selectServiceByIndex(2);
    }

    private void selectServiceByIndex(int index) {
        selectFromDropdown(SERVICE_SELECT, index);
        wait.until(d -> {
            String val = waitForPresence(SERVICE_SELECT).getAttribute("value");
            return val != null && !val.isEmpty();
        });
    }

    public String getSelectedServiceName() {
        WebElement root = waitForPresence(SERVICE_SELECT);
        String val = root.getAttribute("value");
        return val != null ? val : "";
    }

    public void selectFirstFeature() {
        WebElement input = resolveInput(FEATURES_SELECT);
        input.click();

        wait.until(d -> !driver.findElements(By.xpath("//div[@role='option']")).isEmpty());

        Actions actions = new Actions(driver);
        actions.pause(Duration.ofMillis(300)).perform();
        actions.sendKeys(Keys.ARROW_DOWN).perform();
        actions.pause(Duration.ofMillis(200)).perform();
        actions.sendKeys(Keys.ENTER).perform();
        actions.pause(Duration.ofMillis(200)).perform();
        actions.sendKeys(Keys.ESCAPE).perform();
        actions.pause(Duration.ofMillis(300)).perform();
    }

    public boolean isFeatureSelected(String featureName) {
        return pageTextContains(featureName);
    }

    public void deselectAllFeatures() {
        WebElement root = waitForPresence(FEATURES_SELECT);
        List<WebElement> removeButtons = root.findElements(
                By.cssSelector("button[tabindex='-1']"));
        for (WebElement btn : removeButtons) {
            btn.click();
        }
    }

    public boolean hasAnyFeatureSelected() {
        WebElement root = waitForPresence(FEATURES_SELECT);
        List<WebElement> removeButtons = root.findElements(
                By.cssSelector("button[tabindex='-1']"));
        return !removeButtons.isEmpty();
    }

    public int getServiceOptionCount() {
        WebElement input = resolveInput(SERVICE_SELECT);
        input.click();

        Actions actions = new Actions(driver);
        actions.pause(Duration.ofMillis(500)).perform();
        actions.sendKeys(Keys.ARROW_DOWN).perform();
        actions.pause(Duration.ofMillis(300)).perform();

        int count = driver.findElements(By.xpath("//div[@role='option']")).size();
        actions.sendKeys(Keys.ESCAPE).perform();
        return count;
    }

    private boolean pageTextContains(String text) {
        return driver.findElement(By.tagName("body")).getText().contains(text);
    }
}
