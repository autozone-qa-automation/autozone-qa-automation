package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestCasesBot extends BaseBot {

    private static final String LIST_PATH = "/test-cases";

    /*
     * Locators nuevos con data-testid.
     * Incluyen fallback a los locators antiguos para no romper pruebas anteriores.
     */
    private static final By TESTCASE_CARDS =
        By.cssSelector("tr[data-testid^='test-case-row-'], tr.mantine-Table-tr:not(:first-child)");

    private static final By TEST_CASES_TABLE =
        By.cssSelector("[data-testid='test-cases-table'], table.mantine-Table-table");

    private static final By TEST_CASE_TITLE_CELLS =
        By.cssSelector("[data-testid='test-case-title-cell']");

    private static final By EDIT_BUTTONS =
        By.cssSelector(
            "[data-testid^='test-case-edit-button-'], " +
            "tr.mantine-Table-tr:not(:first-child) button:last-child"
        );

    private static final By NEW_TEST_CASE_BUTTON =
        By.xpath(
            "//button[@data-testid='test-cases-new-button' " +
            "or normalize-space()='New Test Case' " +
            "or .//*[normalize-space()='New Test Case']]"
        );

    private static final By CREATE_FORM_OR_MODAL =
        By.xpath(
            "//*[@data-testid='test-case-create-form'] " +
            "| //*[normalize-space()='Create Test Case'] " +
            "| //*[normalize-space()='Crear Test Case']"
        );

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
    }

    public boolean isListTitleVisible() {
        return !findElements(TESTCASE_CARDS).isEmpty()
            || !findElements(TEST_CASES_TABLE).isEmpty();
    }

    public void waitUntilListReady() {
        wait.until(driver ->
            !driver.findElements(TEST_CASES_TABLE).isEmpty()
                || !driver.findElements(TESTCASE_CARDS).isEmpty()
                || driver.getPageSource().contains("No test cases available")
                || driver.getPageSource().contains("No test cases")
        );
    }

    public void openCreateModal() {
        WebElement button = waitForPresence(NEW_TEST_CASE_BUTTON);

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            button
        );

        button.click();

        waitForPresence(CREATE_FORM_OR_MODAL);
    }

    public void openEditFirst() {
        waitUntilListReady();

        List<WebElement> buttons = findElements(EDIT_BUTTONS);

        if (buttons.isEmpty()) {
            throw new IllegalStateException("No se encontró ningún botón de edición en la lista de Test Cases.");
        }

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
        waitUntilListReady();

        List<WebElement> titleCells = findElements(TEST_CASE_TITLE_CELLS);

        if (!titleCells.isEmpty()) {
            return titleCells
                .stream()
                .anyMatch(cell -> cell.getText().trim().equals(name));
        }

        return findElements(TESTCASE_CARDS)
            .stream()
            .anyMatch(card -> card.getText().contains(name));
    }

    public boolean hasTestCaseWithTitle(String title) {
        return hasTestCaseNamed(title);
    }
}