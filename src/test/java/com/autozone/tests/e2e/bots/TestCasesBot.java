package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestCasesBot extends BaseBot {

    private static final String LIST_PATH = "/test-cases";

    private static final By TESTCASE_CARDS =
        By.cssSelector("tr.mantine-Table-tr:not(:first-child)");

    private static final By EDIT_BUTTONS =
        By.cssSelector("tr.mantine-Table-tr:not(:first-child) button:last-child");

        // Additional selectors used by older testcases scenarios
        private static final By VIEW_BUTTONS =
            By.cssSelector("[data-testid='view-button']");

        private static final By EMPTY_MESSAGE =
            By.cssSelector("[data-testid='empty-testcases-message']");

    public TestCasesBot(WebDriver driver) {
        super(driver);
    }

    public void openList() {
        openPath(LIST_PATH);
        try {
            wait.until(driver -> driver.getCurrentUrl().contains(LIST_PATH));
        } catch (Exception e) {
            openPath(LIST_PATH);
            wait.until(driver -> driver.getCurrentUrl().contains(LIST_PATH));
        }
    }

    public boolean isListTitleVisible() {
        return findElements(TESTCASE_CARDS).size() > 0;
    }

    public void waitUntilListReady() {
        waitForPresence(TESTCASE_CARDS);
        wait.until(driver -> !driver.findElements(TESTCASE_CARDS).isEmpty());
    }

    public void openEditFirst() {
        List<WebElement> buttons = findElements(EDIT_BUTTONS);
        buttons.get(0).click();
    }

    public boolean hasViewButtons() {
        return findElements(VIEW_BUTTONS).size() > 0;
    }

    public boolean isEmptyMessageVisible() {
        return findElements(EMPTY_MESSAGE).size() > 0;
    }

    public void clickFirstViewButton() {
        WebElement button = waitForPresence(VIEW_BUTTONS);
        button.click();
    }

    public int getViewButtonsCount() {
        List<WebElement> buttons = findElements(VIEW_BUTTONS);
        return buttons.size();
    }

    public boolean hasTestCaseNamed(String name) {
        return findElements(TESTCASE_CARDS)
                .stream()
                .anyMatch(card -> card.getText().contains(name));
    }
}