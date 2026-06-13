Feature: Features
    As a user
    I want to navigate the features screens
    So that features information can be validated through E2E automation

    # ST-FT-01
    @features @list
    Scenario: Features list page is visible with existing features
        Given the user opens the features page
        Then the features page layout should be correctly displayed
        And features should be listed
        And each feature should display its name, description and view button
        And each feature should display its feature number
        And a new feature button should be visible

    # ST-FT-02
    @features @list
    Scenario: Features empty state is shown when service has no features
        Given the user opens the features page
        When the user selects the empty service in the selector
        Then the features empty state should be displayed
        And the features page layout should be correctly displayed
        And a new feature button should be visible

    # ST-FT-03
    @features @detail
    Scenario: Feature detail page is visible with linked test cases
        Given the user opens the features page
        And features should be listed
        When the user clicks View on the first feature
        Then the feature detail page layout should be correctly displayed
        And the feature should have linked test cases visible
        And the test cases count should match the rendered items
        And the edit and delete buttons should be visible

    # ST-FT-04
    @features @detail
    Scenario: Feature detail page shows empty state when no test cases linked
        Given the user opens the features page
        When the user finds and opens a feature with no test cases
        Then the feature detail page layout should be correctly displayed
        And the feature test cases empty state should be displayed
        And the edit and delete buttons should be visible

