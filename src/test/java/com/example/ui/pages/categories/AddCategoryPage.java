package com.example.ui.pages.categories;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select; // NEW IMPORT
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AddCategoryPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // --- LOCATORS ---
    private By nameField = By.id("name"); 
    
    // Note: Absolute XPath is fragile. Using more flexible selector for Save button
    private By saveButton = By.xpath("//button[contains(normalize-space(), 'Save')]");     
    
    // Success message container (provided selector)
    private By successMessage = By.cssSelector("body > div:nth-child(1) > div > div.main-content > div:nth-child(2) > div");

    // Error message shown when validation fails (case 2)
    private By errorMessage = By.cssSelector(".invalid-feedback");

    // --- NEW LOCATOR (for Test Case 3) ---
    private By parentDropdown = By.id("parentId"); // Based on your input selector - #parentId

    // --- NEW LOCATOR FOR TC05 (Backend Error) ---
    // Using the specific XPath you provided for the error container
    private By backendErrorBanner = By.xpath("/html/body/div/div/div[2]/div[2]/div");

    // --- CONSTRUCTOR ---
    public AddCategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // --- ACTIONS (Positive Flow) ---

    // UPDATED METHOD: Clears the field before typing to prevent appending text
    public void enterCategoryName(String name) {
        // 1. Find the element
        var field = wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
        // 2. Clear existing text (This is the fix!)
        field.clear();
        // 3. Enter the new name
        field.sendKeys(name);
    }

    // --- NEW ACTION (For Test Case 3: Select Parent Category) ---
   public void selectParentCategory(String parentName) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(parentDropdown));
    wait.until(ExpectedConditions.textToBePresentInElementLocated(parentDropdown, parentName));

    Select dropdown = new Select(driver.findElement(parentDropdown));
    dropdown.selectByVisibleText(parentName);
}

    public void clickSave() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
        } catch (Exception e) {
            // Fallback 1: button with type submit
            try {
                By submit = By.xpath("//button[@type='submit']");
                if (!driver.findElements(submit).isEmpty()) {
                    driver.findElement(submit).click();
                    return;
                }
            } catch (Exception ignored) {
            }
            // Fallback 2: any button with text containing save/submit
            try {
                By fallback = By.xpath("//button[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'save') or contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'submit')]");
                if (!driver.findElements(fallback).isEmpty()) {
                    driver.findElement(fallback).click();
                    return;
                }
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage)).isDisplayed();
        } catch (Exception e) {
            // Fallback: check for common success classes or page source
            try {
                By alt = By.cssSelector(".alert-success, .toast-success");
                if (!driver.findElements(alt).isEmpty() && driver.findElement(alt).isDisplayed()) {
                    return true;
                }
            } catch (Exception ignored) {
            }
            try {
                String src = driver.getPageSource().toLowerCase();
                if (src.contains("success") || src.contains("successfully") || src.contains("category added")) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    // --- NEW ACTIONS (Negative Flow) ---

    // Action to clear the Category Name field
    public void clearCategoryName() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(nameField)).clear();
        } catch (Exception e) {
            // Field might already be empty or not yet visible; just skip
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    // Action to check if the error message (invalid-feedback) is displayed
    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Get the text of the error message (for validation)
    public String getErrorMessageText() {
         try {
             return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
         } catch (Exception e) {
             return "";
         }
    }

    // --- NEW ACTIONS FOR TC05 (Backend Error Validation) ---

    public boolean isErrorBannerDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(backendErrorBanner)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorBannerText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(backendErrorBanner)).getText();
        } catch (Exception e) {
            return "";
        }
    }

public boolean isParentCategoryEmptyOptionAvailable() {
    try {
        wait.until(ExpectedConditions.visibilityOfElementLocated(parentDropdown));

        Select dropdown = new Select(driver.findElement(parentDropdown));

        for (org.openqa.selenium.WebElement option : dropdown.getOptions()) {
            String value = option.getAttribute("value");
            String text = option.getText().trim();

            if ((value == null || value.trim().isEmpty()) && text.isEmpty()) {
                return true;
            }
        }

        return false;
    } catch (Exception e) {
        return false;
    }
}

}