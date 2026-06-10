Feature: Service edit
    As a user
    I want to update the details of an existing service
    So that the service information is saved and displayed correctly

    @services @edit @success
    Scenario: Successful update of service name and description
        Given the user navigates to service details page for 2
        When the user opens the service edit modal
        And the user updates the service name to "QA Automation Service"
        And the user updates the service description to "Updated by automation test"
        And the user saves the service changes
        Then the service edit success message should be displayed
        And the service edit modal should be closed
        And the user restores the original service name and description

    @services @edit @validation
    Scenario: Service update fails when name is empty
        Given the user navigates to service details page for 2
        When the user opens the service edit modal
        And the user clears the service name field
        And the user saves the service changes
        Then the service edit should not be processed

    @services @edit @cancel
    Scenario: Service edit modal closes when cancelled
        Given the user navigates to service details page for 2
        When the user opens the service edit modal
        And the user updates the service name to "This should not be saved"
        And the user cancels the service edit
        Then the service edit modal should be closed
