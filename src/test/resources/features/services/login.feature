@noAutoLogin
Feature: Login
    As a registered user
    I want to log in and log out
    So that system access can be validated through E2E automation

    @login @success
    Scenario: TC-01 Successful Login
        Given the user is on the login page
        When the user enters a valid username
        And the user enters a valid password
        And the user clicks the "Log In" button
        Then an active session is created for the user
        And the system redirects the user to the dashboard or main page
        And no error messages are displayed

    @login @invalid-password
    Scenario: TC-02 Incorrect Password
        Given the user is on the login page
        When the user enters a valid username
        And the user enters an invalid password
        And the user clicks the "Log In" button
        Then no session is created
        And the user remains on the login page
        And the system displays "Invalid username or password."

    @login @unregistered-user
    Scenario: TC-03 Unregistered User
        Given the user is on the login page
        When the user enters a non-existent username
        And the user enters any password
        And the user clicks the "Log In" button
        Then no session is created
        And the user remains on the login page
        And the system displays an invalid credentials message

    @login @validation @empty-fields
    Scenario: TC-04 Empty Fields
        Given the user is on the login page
        When the user leaves the username field empty
        And the user leaves the password field empty
        And the user clicks the "Log In" button
        Then the login request is not submitted to the server
        And the system displays "Required field" validation messages

    @login @validation @empty-username
    Scenario: TC-05 Empty Username Field
        Given the user is on the login page
        When the user leaves the username field empty
        And the user enters a valid password
        And the user clicks the "Log In" button
        Then the login process is not completed
        And the system displays a message indicating the username is required

    @login @validation @empty-password
    Scenario: TC-06 Empty Password Field
        Given the user is on the login page
        When the user enters a valid username
        And the user leaves the password field empty
        And the user clicks the "Log In" button
        Then the login process is not completed
        And the system displays a message indicating the password is required

    @logout @security
    Scenario: TC-07 Successful Logout
        Given the user logs in successfully
        When the user clicks the "Log Out" button
        And the user attempts to return to the previous page using the browser back button
        Then the session is terminated
        And the system redirects the user to the login page
        And access to the dashboard is denied until the user logs in again

    @login @validation @invalid-email
    Scenario: TC-08 Invalid Email Format
        Given the user is on the login page
        When the user enters an invalid email format
        And the user enters any password
        And the user clicks the "Log In" button
        Then the login request is not submitted to the server
        And the system displays "Invalid email address"

    @login @inactive-account
    Scenario: TC-09 Inactive Account
        Given the user is on the login page
        When the user enters an inactive account username
        And the user enters the inactive account password
        And the user clicks the "Log In" button
        Then no session is created
        And the user remains on the login page
        And the system displays an inactive account message

    @login @session
    Scenario: TC-10 Authenticated User Opens Login Page
        Given the user logs in successfully
        When the authenticated user opens the login page
        Then the system redirects the user to the dashboard or main page
        And an active session is created for the user

    @login @security @protected-route
    Scenario: TC-11 Protected Route Without Session
        Given the user has no active session
        When the user opens a protected page
        Then the system redirects the user to the login page
        And no session is created
