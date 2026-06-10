package com.autozone.integration.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Entry point for the API integration test suite.
 *
 * <p>Run with: {@code ./mvnw -Dtest=IntegrationCucumberTestSuite test}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@ConfigurationParameter(
    key = Constants.GLUE_PROPERTY_NAME,
    value = "com.autozone.integration.cucumber")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
public class IntegrationCucumberTestSuite {}
