package com.autozone.tests.e2e.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverFactory {
    
    private DriverFactory() {
    }

    public static WebDriver createChromeDriver() {
        return new ChromeDriver();
    }
}
