package com.autozone.tests;

import com.autozone.base.BaseTest;
import com.autozone.pages.FeaturePage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FeatureE2ETest extends BaseTest {

    FeaturePage featurePage = new FeaturePage();

    @Test
    public void userCanCreateFeature() {

        // Abrir módulo
        featurePage.openFeaturesModule();

        // Crear feature
        featurePage.clickCreateFeature();

        // Llenar formulario
        featurePage.enterFeatureName("Login Feature");

        featurePage.enterFeatureDescription(
                "Feature for login automation tests"
        );

        // Guardar
        featurePage.clickSave();

        // Validación
        Assert.assertTrue(
                featurePage.successMessageIsDisplayed(),
                "Feature creation failed"
        );
    }
}
