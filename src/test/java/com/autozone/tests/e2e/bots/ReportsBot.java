package com.autozone.tests.e2e.bots;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ReportsBot extends BaseBot {

    private static final String REPORTS_PATH = "/reports";

    private static final By PAGE_CONTAINER    = By.cssSelector("[data-testid='reports-page-container']");
    private static final By GENERATE_BTN      = By.cssSelector("[data-testid='generate-report-btn']");
    private static final By START_DATE_INPUT  = By.cssSelector("[data-testid='reports-start-date-input']");
    private static final By END_DATE_INPUT    = By.cssSelector("[data-testid='reports-end-date-input']");
    private static final By SERVICE_SELECT    = By.cssSelector("[data-testid='reports-service-select']");
    private static final By TAGS_INPUT        = By.cssSelector("[data-testid='reports-tags-input']");
    private static final By EXPORT_CSV_BTN    = By.cssSelector("[data-testid='export-csv-btn']");
    private static final By ERROR_MESSAGE     = By.cssSelector("[data-testid='reports-error-message']");
    private static final By LOADING_MESSAGE   = By.cssSelector("[data-testid='reports-loading-message']");
    private static final By REPORTS_TABLE     = By.cssSelector("[data-testid='reports-table']");
    private static final By RECORDS_SUMMARY   = By.cssSelector("[data-testid='reports-records-summary']");
    private static final By SELECT_ALL_CHECKBOX = By.cssSelector("[data-testid='reports-select-all-checkbox']");

    private static final By REPORT_ROWS = By.cssSelector("[data-testid^='report-row-checkbox-']");

    public ReportsBot(WebDriver driver) {
        super(driver);
    }

    public void openList() {
        openPath(REPORTS_PATH);
    }

    public void waitUntilPageReady() {
        waitForPresence(PAGE_CONTAINER);
        waitForPresence(GENERATE_BTN);
    }

    public void waitUntilTableReady() {
        waitForPresence(REPORTS_TABLE);
        wait.until(d -> !d.findElements(REPORT_ROWS).isEmpty());
    }

    // --- Visibilidad de elementos de layout ---

    public boolean isPageContainerVisible() {
        return waitForPresence(PAGE_CONTAINER).isDisplayed();
    }

    public boolean isGenerateReportButtonVisible() {
        return waitForPresence(GENERATE_BTN).isDisplayed();
    }

    public boolean isStartDateInputVisible() {
        return waitForPresence(START_DATE_INPUT).isDisplayed();
    }

    public boolean isEndDateInputVisible() {
        return waitForPresence(END_DATE_INPUT).isDisplayed();
    }

    public boolean isServiceSelectVisible() {
        return waitForPresence(SERVICE_SELECT).isDisplayed();
    }

    public boolean isTagsInputVisible() {
        return waitForPresence(TAGS_INPUT).isDisplayed();
    }

    public boolean isExportCsvButtonVisible() {
        return !findElements(EXPORT_CSV_BTN).isEmpty();
    }

    public boolean isTableVisible() {
        return !findElements(REPORTS_TABLE).isEmpty();
    }

    public boolean isLoadingVisible() {
        return !findElements(LOADING_MESSAGE).isEmpty();
    }

    public boolean isErrorVisible() {
        return !findElements(ERROR_MESSAGE).isEmpty();
    }

    public boolean isRecordsSummaryVisible() {
        return !findElements(RECORDS_SUMMARY).isEmpty();
    }

    public boolean isSelectAllCheckboxVisible() {
        return waitForPresence(SELECT_ALL_CHECKBOX).isDisplayed();
    }

    // --- Acciones generales ---

    public void clickGenerateReport() {
        waitForPresence(GENERATE_BTN).click();
    }

    // --- Filtros ---

    public void selectService(String serviceName) {
        waitForPresence(SERVICE_SELECT).click();
        By option = By.xpath(
            "//*[contains(@class,'mantine-Select-option') and normalize-space()='" + serviceName + "']"
        );
        waitForPresence(option).click();
    }

    public void clearServiceFilter() {
        By clearBtn = By.cssSelector("[data-testid='reports-service-select'] [aria-label='Clear']");
        List<WebElement> buttons = findElements(clearBtn);
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        }
    }

    public void enterTags(String tags) {
        WebElement input = waitForPresence(TAGS_INPUT);
        input.clear();
        input.sendKeys(tags);
    }

    // Retorna el nombre del servicio de la primera fila visible en la tabla.
    public String getFirstAvailableServiceName() {
        List<String> ids = getVisibleReleaseIds();
        if (ids.isEmpty()) return null;
        return getReleaseService(ids.get(0));
    }

    // --- Selección de filas y exportación ---

    public void clickRowCheckbox(int index) {
        By locator = By.cssSelector("[data-testid='report-row-checkbox-" + index + "']");
        waitForPresence(locator).click();
    }

    public void clickSelectAll() {
        waitForPresence(SELECT_ALL_CHECKBOX).click();
    }

    public void clickExportCsv() {
        waitForPresence(EXPORT_CSV_BTN).click();
    }

    public boolean isExportCsvButtonDisabled() {
        WebElement btn = waitForPresence(EXPORT_CSV_BTN);
        return btn.getAttribute("disabled") != null
            || "true".equals(btn.getAttribute("aria-disabled"))
            || btn.getAttribute("data-disabled") != null;
    }

    // --- Lectura de datos de la tabla ---

    public String getRecordsSummaryText() {
        return waitForPresence(RECORDS_SUMMARY).getText();
    }

    public int getRowCount() {
        return findElements(REPORT_ROWS).size();
    }

    public boolean isRowCheckboxVisible(int index) {
        By locator = By.cssSelector("[data-testid='report-row-checkbox-" + index + "']");
        return findElements(locator).size() > 0;
    }

    public String getReleaseName(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-name-cell-" + releaseId + "']")).getText();
    }

    public String getReleaseVersion(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-version-cell-" + releaseId + "']")).getText();
    }

    public String getReleaseService(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-service-cell-" + releaseId + "']")).getText();
    }

    public String getReleaseObjective(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-objective-cell-" + releaseId + "']")).getText();
    }

    public String getReleaseTags(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-tags-cell-" + releaseId + "']")).getText();
    }

    public String getReleaseLaunchDate(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-launch-date-cell-" + releaseId + "']")).getText();
    }

    public String getReleaseCreationDate(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-creation-date-cell-" + releaseId + "']")).getText();
    }

    public String getReleaseStatus(String releaseId) {
        return waitForPresence(By.cssSelector("[data-testid='report-status-badge-" + releaseId + "']")).getText();
    }

    public boolean hasServiceLink(String releaseId, String serviceId) {
        By locator = By.cssSelector("[data-testid='report-service-link-" + releaseId + "-" + serviceId + "']");
        return findElements(locator).size() > 0;
    }

    public boolean hasNoServicesLabel(String releaseId) {
        By locator = By.cssSelector("[data-testid='report-no-services-" + releaseId + "']");
        return findElements(locator).size() > 0;
    }

    // Devuelve los releaseIds visibles en la tabla leyendo los data-testid de las celdas de nombre
    public List<String> getVisibleReleaseIds() {
        return findElements(By.cssSelector("[data-testid^='report-name-cell-']"))
                .stream()
                .map(el -> el.getAttribute("data-testid"))
                .map(id -> id.replace("report-name-cell-", ""))
                .collect(Collectors.toList());
    }
}
