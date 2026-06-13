@integration @api @services
Feature: Services test endpoint
  As an authenticated ADMIN
  I want to call the hardcoded test endpoint for services
  So that I can confirm it always returns the 3 expected environment URLs

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: Get the hardcoded test service
    When the client sends a GET request to "/api/v1/services/test/1"
    Then the response status code should be 200
    And the response body should contain exactly 3 urls
    And the urls should include the environments "Produccion", "QA" and "Dev"
