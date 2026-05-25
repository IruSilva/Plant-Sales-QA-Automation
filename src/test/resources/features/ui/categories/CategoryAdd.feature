Feature: Category Management
  As an Admin
  I want to manage plant categories
  So that I can organize the inventory

  Background: Admin is logged in
    Given the user is on the login page
    When the user logs in with valid credentials

  # Test Case: TC_UI_ADMIN_CAT_01
  @tc01 @smoke
  Scenario: Verify Admin can successfully add a new Main Category
    Then the user is on the Category Management page
    When the user clicks the "Add A Category" button
    And the user enters generated category name "UI_MAIN_CATEGORY" in the Category Name field
    And the user leaves the Parent Category empty
    And the user clicks the Save button
    Then a success message should be displayed
    And the user should be redirected to the Category List
    And the generated category "UI_MAIN_CATEGORY" should appear in the list

  # Test Case: TC_UI_ADMIN_CAT_02
  @tc02 @regression
  Scenario: Verify Admin is prevented from creating a category with an empty Name
    Given the user opens the Category Management page
    When the user clicks the "Add A Category" button
    And the user leaves the Category Name field empty
    And the user leaves the Parent Category empty
    And the user clicks the Save button
    Then an error message should be displayed indicating the name is required
    And the user should remain on the Add Category page

 # Test Case: TC_UI_ADMIN_CAT_03
@tc03 @regression
Scenario: Verify Admin can successfully create a new Sub-Category
  Given the user opens the Category Management page

  When the user clicks the "Add A Category" button
  And the user enters generated category name "UI_PARENT_CATEGORY" in the Category Name field
  And the user leaves the Parent Category empty
  And the user clicks the Save button
  Then a success message should be displayed
  And the generated category "UI_PARENT_CATEGORY" should appear in the list

  When the user clicks the "Add A Category" button
  And the user enters generated category name "UI_SUB_CATEGORY" in the Category Name field
  And the user selects generated category "UI_PARENT_CATEGORY" from the Parent Category dropdown
  And the user clicks the Save button
  Then a success message should be displayed
  And the user should be redirected to the Category List
  And the generated category "UI_SUB_CATEGORY" should appear in the list
  And the generated category "UI_SUB_CATEGORY" should show generated category "UI_PARENT_CATEGORY" as its parent

  
  # Test Case: TC_UI_ADMIN_CAT_04
  @tc04 @regression @known_bug
  Scenario: Verify System filters category list for multi-word names
    Given the user opens the Category Management page

    When the user clicks the "Add A Category" button
    And the user enters generated category name "UI_MULTI_WORD_CATEGORY" in the Category Name field
    And the user leaves the Parent Category empty
    And the user clicks the Save button
    Then a success message should be displayed
    And the generated category "UI_MULTI_WORD_CATEGORY" should appear in the list

    When the user enters generated category "UI_MULTI_WORD_CATEGORY" in the search box
    And the user clicks the Search button
    Then the category list should only display the generated category "UI_MULTI_WORD_CATEGORY" record

  # Test Case: TC_UI_ADMIN_CAT_05
  @tc05 @regression @known_bug
  Scenario: Verify system prevents renaming a Sub-category to an existing name
    Given the user opens the Category Management page

    When the user clicks the "Add A Category" button
    And the user enters generated category name "UI_DUP_PARENT_CATEGORY" in the Category Name field
    And the user leaves the Parent Category empty
    And the user clicks the Save button
    Then a success message should be displayed
    And the generated category "UI_DUP_PARENT_CATEGORY" should appear in the list

    When the user clicks the "Add A Category" button
    And the user enters generated category name "UI_DUP_SUB_CATEGORY_A" in the Category Name field
    And the user selects generated category "UI_DUP_PARENT_CATEGORY" from the Parent Category dropdown
    And the user clicks the Save button
    Then a success message should be displayed
    And the generated category "UI_DUP_SUB_CATEGORY_A" should appear in the list

    When the user clicks the "Add A Category" button
    And the user enters generated category name "UI_DUP_SUB_CATEGORY_B" in the Category Name field
    And the user selects generated category "UI_DUP_PARENT_CATEGORY" from the Parent Category dropdown
    And the user clicks the Save button
    Then a success message should be displayed
    And the generated category "UI_DUP_SUB_CATEGORY_B" should appear in the list

    When the user enters generated category "UI_DUP_SUB_CATEGORY_A" in the search box
    And the user clicks the Search button
    And the user clicks the Edit button for generated category "UI_DUP_SUB_CATEGORY_A"
    And the user enters generated category name "UI_DUP_SUB_CATEGORY_B" in the Category Name field
    And the user clicks the Save button
    Then a user-friendly error message "Category name already exists" should be displayed

    # Test Case: TC_UI_ADMIN_CAT_06
# Known Bug: BUG_ADMIN_UI_CAT_002
@tc06 @regression @known_bug
Scenario: Verify Parent Category dropdown allows empty selection for creating a Main Category
  Given the user opens the Category Management page
  When the user clicks the "Add A Category" button
  Then the Parent Category dropdown should allow an empty selection