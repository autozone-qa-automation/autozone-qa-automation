package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReleaseDeleteBot extends BaseBot {

    private static final By DELETE_BUTTON = By.cssSelector("[data-testid='button-modal-delete-btn']");
    private static final By CONFIRM_BUTTON = By.cssSelector("[data-testid='delete-release-confirm-btn']");
    private static final By CANCEL_BUTTON = By.cssSelector("[data-testid='delete-release-cancel-btn']");
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), 'Release has been deleted successfully')]");

    public ReleaseDeleteBot(WebDriver driver) {
        super(driver);
    }

    public void clickDeleteButton() {
        waitForPresence(DELETE_BUTTON).click();
    }

    public void confirmDelete() {
        waitForPresence(CONFIRM_BUTTON).click();
    }

    public void cancelDelete() {
        waitForPresence(CANCEL_BUTTON).click();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, "Release has been deleted successfully");
    }
}
