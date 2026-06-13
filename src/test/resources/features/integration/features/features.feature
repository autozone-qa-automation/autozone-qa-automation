@integration @api @features
Feature: Features API
  As an authenticated ADMIN
  I want to manage features that belong to a service
  So that I can confirm the Features API behaves as documented, including its known quirks

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: List all features
    When the client sends a GET request to "/api/v1/features"
    Then the response status code should be 200
    And the response body should be a JSON array
    And each feature in the response should have an "id", a "featureName", a "featureDescription" and an "idService"

  Scenario: Create, read and filter a feature, then update its name and description
    Given an existing service from the services list
    When the client creates a new feature with a unique name for that service
    Then the response status code should be 201
    And the response should echo the created feature name, description and service id
    And the response should include the service name and a generated feature id

    When the client retrieves the created feature by id
    Then the response status code should be 200
    And the response body should match the created feature data

    When the client lists features filtered by that service id
    Then the response status code should be 200
    And the response body should be a JSON array
    And the response should include the created feature

    When the client updates the created feature with a new name and description
    Then the response status code should be 200
    And the response should reflect the updated feature name and description
    And the feature's service id should remain unchanged

  Scenario: Updating a feature with another feature's name returns 500 (known quirk)
    Given an existing service from the services list
    And the client has created two features with distinct names for that service
    When the client updates the first feature using the second feature's name
    Then the response status code should be 500

  Scenario: Deactivating a feature returns 200 with an empty body and the feature becomes inactive
    Given an existing service from the services list
    And the client has created a new feature for that service
    When the client deactivates the created feature
    Then the response status code should be 200
    And the response body should be empty

    When the client retrieves the created feature by id
    Then the response status code should be 404

  Scenario: Get a non-existent feature by id
    When the client sends a GET request to "/api/v1/features/999999999"
    Then the response status code should be 404

  Scenario: List features filtered by a non-existent service id
    When the client sends a GET request to "/api/v1/features/filtered/999999999"
    Then the response status code should be 404

  Scenario: Deactivating a non-existent feature returns 404
    When the client deactivates the feature with id 999999999
    Then the response status code should be 404

  Scenario: Creating a feature with a missing required field is rejected
    Given an existing service from the services list
    When the client creates a feature with a null feature name for that service
    Then the response status code should be 400

  Scenario: Creating a feature for a non-existent service returns 404
    When the client creates a feature for service id 999999999
    Then the response status code should be 404
