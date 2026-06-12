package com.autozone.tests.e2e;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class E2eConfig {

    private static final String DEFAULT_BASE_URL = "http://localhost:5173";

    private E2eConfig() {
    }

    public static String baseUrl() {
        String fromSystemProperty = System.getProperty("web.base.url");
        if (isNotBlank(fromSystemProperty)) {
            return normalize(fromSystemProperty);
        }

        String fromEnvironment = System.getenv("WEB_BASE_URL");
        if (isNotBlank(fromEnvironment)) {
            return normalize(fromEnvironment);
        }

        Properties localProperties = loadEnvProperties();
        String fromFile = localProperties.getProperty("web.base.url");
        if (isNotBlank(fromFile)) {
            return normalize(fromFile);
        }

        return DEFAULT_BASE_URL;
    }

    private static Properties loadEnvProperties() {
        Properties properties = new Properties();
        loadPropertiesFile(properties, Path.of(".env.properties"));
        loadPropertiesFile(properties, Path.of("env.properties"));

        return properties;
    }

    private static void loadPropertiesFile(Properties properties, Path envFilePath) {
        if (!Files.exists(envFilePath)) {
            return;
        }

        try (InputStream inputStream = Files.newInputStream(envFilePath)) {
            properties.load(inputStream);
        } catch (IOException ignored) {
            // Keep defaults/fallbacks if local properties cannot be read.
        }
    }

    private static String normalize(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
