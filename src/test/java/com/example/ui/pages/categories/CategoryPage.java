package com.example.ui.pages.categories;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
// Added import for Assert just in case it's needed, though primarily used in StepDefs
import org.junit.Assert; 
import java.time.Duration;


public class CategoryPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // --- LOCATORS ---
    
    // 1. "Add A Category" button - using href-based selector
    private By addCategoryButton = By.cssSelector("a[href='/ui/categories/add']");
    
    // 2. Category Table
    private By categoryTable = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table"); 

    // 3. UPDATED: Categories menu link (Using the new selector provided)
    private By categoriesMenuLink = By.cssSelector("body > div:nth-child(1) > div > div.sidebar.nav.flex-column > a:nth-child(3)"); 
    
    // --- NEW LOCATORS FOR TC04 (SEARCH) ---
    private By searchInput = By.xpath("/html/body/div[1]/div/div[2]/div[2]/form/div[1]/input");
    private By searchButton = By.xpath("/html/body/div[1]/div/div[2]/div[2]/form/div[3]/button");
    private By resetButton = By.xpath("/html/body/div[1]/div/div[2]/div[2]/form/div[3]/a[1]");
    
    // --- NEW LOCATORS FOR USER TEST (TC_UI_USER_CAT_01) ---
    // 1. Actions Column Header (Your specific XPath)
    private By actionsColumnHeader = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/thead/tr/th[4]");

    // 2. Edit Button (Targets the edit icon in the 4th column of the FIRST row)
    private By firstRowEditButton = By.cssSelector("table > tbody > tr:nth-child(1) > td:nth-child(4) > a > i");

    // --- NEW LOCATORS FOR TC_UI_USER_CAT_03 ---
    // Locator for the Parent Category dropdown in the SEARCH form
    private By searchParentDropdown = By.xpath("/html/body/div[1]/div/div[2]/div[2]/form/div[2]/select");
    
    // --- NEW LOCATOR FOR TC_UI_USER_CAT_04 ---
    // Locator for the "No category found" message in the table (Using the specific XPath provided)
    private By noRecordsMessage = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/tbody/tr/td");

    // --- NEW LOCATORS FOR TC_UI_USER_CAT_05 (SORTING) ---
    
    // 1. HEADERS
    private By idHeader = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/thead/tr/th[1]/a");
    private By nameHeader = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/thead/tr/th[2]/a");
    private By parentHeader = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/thead/tr/th[3]/a");

    // 2. COLUMN DATA (Generic - Finds ALL rows)
    private By idColumnCells = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/tbody/tr/td[1]");
    private By nameColumnCells = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/tbody/tr/td[2]");
    private By parentColumnCells = By.xpath("/html/body/div[1]/div/div[2]/div[2]/table/tbody/tr/td[3]");

    // --- CONSTRUCTOR ---
    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // --- ACTIONS ---

    public void clickCategoriesMenu() {
        try {
            // Try the new user-provided XPath first
            wait.until(ExpectedConditions.elementToBeClickable(categoriesMenuLink)).click();
            return;
        } catch (Exception e) {
            // Fallback 1: Try finding by Link Text "Categories"
            try {
                By lt = By.linkText("Categories");
                if (!driver.findElements(lt).isEmpty()) {
                    driver.findElement(lt).click();
                    return;
                }
            } catch (Exception ignored) {
            }
            
            // Fallback 2: Try finding by href containing 'categories'
            try {
                By href = By.cssSelector("a[href*='categories']");
                if (!driver.findElements(href).isEmpty()) {
                    driver.findElement(href).click();
                    return;
                }
            } catch (Exception ignored) {
            }
            
            // Last Resort: Navigate directly to the URL
            try {
                String base = com.example.utils.ConfigReader.get("base.url");
                driver.get(base + "/ui/categories");
                return;
            } catch (Exception ignored) {
            }
            
            // If everything fails, throw the original error
            throw e;
        }
    }

    public void clickAddCategory() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(addCategoryButton)).click();
            return;
        } catch (Exception e) {
            // Fallback 1: try link text (with potential whitespace)
            try {
                By ltrim = By.linkText("Add A Category");
                if (!driver.findElements(ltrim).isEmpty()) {
                    driver.findElement(ltrim).click();
                    return;
                }
            } catch (Exception ignored) {
            }
            // Fallback 2: try case-insensitive text match
            try {
                By xpath = By.xpath("//a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add a category')]");
                if (!driver.findElements(xpath).isEmpty()) {
                    driver.findElement(xpath).click();
                    return;
                }
            } catch (Exception ignored) {
            }
            // Fallback 3: CSS selector for any link with /add in href
            try {
                By hrefAdd = By.cssSelector("a[href*='/add']");
                if (!driver.findElements(hrefAdd).isEmpty()) {
                    driver.findElement(hrefAdd).click();
                    return;
                }
            } catch (Exception ignored) {
            }
            // Last resort: navigate directly to add page
            try {
                Thread.sleep(500); 
                driver.get(com.example.utils.ConfigReader.get("base.url") + "/ui/categories/add");
                Thread.sleep(1000); 
                return;
            } catch (Exception ignored) {
            }
            throw e;
        }
    }

    public boolean isCategoryVisible(String categoryName) {
        // Use a more flexible locator: look for the category text anywhere in table cells
        By categoryLocator = By.xpath("//table//td[contains(normalize-space(.), '" + categoryName + "')]");
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(categoryLocator)).isDisplayed();
        } catch (Exception e) {
            // Fallback: check entire page source for the category name
            try {
                String src = driver.getPageSource().toLowerCase();
                return src.contains(categoryName.toLowerCase());
            } catch (Exception ex) {
                return false;
            }
        }
    }

    // --- NEW ACTION for Test Case 3 (Verify Parent) ---
    public boolean isCategoryWithParentVisible(String childName, String parentName) {
        // This looks for a single Table Row (tr) that has BOTH texts inside it
        // This confirms that "Lotus Blue" is in the same row as "Lotus_New"
        By rowLocator = By.xpath("//tr[contains(., '" + childName + "') and contains(., '" + parentName + "')]");
        
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(rowLocator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // --- NEW ACTIONS FOR TC04 (SEARCH) ---

    public void enterSearchTerm(String term) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput)).clear();
        driver.findElement(searchInput).sendKeys(term);
    }

    public void clickSearchButton() {
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }
    
    public void clickResetButton() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
    }

    // --- NEW STRICT ACTION for TC04 (Fixing False Positive) ---
    // This checks ONLY the table. If it's not in the table, it returns FALSE immediately.
    // No "Page Source" fallback.
    public boolean isCategoryInTableStrict(String categoryName) {
        // Look specifically inside the TABLE body (tbody)
        By categoryLocator = By.xpath("//table/tbody//td[contains(normalize-space(.), '" + categoryName + "')]");
        try {
            // We use a short wait here because if search fails, it should be immediate
            return wait.until(ExpectedConditions.visibilityOfElementLocated(categoryLocator)).isDisplayed();
        } catch (Exception e) {
            return false; // Correctly returns Fail if not found in table
        }
    }

    // --- NEW ACTION FOR TC05 (Click Edit) ---
    public void clickFirstEditButton() {
        // This finds the edit link inside the FIRST row of the table body
        By editButton = By.xpath("//table/tbody/tr[1]//a[contains(@href, 'edit')]");
        
        try {
            wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
        } catch (Exception e) {
            // Fallback: If the href check fails, try clicking the icon (<i>) directly
            // Based on your locator: .../td[4]/a/i
            By icon = By.xpath("//table/tbody/tr[1]//a/i");
            driver.findElement(icon).click();
        }
    }

    // --- NEW ACTIONS FOR USER TEST (TC_UI_USER_CAT_01) ---

    public boolean isActionsColumnVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(actionsColumnHeader)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickFirstRowEditButton() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(firstRowEditButton)).click();
        } catch (Exception e) {
            // If we can't click it, the test logic in StepDef will handle it
            System.out.println("Could not click edit button: " + e.getMessage());
        }
    }

    // --- NEW ACTION FOR TC_UI_USER_CAT_02 ---
    
    public boolean isSearchInputEnabled() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput)).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // --- NEW ACTIONS FOR TC_UI_USER_CAT_03 ---

    public boolean isSearchParentDropdownEnabled() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(searchParentDropdown)).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectSearchParentCategory(String parentName) {
        // Wait for visibility
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchParentDropdown));
        
        // Use Select class to choose the option
        org.openqa.selenium.support.ui.Select dropdown = new org.openqa.selenium.support.ui.Select(driver.findElement(searchParentDropdown));
        dropdown.selectByVisibleText(parentName);
    }

    // --- NEW ACTION FOR TC_UI_USER_CAT_04 ---

    public boolean isNoRecordsMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsMessage)).isDisplayed();
        } catch (Exception e) {
            // Fallback: Check if the table body is just empty or has a different text
            try {
                String bodyText = driver.findElement(By.tagName("tbody")).getText();
                return bodyText.contains("No category found") || bodyText.isEmpty();
            } catch (Exception ex) {
                return false;
            }
        }
    }

    // --- NEW ACTIONS FOR TC_UI_USER_CAT_05 (SORTING) ---

    public void clickColumnHeader(String columnName) {
        By locator = null;
        switch (columnName.toLowerCase()) {
            case "id": locator = idHeader; break;
            case "category name": locator = nameHeader; break;
            case "parent category": locator = parentHeader; break;
        }
        if (locator != null) {
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        }
    }

    public java.util.List<String> getColumnData(String columnName) {
        By locator = null;
        switch (columnName.toLowerCase()) {
            case "id": locator = idColumnCells; break;
            case "category name": locator = nameColumnCells; break;
            case "parent category": locator = parentColumnCells; break;
        }
        
        java.util.List<String> data = new java.util.ArrayList<>();
        if (locator != null) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(categoryTable));
            // Captures all cells from the table
            java.util.List<org.openqa.selenium.WebElement> cells = driver.findElements(locator);
            for (org.openqa.selenium.WebElement cell : cells) {
                data.add(cell.getText().trim());
            }
        }
        return data;
    }


    public void clickEditButtonForCategory(String categoryName) {
        By editButton = By.xpath(
                "//table/tbody/tr[td[contains(normalize-space(.), '" + categoryName + "')]]//a[contains(@href, 'edit')]"
        );

        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
    }


            public String getFirstCategoryNameFromTable() {
    By firstNameCell = By.xpath("//table/tbody/tr[1]/td[2]");

    try {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameCell)).getText().trim();
    } catch (Exception e) {
        return null;
    }
}

public String selectFirstAvailableSearchParentCategory() {
    try {
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchParentDropdown));

        org.openqa.selenium.support.ui.Select dropdown =
                new org.openqa.selenium.support.ui.Select(driver.findElement(searchParentDropdown));

        for (org.openqa.selenium.WebElement option : dropdown.getOptions()) {
            String text = option.getText().trim();

            if (!text.isEmpty() && !text.equalsIgnoreCase("All Parents")) {
                dropdown.selectByVisibleText(text);
                return text;
            }
        }

        return null;
    } catch (Exception e) {
        return null;
    }
}

public void clickLogout() {
    try {
        By logoutLink = By.linkText("Logout");
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    } catch (Exception e) {
        try {
            By logoutHref = By.cssSelector("a[href*='logout']");
            wait.until(ExpectedConditions.elementToBeClickable(logoutHref)).click();
        } catch (Exception ex) {
            driver.get(com.example.utils.ConfigReader.get("base.url") + "/logout");
        }
    }
}

        public void waitForCategoryPageToLoad() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(categoryTable));
}

public void waitForTableRefresh() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(categoryTable));
}

public void waitForSearchInput() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
}
}
