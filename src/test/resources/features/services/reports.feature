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