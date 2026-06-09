package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import static org.testng.Assert.fail;

public class ReleasesBot extends BaseBot {
    
    private static final String LIST_PATH = "/releases";
    private static final String RELEASE_CARD_PREFIX = "release-card-" ;

    private static final By RELEASES_PAGE = By.cssSelector("[data-testid='releases-page']");
    private static final By LOADING_STATE = By.cssSelector("[data-testid='releases-loading-state']");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-testid='releases-error-message']");
    
    private static final By RELEASES_SEARCH_INPUT = By.cssSelector("[data-testid='releases-search-input']");
    private static final By RELEASES_SORT_SELECT = By.cssSelector("[data-testid='releases-sort-select']");
    private static final By RELEASES_EMPTY_MESSAGE = By.cssSelector("[data-testid='releases-empty-message']");
    
    private static final By RELEASE_CARDS = By.cssSelector("[data-testid^='release-card-']");
    
    public ReleasesBot(WebDriver driver) {
        super(driver);
    }
    
    public void openList() {
        openPath(LIST_PATH);
    }

    public boolean isPageVisible() {
        return findElements(RELEASES_PAGE).size() > 0;
    }

    public boolean isLoadingVisible() {
        return findElements(LOADING_STATE).size() > 0;
    }

    public boolean isErrorVisible() {
        return findElements(ERROR_MESSAGE).size() > 0;
    }

    public boolean isSearchVisible() {
        return waitForPresence(RELEASES_SEARCH_INPUT).isDisplayed();
    }

    public boolean isEmptyMessageVisible() {
        return findElements(RELEASES_EMPTY_MESSAGE).size() > 0;
    }

    public String getEmptyMessageText() {
        return waitForPresence(RELEASES_EMPTY_MESSAGE).getText();
    }

    public void waitUntilListReady() {
        waitForPresence(RELEASE_CARDS);
        wait.until(webDriver -> !webDriver.findElements(RELEASE_CARDS).isEmpty());
    }

    public List<String> getListedReleaseIds() {
        return getReleaseCards().stream()
                .map(card -> card.getAttribute("data-testid"))
                .filter(id -> id != null && id.startsWith(RELEASE_CARD_PREFIX))
                .map(id -> id.replace(RELEASE_CARD_PREFIX, ""))
                .collect(Collectors.toList());
    }

    public void searchRelease(String text) {
        WebElement input = waitForPresence(RELEASES_SEARCH_INPUT);
        input.clear();
        input.sendKeys(text);
    }

    public boolean isReleaseListed(String name) {
        return hasReleaseNamed(name);
    }

    public boolean hasReleaseNamed(String name) {
        if (isEmptyMessageVisible()) {
            return false;
        }
        return getReleaseCards().stream().anyMatch(card -> card.getText().contains(name));
    }
    
    public void sortBy(String criteria) {
        // Hace clic en el componente Select de Mantine para desplegar opciones
        WebElement selectDropdown = waitForPresence(RELEASES_SORT_SELECT);
        selectDropdown.click();

        // Localiza dinámicamente la opción basándose en el texto ("Newest" u "Oldest")
        By optionLocator = By.xpath("//*[contains(@class, 'mantine-Select-option') and text()='" + criteria + "']");
        WebElement option = waitForPresence(optionLocator);
        option.click();
    }
    
    public void openReleaseDetails(String releaseId) {
        WebElement card = findReleaseCard(releaseId);
        card.click();

        wait.until(driver ->
                driver.getCurrentUrl().endsWith(LIST_PATH + "/" + releaseId)
        );
    }

    private List<WebElement> getReleaseCards() {
        waitUntilListReady();
        return findElements(RELEASE_CARDS);
    }

    private WebElement findReleaseCard(String releaseId) {
        waitUntilListReady();
        return waitForElementOrFail(By.cssSelector("[data-testid='" + RELEASE_CARD_PREFIX + releaseId + "']"), RELEASE_CARD_PREFIX + releaseId);
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