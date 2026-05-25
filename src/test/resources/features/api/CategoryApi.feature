Feature: Category API Management

 @api @tc_api_01
Scenario: Verify standard user can retrieve a dynamically created category with a valid ID
  Given the API server is up and running
  And the user is authenticated as "admin"
  And the user creates a generated main category "GET_CATEGORY" through API
  And the user is authenticated as "user"
  When the user sends a GET request to "/api/categories" using generated category id "GET_CATEGORY"
  Then the API response status code should be 200
  And the response body should contain generated category id "GET_CATEGORY"
  And the response body should contain generated category name "GET_CATEGORY"


@api @known_bug @tc_api_01_schema
Scenario: Verify GET category response schema includes parent and subCategories fields
  Given the API server is up and running
  And the user is authenticated as "admin"
  And the user creates a generated main category "GET_SCHEMA_CATEGORY" through API
  And the user is authenticated as "user"
  When the user sends a GET request to "/api/categories" using generated category id "GET_SCHEMA_CATEGORY"
  Then the API response status code should be 200
  And the response body should contain generated category id "GET_SCHEMA_CATEGORY"
  And the response body should contain generated category name "GET_SCHEMA_CATEGORY"
  And the response body should contain the field "parent"
  And the response body should contain the field "subCategories"


@api @tc_api_02
Scenario: Verify standard user receives 404 when searching for a generated non-existent Category ID
  Given the API server is up and running
  And the user is authenticated as "admin"
  And the user prepares a generated non-existing category id "NON_EXISTING_CATEGORY_ID"
  And the user is authenticated as "user"
  When the user sends a GET request to "/api/categories" using generated non-existing category id "NON_EXISTING_CATEGORY_ID"
  Then the API response status code should be 404
  And the response body should contain the field "error" with string value "NOT_FOUND"
  And the response message should contain generated non-existing category id "NON_EXISTING_CATEGORY_ID"

 @api @known_bug @tc_api_03
Scenario Outline: Verify validation for invalid data type in ID parameter
  Given the API server is up and running
  And the user is authenticated as "user"
  When the user sends a GET request to "/api/categories/<invalidId>"
  Then the API response status code should be 400
  And the response body should contain the field "error" with string value "Bad Request"
  And the response body should contain the field "status" with value 400

  Examples:
    | invalidId     |
    | invalid_text  |
    | abc123        |
    | test          |


  @api @tc_api_04
Scenario: Verify unauthorized access protection for a valid generated category endpoint
  Given the API server is up and running
  And the user is authenticated as "admin"
  And the user creates a generated main category "AUTH_CATEGORY" through API
  And the user provides an invalid authentication token
  When the user sends a GET request to "/api/categories" using generated category id "AUTH_CATEGORY"
  Then the API response status code should be 401
  And the response body should contain the field "error" with string value "UNAUTHORIZED"
  And the response body should contain the field "message" with string value "Unauthorized - Use Basic Auth or JWT"


 @api @known_bug @tc_api_05
Scenario: Verify GET sub-category response includes parent relationship and subCategories information
  Given the API server is up and running
  And the user is authenticated as "admin"
  And the user creates a generated main category "GET_PARENT_CATEGORY" through API
  When the user sends a POST request to "/api/categories" with generated sub category "GET_SUB_CATEGORY" under generated parent "GET_PARENT_CATEGORY"
  Then the API response status code should be 201

  And the user is authenticated as "user"
  When the user sends a GET request to "/api/categories" using generated category id "GET_SUB_CATEGORY"
  Then the API response status code should be 200
  And the response body should contain generated category name "GET_SUB_CATEGORY"
  And the response body should contain the field "parent"
  And the response body should contain the field "subCategories"