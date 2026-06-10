package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UsersBot extends BaseBot {

    private static final String USERS_PATH = "/users";

    private static final By USER_ROW = By.cssSelector("[data-testid='users-table-row']");
    private static final By EDIT_BUTTON = By.cssSelector("[data-testid='user-edit-button']");

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
}
