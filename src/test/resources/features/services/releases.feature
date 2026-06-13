Feature: Releases
    As a user
    I want to navigate releases screens
    So that releases information can be validated through E2E automation

    @releases @list
    Scenario: Releases list page is visible
        Given the user opens the releases list page
        Then the releases page layout should be correctly displayed
        And releases should be listed with metadata including name, version, status, and dates
        And the release details should be visible when a release is clicked

    @releases @search
    Scenario: Search for a release
        Given the user opens the releases list page to search for a release
        When the user searches for the release "Release 1.1.0"
        Then the release "Release 1.1.0" should be displayed in the list

    @releases @search @empty
    Scenario: Search for a non-existent release
        Given the user opens the releases list page to search for a release
        When the user searches for the release "Invalid Release Name 999"
        Then the releases empty message "No releases found" should be displayed

    @releases @list @sorting
    Scenario: Sort releases list by creation date
        Given the user opens the releases list page
        When the user sorts the list by "Newest"
        Then the releases should be ordered by newest creation date first
        When the user sorts the list by "Oldest"
        Then the releases should be ordered by oldest creation date first

    @releases @details
    Scenario: Release details page layout and hierarchy are visible
        Given the user opens the release details page for 1
        Then the release details page layout should be correctly displayed
        And the single associated service should be displayed
        When the user selects the service   
        Then the service features should be listed