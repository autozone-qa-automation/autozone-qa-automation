package com.autozone.tests.e2e.bots;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginBot extends BaseBot {

    private static final String HOME_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String PROTECTED_PATH = "/services";

    private static final By BODY = By.tagName("body");
    private static final By LOGIN_TITLE = By.cssSelector("[data-testid='login-page-title']");
    private static final By EMAIL_INPUT = By.cssSelector("[data-testid='login-email-input']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-testid='login-password-input']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='login-submit-button']");
    private static final By USER_MENU_BUTTON = By.xpath("(//button)[last()]");
    private static final By LOGOUT_OPTION = By.xpath("//*[normalize-space()='Log Out']");

    private String lastAlertText;

    public LoginBot(WebDriver driver) {
        super(driver);
    }

    public void openLogin() {
        openPath(LOGIN_PATH);
        wait.until(webDriver ->
                webDriver.getCurrentUrl().contains(LOGIN_PATH)
                        || (hasAuthToken() && !webDriver.getCurrentUrl().contains(LOGIN_PATH))
        );
    }

    public void openLoginForm() {
        openPath(LOGIN_PATH);
        waitForPresence(LOGIN_TITLE);
    }

    public void openHome() {
        openPath(HOME_PATH);
    }

    public void openProtectedPage() {
        openPath(PROTECTED_PATH);
    }

    public void clearSession() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
    }

    public void enterUsername(String username) {
        type(EMAIL_INPUT, username);
    }

    public void enterPassword(String password) {
        type(PASSWORD_INPUT, password);
    }

    public void clearUsername() {
        type(EMAIL_INPUT, "");
    }

    public void clearPassword() {
        type(PASSWORD_INPUT, "");
    }

    public void submit() {
        waitForPresence(SUBMIT_BUTTON).click();
    }

    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        submit();
    }

    public boolean waitUntilLoggedIn() {
        return wait.until(webDriver -> {
            captureAndDismissAlertIfPresent();
            return hasAuthToken() && !webDriver.getCurrentUrl().contains(LOGIN_PATH);
        });
    }

    public boolean waitUntilLoginPage() {
        return wait.until(webDriver -> webDriver.getCurrentUrl().contains(LOGIN_PATH));
    }

    public boolean isOnLoginPage() {
        captureAndDismissAlertIfPresent();
        return currentUrl().contains(LOGIN_PATH) && !findElements(LOGIN_TITLE).isEmpty();
    }

    public boolean hasAuthToken() {
        try {
            return localStorageValue("authToken") != null;
        } catch (org.openqa.selenium.UnhandledAlertException e) {
            captureAndDismissAlertIfPresent();
            return false;
        }
    }

    public boolean hasNoAuthToken() {
        captureAndDismissAlertIfPresent();
        return !hasAuthToken();
    }

    public boolean hasNoErrorMessages() {
        captureAndDismissAlertIfPresent();
        String pageText = pageText();
        return !containsAny(pageText,
                "Invalid username or password.",
                "Error logging in:",
                "Invalid email address",
                "Minimum 1 characters required",
                "Required field"
        );
    }

    public boolean isLoginErrorVisible(String expectedMessage) {
        String alertText = captureAndDismissAlertIfPresent();
        String pageText = pageText();

        if (contains(alertText, expectedMessage) || contains(pageText, expectedMessage)) {
            return true;
        }

        return "Invalid username or password.".equals(expectedMessage)
                && (contains(alertText, "Error logging in:") || contains(pageText, "Error logging in:"));
    }

    public boolean hasInvalidCredentialsMessage() {
        return isLoginErrorVisible("Invalid username or password.");
    }

    public boolean hasInactiveAccountMessage() {
        String alertText = captureAndDismissAlertIfPresent();
        String pageText = pageText();
        return containsAny(alertText,
                "inactive",
                "User account is inactive",
                "Please contact support"
        ) || containsAny(pageText,
                "inactive",
                "User account is inactive",
                "Please contact support"
        );
    }

    public boolean hasRequiredFieldValidationMessages() {
        String pageText = pageText();
        return contains(pageText, "Required field")
                || (hasUsernameRequiredValidationMessage() && hasPasswordRequiredValidationMessage());
    }

    public boolean hasUsernameRequiredValidationMessage() {
        String pageText = pageText();
        return containsAny(pageText,
                "Required field",
                "Invalid email address",
                "Minimum 3 characters required"
        );
    }

    public boolean hasInvalidEmailFormatValidationMessage() {
        return contains(pageText(), "Invalid email address");
    }

    public boolean hasPasswordRequiredValidationMessage() {
        String pageText = pageText();
        return containsAny(pageText,
                "Required field",
                "Minimum 1 characters required"
        );
    }

    public void openUserMenu() {
        waitForPresence(USER_MENU_BUTTON).click();
        waitForPresence(LOGOUT_OPTION);
    }

    public void logout() {
        openUserMenu();
        waitForPresence(LOGOUT_OPTION).click();
        waitUntilLoginPage();
    }

    public void navigateBack() {
        driver.navigate().back();
    }

    public String captureAndDismissAlertIfPresent() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent());
            lastAlertText = alert.getText();
            alert.accept();
        } catch (TimeoutException ignored) {
            // No alert is present.
        }
        return lastAlertText;
    }

    private void type(By locator, String text) {
        WebElement input = waitForPresence(locator);
        input.clear();
        if (text != null && !text.isEmpty()) {
            input.sendKeys(text);
        }
    }

    private String pageText() {
        return waitForPresence(BODY).getText();
    }

    private String localStorageValue(String key) {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem(arguments[0]);", key);
    }

    private boolean contains(String text, String expected) {
        return text != null && expected != null && text.contains(expected);
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (contains(text, value)) {
                return true;
            }
        }
        return false;
    }
}
