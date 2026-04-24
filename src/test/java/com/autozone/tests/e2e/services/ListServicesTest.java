package com.autozone.tests.e2e.services;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import com.autozone.tests.e2e.E2eConfig;

public class ListServicesTest {
    private static final String LIST_URL = E2eConfig.baseUrl() + "/services";

    @Test
    public void shouldDisplayHeader() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(LIST_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='title-header-breadcrumbs']"))).isDisplayed(),
                    "Breadcrumb header should be visible"
            );
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='title-header-title']"))).isDisplayed(),
                    "Main title should be visible"
            );
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='title-header-meta']"))).isDisplayed(),
                    "Header meta should be visible"
            );
            
        } finally {
            driver.quit();
        }
    }

    @Test
    public void shouldDisplayAddServiceButton() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(LIST_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                        Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='add-service-button']"))).isDisplayed(),
                    "Add service button should be visible"
            );
        } finally {
            driver.quit();
        }
    }

    @Test
    public void shouldDisplaySearchBar() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(LIST_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='service-search-input']"))).isDisplayed(),
                    "Service search input should be visible"
            );
        } finally {
            driver.quit();
        }
    }

    @Test
    public void shouldDisplayAddServiceCard() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(LIST_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='add-service-card']"))).isDisplayed(),
                    "Add service card should be visible"
            );
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='add-service-card-icon']"))).isDisplayed(),
                    "Add service icon should be visible"
            );
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='add-service-card-title']"))).isDisplayed(),
                    "Add service title should be visible"
            );
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='add-service-card-description']"))).isDisplayed(),
                    "Add service description should be visible"
            );

        } finally {
            driver.quit();
        }
    }

    @Test
    public void shouldDisplayServiceListCards() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(LIST_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Assert.assertTrue(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid^='service-card-']"))).isDisplayed(),
                    "Service list should be visible"
            );
        } finally {
            driver.quit();
        }
    }



}
