Feature: Admin Category Management API

  @api @admin @tc_admin_create_01
  Scenario: Verify Admin can successfully create a new Main Category
    Given the API server is up and running
    And the user is authenticated as "admin"
    When the user sends a POST request to "/api/categories" with generated main category "MAIN_CATEGORY"
    Then the API response status code should be 201
    And the response body should contain generated category name "MAIN_CATEGORY"
    And the response body should contain the field "id"
    And the response body should contain the field "subCategories"


 @api @admin @known_bug @tc_admin_create_02
Scenario: Verify POST sub-category response includes parent relationship information
  Given the API server is up and running
  And the user is authenticated as "admin"
  And the user creates a generated main category "PARENT_CATEGORY" through API
  When the user sends a POST request to "/api/categories" with generated sub category "SUB_CATEGORY" under generated parent "PARENT_CATEGORY"
  Then the API response status code should be 201
  And the response body should contain generated category name "SUB_CATEGORY"
  And the response body should contain the field "id"
  And the response body should contain the field "parent"


  @api @admin @tc_admin_create_03
Scenario Outline: Verify validation for invalid Category Name length
  Given the API server is up and running
  And the user is authenticated as "admin"
  When the user sends a POST request to "/api/categories" with name "<categoryName>" and no parent
  Then the API response status code should be 400
  And the response body should contain the field "error" with string value "BAD_REQUEST"
  And the response body should contain the field "message" with string value "Validation failed"

Examples:
  | categoryName |
  | AB           |
  | A            |
  | ABCDEFGHIJK  |


  @api @admin @tc_admin_create_04
  Scenario: Verify Unique Name Constraint prevents duplicate Main Categories
    Given the API server is up and running
    And the user is authenticated as "admin"
    When the user sends a POST request to "/api/categories" with generated main category "DUPLICATE_CATEGORY"
    Then the API response status code should be 201
    And the response body should contain generated category name "DUPLICATE_CATEGORY"

    When the user sends a POST request to "/api/categories" with existing generated main category "DUPLICATE_CATEGORY"
    Then the API response status code should be 400
    And the response body should contain the field "error" with string value "DUPLICATE_RESOURCE"
    And the response message should contain generated category name "DUPLICATE_CATEGORY"


  @api @admin @tc_admin_create_05
  Scenario: Verify Role-Based Access Control prevents standard user from creating category
    Given the API server is up and running
    And the user is authenticated as "user"
    When the user sends a POST request to "/api/categories" with generated main category "USER_RESTRICTED_CATEGORY"
    Then the API response status code should be 403
    And the response body should contain the field "error" with string value "Forbidden"