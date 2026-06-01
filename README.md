# Plant Sales UI Automation Framework

This repository contains an automated testing framework for the **QA Training Plant Sales** web application. The project focuses on validating the **Category Management** module using UI automation and behavior-driven test scenarios.

The framework is developed using **Java**, **Selenium WebDriver**, **Cucumber BDD**, **TestNG**, and **Maven**, with a maintainable structure based on the **Page Object Model**.

---

## Project Overview

The main purpose of this project is to automate important category management workflows in the Plant Sales application. The automated scenarios cover both **Admin** and **Standard User** behavior to verify that the module works correctly for different user roles.

The project demonstrates:

- UI automation using Selenium WebDriver
- BDD scenario writing using Cucumber feature files
- Test execution using TestNG and Maven
- Page Object Model implementation
- Role-based test coverage for Admin and Standard User workflows
- Data-driven test execution using generated and reusable test data
- Scenario-level data sharing using ScenarioContext
- Clean test execution using Cucumber tags
- Logging and reporting support

---

## Tech Stack

| Area | Technology |
|---|---|
| Programming Language | Java |
| UI Automation | Selenium WebDriver |
| BDD Framework | Cucumber |
| Test Runner | TestNG |
| Build Tool | Maven |
| API Testing Support | Rest Assured |
| Driver Management | WebDriverManager |
| Reporting Support | Extent Reports, Allure |
| Logging | Log4j2 |
| Design Pattern | Page Object Model |

---

## Module Covered

### Category Management

The automated test coverage focuses on the Category Management module of the Plant Sales application.

Covered areas include:

- Admin category creation
- Main category creation
- Sub-category creation
- Required field validation
- Category search
- Parent category filtering
- Standard User category access
- Standard User search and filtering
- Sorting validation
- Negative test scenarios

---

## Key Features Implemented

### 1. Page Object Model Structure

The framework follows the Page Object Model design pattern. Page-level actions and locators are separated from step definitions to improve readability, reusability, and maintainability.

This makes the test code easier to update when UI behavior changes.

---

### 2. Cucumber BDD Scenarios

Test cases are written in Gherkin syntax, making them readable for both technical and non-technical users.

Example:

```gherkin
Scenario: Verify Admin can successfully add a new Main Category
  Given the user is on the login page
  When the user logs in with valid credentials
  Then the user is on the Category Management page
  When the user clicks the "Add A Category" button
  And the user enters a category name in the Category Name field
  And the user clicks the Save button
  Then a success message should be displayed
```

---

### 3. Role-Based Test Coverage

The framework includes separate workflows for:

- **Admin users** who can manage categories
- **Standard users** who can view, search, filter, and sort category records

This helps validate the application behavior according to user permissions.

---

### 4. Dynamic and Reusable Test Data

The framework supports generated test data for category scenarios. Generated values are stored during test execution and reused in later steps when needed.

Example generated data keys:

```text
UI_MAIN_CATEGORY
UI_PARENT_CATEGORY
UI_SUB_CATEGORY
UI_MULTI_WORD_CATEGORY
USER_FILTER_PARENT_CATEGORY
USER_FILTER_SUB_CATEGORY
```

This approach helps reduce dependency on fixed database records and supports more reliable regression execution.

---

### 5. ScenarioContext Usage

`ScenarioContext` is used to store values generated during a scenario and reuse them across different step definitions.

Example usage:

```text
Generated category name -> Stored in ScenarioContext -> Used later for validation
```

This helps maintain continuity between test setup, action, and verification steps.

---

### 6. TestDataGenerator Utility

`TestDataGenerator` is used to create unique test data during runtime. This helps avoid duplicate-data issues and makes the automated tests more suitable for repeated execution.

---

### 7. Tag-Based Test Execution

Cucumber tags are used to run selected groups of tests.

| Tag | Purpose |
|---|---|
| `@smoke` | Runs critical high-level test scenarios |
| `@regression` | Runs regression test scenarios |
| `@tc01`, `@tc02`, etc. | Runs a specific Admin category test case |
| `@tc_user_01`, `@tc_user_02`, etc. | Runs a specific Standard User category test case |

---

## Project Structure

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

---

## Test Scenarios

### Admin Category Scenarios

| Test Case | Scenario |
|---|---|
| TC_UI_ADMIN_CAT_01 | Verify Admin can successfully add a new main category |
| TC_UI_ADMIN_CAT_02 | Verify Admin is prevented from creating a category with an empty name |
| TC_UI_ADMIN_CAT_03 | Verify Admin can successfully create a new sub-category |
| TC_UI_ADMIN_CAT_04 | Verify category search using multi-word category names |
| TC_UI_ADMIN_CAT_05 | Verify duplicate category name validation during update |
| TC_UI_ADMIN_CAT_06 | Verify category creation behavior with parent category selection |

### Standard User Category Scenarios

| Test Case | Scenario |
|---|---|
| TC_UI_USER_CAT_01 | Verify category management restrictions for non-admin users |
| TC_UI_USER_CAT_02 | Verify Standard User can search for an existing category |
| TC_UI_USER_CAT_03 | Verify Standard User can filter categories by parent category |
| TC_UI_USER_CAT_04 | Verify proper feedback for non-existing category search |
| TC_UI_USER_CAT_05A | Verify Standard User can sort categories by ID |
| TC_UI_USER_CAT_05B | Verify Standard User can sort categories by category name |
| TC_UI_USER_CAT_05C | Verify Standard User can sort categories by parent category |

---

## Prerequisites

Before running the automation framework, make sure the following are installed:

- Java JDK
- Maven
- Google Chrome browser
- Git
- Plant Sales application running locally

The application base URL is configured in:

```text
src/test/resources/config.properties
```

Default local URL:

```text
http://localhost:8080
```

---

## Configuration

The main execution settings are managed in `config.properties`.

Example configuration areas:

```properties
base.url=http://localhost:8080
login.url=http://localhost:8080/ui/login
browser=chrome
headless=false
implicit.wait=10
page.load.timeout=30
explicit.wait=20
screenshot.on.failure=true
```

User credentials and test data are also maintained through the configuration file to keep the framework easy to update.

---

## How to Run the Tests

### Run all tests

```powershell
mvn test
```

### Run smoke tests

```powershell
mvn test "-Dcucumber.filter.tags=@smoke"
```

### Run regression tests

```powershell
mvn test "-Dcucumber.filter.tags=@regression"
```

### Run a specific Admin test case

```powershell
mvn test "-Dcucumber.filter.tags=@tc01"
```

### Run a specific Standard User test case

```powershell
mvn test "-Dcucumber.filter.tags=@tc_user_02"
```

---

## Reporting and Logs

The framework includes reporting and logging support to make test execution easier to review.

Supported reporting and logging areas include:

- Console logs
- Log file generation
- Screenshot capture on failure
- Extent Reports support
- Allure reporting support

Log configuration is maintained using `log4j2.xml`.

---

## Demo Execution

For demonstration purposes, the browser can run in visible mode using:

```properties
headless=false
```

This allows the test execution flow to be recorded or presented clearly during a project demonstration.

---

## Project Highlights

This project showcases practical QA automation skills, including:

- Translating SRS-based test cases into automated BDD scenarios
- Automating role-based UI workflows
- Designing reusable Page Object classes
- Managing dynamic test data
- Using Maven commands for controlled test execution
- Organizing tests with Cucumber tags
- Validating both positive and negative scenarios
- Maintaining a clean and understandable automation framework structure

---

## Repository Purpose

This repository is created as a QA automation project to demonstrate the implementation of automated UI test scenarios for the Plant Sales application. It highlights practical automation concepts used in real-world QA work, including maintainability, reusability, role-based validation, and regression test execution.
