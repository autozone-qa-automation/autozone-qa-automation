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

    # ST-FT-02
    @features @list @empty
    Scenario: Features empty state is shown when service has no features
        Given the user opens the features page
        When the user selects the service "Orphan-Service" in the selector
        Then the features empty state should be displayed

    # ST-FT-03
    @features @detail
    Scenario: Feature detail page is visible with linked test cases
        Given the user opens the features page
        And features should be listed
        When the user clicks View on the first feature
        Then the feature detail page layout should be correctly displayed
        And the feature should have linked test cases visible
        And the test cases count should match the rendered items

    # ST-FT-04
    @features @detail @empty-tc
    Scenario: Feature detail page shows empty state when no test cases linked
        Given the user opens the features page
        When the user clicks View on the feature with no test cases
        Then the feature detail page layout should be correctly displayed
        And the feature test cases empty state should be displayed

    # ST-FT-05
    @features @filter
    Scenario: Filtering by a specific service shows only its features
        Given the user opens the features page
        When the user selects the service "Auth-Service" in the selector
        Then only features from "Auth-Service" should be displayed

    # ST-FT-06
    @features @filter
    Scenario: Service selector has no value selected by default
        Given the user opens the features page
        Then the features page layout should be correctly displayed
        And the service selector should have no value selected by default
        And features should be listed