Feature: Services
    As a user
    I want to navigate services screens
    So that services information can be validated through E2E automation

    @services @list
    Scenario: Services list page is visible
        Given the user opens the services list page
        Then the services page layout should be correctly displayed
        And services should be listed
        And the service details should be visible when a service is clicked
    
    @services @search
    Scenario: Search for a service
        Given the user opens the services list page to search for a service
        When the user searches for "Oil Change"
        Then the service "Oil Change" should be displayed in the list

    @services @details
    Scenario: Service details page is visible
        Given the user opens the service details page for 1
        Then the service details page layout should be correctly displayed
        And the service features should be listed 
        And the last releases section should be displayed
        And the feature details should be visible when a feature is clicked
        
    @services @details @invalid
    Scenario: Service details page for invalid service
        Given the user opens the service details page for 999 invalid service
        Then the service not found message should be displayed

    @services @details @empty-features
    Scenario: Service details page with no features
        Given the user opens the service details page for 2 service with no features
        Then the service features empty message should be displayed