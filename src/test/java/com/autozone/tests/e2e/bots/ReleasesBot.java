package com.autozone.tests.e2e.bots;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ReleasesBot extends BaseBot {

    private static final String RELEASES_PATH = "/releases";

    private static final By RELEASE_CARD = By.cssSelector("[data-testid='release-card']");
    private static final By CREATE_OPEN_BUTTON = By.cssSelector("[data-testid='release-create-open-btn']");
    private static final By DRAFT_FILTER_BUTTON = By.xpath("//button[normalize-space(.)='Draft']");

    public ReleasesBot(WebDriver driver) {
        super(driver);
    }

    public void openList() {
        openPath(RELEASES_PATH);
    }

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
        List<WebElement> cards = waitForAllPresent(RELEASE_CARD);
        WebElement firstCard = cards.get(0);
        String title = firstCard.getText().split("\n")[0].trim();
        firstCard.click();
        return title;
    }

    public boolean isReleaseListed(String name) {
        return findElements(RELEASE_CARD).stream()
                .anyMatch(card -> card.getText().contains(name));
    }

    public boolean waitUntilReleaseDisappears(String name) {
        return wait.until(driver -> !isReleaseListed(name));
    }
}
