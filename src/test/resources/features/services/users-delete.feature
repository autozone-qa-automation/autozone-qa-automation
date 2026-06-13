Feature: User delete
    As an administrator
    I want to delete a user
    So that the user is removed and is no longer displayed in the users list

    @users @delete @success
    Scenario: Successful delete of a user
        Given the user opens the users list page
        And the user creates a new user named "QA Delete" for the delete test
        When the user opens the delete modal for "QA Delete"
        And the user confirms the user deletion
        Then the user deletion success message should be displayed
        And the deleted user should no longer be listed

    @users @delete @validation
    Scenario: Canceled delete of a user
        Given the user opens the users list page
        And the user creates a new user named "QA Cancel" for the delete test
        When the user opens the delete modal for "QA Cancel"
        And the user clicks Cancel on the delete modal
        Then the delete modal is closed
        And the user "QA Cancel" should still be listed
