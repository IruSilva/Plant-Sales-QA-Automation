package com.example.api.stepDefinitions;

import com.example.utils.ConfigReader;
import com.example.utils.ScenarioContext;
import com.example.utils.TestDataGenerator;
import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CategoryApiStepDefinitions {

    // Shared variables used by API step definition classes
    public static String baseUrl = ConfigReader.get("base.url");
    public static String userToken;
    public static Response lastResponse;

    @Given("the API server is up and running")
    public void api_server_is_up() {
        RestAssured.baseURI = baseUrl;
    }

    @Given("the user is authenticated as {string}")
    public void user_is_authenticated(String role) {
        String username = ConfigReader.get(role + ".username");
        String password = ConfigReader.get(role + ".password");

        String loginPayload = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        Response loginResponse = given()
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post("/api/auth/login");

        if (loginResponse.getStatusCode() == 200) {
            userToken = loginResponse.jsonPath().getString("token");
        } else {
            throw new RuntimeException(
                    "Failed to authenticate via API. Status: " + loginResponse.getStatusCode()
                            + " Body: " + loginResponse.getBody().asString()
            );
        }
    }

    @Given("the user provides an invalid authentication token")
    public void user_provides_invalid_token() {
        userToken = "invalid_token_12345";
    }

    @When("the user sends a GET request to {string}")
    public void user_sends_get_request(String endpoint) {
        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .when()
                .get(endpoint);
    }

    @When("the user sends a POST request to {string} with name {string} and no parent")
    public void user_sends_post_category_main(String endpoint, String categoryName) {
        String payload = createMainCategoryPayload(categoryName);

        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(endpoint);
    }

    @When("the user sends a POST request to {string} with name {string} and parent ID {int}")
    public void user_sends_post_category_sub(String endpoint, String subCategoryName, int parentId) {
        String payload = createSubCategoryPayload(subCategoryName, parentId);

        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(endpoint);
    }

    @When("the user sends a POST request to {string} with generated main category {string}")
    public void user_sends_post_with_generated_main_category(String endpoint, String key) {
        String categoryName = TestDataGenerator.uniqueCategoryName("Cat");
        ScenarioContext.set(key, categoryName);

        String payload = createMainCategoryPayload(categoryName);

        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(endpoint);

        if (lastResponse.getStatusCode() == 201) {
            Integer generatedId = lastResponse.jsonPath().getInt("id");
            ScenarioContext.set(key + "_ID", generatedId);
        }
    }

    @Given("the user creates a generated main category {string} through API")
    public void user_creates_generated_main_category_through_api(String key) {
        String categoryName = TestDataGenerator.uniqueCategoryName("Par");
        ScenarioContext.set(key, categoryName);

        String payload = createMainCategoryPayload(categoryName);

        Response response = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/api/categories");

        response.then().statusCode(201);

        Integer generatedId = response.jsonPath().getInt("id");
        ScenarioContext.set(key + "_ID", generatedId);
    }

    @When("the user sends a POST request to {string} with generated sub category {string} under generated parent {string}")
    public void user_sends_post_with_generated_sub_category(String endpoint, String childKey, String parentKey) {
        String subCategoryName = TestDataGenerator.uniqueCategoryName("Sub");
        ScenarioContext.set(childKey, subCategoryName);

        Integer parentId = ScenarioContext.getInt(parentKey + "_ID");

        if (parentId == null) {
            throw new RuntimeException("Parent category ID was not found in ScenarioContext for key: " + parentKey);
        }

        String payload = createSubCategoryPayload(subCategoryName, parentId);

        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(endpoint);

        if (lastResponse.getStatusCode() == 201) {
            Integer generatedSubCategoryId = lastResponse.jsonPath().getInt("id");
            ScenarioContext.set(childKey + "_ID", generatedSubCategoryId);
        }
    }

    @When("the user sends a POST request to {string} with existing generated main category {string}")
    public void user_sends_post_with_existing_generated_main_category(String endpoint, String key) {
        String categoryName = ScenarioContext.getString(key);

        if (categoryName == null) {
            throw new RuntimeException("Generated category name was not found in ScenarioContext for key: " + key);
        }

        String payload = createMainCategoryPayload(categoryName);

        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post(endpoint);
    }

    @When("the user sends a GET request to {string} using generated category id {string}")
    public void user_sends_get_request_using_generated_category_id(String endpointPrefix, String key) {
        Integer categoryId = ScenarioContext.getInt(key + "_ID");

        if (categoryId == null) {
            throw new RuntimeException("Generated category ID was not found in ScenarioContext for key: " + key);
        }

        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .when()
                .get(endpointPrefix + "/" + categoryId);
    }

    @Given("the user prepares a generated non-existing category id {string}")
    public void user_prepares_generated_non_existing_category_id(String key) {
        String categoryName = TestDataGenerator.uniqueCategoryName("Tmp");
        String payload = createMainCategoryPayload(categoryName);

        Response response = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/api/categories");

        response.then().statusCode(201);

        Integer existingId = response.jsonPath().getInt("id");
        Integer nonExistingId = existingId + 100000;

        ScenarioContext.set(key, nonExistingId);
    }

    @When("the user sends a GET request to {string} using generated non-existing category id {string}")
    public void user_sends_get_request_using_generated_non_existing_category_id(String endpointPrefix, String key) {
        Integer nonExistingId = ScenarioContext.getInt(key);

        if (nonExistingId == null) {
            throw new RuntimeException("Generated non-existing category ID was not found in ScenarioContext for key: " + key);
        }

        lastResponse = given()
                .header("Authorization", "Bearer " + userToken)
                .header("Content-Type", "application/json")
                .when()
                .get(endpointPrefix + "/" + nonExistingId);
    }

    @Then("the API response status code should be {int}")
    public void verify_status_code(int expectedStatusCode) {
        if (lastResponse == null) {
            throw new RuntimeException("No API response found. Please check whether the request step executed correctly.");
        }

        System.out.println("Response Body: " + lastResponse.getBody().asString());
        lastResponse.then().statusCode(expectedStatusCode);
    }

    @Then("the response body should contain the field {string}")
    public void verify_body_has_field(String key) {
        lastResponse.then().body("$", hasKey(key));
    }

    @Then("the response body should contain the field {string} with value {int}")
    public void verify_body_field_int_value(String key, int value) {
        lastResponse.then().body(key, equalTo(value));
    }

    @Then("the response body should contain the field {string} with string value {string}")
    public void verify_body_field_string_value(String key, String expectedValue) {
        lastResponse.then().body(key, equalTo(expectedValue));
    }

    @Then("the response body should contain generated category id {string}")
    public void verify_response_contains_generated_category_id(String key) {
        Integer categoryId = ScenarioContext.getInt(key + "_ID");

        if (categoryId == null) {
            throw new RuntimeException("Generated category ID was not found in ScenarioContext for key: " + key);
        }

        lastResponse.then().body("id", equalTo(categoryId));
    }

    @Then("the response body should contain generated category name {string}")
    public void verify_generated_category_name(String key) {
        String categoryName = ScenarioContext.getString(key);

        if (categoryName == null) {
            throw new RuntimeException("Generated category name was not found in ScenarioContext for key: " + key);
        }

        lastResponse.then().body("name", equalTo(categoryName));
    }

    @Then("the response message should contain generated category name {string}")
    public void verify_response_message_contains_generated_category_name(String key) {
        String categoryName = ScenarioContext.getString(key);

        if (categoryName == null) {
            throw new RuntimeException("Generated category name was not found in ScenarioContext for key: " + key);
        }

        lastResponse.then().body("message", containsString(categoryName));
    }

    @Then("the response message should contain generated non-existing category id {string}")
    public void verify_response_message_contains_generated_non_existing_category_id(String key) {
        Integer nonExistingId = ScenarioContext.getInt(key);

        if (nonExistingId == null) {
            throw new RuntimeException("Generated non-existing category ID was not found in ScenarioContext for key: " + key);
        }

        lastResponse.then().body("message", containsString(nonExistingId.toString()));
    }

    private String createMainCategoryPayload(String categoryName) {
        return "{\n" +
                "  \"name\": \"" + categoryName + "\",\n" +
                "  \"parent\": null,\n" +
                "  \"subCategories\": []\n" +
                "}";
    }

    private String createSubCategoryPayload(String categoryName, int parentId) {
        return "{\n" +
                "  \"name\": \"" + categoryName + "\",\n" +
                "  \"parent\": { \"id\": " + parentId + " },\n" +
                "  \"subCategories\": []\n" +
                "}";
    }
}