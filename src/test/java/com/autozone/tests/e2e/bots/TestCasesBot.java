package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestCasesBot extends BaseBot {

    private static final String TEST_CASES_PATH = "/testcases";

    private static final By VIEW_BUTTONS =
            By.cssSelector("[data-testid='view-button']");

    private static final By EMPTY_MESSAGE =
            By.cssSelector("[data-testid='empty-testcases-message']");

    public TestCasesBot(WebDriver driver) {
        super(driver);
    }

    public void openList() {
        openPath(TEST_CASES_PATH);
    }

    public boolean hasViewButtons() {
        return findElements(VIEW_BUTTONS).size() > 0;
    }

    public boolean isEmptyMessageVisible() {
        return findElements(EMPTY_MESSAGE).size() > 0;
    }

    public void clickFirstViewButton() {

        WebElement button =
                waitForPresence(VIEW_BUTTONS);

        button.click();
    }

    public int getViewButtonsCount() {
        List<WebElement> buttons =
                findElements(VIEW_BUTTONS);

        return buttons.size();
    }
}