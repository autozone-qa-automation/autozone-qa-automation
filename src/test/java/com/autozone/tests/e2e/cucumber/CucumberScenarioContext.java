package com.autozone.tests.e2e.cucumber;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.autozone.tests.e2e.bots.BaseBot;
import com.autozone.tests.e2e.bots.FeatureBot;
import com.autozone.tests.e2e.bots.LoginBot;
import com.autozone.tests.e2e.bots.ReleaseDeleteBot;
import com.autozone.tests.e2e.bots.ReleasesBot;
import com.autozone.tests.e2e.bots.ServiceIdBot;
import com.autozone.tests.e2e.bots.ServicesBot;
import com.autozone.tests.e2e.bots.TestCasesBot;
import com.autozone.tests.e2e.bots.TestCasesCreateBot;

public class CucumberScenarioContext {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<Map<Class<?>, BaseBot>> BOTS =
            ThreadLocal.withInitial(HashMap::new);

    private CucumberScenarioContext() {}

    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
        BOTS.get().clear();
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver is not initialized for this scenario.");
        }
        return driver;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseBot> T getBot(Class<T> botClass) {
        Map<Class<?>, BaseBot> registry = BOTS.get();

        return (T) registry.computeIfAbsent(
                botClass,
                cls -> {
                    try {
                        return botClass.getConstructor(WebDriver.class)
                                .newInstance(getDriver());
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot create bot: " + botClass.getSimpleName(), e);
                    }
                }
        );
    }

    public static void clear() {
        BOTS.remove();
        DRIVER.remove();
    }

    // Convenience methods (optional but useful)
    public static ServicesBot getServicesBot() {
        return getBot(ServicesBot.class);
    }

    public static ServiceIdBot getServiceIdBot() {
        return getBot(ServiceIdBot.class);
    }

    public static FeatureBot getFeatureBot() {
        return getBot(FeatureBot.class);
    }

    public static TestCasesBot getTestCasesBot() {
        return getBot(TestCasesBot.class);
    }

    public static TestCasesEditBot getTestCasesEditBot() {
        return getBot(TestCasesEditBot.class);
    }

    public static TestCasesCreateBot getTestCasesCreateBot() {
        return getBot(TestCasesCreateBot.class);
    }
}
