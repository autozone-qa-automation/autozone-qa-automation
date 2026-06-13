@integration @api @services
Feature: Services creation validation
  As an authenticated ADMIN
  I want the Services API to reject invalid service creation requests
  So that bad data never reaches the database

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: Creating a service with a null name is rejected
    When the client creates a service with a null name
    Then the response status code should be 400

  Scenario: Creating a service with a duplicate name is rejected
    Given an existing service from the services list
    When the client creates a service using that existing service's name
    Then the response status code should be 409
