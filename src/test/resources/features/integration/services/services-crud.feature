@integration @api @services
Feature: Services CRUD lifecycle
  As an authenticated ADMIN
  I want to create, read, update and delete a service
  So that I can confirm the full Services API lifecycle works end to end

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: Create, read, update and delete a service
    When the client creates a new service with a unique name
    Then the response status code should be 201
    And the response should echo the created service name
    And the response should include a generated service id

    When the client retrieves the created service by id
    Then the response status code should be 200
    And the response body should match the created service data

    When the client updates the created service with new name, description and urls
    Then the response status code should be 200
    And the response should reflect the updated service data

    When the client deletes the created service
    Then the response status code should be 204
    And the response body should be empty

    When the client retrieves the created service by id
    Then the response status code should be 404
