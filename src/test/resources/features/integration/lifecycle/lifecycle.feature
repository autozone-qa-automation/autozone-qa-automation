@integration @api @lifecycle
Feature: Full lifecycle across Services, Features, Test Cases, Releases and Reports
  As an authenticated ADMIN
  I want to create a service, a feature, a test case and a release that bundles that feature
  So that I can confirm the full chain is reflected consistently in the Releases and Reports APIs

  Background:
    Given the API base URL is configured
    And the login endpoint is configured
    And valid ADMIN credentials are configured
    And the client requests an access token

  Scenario: A new service, feature, test case and release flow through to the reports and CSV export
    When the client creates a new service for the lifecycle scenario
    Then the response status code should be 201
    And the response should include a generated lifecycle service id

    When the client creates a new feature for the lifecycle service
    Then the response status code should be 201
    And the response should include a generated lifecycle feature id

    When the client creates a new test case for the lifecycle feature
    Then the response status code should be 201
    And the response should include a generated lifecycle test case id

    When the client creates a new Draft release for the lifecycle service including the lifecycle feature
    Then the response status code should be 201
    And the response should include a generated lifecycle release id

    When the client lists reports filtered by the lifecycle release's tag
    Then the response status code should be 200
    And the response should include the lifecycle release with the service, feature and test case in its hierarchy

    When the client requests the reports CSV export for the lifecycle release
    Then the response status code should be 200
    And the exported CSV should contain the lifecycle release, service, feature and test case
