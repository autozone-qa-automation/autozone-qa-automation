package com.autozone.tests.e2e.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class E2eTestCredentials {

    private static final Properties PROPERTIES = loadProperties();

    private E2eTestCredentials() {
    }

    public static String validUsername() {
        return required("test.username");
    }

    public static String validPassword() {
        return required("test.password");
    }

    public static String invalidPassword() {
        return optional("test.invalid.password", "invalid-password");
    }

    public static String unregisteredUsername() {
        return optional("test.unregistered.username", "missing-user-e2e@autozone.invalid");
    }

    public static String anyPassword() {
        return optional("test.any.password", "any-password");
    }

    public static String invalidEmailFormat() {
        return optional("test.invalid.email", "not-an-email");
    }

    public static String inactiveUsername() {
        return required("test.inactive.username");
    }

    public static String inactivePassword() {
        return required("test.inactive.password");
    }

    private static String required(String key) {
        String value = optional(key, null);
        if (isBlank(value)) {
            throw new IllegalStateException(
                    "Missing required E2E credential '" + key + "'. "
                            + "Set it in .env.properties, config.properties, an environment variable, "
                            + "or with -D" + key + "=..."
            );
        }
        return value;
    }

    private static String optional(String key, String fallback) {
        String fromSystemProperty = System.getProperty(key);
        if (!isBlank(fromSystemProperty)) {
            return fromSystemProperty;
        }

        String fromEnvironment = System.getenv(toEnvironmentKey(key));
        if (!isBlank(fromEnvironment)) {
            return fromEnvironment;
        }

        String fromFile = PROPERTIES.getProperty(key);
        if (!isBlank(fromFile)) {
            return fromFile;
        }

        return fallback;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        loadClasspathProperties(properties, "config.properties");
        loadLocalProperties(properties, Path.of(".env.properties"));
        loadLocalProperties(properties, Path.of("env.properties"));

        return properties;
    }

    private static void loadClasspathProperties(Properties properties, String resourceName) {
        try (InputStream inputStream =
                     E2eTestCredentials.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ignored) {
            // Keep defaults/fallbacks if classpath properties cannot be read.
        }
    }

    private static void loadLocalProperties(Properties properties, Path path) {
        if (!Files.exists(path)) {
            return;
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);
        } catch (IOException ignored) {
            // Keep defaults/fallbacks if local properties cannot be read.
        }
    }

    private static String toEnvironmentKey(String key) {
        return key.toUpperCase().replace('.', '_').replace('-', '_');
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
