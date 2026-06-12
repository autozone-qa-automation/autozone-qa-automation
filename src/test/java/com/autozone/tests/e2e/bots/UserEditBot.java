package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserEditBot extends BaseBot {

    private static final By EDIT_FORM = By.cssSelector("[data-testid='user-create-form']");
    private static final By NAME_INPUT = By.cssSelector("[data-testid='user-name-input']");
    private static final By LASTNAME_INPUT = By.cssSelector("[data-testid='user-lastname-input']");
    private static final By EMAIL_INPUT = By.cssSelector("[data-testid='user-email-input']");
    private static final By SAVE_BUTTON = By.cssSelector("[data-testid='user-submit-btn']");
    private static final By CANCEL_BUTTON = By.cssSelector("[data-testid='user-cancel-btn']");
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), 'User updated successfully')]");

    public UserEditBot(WebDriver driver) {
        super(driver);
    }

    public boolean isModalVisible() {
        return findElements(EDIT_FORM).size() > 0;
    }

    public void setName(String name) {
        clearAndType(waitForPresence(NAME_INPUT), name);
    }

    public void setLastName(String lastName) {
        clearAndType(waitForPresence(LASTNAME_INPUT), lastName);
    }

    public void clearName() {
        clearAndType(waitForPresence(NAME_INPUT), "");
    }

    public String getNameValue() {
        return waitForPresence(NAME_INPUT).getAttribute("value");
    }

    public String getLastNameValue() {
        return waitForPresence(LASTNAME_INPUT).getAttribute("value");
    }

    private void clearAndType(WebElement element, String value) {
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        if (value != null && !value.isEmpty()) {
            element.sendKeys(value);
        }
    }

    public void clickSave() {
        waitForPresence(SAVE_BUTTON).click();
    }

    public void clickCancel() {
        waitForPresence(CANCEL_BUTTON).click();
    }

    public boolean hasSuccessNotification() {
        return findElements(SUCCESS_NOTIFICATION).size() > 0;
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, "User updated successfully");
    }
}
