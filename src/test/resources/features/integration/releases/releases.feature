@integration @api @releases
Feature: Releases API
  As an authenticated ADMIN
  I want to manage releases and their status lifecycle
  So that I can confirm the Releases API behaves as documented, including its known quirks

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: List all releases
    When the client sends a GET request to "/api/v1/releases"
    Then the response status code should be 200
    And the response body should be a JSON array
    And each release in the response should have a "releaseId", a "releaseName", a "releaseDescription", a "releaseVersion", a "releaseStatus", "releaseTags", a "releaseCreationDate" and a "releaseIsActive" flag

  Scenario: List the most recent releases
    When the client sends a GET request to "/api/v1/releases/last"
    Then the response status code should be 200
    And the response body should be a JSON array

  Scenario: Create, read and filter a Draft release
    Given an existing service from the services list
    When the client creates a new Draft release with a unique name
    Then the response status code should be 201
    And the response should echo the created release name, description, version, status and tags
    And the response should be active and include a generated release id

    When the client retrieves the created release by id
    Then the response status code should be 200
    And the response body should match the created release data

    When the client lists releases filtered by status "Draft"
    Then the response status code should be 200
    And the response body should be a JSON array
    And the response should include the created release

  Scenario: Filtering releases by tag returns matching releases
    Given an existing service from the services list
    When the client creates a new Draft release with the tag "qa-smoke"
    Then the response status code should be 201

    When the client lists releases filtered by tag "qa-smoke"
    Then the response status code should be 200
    And the response body should be a JSON array
    And the response should include the created release

  Scenario: Get a non-existent release returns an empty 404
    When the client sends a GET request to "/api/v1/releases/999999999"
    Then the response status code should be 404
    And the response body should be empty

  Scenario: Valid status transitions succeed but a non-Draft release cannot be deleted
    Given an existing service from the services list
    When the client creates a new Draft release with a unique name
    Then the response status code should be 201

    When the client transitions the created release to status "Progress"
    Then the response status code should be 200
    And the response "releaseStatus" should equal "Progress"

    When the client transitions the created release to status "Active"
    Then the response status code should be 200
    And the response "releaseStatus" should equal "Active"

    When the client deletes the created release
    Then the response status code should be 400
    And the response body should contain "DRAFT"

  Scenario: An invalid status transition returns 400 with a plain-text body (known quirk)
    Given an existing service from the services list
    When the client creates a new release with a unique name and status "Active"
    Then the response status code should be 201

    When the client transitions the created release to status "Draft"
    Then the response status code should be 400
    And the response content type should be "application/json"
    And the response body should contain "Invalid status transition"

  Scenario: An invalid status enum value returns 400 with a JSON error body
    Given an existing service from the services list
    When the client creates a new Draft release with a unique name
    Then the response status code should be 201

    When the client transitions the created release to status "NotARealStatus"
    Then the response status code should be 400
    And the response content type should be "application/json"
    And the response body should contain "NotARealStatus"

    When the client deletes the created release
    Then the response status code should be 204

  Scenario: Updating the status of a non-existent release returns 404 with a plain-text body
    When the client transitions the release with id 999999999 to status "Active"
    Then the response status code should be 404
    And the response content type should be "application/json"
    And the response body should contain "999999999"

  Scenario: Deleting a non-existent release returns 404 with a JSON error body
    When the client sends a DELETE request to "/api/v1/releases/999999999"
    Then the response status code should be 404
    And the response content type should be "application/json"

  Scenario: Creating a release with a missing required field is rejected
    Given an existing service from the services list
    When the client creates a release with a null release name
    Then the response status code should be 400
