package com.autozone.tests.e2e.cucumber.reports;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.autozone.tests.e2e.bots.ReportsBot;
import com.autozone.tests.e2e.cucumber.CucumberScenarioContext;
import com.autozone.tests.e2e.support.CsvHelper;
import com.autozone.tests.e2e.support.DriverFactory;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReportsStepDefinitions {

    private static final List<String> REQUIRED_HEADERS = List.of(
        "Versión del release",
        "Nombre del release",
        "Tags del release",
        "Estatus",
        "Fecha de creación",
        "Fecha de lanzamiento",
        "Servicio",
        "Feature",
        "Caso de prueba"
    );

    // Used to carry the selected service name across steps within the same scenario.
    private String filteredServiceName;

    @Given("the user opens the reports page")
    public void theUserOpensTheReportsPage() {
        CucumberScenarioContext.getReportsBot().openList();
        CucumberScenarioContext.getReportsBot().waitUntilPageReady();
    }

    @Then("the reports page container should be visible")
    public void theReportsPageContainerShouldBeVisible() {
        assertTrue(
            CucumberScenarioContext.getReportsBot().isPageContainerVisible(),
            "Expected reports page container to be visible"
        );
    }

    @And("the generate report button should be visible")
    public void theGenerateReportButtonShouldBeVisible() {
        assertTrue(
            CucumberScenarioContext.getReportsBot().isGenerateReportButtonVisible(),
            "Expected generate report button to be visible"
        );
    }

    @And("the filter form fields should be visible")
    public void theFilterFormFieldsShouldBeVisible() {
        ReportsBot bot = CucumberScenarioContext.getReportsBot();
        assertTrue(bot.isStartDateInputVisible(), "Expected start date input to be visible");
        assertTrue(bot.isEndDateInputVisible(), "Expected end date input to be visible");
        assertTrue(bot.isServiceSelectVisible(), "Expected service select to be visible");
        assertTrue(bot.isTagsInputVisible(), "Expected tags input to be visible");
    }

    @When("the user clicks generate report")
    public void theUserClicksGenerateReport() {
        CucumberScenarioContext.getReportsBot().clickGenerateReport();
        CucumberScenarioContext.getReportsBot().waitUntilTableReady();
    }

    @Then("the reports table should be visible")
    public void theReportsTableShouldBeVisible() {
        assertTrue(
            CucumberScenarioContext.getReportsBot().isTableVisible(),
            "Expected reports table to be visible"
        );
    }

    @And("the select all checkbox should be visible")
    public void theSelectAllCheckboxShouldBeVisible() {
        assertTrue(
            CucumberScenarioContext.getReportsBot().isSelectAllCheckboxVisible(),
            "Expected select-all checkbox in table header to be visible"
        );
    }

    @And("at least one report row should be displayed")
    public void atLeastOneReportRowShouldBeDisplayed() {
        assertTrue(
            CucumberScenarioContext.getReportsBot().getRowCount() > 0,
            "Expected at least one report row in the table"
        );
    }

    @And("the records summary should be visible")
    public void theRecordsSummaryShouldBeVisible() {
        assertTrue(
            CucumberScenarioContext.getReportsBot().isRecordsSummaryVisible(),
            "Expected records summary (Showing X of Y records) to be visible"
        );
    }

    @Then("each report row has a checkbox visible by index")
    public void eachReportRowHasACheckboxVisibleByIndex() {
        int rowCount = CucumberScenarioContext.getReportsBot().getRowCount();
        assertTrue(rowCount > 0, "Expected at least one row to validate checkboxes");
        for (int i = 0; i < rowCount; i++) {
            assertTrue(
                CucumberScenarioContext.getReportsBot().isRowCheckboxVisible(i),
                "Expected checkbox to be visible for row index " + i
            );
        }
    }

    @And("each report row has name, version, service, objective, tags, dates and status cells")
    public void eachReportRowHasAllRequiredCells() {
        ReportsBot bot = CucumberScenarioContext.getReportsBot();
        List<String> releaseIds = bot.getVisibleReleaseIds();

        assertTrue(!releaseIds.isEmpty(), "Expected at least one release in the table");

        for (String releaseId : releaseIds) {
            assertTrue(bot.getReleaseName(releaseId) != null,
                "Expected name cell for release " + releaseId);
            assertTrue(bot.getReleaseVersion(releaseId) != null,
                "Expected version cell for release " + releaseId);
            assertTrue(bot.getReleaseService(releaseId) != null,
                "Expected service cell for release " + releaseId);
            assertTrue(bot.getReleaseObjective(releaseId) != null,
                "Expected objective cell for release " + releaseId);
            assertTrue(bot.getReleaseTags(releaseId) != null,
                "Expected tags cell for release " + releaseId);
            assertTrue(bot.getReleaseLaunchDate(releaseId) != null,
                "Expected launch date cell for release " + releaseId);
            assertTrue(bot.getReleaseCreationDate(releaseId) != null,
                "Expected creation date cell for release " + releaseId);
            assertTrue(bot.getReleaseStatus(releaseId) != null,
                "Expected status badge for release " + releaseId);
        }
    }

    @And("the export CSV button should be visible")
    public void theExportCsvButtonShouldBeVisible() {
        assertTrue(
            CucumberScenarioContext.getReportsBot().isExportCsvButtonVisible(),
            "Expected export CSV button to be visible"
        );
    }

    // ── ST-RP-01: Filtrado ────────────────────────────────────────────────────

    @And("the user filters by the service of the first available result")
    public void theUserFiltersByServiceOfFirstResult() {
        filteredServiceName = CucumberScenarioContext.getReportsBot()
                .selectFirstAvailableServiceFromDropdown();
        assertFalse(
            filteredServiceName == null || filteredServiceName.isBlank(),
            "Expected at least one service option in the filter dropdown"
        );
    }

    @Then("all visible results belong to the filtered service")
    public void allVisibleResultsBelongToFilteredService() {
        ReportsBot bot = CucumberScenarioContext.getReportsBot();
        List<String> releaseIds = bot.getVisibleReleaseIds();
        assertTrue(!releaseIds.isEmpty(), "Expected at least one result after filtering");

        for (String id : releaseIds) {
            String service = bot.getReleaseService(id);
            assertTrue(
                service.contains(filteredServiceName),
                "Expected service '" + filteredServiceName + "' but found '" + service + "' for release " + id
            );
        }
    }

    // ── ST-RP-02, ST-RP-03, ST-RP-04, ST-RP-05: CSV Export ──────────────────

    @And("the user selects the first report row checkbox")
    public void theUserSelectsTheFirstRowCheckbox() {
        CucumberScenarioContext.getReportsBot().clickRowCheckbox(0);
    }

    @And("the user selects all report rows")
    public void theUserSelectsAllReportRows() {
        CucumberScenarioContext.getReportsBot().clickSelectAll();
    }

    @And("the user does not select any release")
    public void theUserDoesNotSelectAnyRelease() {
        // No action — intentionally leave all checkboxes unchecked.
    }

    @And("the user triggers the CSV export")
    public void theUserTriggersTheCsvExport() {
        CsvHelper.deleteAllCsvFiles(DriverFactory.DOWNLOAD_DIR);
        CucumberScenarioContext.getReportsBot().clickExportCsv();
    }

    @Then("a CSV file should be downloaded to the configured directory")
    public void aCsvFileShouldBeDownloaded() {
        File csv = CsvHelper.waitForCsvFile(DriverFactory.DOWNLOAD_DIR);
        assertTrue(csv.exists() && csv.length() > 0, "Expected a non-empty CSV file to be downloaded");
    }

    @Then("the CSV file should contain the required headers")
    public void theCsvFileShouldContainRequiredHeaders() throws IOException {
        File csv = CsvHelper.waitForCsvFile(DriverFactory.DOWNLOAD_DIR);
        List<String> headers = CsvHelper.readHeaders(csv);

        for (String expected : REQUIRED_HEADERS) {
            assertTrue(
                headers.stream().anyMatch(h -> h.equalsIgnoreCase(expected)),
                "Expected CSV header '" + expected + "' not found. Actual headers: " + headers
            );
        }
    }

    @Then("the CSV file should contain multiple release records")
    public void theCsvFileShouldContainMultipleReleaseRecords() throws IOException {
        File csv = CsvHelper.waitForCsvFile(DriverFactory.DOWNLOAD_DIR);
        List<String> dataRows = CsvHelper.readDataRows(csv);
        assertTrue(
            dataRows.size() > 1,
            "Expected multiple release records in CSV, but found: " + dataRows.size()
        );
    }

    @Then("the CSV file should include service, feature and test case columns")
    public void theCsvFileShouldIncludeServiceFeatureTestCaseColumns() throws IOException {
        File csv = CsvHelper.waitForCsvFile(DriverFactory.DOWNLOAD_DIR);
        List<String> headers = CsvHelper.readHeaders(csv);

        List<String> serviceColumns = List.of("Servicio", "Feature", "Caso de prueba");
        for (String col : serviceColumns) {
            assertTrue(
                headers.stream().anyMatch(h -> h.equalsIgnoreCase(col)),
                "Expected column '" + col + "' in CSV headers. Actual: " + headers
            );
        }
    }

    // ── ST-RP-06: Sin selección ───────────────────────────────────────────────

    @Then("the system should not download any CSV file")
    public void theSystemShouldNotDownloadAnyCsvFile() {
        assertTrue(
            CsvHelper.noCsvDownloaded(DriverFactory.DOWNLOAD_DIR),
            "Expected no CSV file to be downloaded when no release is selected"
        );
    }
}