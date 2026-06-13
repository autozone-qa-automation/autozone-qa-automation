package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import static org.testng.Assert.fail;

public class ReleasesBot extends BaseBot {
    
    private static final String RELEASES_PATH = "/releases";
    private static final String RELEASE_CARD_PREFIX = "release-card-";
    private static final By RELEASE_DETAIL_MODAL = By.cssSelector("[data-testid='release-id-page']");
    private static final String CLICKABLE_RELEASE_CARD_SELECTOR = "[data-testid='release-card']";

    private static final By RELEASES_PAGE = By.cssSelector("[data-testid='releases-page']");
    private static final By LOADING_STATE = By.cssSelector("[data-testid='releases-loading-state']");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-testid='releases-error-message']");
    
    private static final By RELEASES_SEARCH_INPUT = By.cssSelector("[data-testid='releases-search-input']");
    private static final By RELEASES_SORT_SELECT = By.cssSelector("[data-testid='releases-sort-select']");
    private static final By RELEASES_SORT_OPTION_NEWEST = By.cssSelector("[data-testid='releases-sort-option-newest']");
    private static final By RELEASES_SORT_OPTION_OLDEST = By.cssSelector("[data-testid='releases-sort-option-oldest']");
    private static final By RELEASES_EMPTY_MESSAGE = By.cssSelector("[data-testid='releases-empty-message']");
    
    private static final By RELEASE_CARDS_ALL = By.cssSelector("[data-testid^='release-card-']");
    private static final By CREATE_OPEN_BUTTON = By.cssSelector("[data-testid='release-create-open-btn']");
    private static final By DRAFT_FILTER_BUTTON = By.cssSelector("[data-testid='releases-filter-button-draft']");
    
    public ReleasesBot(WebDriver driver) {
        super(driver);
    }
    
    public void openList() {
        openPath(RELEASES_PATH);
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

    /**
     * Espera a que la página esté lista utilizando el botón de creación 
     * (un elemento estático y seguro que no se ve afectado si la lista viene vacía).
     */
    public void waitUntilListReady() {
        waitForPresence(CREATE_OPEN_BUTTON);
    }

    public void filterByDraft() {
        waitForPresence(DRAFT_FILTER_BUTTON).click();
    }

    public void filterByStatus(String status) {
        By filterButton = By.cssSelector("[data-testid='releases-filter-button-" + status.toLowerCase() + "']");
        waitForPresence(filterButton).click();
    }

    public String openFirstRelease() {
        List<WebElement> cards = waitForAllPresent(RELEASE_CARDS_ALL);
        WebElement firstCard = cards.get(0).findElement(By.cssSelector(CLICKABLE_RELEASE_CARD_SELECTOR));
        String title = firstCard.getText().split("\n")[0].trim();
        firstCard.click();
        waitForPresence(RELEASE_DETAIL_MODAL);
        return title;
    }

    public boolean waitUntilReleaseDisappears(String name) {
        return wait.until(driver -> !isReleaseListed(name));
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
        if (isEmptyMessageVisible()) {
            return false;
        }
        return getReleaseCards().stream().anyMatch(card -> card.getText().contains(name));
    }
    
    public void sortBy(String criteria) {
        WebElement selectDropdown = waitForPresence(RELEASES_SORT_SELECT);
        selectDropdown.click();

        By optionLocator = switch (criteria.trim().toLowerCase()) {
            case "newest" -> RELEASES_SORT_OPTION_NEWEST;
            case "oldest" -> RELEASES_SORT_OPTION_OLDEST;
            default -> throw new IllegalArgumentException("Unsupported release sort criteria: " + criteria);
        };
        WebElement option = waitForPresence(optionLocator);
        option.click();
    }
    
    public void openReleaseDetails(String releaseId) {
        WebElement card = findReleaseCard(releaseId);
        card.click();
        waitForPresence(RELEASE_DETAIL_MODAL);
    }

    private List<WebElement> getReleaseCards() {
        return findElements(RELEASE_CARDS_ALL);
    }

    private WebElement findReleaseCard(String releaseId) {
        return waitForElementOrFail(
                By.cssSelector(
                        "[data-testid='" + RELEASE_CARD_PREFIX + releaseId + "'] "
                                + CLICKABLE_RELEASE_CARD_SELECTOR
                ),
                RELEASE_CARD_PREFIX + releaseId
        );
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
