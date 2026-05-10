package com.autozone.base;

import com.autozone.utils.DriverFactory;

import org.openqa.selenium.WebDriver;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest { //debe ser abstract pq es el papdre de los tests, y asi TestNG no la intenta correr como si fuera otro test xd

    //setup del driver de selenium, asegurense de tenerlo instalado para chrome

    protected WebDriver driver;

    @BeforeMethod
    public void setup() {

        driver =
                DriverFactory.createDriver();
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {

            driver.quit();
        }
    }
}