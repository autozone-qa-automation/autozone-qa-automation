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

    public TestCasesBot(WebDriver driver) {
        super(driver);
    }

    public void openList() {
        openPath(LIST_PATH);
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

    public boolean hasTestCaseNamed(String name) {
        return findElements(TESTCASE_CARDS)
                .stream()
                .anyMatch(card -> card.getText().contains(name));
    }
}