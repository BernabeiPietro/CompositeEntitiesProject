package it.exame.rest.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import it.exame.scorpiobroker.ScorpioBrokerServer;

@QuarkusTest
@QuarkusTestResource(value = ScorpioBrokerServer.class, restrictToAnnotatedClass = true)
public class RestControllerServiceIT {

	String url = "http://localhost:9090/ngsi-ld/v1/entities/";
	String idMachine = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d890";
	String idModel = "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9";
	String idModelOperation = "urn:ngsi-ld:MachineOperationModel:27577638-bd8a-4732-b418-fc8b949a0t90";
	private Request request;
	private Response response;
	private String bodyMachine;
	private String bodyModel;
	private String bodyOperationModel;

	@BeforeEach
	void setup() throws ClientProtocolException, IOException {
		bodyMachine = "{\"@context\": [\"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\"], \"description\": \"Auger - Trivella\",\"id\":\""
				+ idMachine
				+ "\",\"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\"type\": \"ManufacturingMachine\",\"machineComponent\": {"
				+ "        \"value\":{\n"
				+ "           \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\","
				+ "           \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd64t9\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0\","
				+ "           \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd69c3\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5dq90\""
				+ "        }," + "\"type\":\"Property\"" + "}}";
		bodyModel = "{\n" + "    \"@context\": [\n"
				+ "        \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\"\n"
				+ "    ]," + "    \"id\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
				+ "    \"type\": \"ManufacturingMachineModel\",\n" + "    \"name\": \"Auger Machine\",\n"
				+ "    \"description\": \"Machine to digger\",\n" + "    \"brandName\": \"QuickT\",\n"
				+ "    \"version\": \"v1\",\n" + "    \"root\": \"false\",\n"
				+ "    \"machineModelParent\": \"urn:ngsi-ld:MachineModel:4146335f-839f-4ff9-a575-6b4e6232b734\",\n"
				+ "    \"machineModelChildren\":\n"
				+ "        \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
				+ "    \"processDescription\": \"Industrial Drilling\",\n" + "    \"operationModel\":{\n"
				+ "        \"type\": \"Property\",\n" + "        \"value\": \n"
				+ "            {\"Drill\": \"urn:ngsi-ld:MachineOperationModel:27577638-bd8a-4732-b418-fc8b949a0t90\","
				+ "				  \"Install\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd64t9\","
				+ "				 \"Forward\": \"urn:ngsi-ld:MachineOperationModel:27577638-bd8a-4732-b418-fc8b949a0t90\"}"
				+ "    }}";
		bodyOperationModel = "{\n" + "   \"@context\": [\n"
				+ "             \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\"\n"
				+ "   ],\n"
				+ "    \"id\": \"urn:ngsi-ld:MachineOperationModel:27577638-bd8a-4732-b418-fc8b949a0t90\",\n"
				+ "    \"type\": \"ManufacturingMachineOperationModel\",\n"
				+ "    \"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
				+ "    \"operationType\": \"process\",\n" + "    \"description\": \"Forward\",\n"
				+ "    \"operation\": \"Forward\",\n" + "    \"operationComposite\": {\n"
				+ "        \"type\": \"Property\",\n" + "        \"value\": \n" + "            {\n"
				+ "            \"Forward\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd69c3\", \n"
				+ "            \"Install\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd64t9\",\n"
				+ "            \"Drill\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\"\n"
				+ "            }\n" + "        \n" + "    }\n" + "}";
		response = postEntity(bodyMachine);
		postEntity(bodyModel);
		postEntity(bodyOperationModel);

	}

	@AfterEach
	void teardown() throws ClientProtocolException, IOException {
		Request.Delete(url + idMachine).execute();
		Request.Delete(url + idModel).execute();
		Request.Delete(url + idModelOperation).execute();
	}

	@Test
	void getAllMachineAvailableIT() {
		given().when().get("/machine/").then().statusCode(200).body(is(equalTo(
				"[{\"id\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d890\",\"type\":\"ManufacturingMachine\",\"description\":{\"type\":\"Property\",\"value\":\"Auger - Trivella\"},\"machineModel\":{\"type\":\"Property\",\"value\":\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\"},\"machineComponent\":{\"type\":\"Property\",\"value\":{\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd64t9\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0\",\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd69c3\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5dq90\"}}}]")));
	}

	@Test
	void getOperationOfMachineIT() {
		given().when().get("/machine/urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d890/operation").then()
				.statusCode(200).body(is(equalTo("[\"Drill\",\"Forward\",\"Install\"]")));
	}

	@Test
	void postNewEntityIT() throws ClientProtocolException, IOException {
		String idMachineToPost = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d480";

		String jsonMachineInput = "{\"description\": \"Auger - Trivella\",\"id\":\"" + idMachineToPost
				+ "\",\"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\"type\": \"ManufacturingMachine\"}";
		given().header("Content-type", "application/json").and().body(jsonMachineInput).when().post("/machine").then()
				.statusCode(201);
		assertThat(getEntity(idMachineToPost).returnContent().toString()).isEqualTo("{\n"
				+ "  \"id\" : \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d480\",\n"
				+ "  \"type\" : \"ManufacturingMachine\",\n" + "  \"description\" : {\n"
				+ "    \"type\" : \"Property\",\n" + "    \"value\" : \"Auger - Trivella\"\n" + "  },\n"
				+ "  \"machineModel\" : {\n" + "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\"\n" + "  },\n"
				+ "  \"@context\" : [ \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\", \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context-v1.7.jsonld\" ]\n"
				+ "}");
		Request.Delete(url + idMachineToPost).execute();
	}

	@Test
	void postExecuteOperationIT() throws Exception {
		List<HashMap<String, Object>> operation = new ObjectMapper().readValue(
				getEntityOfType("ManufacturingMachineOperation").returnContent().toString(), ArrayList.class);
		assertThat(operation).isEmpty();
		given().header("Content-type", "application/json").and().when()
				.post("/machine/urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d890/operation/forward").then()
				.statusCode(201);
		operation = new ObjectMapper().readValue(
				getEntityOfType("ManufacturingMachineOperation").returnContent().toString(), ArrayList.class);
		assertThat(operation).isNotEmpty();
		operation.forEach(x -> {
			try {
				Request.Delete(url + x.get("id")).execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Test
	void deleteIT() {
		given().header("Content-type", "application/json").and().when()
				.delete("/machine/urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d890").then().statusCode(204);
	}

	private Response postEntity(String bodyModel) throws ClientProtocolException, IOException {
		request = Request.Post(url).addHeader("Accept", "application/ld+json")
				.addHeader("Content-type", "application/ld+json")
				.bodyString(bodyModel, ContentType.parse("application/ld+json"));
		return request.execute();
	}

	private Response getEntity(String idEntity) throws ClientProtocolException, IOException {
		request = Request.Get(url + idEntity).addHeader("Accept", "application/ld+json")
				.addHeader("Content-type", "application/ld+json").addHeader("Link",
						"<https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\"");
		return request.execute();
	}

	private Response getEntityOfType(String type) throws ClientProtocolException, IOException {
		request = Request.Get(url + "?type=" + type).addHeader("Accept", "application/ld+json")
				.addHeader("Content-type", "application/ld+json").addHeader("Link",
						"<https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\"");
		return request.execute();
	}
}
