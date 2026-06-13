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

    // ── Métodos nuevos para ST-US-10 y ST-US-11 ──────────────────

    private static final By USERS_TABLE = By.tagName("table");

    private static final By ROLE_FILTER_INPUT =
            By.cssSelector("input[role='combobox']");

    private static final By ROLE_FILTER_OPTIONS =
            By.cssSelector("[role='option']");

    public boolean isTableVisible() {
        return waitForPresence(USERS_TABLE).isDisplayed();
    }

    public boolean hasUsers() {
        waitUntilListReady();
        return !findElements(USER_ROW).isEmpty();
    }

    public void filterByRole(String roleLabel) {
        waitForPresence(ROLE_FILTER_INPUT).click();
        List<WebElement> options = waitForAllPresent(ROLE_FILTER_OPTIONS);
        options.stream()
                .filter(o -> o.getText().trim().equalsIgnoreCase(roleLabel))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Role option not found in filter: " + roleLabel))
                .click();
        waitUntilListReady();
    }

    public String getSelectedRoleFilter() {
        return waitForPresence(ROLE_FILTER_INPUT).getAttribute("value");
    }
}
