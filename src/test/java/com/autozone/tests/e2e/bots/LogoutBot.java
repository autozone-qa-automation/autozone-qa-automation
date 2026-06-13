package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class LogoutBot extends BaseBot {

    private static final String LOGIN_PATH = "/login";

    private static final By USER_MENU_BUTTON = By.xpath("(//button)[last()]");
    private static final By LOGOUT_OPTION = By.xpath("//*[normalize-space()='Log Out']");

    public LogoutBot(WebDriver driver) {
        super(driver);
    }

    /**
     * Abre el menú de usuario donde se encuentra la opción de cerrar sesión.
     */
    public void openUserMenu() {
        waitForPresence(USER_MENU_BUTTON).click();
        waitForPresence(LOGOUT_OPTION);
    }

    /**
     * Ejecuta el proceso de logout.
     */
    public void logout() {
        openUserMenu();
        waitForPresence(LOGOUT_OPTION).click();
        waitUntilLoginPage();
    }

    /**
     * Espera hasta que el usuario sea redirigido a la página de login.
     */
    public boolean waitUntilLoginPage() {
        return wait.until(driver -> driver.getCurrentUrl().contains(LOGIN_PATH));
    }

    /**
     * Verifica que el usuario se encuentre en la pantalla de login.
     */
    public boolean isOnLoginPage() {
        return currentUrl().contains(LOGIN_PATH);
    }

    /**
     * Verifica que el token de autenticación haya sido eliminado.
     */
    public boolean hasNoAuthToken() {
        String authToken = (String) ((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('authToken');");

        return authToken == null;
    }

    /**
     * Verifica que aún exista una sesión activa.
     */
    public boolean hasAuthToken() {
        String authToken = (String) ((JavascriptExecutor) driver)
                .executeScript("return window.localStorage.getItem('authToken');");

        return authToken != null;
    }

    /**
     * Intenta regresar a la página anterior.
     */
    public void navigateBack() {
        driver.navigate().back();
    }

    /**
     * Verifica que la sesión haya sido cerrada correctamente.
     */
    public boolean sessionEnded() {
        return hasNoAuthToken() && isOnLoginPage();
    }

    
}