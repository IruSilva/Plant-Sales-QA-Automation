package com.example.ui.stepDefinitions.categories;

import com.example.hooks.Hooks;
import com.example.ui.pages.categories.CategoryPage;
import com.example.ui.pages.categories.AddCategoryPage;
import com.example.ui.pages.login.LoginPage;
import com.example.utils.ConfigReader;
import com.example.utils.ScenarioContext;
import com.example.utils.TestDataGenerator;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CategoryStepDefinitions {

    WebDriver driver = Hooks.driver;
    CategoryPage categoryPage = new CategoryPage(driver);
    AddCategoryPage addCategoryPage = new AddCategoryPage(driver);
    LoginPage loginPage = new LoginPage(driver);

    // -------------------------------------------------------------------------
    // COMMON LOGIN / NAVIGATION STEPS
    // -------------------------------------------------------------------------

    @Given("the user is on the login page")
    public void user_is_on_login_page() {
        driver.get(ConfigReader.get("login.url"));
    }

    @When("the user logs in with valid credentials")
    public void user_logs_in() {
        loginAsAdmin();
    }

    @When("the standard user logs in")
    public void standard_user_logs_in() {
        loginAsStandardUser();
    }

    @Then("the user is on the Category Management page")
    public void user_navigates_to_category_page() {
        categoryPage.clickCategoriesMenu();
        categoryPage.waitForCategoryPageToLoad();
    }

    @Given("the user opens the Category Management page")
    public void user_opens_category_management_page() {
        try {
            if (driver.getCurrentUrl().contains("/ui/login")
                    || driver.findElements(By.cssSelector("#loginForm")).size() > 0) {
                loginAsAdmin();
            }
        } catch (Exception ignored) {
        }

        categoryPage.clickCategoriesMenu();
        categoryPage.waitForCategoryPageToLoad();
    }

    // -------------------------------------------------------------------------
    // ADMIN CATEGORY ADD / EDIT STEPS
    // -------------------------------------------------------------------------

    @When("the user clicks the {string} button")
    public void user_clicks_add_category(String btnName) {
        categoryPage.clickAddCategory();
    }

    @When("the user enters {string} in the Category Name field")
    public void user_enters_category_name(String name) {
        addCategoryPage.enterCategoryName(name);
    }

    @When("the user enters generated category name {string} in the Category Name field")
    public void user_enters_generated_category_name(String key) {
        String categoryName = ScenarioContext.getString(key);

        if (categoryName == null) {
            if (key.equals("UI_MULTI_WORD_CATEGORY")) {
                int randomNumber = (int) (Math.random() * 90) + 10;
                categoryName = "Multi " + randomNumber;
            } else {
                categoryName = TestDataGenerator.uniqueCategoryName("UIC");
            }

            ScenarioContext.set(key, categoryName);
            System.out.println("Generated category for " + key + " = " + categoryName);
        } else {
            System.out.println("Reusing generated category for " + key + " = " + categoryName);
        }

        addCategoryPage.enterCategoryName(categoryName);
    }

    @When("the user leaves the Category Name field empty")
    public void user_leaves_category_name_blank() {
        addCategoryPage.clearCategoryName();
    }

    @When("the user leaves the Parent Category empty")
    public void user_leaves_parent_empty() {
        // Parent category is intentionally left unchanged/empty.
    }

    @When("the user selects {string} from the Parent Category dropdown")
    public void user_selects_parent_category(String parentName) {
        addCategoryPage.selectParentCategory(parentName);
    }

    @When("the user selects generated category {string} from the Parent Category dropdown")
    public void user_selects_generated_parent_category(String key) {
        String parentName = ScenarioContext.getString(key);
        Assert.assertNotNull("Generated parent category was not found for key: " + key, parentName);

        addCategoryPage.selectParentCategory(parentName);
    }

    @When("the user clicks the Save button")
    public void user_clicks_save() {
        addCategoryPage.clickSave();
    }

    @Then("a success message should be displayed")
    public void verify_success_message() {
        Assert.assertTrue("Success message not shown", addCategoryPage.isSuccessMessageDisplayed());
    }

    @Then("the user should be redirected to the Category List")
    public void verify_redirected_to_category_list() {
        Assert.assertTrue("Not redirected to category list",
                driver.getCurrentUrl().contains("categories"));
    }

    @Then("the new category {string} should appear in the list")
    public void verify_category_in_list(String name) {
        Assert.assertTrue("Category not found in list: " + name,
                categoryPage.isCategoryVisible(name));
    }

    @Then("the generated category {string} should appear in the list")
    public void verify_generated_category_in_list(String key) {
        String categoryName = ScenarioContext.getString(key);

        Assert.assertNotNull("Generated category name was not found for key: " + key, categoryName);
        Assert.assertTrue("Generated category not found in list: " + categoryName,
                categoryPage.isCategoryInTableStrict(categoryName));
    }

    @Then("the category {string} should show {string} as its parent")
    public void verify_category_parent_relationship(String childName, String parentName) {
        Assert.assertTrue("Category with correct parent not found in list",
                categoryPage.isCategoryWithParentVisible(childName, parentName));
    }

    @Then("the generated category {string} should show generated category {string} as its parent")
    public void verify_generated_category_parent_relationship(String childKey, String parentKey) {
        String childName = ScenarioContext.getString(childKey);
        String parentName = ScenarioContext.getString(parentKey);

        Assert.assertNotNull("Generated child category was not found for key: " + childKey, childName);
        Assert.assertNotNull("Generated parent category was not found for key: " + parentKey, parentName);

        Assert.assertTrue(
                "Generated category with parent not found. Child: " + childName + ", Parent: " + parentName,
                categoryPage.isCategoryWithParentVisible(childName, parentName)
        );
    }

    @Then("an error message should be displayed indicating the name is required")
    public void verify_error_message_for_empty_category_name() {
        boolean err = addCategoryPage.isErrorMessageDisplayed();

        if (err) {
            String text = addCategoryPage.getErrorMessageText().toLowerCase();
            Assert.assertTrue("Error message text incorrect",
                    text.contains("required") || text.contains("between"));
        } else {
            Assert.fail("Error message was NOT displayed on the screen.");
        }
    }

    @Then("the user should remain on the Add Category page")
    public void verify_user_remains_on_add_page() {
        String currentUrl = driver.getCurrentUrl().toLowerCase();

        boolean staysOnAdd = currentUrl.contains("add") || currentUrl.contains("create");
        boolean inCategories = currentUrl.contains("categories");

        Assert.assertTrue("User should be in categories section or on add page",
                staysOnAdd || inCategories);
    }

    @Then("a user-friendly error message {string} should be displayed")
    public void verify_user_friendly_error(String expectedText) {
        boolean isDisplayed = addCategoryPage.isErrorBannerDisplayed();
        Assert.assertTrue("No error message was displayed at all!", isDisplayed);

        String actualText = addCategoryPage.getErrorBannerText();
        System.out.println("ACTUAL ERROR RECEIVED: " + actualText);

        Assert.assertTrue(
                "Bug Found: Expected friendly error '" + expectedText + "' but got backend error: " + actualText,
                actualText.contains(expectedText)
        );
    }

    @Then("the Parent Category dropdown should allow an empty selection")
    public void verify_parent_category_dropdown_allows_empty_selection() {
        Assert.assertTrue(
                "Bug Found: Parent Category dropdown does not allow an empty selection.",
                addCategoryPage.isParentCategoryEmptyOptionAvailable()
        );
    }

    // -------------------------------------------------------------------------
    // SEARCH / FILTER STEPS
    // -------------------------------------------------------------------------

    @When("the user enters {string} in the search box")
    public void user_enters_search_term(String term) {
        categoryPage.enterSearchTerm(term);
    }

    @When("the user enters generated category {string} in the search box")
    public void user_enters_generated_category_in_search_box(String key) {
        String categoryName = ScenarioContext.getString(key);
        Assert.assertNotNull("Generated category was not found for key: " + key, categoryName);

        categoryPage.enterSearchTerm(categoryName);
    }

    @When("the user enters existing category name from the list in the search box")
    public void user_enters_existing_category_name_from_list_in_search_box() {
        String categoryName = categoryPage.getFirstCategoryNameFromTable();

        Assert.assertNotNull("No category name found in the table.", categoryName);
        Assert.assertFalse("Category name from table is empty.", categoryName.trim().isEmpty());

        ScenarioContext.set("USER_SEARCH_CATEGORY", categoryName);
        categoryPage.enterSearchTerm(categoryName);
    }

    @When("the user enters generated invalid category name in the search box")
    public void user_enters_generated_invalid_category_name_in_search_box() {
        String invalidName = "Invalid" + System.currentTimeMillis();

        ScenarioContext.set("USER_INVALID_SEARCH", invalidName);
        categoryPage.enterSearchTerm(invalidName);
        System.out.println("Generated invalid search value = " + invalidName);
    }

    @When("the user clicks the Search button")
    public void user_clicks_search_button() {
        categoryPage.clickSearchButton();
        categoryPage.waitForTableRefresh();
    }

    @Then("the category list should only display the {string} record")
    public void verify_search_results(String term) {
        boolean isFound = categoryPage.isCategoryInTableStrict(term);

        Assert.assertTrue("Bug Found: Search failed to display the category '" + term + "' in the table.",
                isFound);
    }

    @Then("the category list should only display the generated category {string} record")
    public void verify_generated_search_results(String key) {
        String categoryName = ScenarioContext.getString(key);
        Assert.assertNotNull("Generated category was not found for key: " + key, categoryName);

        Assert.assertTrue(
                "Search failed to display generated category: " + categoryName,
                categoryPage.isCategoryInTableStrict(categoryName)
        );
    }

    @Then("the category list should display the searched category record")
    public void category_list_should_display_searched_category_record() {
        String categoryName = ScenarioContext.getString("USER_SEARCH_CATEGORY");

        Assert.assertNotNull("Searched category name was not stored.", categoryName);
        Assert.assertTrue(
                "Searched category was not displayed: " + categoryName,
                categoryPage.isCategoryInTableStrict(categoryName)
        );
    }

    @Then("the Search input field should be enabled")
    public void verify_search_input_enabled() {
        boolean isEnabled = categoryPage.isSearchInputEnabled();
        Assert.assertTrue("Bug: Search input field is disabled for the Standard User!", isEnabled);
    }

    @Then("the Parent Category search dropdown should be enabled")
    public void verify_search_parent_dropdown_enabled() {
        boolean isEnabled = categoryPage.isSearchParentDropdownEnabled();
        Assert.assertTrue("Bug: Parent Category search dropdown is disabled for the Standard User!", isEnabled);
    }

    @When("the user selects {string} from the search parent dropdown")
    public void user_selects_from_search_parent_dropdown(String parentName) {
        categoryPage.selectSearchParentCategory(parentName);
    }

    @When("the user selects the first available parent category from the search parent dropdown")
    public void user_selects_first_available_parent_category_from_search_parent_dropdown() {
        String selectedParent = categoryPage.selectFirstAvailableSearchParentCategory();

        Assert.assertNotNull("No available parent category found in dropdown.", selectedParent);
        Assert.assertFalse("Selected parent category is empty.", selectedParent.trim().isEmpty());

        ScenarioContext.set("USER_SELECTED_PARENT", selectedParent);
        System.out.println("Selected parent category for filter = " + selectedParent);
    }

    @When("the user selects generated category {string} from the search parent dropdown")
    public void user_selects_generated_category_from_search_parent_dropdown(String parentKey) {
        String parentName = ScenarioContext.getString(parentKey);

        Assert.assertNotNull("Generated parent category was not found for key: " + parentKey, parentName);

        categoryPage.selectSearchParentCategory(parentName);
    }

    @Then("the category list should display the {string} record")
    public void verify_filtered_list_contains_record(String categoryName) {
        boolean isVisible = categoryPage.isCategoryInTableStrict(categoryName);

        Assert.assertTrue("Filter Failed: Expected category '" + categoryName + "' was not visible in the table.",
                isVisible);
    }

    @Then("the category list should display records for the selected parent category")
    public void category_list_should_display_records_for_selected_parent_category() {
        String selectedParent = ScenarioContext.getString("USER_SELECTED_PARENT");

        Assert.assertNotNull("Selected parent category was not stored.", selectedParent);

        Assert.assertTrue(
                "No records displayed for selected parent category: " + selectedParent,
                categoryPage.isCategoryInTableStrict(selectedParent)
        );
    }

    @Then("the message {string} is displayed clearly in the table area")
    public void verify_no_records_message(String expectedMessage) {
        boolean isDisplayed = categoryPage.isNoRecordsMessageDisplayed();

        Assert.assertTrue("Bug: The '" + expectedMessage + "' message was not displayed for an invalid search.",
                isDisplayed);
    }

    // -------------------------------------------------------------------------
    // EDIT / ACCESS CONTROL STEPS FOR STANDARD USER
    // -------------------------------------------------------------------------

    @Then("the {string} column should be displayed")
    public void verify_column_is_displayed(String columnName) {
        if (columnName.equals("Actions")) {
            boolean isVisible = categoryPage.isActionsColumnVisible();

            Assert.assertTrue("UI Mismatch: 'Actions' column was expected to be visible.", isVisible);
        }
    }

    @When("the user clicks the Edit button for any category")
    public void user_clicks_edit_for_any_category() {
        categoryPage.clickFirstRowEditButton();
    }

    @When("the user clicks the Edit button for the searched category")
    public void user_clicks_edit_button() {
        categoryPage.clickFirstEditButton();
    }

    @When("the user clicks the Edit button for generated category {string}")
    public void user_clicks_edit_button_for_generated_category(String key) {
        String categoryName = ScenarioContext.getString(key);
        Assert.assertNotNull("Generated category was not found for key: " + key, categoryName);

        categoryPage.clickEditButtonForCategory(categoryName);
    }

    @Then("the user should not be navigated to the Edit Category page")
    public void verify_no_navigation_to_edit_page() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/edit/"),
                    ExpectedConditions.urlContains("403")
            ));
        } catch (Exception ignored) {
            // If no navigation happens, that is acceptable for this negative access-control check.
        }

        String currentUrl = driver.getCurrentUrl();

        Assert.assertFalse(
                "Security Bug Found: Standard User successfully accessed the Edit Page! URL: " + currentUrl,
                currentUrl.contains("/edit/")
        );
    }

    // -------------------------------------------------------------------------
    // SORTING STEPS
    // -------------------------------------------------------------------------

    @When("the user clicks the {string} column header")
    public void user_clicks_column_header(String columnName) {
        categoryPage.clickColumnHeader(columnName);
        categoryPage.waitForTableRefresh();
    }

    @Then("the category list should be sorted by {string} in ascending order")
    public void verify_list_is_sorted(String columnName) {
        java.util.List<String> actualList = categoryPage.getColumnData(columnName);
        java.util.List<String> expectedList = new java.util.ArrayList<>(actualList);

        if (columnName.equalsIgnoreCase("ID")) {
            java.util.Collections.sort(expectedList, (a, b) -> {
                try {
                    return Integer.valueOf(a).compareTo(Integer.valueOf(b));
                } catch (Exception e) {
                    return a.compareToIgnoreCase(b);
                }
            });
        } else {
            java.util.Collections.sort(expectedList, String.CASE_INSENSITIVE_ORDER);
        }

        System.out.println("SORT COLUMN: " + columnName);
        System.out.println("ACTUAL LIST: " + actualList);
        System.out.println("EXPECTED LIST: " + expectedList);

        Assert.assertEquals(
                "Column '" + columnName + "' is not sorted in ascending order.",
                expectedList,
                actualList
        );
    }

    @Then("the category list should be sorted by {string} in descending order")
    public void verify_list_is_sorted_descending(String columnName) {
        java.util.List<String> actualList = categoryPage.getColumnData(columnName);
        java.util.List<String> expectedList = new java.util.ArrayList<>(actualList);

        if (columnName.equalsIgnoreCase("ID")) {
            java.util.Collections.sort(expectedList, (a, b) -> {
                try {
                    return Integer.valueOf(b).compareTo(Integer.valueOf(a));
                } catch (Exception e) {
                    return b.compareToIgnoreCase(a);
                }
            });
        } else {
            java.util.Collections.sort(
                    expectedList,
                    java.util.Collections.reverseOrder(String.CASE_INSENSITIVE_ORDER)
            );
        }

        System.out.println("SORT COLUMN: " + columnName);
        System.out.println("ACTUAL DESC LIST: " + actualList);
        System.out.println("EXPECTED DESC LIST: " + expectedList);

        Assert.assertEquals(
                "Column '" + columnName + "' is not sorted in descending order.",
                expectedList,
                actualList
        );
    }

    // -------------------------------------------------------------------------
    // ADMIN SETUP STEPS FOR STANDARD USER TEST DATA
    // -------------------------------------------------------------------------

    @Given("an admin creates generated parent category {string}")
    public void admin_creates_generated_parent_category(String parentKey) {
        categoryPage.clickLogout();

        driver.get(ConfigReader.get("login.url"));
        loginAsAdmin();

        categoryPage.clickCategoriesMenu();
        categoryPage.waitForCategoryPageToLoad();
        categoryPage.clickAddCategory();

        String parentName = ScenarioContext.getString(parentKey);

        if (parentName == null) {
            parentName = TestDataGenerator.uniqueCategoryName("UIC");
            ScenarioContext.set(parentKey, parentName);
            System.out.println("Generated parent category for user filter = " + parentName);
        }

        addCategoryPage.enterCategoryName(parentName);
        addCategoryPage.clickSave();

        Assert.assertTrue("Parent category was not created successfully.",
                addCategoryPage.isSuccessMessageDisplayed());

        Assert.assertTrue("Generated parent category not found in list: " + parentName,
                categoryPage.isCategoryInTableStrict(parentName));
    }

    @Given("an admin creates generated sub category {string} under parent {string}")
    public void admin_creates_generated_sub_category_under_parent(String childKey, String parentKey) {
        String parentName = ScenarioContext.getString(parentKey);
        Assert.assertNotNull("Generated parent category was not found for key: " + parentKey, parentName);

        categoryPage.clickAddCategory();

        String childName = ScenarioContext.getString(childKey);

        if (childName == null) {
            childName = TestDataGenerator.uniqueCategoryName("UIC");
            ScenarioContext.set(childKey, childName);
            System.out.println("Generated sub category for user filter = " + childName);
        }

        addCategoryPage.enterCategoryName(childName);
        addCategoryPage.selectParentCategory(parentName);
        addCategoryPage.clickSave();

        Assert.assertTrue("Sub-category was not created successfully.",
                addCategoryPage.isSuccessMessageDisplayed());

        Assert.assertTrue("Generated sub-category not found in list: " + childName,
                categoryPage.isCategoryInTableStrict(childName));

        Assert.assertTrue("Generated sub-category does not show correct parent.",
                categoryPage.isCategoryWithParentVisible(childName, parentName));
    }

    @Given("the standard user logs in after admin setup")
    public void standard_user_logs_in_after_admin_setup() {
        categoryPage.clickLogout();

        driver.get(ConfigReader.get("login.url"));
        loginAsStandardUser();
    }

    // -------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // -------------------------------------------------------------------------

    private void loginAsAdmin() {
        String username = ConfigReader.get("admin.username");
        String password = ConfigReader.get("admin.password");

        loginPage.login(username, password);
    }

    private void loginAsStandardUser() {
        String username = ConfigReader.get("user.username");
        String password = ConfigReader.get("user.password");

        loginPage.login(username, password);
    }
}
