package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.autozone.tests.e2e.support.WaitSupport;

public class ReleaseStatusBot extends BaseBot {

    private static final By UPDATE_BUTTON      = By.cssSelector("[data-testid='button-modal-update-btn']");
    private static final By STATUS_MODAL        = By.cssSelector("[data-testid='release-status-modal']");
    private static final By STATUS_SELECT       = By.cssSelector("[data-testid='release-status-select']");
    private static final By MODAL_UPDATE_BTN    = By.cssSelector("[data-testid='release-status-update-btn']");
    private static final By CONFIRM_MODAL       = By.cssSelector("[data-testid='release-status-confirm-modal']");
    private static final By CONFIRM_BTN         = By.cssSelector("[data-testid='release-status-confirm-btn']");
    private static final By CANCEL_BTN          = By.cssSelector("[data-testid='release-status-cancel-btn']");
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), 'Release status updated successfully')]");

    public ReleaseStatusBot(WebDriver driver) {
        super(driver);
    }

    public void clickUpdateButton() {
        waitForPresence(UPDATE_BUTTON).click();
        waitForPresence(STATUS_MODAL);
    }

    public void selectStatus(String status) {
        waitForPresence(STATUS_SELECT).click();
        By option = By.xpath("//div[@role='option' and normalize-space(.)='" + status + "']");
        waitForPresence(option).click();
    }

    public void clickModalUpdate() {
        waitForPresence(MODAL_UPDATE_BTN).click();
        waitForPresence(CONFIRM_MODAL);
    }

    public void confirmUpdate() {
        waitForPresence(CONFIRM_BTN).click();
    }

    public void cancelUpdate() {
        waitForPresence(CANCEL_BTN).click();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, "Release status updated successfully");
    }

    public boolean isStatusModalVisible() {
        return findElements(STATUS_MODAL).size() > 0;
    }

    public boolean waitForStatusModalToClose() {
        return WaitSupport.waitForAbsence(wait, STATUS_MODAL);
    }
}
