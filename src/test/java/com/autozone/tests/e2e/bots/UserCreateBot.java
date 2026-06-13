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

/**
 * Bot encargado de interactuar con el modal de creación de usuarios.
 */
public class UserCreateBot extends BaseBot {

    private static final By CREATE_FORM = By.cssSelector("[data-testid='user-create-form']");
    private static final By NAME_INPUT = By.cssSelector("[data-testid='user-name-input']");
    private static final By LASTNAME_INPUT = By.cssSelector("[data-testid='user-lastname-input']");
    private static final By EMAIL_INPUT = By.cssSelector("[data-testid='user-email-input']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-testid='user-password-input']");
    private static final By ROLE_SELECT = By.cssSelector("[data-testid='user-role-select']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='user-submit-btn']");
    private static final By SUCCESS_NOTIFICATION =
            By.xpath("//*[contains(normalize-space(.), 'User created successfully')]");

    public UserCreateBot(WebDriver driver) {
        super(driver);
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

        // The role options are fetched asynchronously after the modal opens, so the
        // dropdown may briefly render with no [data-combobox-option] items (or a
        // "nothing found" placeholder). Poll for a visible option to appear once the
        // roles have loaded, rather than failing as soon as the dropdown opens.
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

    public void fillValidForm(String name, String lastName, String email, String password) {
        waitForPresence(CREATE_FORM);

        setName(name);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        selectFirstRole();
    }

    public void submit() {
        WebElement button = waitForPresence(SUBMIT_BUTTON);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", button);

        button.click();
    }

    public boolean waitForSuccessNotification() {
        return waitForText(SUCCESS_NOTIFICATION, "User created successfully");
    }
}
