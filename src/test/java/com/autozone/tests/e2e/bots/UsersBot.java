package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UsersBot extends BaseBot {

    // <Title order={1}> de Mantine → <h1>
    private static final By USERS_TITLE =
            By.tagName("h1");

    // <Table> de Mantine → <table> real en el DOM
    private static final By USERS_TABLE =
            By.tagName("table");

    private static final By USER_ROWS =
            By.cssSelector("tbody tr");
    private static final String USERS_PATH = "/users";

    private static final By USER_ROW = By.cssSelector("[data-testid='users-table-row']");
    private static final By EDIT_BUTTON = By.cssSelector("[data-testid='user-edit-button']");

    public UsersBot(WebDriver driver) {
        super(driver);
    }

    public boolean isTitleVisible() {
        return waitForPresence(USERS_TITLE).isDisplayed();
    }

    public boolean isTableVisible() {
        return waitForPresence(USERS_TABLE).isDisplayed();
    }

    public void waitUntilUsersListReady() {
        waitForPresence(USERS_TABLE);
        wait.until(d -> !d.findElements(USER_ROWS).isEmpty());
    }

    public boolean hasUsers() {
        return !getUserRows().isEmpty();
    }

    private List<WebElement> getUserRows() {
        waitUntilUsersListReady();
        return findElements(USER_ROWS);
    }
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
