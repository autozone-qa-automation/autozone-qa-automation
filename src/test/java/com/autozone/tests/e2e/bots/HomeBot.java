package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomeBot extends BaseBot {

    private static final By USER_POPOVER_BUTTON =
            By.cssSelector(".m_811560b9");

    private static final By POPOVER_DROPDOWN =
            By.cssSelector(".mantine-Popover-dropdown");

    private static final By USER_MANAGEMENT_LINK =
            By.cssSelector("a[href='/users']");

    public HomeBot(WebDriver driver) {
        super(driver);
    }

    public void openUserPopover() {
        waitForPresence(USER_POPOVER_BUTTON).click();
        waitForPresence(POPOVER_DROPDOWN);
    }

    public void goToUserManagement() {
        waitForPresence(USER_MANAGEMENT_LINK).click();
    }

    // Navegación directa — más estable que interactuar con el popover
    public void navigateToUserManagement() {
        openPath("/users");
    }
}