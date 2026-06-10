package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TestCasesEditBot extends BaseBot {

    private static final By TITLE_INPUT =
        By.cssSelector("input.mantine-TextInput-input");

    private static final By FEATURE_INPUT =
        By.cssSelector(".mantine-Modal-body input.mantine-Select-input");

    private static final By DESCRIPTION_INPUT =
        By.xpath("(//textarea[contains(@class,'mantine-Textarea-input')])[1]");

    private static final By PRECONDITIONS_INPUT =
        By.xpath("(//textarea[contains(@class,'mantine-Textarea-input')])[2]");

    private static final By INPUTS_INPUT =
        By.xpath("(//textarea[contains(@class,'mantine-Textarea-input')])[3]");

    private static final By STEPS_INPUT =
        By.xpath("(//textarea[contains(@class,'mantine-Textarea-input')])[4]");

    private static final By POSTCONDITIONS_INPUT =
        By.xpath("(//textarea[contains(@class,'mantine-Textarea-input')])[5]");

    private static final By EXPECTED_OUTPUT_INPUT =
        By.xpath("(//textarea[contains(@class,'mantine-Textarea-input')])[6]");

    private static final By SAVE_BUTTON =
        By.xpath("//button[normalize-space()='Save Changes']");

    private static final By CANCEL_BUTTON =
        By.cssSelector(".mantine-Modal-header button");

    public TestCasesEditBot(WebDriver driver) {
        super(driver);
    }

    private void clearAndType(WebElement el, String value) {
        el.click();
        el.sendKeys(Keys.CONTROL + "a");
        el.sendKeys(Keys.DELETE);
        el.sendKeys(value);
    }

    public boolean isPageVisible() {
        return findElements(TITLE_INPUT).size() > 0;
    }

    public boolean isErrorVisible() {
        return !findElements(SAVE_BUTTON).isEmpty();
    }

    public boolean isSaveEnabled() {
        List<WebElement> buttons = findElements(SAVE_BUTTON);
        if (buttons.isEmpty()) return false;
        return buttons.get(0).isEnabled();
    }

    public boolean isBackOnList() {
        return wait.until(driver -> findElements(CANCEL_BUTTON).isEmpty());
    }

    public boolean isSuccessVisible() {
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        return findElements(CANCEL_BUTTON).isEmpty();
    }

    public String getTitleValue() {
        return waitForPresence(TITLE_INPUT).getAttribute("value");
    }

    public String getStepsValue() {
        return waitForPresence(STEPS_INPUT).getAttribute("value");
    }

    public String getExpectedOutputValue() {
        return waitForPresence(EXPECTED_OUTPUT_INPUT).getAttribute("value");
    }

    public void setTitle(String value) {
        clearAndType(waitForPresence(TITLE_INPUT), value);
    }

    public void selectFirstFeature() {
        WebElement input = waitForPresence(FEATURE_INPUT);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement option = shortWait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-combobox-option]"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
    }

    public void clearFeature() {
        WebElement input = waitForPresence(FEATURE_INPUT);
        input.click();
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(Keys.DELETE);
    }

    public void setDescription(String value) {
        clearAndType(waitForPresence(DESCRIPTION_INPUT), value);
    }

    public void setType(String value) {
        String selector = value.equalsIgnoreCase("REGRESSION") ?
            "input[id$='-REGRESSION']" : "input[id$='-ON_DEMAND']";
        WebElement input = waitForPresence(By.cssSelector(selector));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);
    }

    public void setPreconditions(String value) {
        clearAndType(waitForPresence(PRECONDITIONS_INPUT), value);
    }

    public void setPostconditions(String value) {
        clearAndType(waitForPresence(POSTCONDITIONS_INPUT), value);
    }

    public void setInputs(String value) {
        clearAndType(waitForPresence(INPUTS_INPUT), value);
    }

    public void setSteps(String value) {
        clearAndType(waitForPresence(STEPS_INPUT), value);
    }

    public void setExpectedOutput(String value) {
        clearAndType(waitForPresence(EXPECTED_OUTPUT_INPUT), value);
    }

    public void save() {
        WebElement btn = waitForPresence(SAVE_BUTTON);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void cancel() {
        waitForPresence(CANCEL_BUTTON).click();
    }
}