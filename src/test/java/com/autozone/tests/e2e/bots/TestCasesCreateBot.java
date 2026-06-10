package com.autozone.tests.e2e.bots;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Bot encargado de interactuar con el modal de creación de Test Cases.
 */
public class TestCasesCreateBot extends BaseBot {

    private static final By CREATE_FORM =
        By.cssSelector("[data-testid='test-case-create-form']");

    private static final By NAME_INPUT =
        By.cssSelector("[data-testid='test-case-create-title-input']");

    private static final By FEATURE_SELECT =
        By.cssSelector("[data-testid='test-case-create-feature-select']");

    private static final By TYPE_CONTROL =
        By.cssSelector("[data-testid='test-case-create-type-control']");

    private static final By DESCRIPTION_INPUT =
        By.cssSelector("[data-testid='test-case-create-description-input']");

    private static final By PRECONDITIONS_INPUT =
        By.cssSelector("[data-testid='test-case-create-preconditions-input']");

    private static final By INPUTS_INPUT =
        By.cssSelector("[data-testid='test-case-create-inputs-input']");

    private static final By STEPS_INPUT =
        By.cssSelector("[data-testid='test-case-create-steps-input']");

    private static final By POSTCONDITIONS_INPUT =
        By.cssSelector("[data-testid='test-case-create-postconditions-input']");

    private static final By EXPECTED_OUTPUT_INPUT =
        By.cssSelector("[data-testid='test-case-create-expected-output-input']");

    private static final By CREATE_BUTTON =
        By.cssSelector("[data-testid='test-case-create-submit-button']");

    private static final By VALIDATION_ALERT =
        By.cssSelector("[data-testid='test-case-create-validation-alert']");

    private static final By ERROR_ALERT =
        By.cssSelector("[data-testid='test-case-create-error-alert']");

    private static final By CLOSE_BUTTON =
        By.cssSelector(".mantine-Modal-header button");

    private static final By SUCCESS_NOTIFICATION =
        By.xpath(
            "//*[contains(normalize-space(),'Test case creado') "
                + "or contains(normalize-space(),'El test case se creó correctamente') "
                + "or contains(normalize-space(),'Test case created') "
                + "or contains(normalize-space(),'created successfully') "
                + "or contains(normalize-space(),'se creó correctamente')]");

    public TestCasesCreateBot(WebDriver driver) {
        super(driver);
    }

    public boolean isModalVisible() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(CREATE_FORM));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private WebElement getEditableElement(By locator) {
        WebElement element = waitForPresence(locator);
        String tagName = element.getTagName();

        if ("input".equalsIgnoreCase(tagName) || "textarea".equalsIgnoreCase(tagName)) {
            return element;
        }

        return element.findElement(By.cssSelector("input, textarea"));
    }

    private void clearAndType(By locator, String value) {
        WebElement element = getEditableElement(locator);

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            element
        );

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        shortWait.until(ExpectedConditions.elementToBeClickable(element));

        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        element.sendKeys(value);
    }

    public void setTitle(String value) {
        clearAndType(NAME_INPUT, value);
    }

    public void setDescription(String value) {
        clearAndType(DESCRIPTION_INPUT, value);
    }

    public void setPreconditions(String value) {
        clearAndType(PRECONDITIONS_INPUT, value);
    }

    public void setInputs(String value) {
        clearAndType(INPUTS_INPUT, value);
    }

    public void setSteps(String value) {
        clearAndType(STEPS_INPUT, value);
    }

    public void setPostconditions(String value) {
        clearAndType(POSTCONDITIONS_INPUT, value);
    }

    public void setExpectedOutput(String value) {
        clearAndType(EXPECTED_OUTPUT_INPUT, value);
    }

    public void selectFirstFeature() {
        WebElement select = waitForPresence(FEATURE_SELECT);

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            select
        );

        WebElement input;

        if ("input".equalsIgnoreCase(select.getTagName())) {
            input = select;
        } else {
            input = select.findElement(By.cssSelector("input"));
        }

        input.click();

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        WebElement option =
            shortWait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("[data-combobox-option]")));

        option.click();
    }

    public void setType(String value) {
        String label = value.equalsIgnoreCase("ON_DEMAND") ? "On demand" : "Regression";

        WebElement option =
            waitForPresence(
                By.xpath(
                    "//*[@data-testid='test-case-create-type-control']"
                        + "//*[normalize-space()='"
                        + label
                        + "']"));

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            option
        );

        option.click();
    }

    public void fillValidForm(String title) {
        waitForPresence(CREATE_FORM);

        setTitle(title);
        selectFirstFeature();
        setType("REGRESSION");
        setDescription("E2E description for creating a test case");
        setPreconditions("User is authenticated and the system is available");
        setInputs("Valid input data");
        setSteps("1. Open test cases page. 2. Create a new test case.");
        setPostconditions("The test case is available in the board");
        setExpectedOutput("The test case is created successfully");
    }

    public void fillOnlyTitle(String title) {
        waitForPresence(CREATE_FORM);
        setTitle(title);
    }

    public void create() {
        WebElement button = waitForPresence(CREATE_BUTTON);

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            button
        );

        button.click();
    }

    public boolean isSuccessVisible() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_NOTIFICATION));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isValidationMessageVisible() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(
                ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(VALIDATION_ALERT),
                    ExpectedConditions.visibilityOfElementLocated(ERROR_ALERT)
                )
            );
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void close() {
        waitForPresence(CLOSE_BUTTON).click();
    }
}