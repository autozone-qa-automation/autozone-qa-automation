@integration @api @test-cases
Feature: Test Cases API
  As an authenticated ADMIN
  I want to manage test cases that belong to a feature
  So that I can confirm the Test Cases API behaves as documented

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: List all test cases
    When the client sends a GET request to "/api/v1/test-cases"
    Then the response status code should be 200
    And the response body should be a JSON array
    And each test case in the response should have an "id", a "title", a "relatedFeature", a "type", "steps", "expectedOutput" and an "active" flag

  Scenario: Create, read, list by feature and update a test case
    Given an existing feature for test cases
    When the client creates a new test case with a unique title for that feature
    Then the response status code should be 201
    And the response should echo the created test case title, type, steps, expected output and related feature
    And the response should be active and include a generated test case id

    When the client retrieves the created test case by id
    Then the response status code should be 200
    And the response body should match the created test case data
    And the response "featureName" should be null

    When the client lists test cases by that feature
    Then the response status code should be 200
    And the response body should be a JSON array
    And the response should include the created test case

    When the client updates the created test case with a new title, steps and expected output
    Then the response status code should be 200
    And the response should reflect the updated test case title, steps and expected output

  Scenario: Listing all test cases includes the feature name
    Given an existing feature for test cases
    And the client has created a new test case for that feature
    When the client sends a GET request to "/api/v1/test-cases"
    Then the response status code should be 200
    And the response should include the created test case with its feature name populated

  Scenario: Creating a test case with a missing required field is rejected
    Given an existing feature for test cases
    When the client creates a test case with a null title for that feature
    Then the response status code should be 400

  Scenario: Creating a test case with a duplicate title is rejected
    Given an existing feature for test cases
    And the client has created a new test case for that feature
    When the client creates another test case using the same title for that feature
    Then the response status code should be 409

  Scenario: Updating a test case with a mismatched id in the body is rejected
    Given an existing feature for test cases
    And the client has created a new test case for that feature
    When the client updates the created test case using a different id in the body
    Then the response status code should be 400

  Scenario: Updating a test case with another test case's title is rejected
    Given an existing feature for test cases
    And the client has created two test cases with distinct titles for that feature
    When the client updates the first test case using the second test case's title
    Then the response status code should be 409

  Scenario: Deactivating a test case returns 204 and the test case becomes inactive
    Given an existing feature for test cases
    And the client has created a new test case for that feature
    When the client deactivates the created test case
    Then the response status code should be 204
    And the response body should be empty

    When the client retrieves the created test case by id
    Then the response status code should be 404

  Scenario: Deactivating a non-existent test case returns 404
    When the client deactivates the test case with id 999999999
    Then the response status code should be 404

  Scenario: Get a non-existent test case by id
    When the client sends a GET request to "/api/v1/test-cases/999999999"
    Then the response status code should be 404
