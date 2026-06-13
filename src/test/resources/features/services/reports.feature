Feature: Reports
    As an authenticated user
    I want to navigate to the reports section
    So that I can generate and read release reports

    @reports @layout
    Scenario: Reports page layout is correctly displayed
        Given the user opens the reports page
        Then the reports page container should be visible
        And the generate report button should be visible
        And the filter form fields should be visible

    @reports @read
    Scenario: User generates a report and the table is displayed
        Given the user opens the reports page
        When the user clicks generate report
        Then the reports table should be visible
        And the select all checkbox should be visible
        And at least one report row should be displayed
        And the records summary should be visible

    @reports @read
    Scenario: Each report row shows the required data cells
        Given the user opens the reports page
        When the user clicks generate report
        Then each report row has a checkbox visible by index
        And each report row has name, version, service, objective, tags, dates and status cells

    @reports @export
    Scenario: Export CSV button is visible after generating a report
        Given the user opens the reports page
        When the user clicks generate report
        Then the export CSV button should be visible

    @reports @filter
    Scenario: ST-RP-01 - Filtering reports by service shows only matching results
        Given the user opens the reports page
        When the user clicks generate report
        And the user filters by the service of the first available result
        And the user clicks generate report
        Then the reports table should be visible
        And all visible results belong to the filtered service

    @reports @export @csv
    Scenario: ST-RP-02 - Export CSV with selected releases downloads a file
        Given the user opens the reports page
        When the user clicks generate report
        And the user selects the first report row checkbox
        And the user triggers the CSV export
        Then a CSV file should be downloaded to the configured directory

    @reports @export @csv
    Scenario: ST-RP-03 - Downloaded CSV file contains the required headers
        Given the user opens the reports page
        When the user clicks generate report
        And the user selects the first report row checkbox
        And the user triggers the CSV export
        Then the CSV file should contain the required headers

    @reports @export @csv
    Scenario: ST-RP-04 - CSV with multiple selected releases contains all of them
        Given the user opens the reports page
        When the user clicks generate report
        And the user selects all report rows
        And the user triggers the CSV export
        Then the CSV file should contain multiple release records

    @reports @export @csv
    Scenario: ST-RP-05 - CSV contains service, feature and test case columns
        Given the user opens the reports page
        When the user clicks generate report
        And the user selects the first report row checkbox
        And the user triggers the CSV export
        Then the CSV file should include service, feature and test case columns

    @reports @export @csv
    Scenario: ST-RP-06 - Export without selecting releases does not download a file
        Given the user opens the reports page
        When the user clicks generate report
        And the user does not select any release
        And the user triggers the CSV export
        Then the system should not download any CSV file