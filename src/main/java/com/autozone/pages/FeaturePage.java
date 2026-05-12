package com.autozone.pages;

public class FeaturePage {

    public void openFeaturesModule() {
        System.out.println("Opening Features Module");
    }

    public void clickCreateFeature() {
        System.out.println("Clicking Create Feature");
    }

    public void enterFeatureName(String name) {
        System.out.println("Entering feature name: " + name);
    }

    public void enterFeatureDescription(String description) {
        System.out.println("Entering feature description: " + description);
    }

    public void clickSave() {
        System.out.println("Saving feature");
    }

    public boolean successMessageIsDisplayed() {
        return true;
    }
}
