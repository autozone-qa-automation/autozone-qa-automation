Feature: Feature delete
    As a user
    I want to delete a feature
    So that the feature is deactivated and isn't displayed in the feature list

    @features @edit @success
    Scenario: Successful delete of feature
        Given the user opens the feature details page for 1
        When the user opens the delete feature modal
        And the user confirms the feature deletion
        Then the user is redirected to the features page


    @features @edit @validation
    Scenario: Canceled delete of feature
        Given the user opens the feature details page for 1
        When the user opens the delete feature modal
        And the user clicks Cancel
        Then the modal is closed and the user is kept on the feature details page


