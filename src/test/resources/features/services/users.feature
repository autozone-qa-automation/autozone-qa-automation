Feature: Users Management
    As an authenticated user
    I want to navigate to the users section
    So that I can view the registered users list

    @users @list
    Scenario: User successfully navigates to users list
        When the user opens the users list page
        Then the users page layout should be correctly displayed
        And users should be listed