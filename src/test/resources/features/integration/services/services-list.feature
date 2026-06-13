@integration @api @services
Feature: List services
  As an authenticated ADMIN
  I want to retrieve the list of registered services
  So that I can confirm the Services API exposes all services with their URLs

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: ADMIN retrieves the list of services
    When the client sends a GET request to "/api/v1/services"
    Then the response status code should be 200
    And the response content type should be "application/json"
    And the response body should be a JSON array
    And each service in the response should have an "id", a "name", an "isActive" flag and a "urls" array
