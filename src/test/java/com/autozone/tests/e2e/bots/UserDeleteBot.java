package com.autozone.tests.e2e.bots;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Bot encargado de interactuar con el modal de confirmación de borrado de usuarios.
 */
public class UserDeleteBot extends BaseBot {

    private static final By CONFIRM_BUTTON = By.cssSelector("[data-testid='delete-user-confirm-btn']");
    private static final By CANCEL_BUTTON = By.cssSelector("[data-testid='delete-user-cancel-btn']");
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), 'has been deleted successfully')]");

    public UserDeleteBot(WebDriver driver) {
        super(driver);
    }

    public boolean isModalVisible() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(CONFIRM_BUTTON));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void confirmDelete() {
        waitForPresence(CONFIRM_BUTTON).click();
    }

    public void cancelDelete() {
        waitForPresence(CANCEL_BUTTON).click();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, "has been deleted successfully");
    }

    public boolean isModalClosed() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(CONFIRM_BUTTON));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
