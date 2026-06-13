package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LogoutBot extends BaseBot {

    private static final String LOGIN_PATH = "/login";
    private static final String PROTECTED_PATH = "/services";

    private static final By LOGIN_TITLE = By.cssSelector("[data-testid='login-page-title']");
    private static final By USER_MENU_TRIGGER = By.xpath("//*[contains(text(), 'Carlos Mendoza')]");
    private static final By LOGOUT_OPTION = By.xpath("//*[text()='Log Out' or contains(text(), 'Log Out')]");

    public LogoutBot(WebDriver driver) {
        super(driver);
    }

    public void logout() {
        // Despliega el menú del usuario
        WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(USER_MENU_TRIGGER));
        new Actions(driver).moveToElement(trigger).click().perform();

        // Breve pausa para la animación de Mantine UI
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Clic en Log Out
        WebElement logoutBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_OPTION));
        try {
            logoutBtn.click();
        } catch (Exception e) {
            new Actions(driver).moveToElement(logoutBtn).click().perform();
        }

        waitUntilLoginPage();
    }

    public boolean waitUntilLoginPage() {
        return wait.until(d -> d.getCurrentUrl().contains(LOGIN_PATH));
    }

    public boolean isOnLoginPage() {
        return currentUrl().contains(LOGIN_PATH) && !findElements(LOGIN_TITLE).isEmpty();
    }

    public boolean hasNoAuthToken() {
        String authToken = (String) ((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('authToken');");
        return authToken == null;
    }

    public void openProtectedPage() {
        openPath(PROTECTED_PATH);
        waitUntilLoginPage();
    }
}