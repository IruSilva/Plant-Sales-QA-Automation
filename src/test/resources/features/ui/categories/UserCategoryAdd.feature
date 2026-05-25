Feature: Category Management (Standard User)
  As a Standard User
  I want to view categories
  So that I can see product organization
  But I should NOT be able to edit or delete them

  Background: Standard User is logged in
    Given the user is on the login page
    When the standard user logs in

 # Test Case: TC_UI_USER_CAT_01
# Known Bug: Standard user can access Edit Category page even though SRS allows Add/Edit/Delete Category only for Admin
@tc_user_01 @regression @known_bug
Scenario: Verify Edit Category functionality is restricted for non-admin users
  Given the user opens the Category Management page
  When the user clicks the Edit button for any category
  Then the user should not be navigated to the Edit Category page


  # Test Case: TC_UI_USER_CAT_02
  @tc_user_02 @regression
  Scenario: Verify non-admin User can successfully search for an existing category
    Given the user opens the Category Management page
    Then the Search input field should be enabled
    When the user enters existing category name from the list in the search box
    And the user clicks the Search button
    Then the category list should display the searched category record

 # Test Case: TC_UI_USER_CAT_03
@tc_user_03 @regression
Scenario: Verify non-admin User can filter the category list by selecting a Parent Category
  Given an admin creates generated parent category "USER_FILTER_PARENT_CATEGORY"
  And an admin creates generated sub category "USER_FILTER_SUB_CATEGORY" under parent "USER_FILTER_PARENT_CATEGORY"
  And the standard user logs in after admin setup
  Given the user opens the Category Management page
  Then the Parent Category search dropdown should be enabled
  When the user selects generated category "USER_FILTER_PARENT_CATEGORY" from the search parent dropdown
  And the user clicks the Search button
  Then the generated category "USER_FILTER_SUB_CATEGORY" should appear in the list
  And the generated category "USER_FILTER_SUB_CATEGORY" should show generated category "USER_FILTER_PARENT_CATEGORY" as its parent

  
  # Test Case: TC_UI_USER_CAT_04
  @tc_user_04 @regression
  Scenario: Verify correct feedback when User searches for a non-existent category
    Given the user opens the Category Management page
    When the user enters generated invalid category name in the search box
    And the user clicks the Search button
    Then the message "No category found" is displayed clearly in the table area

# Test Case: TC_UI_USER_CAT_05
# Test Case: TC_UI_USER_CAT_05A
@tc_user_05a @regression
Scenario: Verify non-admin User can sort category list by ID in ascending and descending order
  Given the user opens the Category Management page

  When the user clicks the "ID" column header
  Then the category list should be sorted by "ID" in ascending order

  When the user clicks the "ID" column header
  Then the category list should be sorted by "ID" in descending order


# Test Case: TC_UI_USER_CAT_05B
@tc_user_05b @regression
Scenario: Verify non-admin User can sort category list by Category Name in descending and ascending order
  Given the user opens the Category Management page

  When the user clicks the "Category Name" column header
  Then the category list should be sorted by "Category Name" in descending order

  When the user clicks the "Category Name" column header
  Then the category list should be sorted by "Category Name" in ascending order
  

# Test Case: TC_UI_USER_CAT_05C
# Known Bug: BUG_USER_UI_CAT_003
@tc_user_05c @regression @known_bug
Scenario: Verify non-admin User can sort category list by Parent Category in ascending and descending order
  Given the user opens the Category Management page

  When the user clicks the "Parent Category" column header
  Then the category list should be sorted by "Parent Category" in ascending order

  When the user clicks the "Parent Category" column header
  Then the category list should be sorted by "Parent Category" in descending order  