package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.autozone.tests.e2e.support.WaitSupport;

public class UserCreateBot extends BaseBot {

    private static final By OPEN_BUTTON = By.cssSelector("[data-testid='user-create-open-btn']");
    private static final By FORM = By.cssSelector("[data-testid='user-create-form']");
    private static final By NAME_INPUT = By.cssSelector("[data-testid='user-name-input']");
    private static final By LASTNAME_INPUT = By.cssSelector("[data-testid='user-lastname-input']");
    private static final By EMAIL_INPUT = By.cssSelector("[data-testid='user-email-input']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-testid='user-password-input']");
    private static final By ROLE_SELECT = By.cssSelector("[data-testid='user-role-select']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='user-submit-btn']");
    private static final By CANCEL_BUTTON = By.cssSelector("[data-testid='user-cancel-btn']");
    private static final By SUCCESS_NOTIFICATION = By.xpath("//*[contains(normalize-space(.), 'User created successfully')]");
    private static final By TABLE_ROW = By.cssSelector("[data-testid='users-table-row']");
    private static final By ROLE_OPTION = By.cssSelector("[role='option']");

    private static final String DEFAULT_NAME = "Test";
    private static final String DEFAULT_LAST_NAME = "User";
    private static final String DEFAULT_EMAIL = "test.user@example.com";
    private static final String DEFAULT_PASSWORD = "TestPass123";
    private static final String DEFAULT_ROLE = "READ_ONLY";

    public UserCreateBot(WebDriver driver) {
        super(driver);
    }

    public void openCreateForm() {
        waitForPresence(OPEN_BUTTON).click();
        waitForPresence(FORM);
    }

    public boolean isFormVisible() {
        return findElements(FORM).size() > 0;
    }

    public void fillForm(String name, String lastName, String email, String password, String role) {
        setName(name);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        selectRole(role);
    }

    public void fillWithValidData() {
        fillForm(DEFAULT_NAME, DEFAULT_LAST_NAME, DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_ROLE);
    }

    public void setName(String name) {
        clearAndType(waitForPresence(NAME_INPUT), name);
    }

    public void setLastName(String lastName) {
        clearAndType(waitForPresence(LASTNAME_INPUT), lastName);
    }

    public void setEmail(String email) {
        clearAndType(waitForPresence(EMAIL_INPUT), email);
    }

    public void setPassword(String password) {
        clearAndType(waitForPresence(PASSWORD_INPUT), password);
    }

    public void selectRole(String roleLabel) {
        WebElement select = waitForPresence(ROLE_SELECT);
        select.click();
        WaitSupport.waitForPresence(wait, ROLE_OPTION);
        List<WebElement> options = findElements(ROLE_OPTION);
        for (WebElement option : options) {
            if (option.getText().trim().equalsIgnoreCase(roleLabel)
                    || option.getText().trim().contains(roleLabel)
                    || matchesSpanishRole(roleLabel, option.getText().trim())) {
                option.click();
                return;
            }
        }
        if (!options.isEmpty()) {
            options.get(0).click();
        }
    }

    public void submit() {
        waitForPresence(SUBMIT_BUTTON).click();
    }

    public void clickCancel() {
        waitForPresence(CANCEL_BUTTON).click();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, "User created successfully");
    }

    public boolean hasSuccessNotification() {
        return findElements(SUCCESS_NOTIFICATION).size() > 0;
    }

    public boolean waitForModalToClose() {
        return WaitSupport.waitForAbsence(wait, FORM);
    }

    public boolean hasValidationErrors() {
        String pageText = driver.findElement(By.tagName("body")).getText();
        return pageText.contains("Name is required")
                || pageText.contains("Last name is required")
                || pageText.contains("Valid email required")
                || pageText.contains("Password is required")
                || pageText.contains("Role is required");
    }

    public boolean hasInvalidEmailFormatError() {
        String pageText = driver.findElement(By.tagName("body")).getText();
        return pageText.contains("Valid email required");
    }

    public boolean isNewUserInList(String name) {
        List<WebElement> rows = waitForAllPresent(TABLE_ROW);
        for (WebElement row : rows) {
            if (row.getText().contains(name)) {
                return true;
            }
        }
        return false;
    }

    private void clearAndType(WebElement element, String value) {
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        if (value != null && !value.isEmpty()) {
            element.sendKeys(value);
        }
    }

    private boolean matchesSpanishRole(String spanishLabel, String englishLabel) {
        switch (spanishLabel.toLowerCase()) {
            case "usuario estándar":
            case "usuario estandar":
            case "estándar":
            case "estandar":
                return "READ_ONLY".equalsIgnoreCase(englishLabel);
            case "administrador":
            case "admin":
                return "ADMIN".equalsIgnoreCase(englishLabel);
            case "desarrollador":
            case "developer":
                return "DEV".equalsIgnoreCase(englishLabel);
            default:
                return false;
        }
    }
}
