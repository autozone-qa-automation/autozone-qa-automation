Feature: Release status update
    As a user
    I want to update the status of a draft release
    So that the release progresses through the workflow

    @releases @status @success
    Scenario: Successfully update release status from Draft to Progress
        Given the user opens a draft release
        When the user clicks the update release button
        And the user changes the release status to "Progress"
        And the user submits the status update
        Then the release status update success message should be displayed
        And the user navigates back to the releases page
        And the user filters releases by "Progress"
        Then the updated release should be visible in the list
