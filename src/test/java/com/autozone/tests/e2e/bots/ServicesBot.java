package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.fail;

//import com.autozone.tests.e2e.E2eConfig;

public class ServicesBot extends BaseBot {
    
    private static final String LIST_PATH = "/services";
    private static final String SERVICE_CARD_PREFIX = "service-card-";

    //private static final By BODY = By.tagName("body");
    private static final By SERVICE_TITLE = By.cssSelector("[data-testid='title-header-title']");
    private static final By SERVICE_META = By.cssSelector("[data-testid='title-header-meta']");
    private static final By SERVICE_SEARCH_INPUT = By.cssSelector("[data-testid='service-search-input']");
    private static final By ADD_SERVICE_BUTTON = By.cssSelector("[data-testid='add-service-button']");
    private static final By ADD_SERVICE_CARD = By.cssSelector("[data-testid='add-service-card']");
    private static final By SERVICE_CARDS = By.cssSelector("[data-testid^='service-card-']:not([data-testid^='service-card-title-'])");
    
    public ServicesBot(WebDriver driver) {
        super(driver);
    }
    
    public void openList() {
        openPath(LIST_PATH);
    }

    public boolean isListTitleVisible() {
        return waitForPresence(SERVICE_TITLE).isDisplayed();
    }

    public boolean isListMetaVisible() {
        return waitForPresence(SERVICE_META).isDisplayed();
    }

    public boolean isSearchVisible() {
        return waitForPresence(SERVICE_SEARCH_INPUT).isDisplayed();
    }

    public boolean isAddServiceButtonVisible() {
        return waitForPresence(ADD_SERVICE_BUTTON).isDisplayed();
    }

    public boolean isAddServiceCardVisible() {
        return waitForPresence(ADD_SERVICE_CARD).isDisplayed();
    }

    public void waitUntilListReady() {
        waitForPresence(SERVICE_CARDS);
        wait.until(webDriver -> !webDriver.findElements(SERVICE_CARDS).isEmpty());
    }

    public List<String> getListedServiceIds() {
        return getServiceCards().stream()
                .map(card -> card.getAttribute("data-testid"))
                .filter(id -> id != null && id.startsWith(SERVICE_CARD_PREFIX))
                .map(id -> id.replace(SERVICE_CARD_PREFIX, ""))
                .collect(Collectors.toList());
    }

    public void searchService(String text) {
        WebElement input = waitForPresence(SERVICE_SEARCH_INPUT);
        input.clear();
        input.sendKeys(text);
    }

    public boolean isServiceListed(String name) {
        return hasServiceNamed(name);
    }

    public boolean hasServiceNamed(String name) {
        return getServiceCards().stream().anyMatch(card -> card.getText().contains(name));
    }
    
    public void openServiceDetails(String serviceId) {
        WebElement card = findServiceCard(serviceId);

        card.click();

        wait.until(driver ->
                driver.getCurrentUrl().endsWith(LIST_PATH + "/" + serviceId)
        );
    }

    private List<WebElement> getServiceCards() {
        waitUntilListReady();
        return findElements(SERVICE_CARDS);
    }

    private WebElement findServiceCard(String serviceId) {
        waitUntilListReady();
        return waitForElementOrFail(By.cssSelector("[data-testid='" + SERVICE_CARD_PREFIX + serviceId + "']"), SERVICE_CARD_PREFIX + serviceId);
    
    }

    private WebElement waitForElementOrFail(By locator, String selectorName) {
        try {
            return waitForPresence(locator);
        } catch (TimeoutException ex) {
            fail(
                "Selector not found: [data-testid='" + selectorName + "']. "
                            + "Please add this selector in the web page to support stable E2E tests."
            );
            return null;
        }
    }
}
