Feature: Test Cases
    As a user
    I want to navigate test cases
    So that test case information can be validated through E2E automation

    @testcases @list
    Scenario: Test cases list page is visible
        Given the user opens the test cases list page
        Then test cases should be listed

    @testcases @modal
    Scenario: Test case modal opens correctly
        Given the user opens the test cases list page
        When the user clicks the first view button
        Then the test case modal should be visible

    @disabled @testcases @empty
    Scenario: Empty test cases message is visible
        Given the user opens the test cases list page
        Then the empty test cases message should be displayed