Feature: Test Case Deletion
    As a user
    I want to delete existing test cases
    So that I can keep the test case board clean and up-to-date

    @testcases @delete @success @ST-TC-10
    Scenario: ST-TC-10 - Eliminación exitosa de un Test Case
        Given the user is on the test cases page
        And there is at least one test case on the board
        When the user clicks delete on a test case
        Then a confirmation message to delete the test case should be displayed
        When the user confirms the deletion
        Then the test case should be deleted successfully
        And the test case should no longer appear on the board
        And a success message should be shown

    @testcases @delete @cancel @ST-TC-11
    Scenario: ST-TC-11 - Cancelación de eliminación de un Test Case
        Given the user is on the test cases page
        And there is at least one test case on the board
        When the user clicks delete on a test case
        Then a confirmation message to delete the test case should be displayed
        When the user cancels the deletion
        Then the deletion operation should be cancelled
        And the test case should continue to be visible on the board