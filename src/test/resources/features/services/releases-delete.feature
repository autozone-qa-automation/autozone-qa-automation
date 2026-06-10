Feature: Release deletion
    As an administrator
    I want to delete a draft release
    So that releases that are no longer needed can be removed

    @releases @delete @success
    Scenario: Successfully delete the first draft release
        Given the user opens the releases page
        When the user filters releases by draft status
        And the user opens the first draft release
        And the user clicks the delete release button
        And the user confirms the release deletion
        Then the release deletion success message should be displayed
        And the deleted release should no longer be listed
