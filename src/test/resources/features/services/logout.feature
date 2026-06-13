Feature: Gestión de Sesión y Logout

  @ST-US-14
  Scenario: Logout despues de login
    Given the user logs in successfully
    When the user logs out from the application
    Then the user session must be closed completely
    And the user is redirected to the login screen
    And the user cannot access protected routes without a new session