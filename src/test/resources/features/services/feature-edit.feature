Feature: Feature edit
    As a user
    I want to update the details of an existing feature
    So that the feature information is saved and displayed correctly

    @features @edit @success
    Scenario: Successful update of feature name and description
        Given the user opens the feature details page for 1
        When the user opens the edit feature modal
        And the user updates the feature name to "Gestión de Perfil de Usuario - Actualizado"
        And the user updates the feature description to "Permite a los usuarios actualizar su foto, correo y contraseña de forma segura."
        And the user clicks save changes
        Then the feature success message should be displayed
        And the feature detail title should show "Gestión de Perfil de Usuario - Actualizado"
        And the feature description should show "Permite a los usuarios actualizar su foto, correo y contraseña de forma segura."

    @features @edit @validation
    Scenario: Feature update fails when the name is empty
        Given the user opens the feature details page for 1
        And the user opens the edit feature modal
        And the user clears the feature name field
        When the user clicks save changes
        Then the feature update should not be processed
