package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FeatureDetailBot extends BaseBot {

    private static final By PAGE = By.cssSelector("[data-testid='feature-detail-container']");
    private static final By TITLE = By.cssSelector("[data-testid='feature-detail-title']");
    private static final By BREADCRUMB = By.cssSelector("[data-testid='title-header-breadcrumbs']");
    private static final By DESCRIPTION = By.cssSelector("[data-testid='feature-description-text']");
    private static final By TC_COUNT = By.cssSelector("[data-testid='feature-detail-testcases-count']");
    private static final By TC_LIST = By.cssSelector("[data-testid='feature-detail-testcases-list']");
    private static final By TC_EMPTY_STATE = By.cssSelector("[data-testid='feature-detail-testcases-empty-state']");
    private static final By TC_ITEMS = By.cssSelector("[data-testid^='linked-tc-item-']");
    private static final By ACCORDION_CONTROL = By.cssSelector("button[data-accordion-control]");

    public FeatureDetailBot(WebDriver driver) {
        super(driver);
    }

    public void waitUntilPageReady() {
        waitForPresence(PAGE);
    }

    public boolean isPageVisible() {
        return findElements(PAGE).size() > 0;
    }

    public String getTitle() {
        return waitForPresence(TITLE).getText();
    }

    public boolean isTitleVisible() {
        return waitForPresence(TITLE).isDisplayed();
    }

    public boolean isBreadcrumbVisible() {
        return waitForPresence(BREADCRUMB).isDisplayed();
    }

    public String getDescription() {
        return waitForPresence(DESCRIPTION).getText();
    }

    public boolean isDescriptionVisible() {
        return waitForPresence(DESCRIPTION).isDisplayed();
    }

    public void openAccordion() {
        waitForPresence(ACCORDION_CONTROL).click();
    }

    public boolean isTestCasesListVisible() {
        return findElements(TC_LIST).size() > 0;
    }

    public boolean isTestCasesEmptyStateVisible() {
        try {
            wait.until(driver -> !driver.findElements(TC_EMPTY_STATE).isEmpty());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasTestCases() {
        return findElements(TC_ITEMS).size() > 0;
    }

    public int getTestCasesCount() {
        String text = waitForPresence(TC_COUNT).getText().replaceAll("[^0-9]", "");
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    public int getRenderedTestCasesCount() {
        return findElements(TC_ITEMS).size();
    }

    public void waitUntilTestCasesVisible() {
        wait.until(driver -> !driver.findElements(TC_ITEMS).isEmpty());
    }

    public List<String> getTestCaseIds() {
        return findElements(TC_ITEMS).stream()
            .map(e -> e.getAttribute("data-testid"))
            .filter(id -> id != null && id.startsWith("linked-tc-item-"))
            .map(id -> id.replace("linked-tc-item-", ""))
            .collect(Collectors.toList());
    }

    public String getTestCaseIdText(String tcId) {
        return waitForPresence(
            By.cssSelector("[data-testid='linked-tc-id-" + tcId + "']")
        ).getText();
    }

    public String getTestCaseNameText(String tcId) {
        return waitForPresence(
            By.cssSelector("[data-testid='linked-tc-name-" + tcId + "']")
        ).getText();
    }
    public boolean isTitleFallback() {
        try {
            wait.until(d -> {
                String text = d.findElement(TITLE).getText();
                return !text.isEmpty() && !text.equals("Feature Detail");
            });
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public String getCurrentUrl() {
        return currentUrl();
    }
}