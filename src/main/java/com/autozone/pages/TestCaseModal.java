package com.autozone.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TestCaseModal { //modal es la lista de los testcases

    private WebDriver driver; //maybe esto sobra pero lo dejo por escalabilidad

    private WebDriverWait wait;

    private final By modal = //aqui señalas a la lista frontend testcases
            By.cssSelector("[data-testid='testcase-modal']");

    public TestCaseModal(WebDriver driver) {

        this.driver = driver;

        this.wait =
            new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
            );
    }

    public boolean isVisible() {

        try {

            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    modal
                )
            );

            return true;

        } catch (TimeoutException e) {

            return false;
        }
    }
}