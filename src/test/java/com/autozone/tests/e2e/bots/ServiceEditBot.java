package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.autozone.tests.e2e.support.WaitSupport;

public class ServiceEditBot extends BaseBot {

    private static final By EDIT_BUTTON       = By.cssSelector("[data-testid='service-id-edit-button']");
    private static final By MODAL             = By.cssSelector("[data-testid='service-edit-modal']");
    private static final By NAME_INPUT        = By.cssSelector("[data-testid='service-name-input']");
    private static final By DESCRIPTION_INPUT = By.cssSelector("[data-testid='service-description-input']");
    private static final By SAVE_BUTTON       = By.cssSelector("[data-testid='service-edit-save-btn']");
    private static final By CANCEL_BUTTON     = By.cssSelector("[data-testid='service-edit-cancel-btn']");
    private static final By SUCCESS_TOAST     = By.xpath("//*[contains(normalize-space(.), 'Service updated')]");
    private static final By ERROR_TOAST       = By.xpath("//*[contains(normalize-space(.), 'Error')]");
    private static final By NAME_ERROR        = By.xpath("//*[contains(normalize-space(.), 'Name is required')]");

    public ServiceEditBot(WebDriver driver) {
        super(driver);
    }

    public void openEditModal() {
        WaitSupport.waitForAbsence(wait, SUCCESS_TOAST);
        waitForPresence(EDIT_BUTTON).click();
        waitForPresence(MODAL);
    }

    public boolean isModalVisible() {
        return findElements(MODAL).size() > 0;
    }

    public boolean waitForModalToClose() {
        return WaitSupport.waitForAbsence(wait, MODAL);
    }

    public String getNameValue() {
        return waitForPresence(NAME_INPUT).getAttribute("value");
    }

    public String getDescriptionValue() {
        return waitForPresence(DESCRIPTION_INPUT).getAttribute("value");
    }

    public void setName(String name) {
        clearAndType(waitForPresence(NAME_INPUT), name);
    }

    public void setDescription(String description) {
        clearAndType(waitForPresence(DESCRIPTION_INPUT), description);
    }

    public void clearName() {
        clearAndType(waitForPresence(NAME_INPUT), "");
    }

    public void setUrlNombre(int index, String nombre) {
        By locator = By.cssSelector("[data-testid='service-url-nombre-input-" + index + "']");
        clearAndType(waitForPresence(locator), nombre);
    }

    public void setUrlValue(int index, String url) {
        By locator = By.cssSelector("[data-testid='service-url-input-" + index + "']");
        clearAndType(waitForPresence(locator), url);
    }

    public void clickSave() {
        waitForPresence(SAVE_BUTTON).click();
    }

    public void clickCancel() {
        waitForPresence(CANCEL_BUTTON).click();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_TOAST, "Service updated");
    }

    public boolean hasSuccessNotification() {
        return findElements(SUCCESS_TOAST).size() > 0;
    }

    public boolean hasNameRequiredError() {
        return findElements(NAME_ERROR).size() > 0;
    }

    private void clearAndType(WebElement element, String value) {
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        if (value != null && !value.isEmpty()) {
            element.sendKeys(value);
        }
    }
}
