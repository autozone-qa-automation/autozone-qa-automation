Feature: Users Management
  As an authenticated user
  I want to navigate to the users section
  So that I can view and filter the registered users list

  # ST-US-10
  @users @list
  Scenario: ST-US-10 - Successful visualization of the global users list
    When the user navigates to the users management section
    Then the users table should be visible
    And the users list should contain at least one user

  # ST-US-11
  @users @list @filter
  Scenario Outline: ST-US-11 - Successful visualization of users list filtered by role
    When the user navigates to the users management section
    And the user filters the users list by role "<role>"
    Then the users list should be filtered by role "<role>"

    Examples:
      | role      |
      | ADMIN     |
      | DEV       |
      | READ_ONLY |