package com.autozone.tests.e2e.bots;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TestCaseModalBot extends BaseBot {

    private static final By MODAL =
            By.cssSelector("[data-testid='testcase-modal']");

    public TestCaseModalBot(WebDriver driver) {
        super(driver);
    }

    public boolean isVisible() {
        return findElements(MODAL).size() > 0;
    }
}