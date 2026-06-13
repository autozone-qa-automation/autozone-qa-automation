Feature: Services
    As a user
    I want to navigate services screens
    So that services information can be validated through E2E automation

  @services @list
  Scenario: Services list page is visible
    Given the user opens the services list page
    Then the services page layout should be correctly displayed
    And services should be listed
    And the service details should be visible when a service is clicked

  @services @search
  Scenario: Search for a service
    Given the user opens the services list page to search for a service
    When the user searches for "As"
    Then the services displayed should contain "As"

  @services @details
  Scenario: Service details page is visible
    Given the user opens the service details page for 1
    Then the service details page layout should be correctly displayed
    And the service features should be listed
    And the last releases section should be displayed
    And the feature details should be visible when a feature is clicked

  @services @details @invalid
  Scenario: Service details page for invalid service
    Given the user opens the service details page for 999 invalid service
    Then the service not found message should be displayed

  @services @create
  Scenario: Create service successfully
    Given the user opens the services list page
    When the user opens the create service modal
    And the user enters service name "Authentication API"
    And the user enters service description "Maneja el inicio de sesión y los tokens."
    And the user enters URL name "Repository"
    And the user enters URL "https://github.com/example/auth-api"
    And the user saves the service
    Then the service "Authentication API" should be displayed in the list

  @services @create @duplicate
  Scenario: Prevent duplicate service creation (case-insensitive)
    Given the user opens the services list page
    When the user opens the create service modal
    And the user enters service name "Catalog Sync Service"
    And the user enters service description "Service created for testing duplicates"
    And the user enters URL name "Repository"
    And the user enters URL "https://github.com/example/catalog-sync"
    And the user saves the service
    And the user opens the create service modal
    And the user enters service name "catalog sync SERVICE"
    And the user enters service description "Duplicate attempt"
    And the user enters URL name "Repository"
    And the user enters URL "https://github.com/example/catalog-sync-dup"
    And the user saves the service
    Then the service system displays "already exists"

  @services @create
  Scenario: Create service with empty description uses default value
    Given the user opens the services list page
    When the user opens the create service modal
    And the user enters service name "Payment Gateway"
    And the user leaves service description empty
    And the user enters URL name "Repository"
    And the user enters URL "https://github.com/example/payment"
    And the user saves the service
    Then the service "Payment Gateway" should be displayed in the list
    And the service description default message is shown

  @services @create @trim
  Scenario: Service name is trimmed automatically
    Given the user opens the services list page
    When the user opens the create service modal
    And the user enters service name "   Order Processing   "
    And the user enters service description "Procesa las órdenes."
    And the user enters URL name "Repository"
    And the user enters URL "https://github.com/example/orders"
    And the user saves the service
    Then the service "Order Processing" should be displayed in the list

  @services @create @validation
  Scenario: Prevent creating a service with invalid data
    Given the user opens the services list page
    When the user opens the create service modal
    And the user leaves the service name empty
    And the user enters service description "Servicio de prueba"
    And the user enters URL name "Repository"
    And the user enters URL "https://github.com/example/test"
    And the user saves the service
    Then the service system displays "Name must be at least 2 characters"
    And the service "Servicio de prueba" should not be displayed in the list
    
  @services @delete
  Scenario: Delete service
      Given the user opens an existing service
      When the user deletes the service
      Then the user should be redirected to the services list page

  @services @delete @cancel
  Scenario: Cancel service deletion
    Given the user opens an existing service
    When the user cancels the service deletion
    Then the service should not be deleted
    And the user should remain on the service details page
