Feature: Test Case Deletion
    As a user
    I want to delete existing test cases
    So that I can keep the test case board clean and up-to-date

    @testcases @delete @success @ST-TC-17
    Scenario: ST-TC-17 - Successful deletion of a Test Case
        Given the user is on the test cases page
        And there is at least one test case on the board
        When the user clicks the delete button for a specific test case
        Then a confirmation message to delete the test case should be displayed
        When the user clicks the "Confirm Delete" option on the modal
        Then the test case should be deleted successfully
        And the test case should no longer appear on the board
        And a deletion confirmation message should be shown

    @testcases @delete @cancel @ST-TC-18
    Scenario: ST-TC-18 - Cancel deletion of a Test Case
        Given the user is on the test cases page
        And there is at least one test case on the board
        When the user clicks the delete button for a specific test case
        Then a confirmation message to delete the test case should be displayed
        When the user clicks the "Cancel" option on the modal
        Then the deletion operation should be cancelled
        And the test case should remain registered in the system
        And the test case should continue to be visible on the board

    @testcases @delete @error @ST-TC-19
    Scenario: ST-TC-19 - Attempt to delete a non-existent Test Case
        Given the user is on the test cases page
        When the user attempts to delete a test case using an invalid or outdated reference
        Then a message should indicate that the test case does not exist or was already deleted
        And no records should be deleted
        And the user should remain on the test cases page

    @testcases @delete @validation @ST-TC-20
    Scenario: ST-TC-20 - Visual validation after deleting a Test Case
        Given the user is on the test cases page
        And there is at least one test case on the board
        When the user successfully deletes an existing test case
        Then the test cases board should update automatically
        And the deleted test case should no longer appear on the board
        And the test cases list should update correctly without reloading the page