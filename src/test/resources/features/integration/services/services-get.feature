@integration @api @services
Feature: Get a single service
  As an authenticated ADMIN
  I want to retrieve a single service by id
  So that I can confirm the Services API returns the correct service shape and a 404 for unknown ids

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: Get an existing service by id
    Given an existing service from the services list
    When the client sends a GET request for that service by id
    Then the response status code should be 200
    And the response body should match the ServicesVO shape
    And the returned service id should equal the requested id

  Scenario: Get a non-existent service by id
    When the client sends a GET request to "/api/v1/services/999999999"
    Then the response status code should be 404
