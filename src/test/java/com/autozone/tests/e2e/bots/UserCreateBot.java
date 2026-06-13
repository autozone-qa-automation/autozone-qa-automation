package com.autozone.tests.e2e.bots;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.autozone.tests.e2e.support.WaitSupport;

public class UserCreateBot extends BaseBot {

    private static final By OPEN_BUTTON = By.cssSelector("[data-testid='user-create-open-btn']");
    private static final By CREATE_FORM = By.cssSelector("[data-testid='user-create-form']");
    private static final By NAME_INPUT = By.cssSelector("[data-testid='user-name-input']");
    private static final By LASTNAME_INPUT = By.cssSelector("[data-testid='user-lastname-input']");
    private static final By EMAIL_INPUT = By.cssSelector("[data-testid='user-email-input']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-testid='user-password-input']");
    private static final By ROLE_SELECT = By.cssSelector("[data-testid='user-role-select']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='user-submit-btn']");
    private static final By CANCEL_BUTTON = By.cssSelector("[data-testid='user-cancel-btn']");
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), 'User created successfully')]");
    private static final By TABLE_ROW = By.cssSelector("[data-testid='users-table-row']");

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
        waitForPresence(CREATE_FORM);
    }

    public boolean isModalVisible() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(CREATE_FORM));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean waitForModalToClose() {
        return WaitSupport.waitForAbsence(wait, CREATE_FORM);
    }

    public void fillForm(String name, String lastName, String email, String password, String role) {
        waitForPresence(CREATE_FORM);
        setName(name);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        selectRole(role);
    }

    public void fillWithValidData() {
        fillForm(DEFAULT_NAME, DEFAULT_LAST_NAME, DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_ROLE);
    }

    public void fillValidForm(String name, String lastName, String email, String password) {
        waitForPresence(CREATE_FORM);
        setName(name);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        selectFirstRole();
    }

    public void setName(String value) {
        clearAndType(NAME_INPUT, value);
    }

    public void setLastName(String value) {
        clearAndType(LASTNAME_INPUT, value);
    }

    public void setEmail(String value) {
        clearAndType(EMAIL_INPUT, value);
    }

    public void setPassword(String value) {
        clearAndType(PASSWORD_INPUT, value);
    }

    public void selectFirstRole() {
        WebElement select = waitForPresence(ROLE_SELECT);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", select);

        WebElement input = "input".equalsIgnoreCase(select.getTagName())
                ? select
                : select.findElement(By.cssSelector("input"));

        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(input));

        input.click();

        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(driver -> {
                    List<WebElement> options =
                            driver.findElements(By.cssSelector("[data-combobox-option]"));
                    for (WebElement candidate : options) {
                        if (candidate.isDisplayed() && candidate.isEnabled()) {
                            return candidate;
                        }
                    }
                    return null;
                });

        option.click();
    }

    public void selectRole(String roleLabel) {
        WebElement select = waitForPresence(ROLE_SELECT);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", select);

        WebElement input = "input".equalsIgnoreCase(select.getTagName())
                ? select
                : select.findElement(By.cssSelector("input"));

        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(input));

        input.click();

        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(driver -> {
                    List<WebElement> options =
                            driver.findElements(By.cssSelector("[data-combobox-option]"));
                    for (WebElement candidate : options) {
                        if (candidate.isDisplayed() && candidate.isEnabled()
                                && (candidate.getText().trim().equalsIgnoreCase(roleLabel)
                                        || candidate.getText().trim().contains(roleLabel)
                                        || matchesSpanishRole(roleLabel, candidate.getText().trim()))) {
                            return candidate;
                        }
                    }
                    return null;
                });

        option.click();
    }

    public void submit() {
        WebElement button = waitForPresence(SUBMIT_BUTTON);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", button);
        button.click();
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

    private void clearAndType(By locator, String value) {
        WebElement element = waitForPresence(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);

        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(element));

        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        element.sendKeys(value);
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
