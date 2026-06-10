Feature: Test Cases
    As a user
    I want to edit test cases
    So that I can update and validate their information

    @testcases @edit @view
    Scenario: Test Case edit page displays existing data
        Given the user opens the test cases list page
        When the user selects a test case to edit
        Then the test case data should be displayed correctly

    @testcases @edit @success
    Scenario: Edit Test Case successfully
        Given the user opens the test cases list page
        When the user selects a test case to edit
        And the user edits the test case with valid data
        And the user saves the changes
        Then the test case should be updated successfully

    @testcases @edit @validation
    Scenario: Required fields validation when editing
        Given the user opens the test cases list page
        When the user selects a test case to edit
        And the user leaves required fields empty
        Then the system should prevent saving the test case

    @testcases @edit @cancel
    Scenario: Cancel editing without saving changes
        Given the user opens the test cases list page
        When the user selects a test case to edit
        And the user modifies the test case but cancels
        Then the changes should not be saved

    @testcases @create @success
    Scenario: Create Test Case successfully
        Given the user opens the test cases list page
        When the user opens the create test case modal
        And the user fills the create test case form with valid data
        And the user submits the create test case form
        Then the test case should be created successfully

    @testcases @create @validation
    Scenario: Required fields validation when creating a test case
        Given the user opens the test cases list page
        When the user opens the create test case modal
        And the user leaves required create test case fields empty
        And the user submits the create test case form
        Then the system should prevent creating the test case