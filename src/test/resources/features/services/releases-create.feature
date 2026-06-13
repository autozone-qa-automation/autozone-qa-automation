Feature: Release creation
    As a user
    I want to create new releases
    So that releases can be tracked through the workflow

    @releases @create @validation @ST-RL-006
    Scenario: ST-RL-006 - Validate required fields on release creation
        Given the user is on the releases page
        When the user opens the create release modal
        And the user leaves the release name empty
        And the user leaves the release version empty
        And the user clicks the create release submit button
        Then validation errors should be shown for the required fields

    @releases @create @success @ST-RL-007
    Scenario: ST-RL-007 - Successfully create a release with all fields
        Given the user is on the releases page
        When the user opens the create release modal
        And the user enters the release name "E2E Test Release"
        And the user enters the objective "Automated E2E test for release creation"
        And the user enters the release version "1.0.0"
        And the user selects the status "Draft"
        And the user selects a service
        And the user selects a feature
        And the user adds a tag "e2e"
        And the user clicks the create release submit button
        Then the release should be created successfully
        And the create release modal should close

    @releases @create @dependency @ST-RL-008
    Scenario: ST-RL-008 - Features field depends on service selection
        Given the user is on the releases page
        When the user opens the create release modal
        Then the features field should be disabled
        And the features placeholder should indicate service selection is required
        When the user selects a service
        Then the features field should be enabled

    @releases @create @status @ST-RL-009
    Scenario: ST-RL-009 - Validate status selection toggles correctly
        Given the user is on the releases page
        When the user opens the create release modal
        Then the status should default to "Draft"
        When the user clicks on the "Progress" status
        Then the "Progress" status should be selected
        When the user clicks on the "Active" status
        Then the "Active" status should be selected
        When the user clicks on the "Draft" status
        Then the "Draft" status should be selected

    @releases @create @dependency @ST-RL-010
    Scenario: ST-RL-010 - Features reset when service changes
        Given the user is on the releases page
        When the user opens the create release modal
        And the user selects a service
        And the user selects a feature
        When the user changes to a different service
        Then the previously selected features should be cleared
