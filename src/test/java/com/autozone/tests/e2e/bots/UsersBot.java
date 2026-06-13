package com.autozone.tests.e2e.bots;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UsersBot extends BaseBot {

    private static final String USERS_PATH = "/users";

    // The Users table (Mantine <Table>) does not render a data-testid on its
    // rows, so we target the rendered <tr> elements inside the table body.
    private static final By USER_ROW = By.cssSelector("table tbody tr");
    private static final By EDIT_BUTTON = By.cssSelector("[data-testid='user-edit-button']");
    private static final By NEW_USER_BUTTON = By.cssSelector("[data-testid='user-create-open-btn']");

    public UsersBot(WebDriver driver) {
        super(driver);
    }

    public void openList() {
        openPath(USERS_PATH);
    }

    public void waitUntilListReady() {
        waitForPresence(USER_ROW);
    }

    public void openEditForFirstUser() {
        List<WebElement> rows = waitForAllPresent(USER_ROW);
        rows.get(0).findElement(EDIT_BUTTON).click();
    }

    public String getFirstUserRowText() {
        List<WebElement> rows = waitForAllPresent(USER_ROW);
        return rows.get(0).getText();
    }

    public void clickNewUser() {
        waitForPresence(NEW_USER_BUTTON).click();
    }

    private WebElement findRowByName(String fullName) {
        List<WebElement> rows = waitForAllPresent(USER_ROW);
        for (WebElement row : rows) {
            if (row.getText().contains(fullName)) {
                return row;
            }
        }
        throw new org.openqa.selenium.NoSuchElementException(
                "No user row found containing: " + fullName);
    }

    public void clickDeleteForUser(String fullName) {
        WebElement row = findRowByName(fullName);
        WebElement deleteButton = row.findElement(
                By.xpath(".//button[normalize-space()='Delete']"));
        deleteButton.click();
    }

    public boolean isUserListed(String fullName) {
        List<WebElement> rows = findElements(USER_ROW);
        for (WebElement row : rows) {
            if (row.getText().contains(fullName)) {
                return true;
            }
        }
        return false;
    }

    public boolean waitUntilUserDisappears(String fullName) {
        return new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.not(d -> isUserListed(fullName)));
    }
}
