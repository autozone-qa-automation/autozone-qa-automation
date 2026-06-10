package com.autozone.tests.e2e.cucumber;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.autozone.tests.e2e.cucumber",
    plugin = {"pretty", "summary"}
)
public class CucumberTest extends AbstractTestNGCucumberTests {
}