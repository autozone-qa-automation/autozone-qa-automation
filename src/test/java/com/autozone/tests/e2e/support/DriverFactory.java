package com.autozone.tests.e2e.support;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static final String DOWNLOAD_DIR =
            System.getProperty("java.io.tmpdir") + File.separator + "autozone-csv-downloads";

    private DriverFactory() {
    }

    public static WebDriver createChromeDriver() {
        new File(DOWNLOAD_DIR).mkdirs();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_DIR);
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", true);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        return new ChromeDriver(options);
    }
}
