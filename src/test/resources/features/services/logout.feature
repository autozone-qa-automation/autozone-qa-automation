@noAutoLogin
Feature: Logout

    As an authenticated user
    I want to log out of the application
    So that my session is terminated securely

    @logout @security
    Scenario: TC-07 Successful Logout
        Given the user logs in successfully
        When the user clicks the "Log Out" button
        And the user attempts to return to the previous page using the browser back button
        Then the session is terminated
        And the system redirects the user to the login page
        And access to the dashboard is denied until the user logs in again