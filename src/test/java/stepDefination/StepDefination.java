package stepDefination;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pojo.AddPlace;
import pojo.Location;
import resources.APIResources;
import resources.TestDataBuild;
import resources.Utils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class StepDefination extends Utils {
	RequestSpecification res;
	ResponseSpecification resspec;
	Response response;
	JsonPath js;
	static String Place_id;
	TestDataBuild data = new TestDataBuild();

	@Given("Add Place Payload with {string} {string} {string}>")
	public void add_Place_Payload_with(String name, String language, String address) throws IOException {
		res = given().spec(requestSpecification()).body(data.addPlacePayLoad(name, language, address));
		throw new cucumber.api.PendingException();

	}

	@When("user calls {string} with {string} http request")
	public void user_calls_with_http_request(String resource, String method) {
		APIResources resourceAPI = APIResources.valueOf(resource);
		System.out.println(resourceAPI.getResource());
		resspec = new ResponseSpecBuilder().expectStatusCode(200).expectContentType(ContentType.JSON).build();
		if (method.equalsIgnoreCase("POST"))
			response = res.when().post(resourceAPI.getResource());
		else if (method.equalsIgnoreCase("GET"))
			response = res.when().get(resourceAPI.getResource());
	}

	@Then("the API call got success with status code {int}")
	public void the_API_call_got_success_with_status_code(Integer int1) {
		assertEquals(response.getStatusCode(), 200);
	}

	@Then("{string} in response body is {string}")
	public void in_response_body_is(String key, String Expectedvalue) {
		String resp = response.asString();
		js = new JsonPath(resp);

		assertEquals(getJsonPath(response, key), Expectedvalue);

	}

	@Then("verify place_Id created maps to {string} using {string}")
	public void verify_place_Id_created_maps_to_using(String expectedname, String resource) throws IOException {
		// reqspec
		Place_id = getJsonPath(response, "place_id");
		res = given().spec(requestSpecification()).queryParam("place_id", Place_id);
		user_calls_with_http_request(resource, "GET");
		String actualname = getJsonPath(response, "name");
		assertEquals(actualname, expectedname);
	}

	@Given("DeletePlace Payload")
	public void deleteplace_Payload() throws IOException {
		// Write code here that turns the phrase above into concrete actions

		res = given().spec(requestSpecification()).body(data.deletePlacePayload(Place_id));
	}
}