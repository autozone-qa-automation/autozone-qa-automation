Feature: User edit
    As an administrator
    I want to update the details of an existing user
    So that the user information is saved and displayed correctly

    @users @edit @success
    Scenario: Successful update of user name and last name
        Given the user opens the users list page
        When the user opens the edit modal for the first user
        And the user updates the user name to "QA"
        And the user updates the user last name to "Automation"
        And the user saves the user changes
        Then the user success message should be displayed
        And the user restores the original name and last name

    @users @edit @validation
    Scenario: User update fails when the name is empty
        Given the user opens the users list page
        And the user opens the edit modal for the first user
        And the user clears the user name field
        When the user saves the user changes
        Then the user update should not be processed
