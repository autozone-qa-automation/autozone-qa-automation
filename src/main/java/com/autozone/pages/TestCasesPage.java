package com.autozone.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TestCasesPage {

    private WebDriver driver;

    private WebDriverWait wait;

    //metodos para los test de la pantalla de testcases
    private final By viewButtons = //metodo pa ver el boton VIEW
            By.cssSelector("[data-testid='view-button']");

    private final By emptyMessage =
        By.cssSelector("[data-testid='empty-testcases-message']");
    
    public TestCasesPage(WebDriver driver) {

        this.driver = driver;

        this.wait =
            new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
            );
    }

    public void open(String url) {

        driver.get(url);
    }

    public boolean hasViewButtons() {

        try {

            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    viewButtons
                )
            );

            List<WebElement> buttons =
                driver.findElements(viewButtons);

            return !buttons.isEmpty();

        } catch (TimeoutException e) {

            return false;
        }
    }

    public boolean isEmptyMessageVisible() {

        try {

            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    emptyMessage
                )
            );

            WebElement message =
                driver.findElement(emptyMessage);

            return message.isDisplayed();

        } catch (TimeoutException e) {

            return false;
        }
    }

    public void clickFirstViewButton() { //pal test de TestCasesLeer

        wait.until(
            ExpectedConditions.elementToBeClickable(
                viewButtons
            )
        );

        List<WebElement> buttons =
                driver.findElements(viewButtons);

        if (buttons.isEmpty()) {

            throw new RuntimeException(
                    "No hay botones View"
            );
        }

        buttons.get(0).click();
    }
}