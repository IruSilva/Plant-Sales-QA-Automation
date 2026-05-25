package com.example.hooks;

import com.example.utils.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;


import java.util.HashMap;
import java.util.Map;

public class Hooks {

    public static WebDriver driver;

    // UPDATE: Added ("not @api") so this ONLY runs for UI tests
    @Before("not @api")
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // --- STRONG FIX FOR PASSWORD & DATA BREACH ALERTS (KEPT AS IS) ---
        Map<String, Object> prefs = new HashMap<>();
        
        // 1. Disable Password Saving & Manager
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        
        // 2. SPECIFIC FIX: Disable "Password Leak Detection"
        prefs.put("profile.password_manager_leak_detection", false);
        
        // 3. Disable Safe Browsing
        prefs.put("safebrowsing.enabled", false); 
        
        options.setExperimentalOption("prefs", prefs);

        // --- BROWSER STABILITY ARGUMENTS (KEPT AS IS) ---
        options.addArguments("--disable-notifications"); 
        options.addArguments("--start-maximized"); 
        options.addArguments("--disable-popup-blocking");
        
        // Hide the "Chrome is being controlled by automated software" bar
        options.addArguments("--disable-infobars"); 
        options.addArguments("--disable-blink-features=AutomationControlled"); 
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
    }

    // UPDATE: Added ("not @api") so we don't try to close a browser that never opened
   @After("not @api")
public void tearDown() {
    if (driver != null) {
        try {
            Thread.sleep(10000); // wait 10 seconds so you can see the UI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.quit();
    }
}

    @After
public void clearScenarioContext() {
    ScenarioContext.clear();
}

}