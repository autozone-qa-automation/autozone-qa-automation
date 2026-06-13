Feature: User creation
    As an administrator
    I want to create new users
    So that they can access the system with appropriate roles

    @users @create @success
    Scenario: Successful creation of a new user
        Given the user opens the users list page
        And the user opens the create user form
        When the user fills the create user form with name "Ana", last name "Gómez", email "anagomez@test.com", password "PasswordSeguro123", and role "Usuario Estándar"
        And the user submits the create user form
        Then the user should see a creation success notification
        And the modal should close
        And the new user should be reflected in the users list

    @users @create @validation
    Scenario: Required fields empty when creating a user
        Given the user opens the users list page
        And the user opens the create user form
        When the user submits the create user form
        Then the user should see validation errors for empty required fields
        And the modal should remain open

    @users @create @validation
    Scenario: Invalid email format validation
        Given the user opens the users list page
        And the user opens the create user form
        When the user fills the create user form with valid data
        And the user sets the email to "correosinroba"
        And the user submits the create user form
        Then the user should see an invalid email format error message
