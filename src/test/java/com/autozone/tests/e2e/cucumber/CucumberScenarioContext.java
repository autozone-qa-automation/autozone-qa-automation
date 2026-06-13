package com.autozone.tests.e2e.cucumber;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.autozone.tests.e2e.bots.BaseBot;
import com.autozone.tests.e2e.bots.FeatureBot;
import com.autozone.tests.e2e.bots.LoginBot;
import com.autozone.tests.e2e.bots.ReleaseCreateBot;
import com.autozone.tests.e2e.bots.ReleaseDeleteBot;
import com.autozone.tests.e2e.bots.ReleaseStatusBot;
import com.autozone.tests.e2e.bots.LogoutBot;
import com.autozone.tests.e2e.bots.ReleaseDeleteBot;
import com.autozone.tests.e2e.bots.ReleaseIdBot;
import com.autozone.tests.e2e.bots.ReleasesBot;
import com.autozone.tests.e2e.bots.ServiceEditBot;
import com.autozone.tests.e2e.bots.ServiceIdBot;
import com.autozone.tests.e2e.bots.ServicesBot;
import com.autozone.tests.e2e.bots.TestCasesBot;
import com.autozone.tests.e2e.bots.TestCasesCreateBot;
import com.autozone.tests.e2e.bots.TestCasesEditBot;
import com.autozone.tests.e2e.bots.TestCaseModalBot;
import com.autozone.tests.e2e.bots.UserCreateBot;
import com.autozone.tests.e2e.bots.UserDeleteBot;
import com.autozone.tests.e2e.bots.UserEditBot;
import com.autozone.tests.e2e.bots.ReportsBot;
import com.autozone.tests.e2e.bots.UsersBot;

public class CucumberScenarioContext {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<Map<Class<?>, BaseBot>> BOTS = ThreadLocal.withInitial(HashMap::new);

    private CucumberScenarioContext() {
    }

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
                });
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

    public static ServiceEditBot getServiceEditBot() {
        return getBot(ServiceEditBot.class);
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

    public static TestCaseModalBot getTestCaseModalBot() {
        return getBot(TestCaseModalBot.class);
    }

    public static LoginBot getLoginBot() {
        return getBot(LoginBot.class);
    }

    public static UsersBot getUsersBot() {
        return getBot(UsersBot.class);
    }

    public static UserEditBot getUserEditBot() {
        return getBot(UserEditBot.class);
    }

    public static UserCreateBot getUserCreateBot() {
        return getBot(UserCreateBot.class);
    }

    public static UserDeleteBot getUserDeleteBot() {
        return getBot(UserDeleteBot.class);
    }

    public static ReleasesBot getReleasesBot() {
        return getBot(ReleasesBot.class);
    }

    public static ReleaseDeleteBot getReleaseDeleteBot() {
        return getBot(ReleaseDeleteBot.class);
    }

    public static ReleaseCreateBot getReleaseCreateBot() {
        return getBot(ReleaseCreateBot.class);
    }

    public static ReleaseStatusBot getReleaseStatusBot() {
        return getBot(ReleaseStatusBot.class);
    }

    public static ReleaseIdBot getReleaseIdBot() {
        return getBot(ReleaseIdBot.class);
    }

    public static LogoutBot getLogoutBot() {
        return getBot(LogoutBot.class);
    }

    public static ReportsBot getReportsBot() {
        return getBot(ReportsBot.class);
    }

}
