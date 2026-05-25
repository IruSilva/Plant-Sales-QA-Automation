Plant Sales UI Automation Framework
This project contains automated UI and API test scenarios for the QA Training Plant Sales application. The framework uses Java, Selenium WebDriver, Cucumber BDD, TestNG, Maven, Page Object Model, and dynamic test data utilities.
Project Enhancement Summary
The category automation module was enhanced to make the framework more professional, maintainable, and reliable.
Main Improvements Completed
Removed hardcoded category names from UI category scenarios.
Replaced fixed test data such as `Lotus`, `Coconut`, `Rose SL`, `Catcus`, and `XYZ_Invalid` with generated or dynamically selected test data.
Introduced generated data keys in feature files, such as:
`UI_MAIN_CATEGORY`
`UI_PARENT_CATEGORY`
`UI_SUB_CATEGORY`
`UI_MULTI_WORD_CATEGORY`
`USER_FILTER_PARENT_CATEGORY`
`USER_FILTER_SUB_CATEGORY`
Used `ScenarioContext` to store generated values during scenario execution and reuse them in later steps.
Used `TestDataGenerator` to create unique category names and avoid duplicate data failures.
Converted user category search and filter scenarios to dynamic data-driven behavior.
Added setup flow where Admin creates required category data, then Standard User verifies search/filter behavior.
Separated known application bugs using the `@known_bug` tag.
Replaced many fixed waits in step definitions with Page Object wait methods.
Split sorting validation into separate test cases for ID, Category Name, and Parent Category.
Added ascending and descending sorting verification for supported columns.
Framework Structure
```text
src/test/java/com/example
├── hooks
│   └── Hooks.java
├── runners
│   └── CucumberRunner.java
├── ui
│   ├── pages
│   │   ├── categories
│   │   │   ├── CategoryPage.java
│   │   │   └── AddCategoryPage.java
│   │   └── login
│   │       └── LoginPage.java
│   └── stepDefinitions
│       └── categories
│           └── CategoryStepDefinitions.java
└── utils
    ├── ConfigReader.java
    ├── ScenarioContext.java
    └── TestDataGenerator.java

src/test/resources
├── features
│   └── ui
│       └── categories
│           ├── CategoryAdd.feature
│           └── UserCategoryAdd.feature
├── config.properties
└── testng.xml
```
Data-Driven Testing Approach
The framework no longer depends on static category names for most category scenarios. Instead, the test creates or reads data dynamically during execution.
Example: Generated Category Name
Feature file step:
```gherkin
And the user enters generated category name "UI_MAIN_CATEGORY" in the Category Name field
Then the generated category "UI_MAIN_CATEGORY" should appear in the list
```
Runtime behavior:
```text
UI_MAIN_CATEGORY = UIC123456
```
The generated category name is stored in `ScenarioContext`, so later steps can reuse the same value.
Example: Standard User Search
Instead of searching for a hardcoded category such as `Lotus`, the test reads an existing category from the table and searches for that value.
```gherkin
When the user enters existing category name from the list in the search box
Then the category list should display the searched category record
```
This makes the test independent from old database records.
Example: Standard User Parent Filter
For parent filtering, the test now creates data using Admin first, then verifies filtering as Standard User.
```gherkin
Given an admin creates generated parent category "USER_FILTER_PARENT_CATEGORY"
And an admin creates generated sub category "USER_FILTER_SUB_CATEGORY" under parent "USER_FILTER_PARENT_CATEGORY"
And the standard user logs in after admin setup
When the user selects generated category "USER_FILTER_PARENT_CATEGORY" from the search parent dropdown
Then the generated category "USER_FILTER_SUB_CATEGORY" should appear in the list
```
This approach prevents failures in fresh databases where no suitable parent/sub-category records exist.
Tags Used
Tag	Purpose
`@smoke`	Runs critical high-level tests only
`@regression`	Runs regression test scenarios
`@known_bug`	Marks valid tests that currently fail because of known application defects
`@tc01`, `@tc02`, etc.	Runs a specific admin category test
`@tc_user_01`, `@tc_user_02`, etc.	Runs a specific standard user category test
How to Run Tests
Run one test case
```powershell
mvn test "-Dcucumber.filter.tags=@tc01"
```
```powershell
mvn test "-Dcucumber.filter.tags=@tc_user_02"
```
Run smoke tests
```powershell
mvn test "-Dcucumber.filter.tags=@smoke"
```
Run all regression tests
```powershell
mvn test "-Dcucumber.filter.tags=@regression"
```
Run clean regression excluding known bugs
```powershell
mvn test "-Dcucumber.filter.tags=@regression and not @known_bug"
```
This is the recommended command for final stable execution.
Category UI Test Status
Admin Category Tests
Test Case	Description	Status
`TC_UI_ADMIN_CAT_01`	Admin can create a generated main category	Passed
`TC_UI_ADMIN_CAT_02`	Admin cannot create category with empty name	Passed
`TC_UI_ADMIN_CAT_03`	Admin can create generated sub-category under generated parent	Passed
`TC_UI_ADMIN_CAT_04`	Multi-word category search	Known Bug
`TC_UI_ADMIN_CAT_05`	Duplicate sub-category rename validation	Known Bug
`TC_UI_ADMIN_CAT_06`	Parent dropdown should allow empty selection	Known Bug
Standard User Category Tests
Test Case	Description	Status
`TC_UI_USER_CAT_01`	Standard User should not access Edit Category page	Known Bug
`TC_UI_USER_CAT_02`	Standard User can search existing category dynamically	Passed
`TC_UI_USER_CAT_03`	Standard User can filter by generated parent category	Passed
`TC_UI_USER_CAT_04`	Standard User sees “No category found” for generated invalid search	Passed
`TC_UI_USER_CAT_05A`	Standard User can sort by ID ascending and descending	Passed
`TC_UI_USER_CAT_05B`	Standard User can sort by Category Name descending and ascending	Passed
`TC_UI_USER_CAT_05C`	Parent Category sorting	Known Bug
Known Bugs
`BUG_ADMIN_UI_CAT_002`
Parent Category dropdown prevents empty selection, although the SRS states that Parent Category selection is optional and empty parent selection should create a main category.
Related scenario:
```text
@tc06 @known_bug
```
`BUG_ADMIN_UI_CAT_003`
Search filter returns “No category found” when searching for valid multi-word category names.
Related scenario:
```text
@tc04 @known_bug
```
Duplicate Category Rename Error
When renaming a sub-category to an existing name, the UI displays a raw backend `500 INTERNAL_SERVER_ERROR` instead of a user-friendly validation message.
Related scenario:
```text
@tc05 @known_bug
```
`BUG_USER_UI_CAT_001`
Edit button is visible to non-admin users and allows access to the Edit Category page.
Related scenario:
```text
@tc_user_01 @known_bug
```
`BUG_USER_UI_CAT_003`
Parent Category column sorting is non-functional.
Related scenario:
```text
@tc_user_05c @known_bug
```
Locator Improvement Notes
The current framework is functional and the clean regression passes. However, some locators still use absolute XPath values such as:
```java
/html/body/div[1]/div/div[2]/div[2]/table
```
For future maintainability, these should be gradually replaced with more stable locators such as:
```java
By.cssSelector("table")
By.cssSelector("a[href='/ui/categories/add']")
By.id("name")
By.id("parentId")
By.xpath("//button[contains(normalize-space(), 'Save')]")
```
Recommended locator priority:
```text
1. id
2. name
3. stable CSS selector
4. link text
5. relative XPath
6. absolute XPath only as last option
```
Final Regression Result
The clean regression suite passed using:
```powershell
mvn test "-Dcucumber.filter.tags=@regression and not @known_bug"
```
This confirms that stable scenarios pass while known application defects are excluded using the `@known_bug` tag.
Notes for Visual Execution
The browser is visible because `headless=false` is configured in `config.properties`.
For screen recording or demo purposes, a temporary wait may be kept in `Hooks.java` before `driver.quit()`.
For final clean automation, remove the temporary wait and keep:
```java
if (driver != null) {
    driver.quit();
}
```