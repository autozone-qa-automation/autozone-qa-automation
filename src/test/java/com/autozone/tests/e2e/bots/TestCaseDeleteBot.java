package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.autozone.tests.e2e.support.WaitSupport;

public class TestCaseDeleteBot extends BaseBot {

    private static final By DELETE_BUTTON = By.cssSelector("[data-testid='testcase-delete-btn']");
    private static final By CONFIRM_MODAL = By.cssSelector("[data-testid='delete-confirm-modal']");
    private static final By CONFIRM_BUTTON = By.cssSelector("[data-testid='confirm-delete-btn']");
    private static final By CANCEL_BUTTON = By.cssSelector("[data-testid='cancel-delete-btn']");
    private static final By TEST_CASES_TABLE = By.cssSelector("[data-testid='testcases-table']");
    private static final By TEST_CASE_ROW = By.cssSelector("[data-testid^='test-case-row-']");

    private static final String SUCCESS_MESSAGE = "Test case deleted successfully";
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), '" + SUCCESS_MESSAGE + "')]");

    public TestCaseDeleteBot(WebDriver driver) {
        super(driver);
    }

    public void clickDeleteButton() {
        waitForPresence(DELETE_BUTTON).click();
    }

    public boolean isConfirmModalVisible() {
        return findElements(CONFIRM_MODAL).size() > 0;
    }

    public void clickConfirmDelete() {
        waitForPresence(CONFIRM_BUTTON).click();
    }

    public void clickCancelDelete() {
        waitForPresence(CANCEL_BUTTON).click();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, SUCCESS_MESSAGE);
    }

    public int getTestCasesCount() {
        return findElements(TEST_CASE_ROW).size();
    }

    public boolean waitForConfirmModalClosed() {
        return WaitSupport.waitForAbsence(wait, CONFIRM_MODAL);
    }
}