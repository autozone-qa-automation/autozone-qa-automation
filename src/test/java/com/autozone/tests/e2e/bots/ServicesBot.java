package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import static org.testng.Assert.fail;

//import com.autozone.tests.e2e.E2eConfig;

public class ServicesBot extends BaseBot {
    
    private static final String LIST_PATH = "/services";
    private static final String SERVICE_CARD_PREFIX = "service-card-";

    private static final By SERVICE_META = By.cssSelector("[data-testid='title-header-meta']");
    private static final By SERVICE_SEARCH_INPUT = By.cssSelector("[data-testid='service-search-input']");
    private static final By ADD_SERVICE_BUTTON = By.cssSelector("[data-testid='add-service-button']");
    private static final By ADD_SERVICE_CARD = By.cssSelector("[data-testid='add-service-card']");
    private static final By SERVICE_CARDS = By.cssSelector("[data-testid^='service-card-']:not([data-testid^='service-card-title-'])");
    private static final By EMPTY_SEARCH_MESSAGE = By.cssSelector("[data-testid='services-empty-search-message']");
    private static final By CREATE_SERVICE_FORM = By.cssSelector("[data-testid='service-create-form']");
    private static final By SERVICE_NAME_INPUT = By.cssSelector("[data-testid='service-name-input']");
    private static final By URL_NAME_INPUT = By.cssSelector("[data-testid='url-nombre-0']");
    private static final By URL_INPUT = By.cssSelector("[data-testid='url-url-0']");
    private static final By SUBMIT_SERVICE_BUTTON = By.cssSelector("[data-testid='service-submit-btn']");
    private static final By SERVICE_DESCRIPTION_INPUT = By.cssSelector("[data-testid='service-description-input']");

    public ServicesBot(WebDriver driver) {
        super(driver);
    }
    
    public void openList() {
        openPath(LIST_PATH);
    }

    public boolean isListTitleVisible() {
        return currentUrl().contains(LIST_PATH);
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

    public void openCreateServiceModal() {

        wait.until(driver ->
            !driver.findElements(ADD_SERVICE_CARD).isEmpty()
        );

        waitForPresence(ADD_SERVICE_CARD).click();

        wait.until(driver ->
            !driver.findElements(CREATE_SERVICE_FORM).isEmpty()
        );
    }

    public boolean isCreateServiceFormVisible() {
        return waitForPresence(CREATE_SERVICE_FORM).isDisplayed();
    }

    public void enterServiceName(String name) {
        WebElement input = waitForPresence(SERVICE_NAME_INPUT);
        input.clear();
        input.sendKeys(name);
    }

    public void enterUrlName(String urlName) {
        WebElement input = waitForPresence(URL_NAME_INPUT);
        input.clear();
        input.sendKeys(urlName);
    }

    public void enterUrl(String url) {
        WebElement input = waitForPresence(URL_INPUT);
        input.clear();
        input.sendKeys(url);
    }

    public void submitCreateService() {
        waitForPresence(SUBMIT_SERVICE_BUTTON).click();
    }

    public void enterServiceDescription(String description) {
        WebElement input = waitForPresence(SERVICE_DESCRIPTION_INPUT);
        input.clear();
        if (description != null && !description.isEmpty()) {
            input.sendKeys(description);
        }
    }

    public boolean waitForSystemMessage(String message) {
        return wait.until(driver -> driver.findElement(By.tagName("body")).getText().contains(message));
    }

    public boolean serviceCardContainsText(String serviceName, String text) {
        return findElements(SERVICE_CARDS).stream()
                .filter(card -> card.getText().contains(serviceName))
                .anyMatch(card -> card.getText().contains(text));
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
        return wait.until(driver -> hasServiceNamed(name));
    }

    public boolean hasServiceNamed(String name) {
        return getServiceCards().stream().anyMatch(card -> card.getText().contains(name));
    }

    public boolean isServiceCurrentlyListed(String name) {
        return getDisplayedServiceNamesWithoutWaiting().stream()
                .anyMatch(cardName -> cardName.contains(name));
    }

    public boolean displayedServicesMatchQuery(String query) {
        String normalizedQuery = query.trim().toLowerCase();
        return wait.until(driver -> {
            if (!driver.findElements(EMPTY_SEARCH_MESSAGE).isEmpty()) {
                return true;
            }

            List<String> displayedNames = getDisplayedServiceNamesWithoutWaiting();
            return !displayedNames.isEmpty()
                    && displayedNames.stream()
                        .allMatch(name -> name.toLowerCase().contains(normalizedQuery));
        });
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

    public void openServiceByName(String name) {
        wait.until(driver -> {
            try {
                WebElement card = findElements(SERVICE_CARDS).stream()
                    .filter(c -> c.getText().contains(name))
                    .findFirst()
                    .orElse(null);

                if (card == null) {
                    return false;
                }

                card.click();
                return true;
            } catch (StaleElementReferenceException ignored) {
                return false;
            }
        });
    }

    private List<String> getDisplayedServiceNamesWithoutWaiting() {
        return findElements(SERVICE_CARDS).stream()
                .map(WebElement::getText)
                .map(text -> text.split("\\R", 2)[0].trim())
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }
}
