package it.exame.rest.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import it.exame.service.Service;
import jakarta.ws.rs.core.Response;

@QuarkusTest
class RestControllerTest {
	// TODO add test for consumption of json

	@InjectMock
	Service machineService;

	@InjectSpy(convertScopes = true)
	RestController machineRest;

	@Captor
	ArgumentCaptor<HashMap<String, Object>> mapCaptor;

	HashMap<String, Object> machine;
	HashMap<String, Object> operation;
	String jsonMachineInput = "{\"description\":\"Industrial machine to create plastic bottles\",\"id\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0\",\"type\":\"ManufacturingMachine\",\"installationNotes\": {\"docUri\": \"http://example.com/sample/machine-instructions.pdf\",\"value\": \"Installed according to manufacturer instructions.\"},\"installedAt\":\"2017-05-04T10:18:16Z\",\"location\":{\"coordinates\": \"[-104.99404, 39.75621]\", \"type\":\"Point\"}}";
	String jsonOperationInput = "{\"id\":\"urn:ngsi-ld:MachineOperation:27577638-bd8a-4732-b418-fc8b949a0b0f\",\"type\":\"ManufacturingMachineOperation\",\"machine\": \"urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e\"}";

	@BeforeEach
	void setup() {
		machine = new HashMap<>();
		machine.put("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0");
		machine.put("type", "ManufacturingMachine");
		machine.put("description", "Industrial machine to create plastic bottles");
		machine.put("installationNotes", Map.of("docUri", "http://example.com/sample/machine-instructions.pdf", "value",
				"Installed according to manufacturer instructions."));
		machine.put("installedAt", "2017-05-04T10:18:16Z");
		machine.put("location", Map.of("coordinates", "[-104.99404, 39.75621]", "type", "Point"));

		operation = new HashMap<>();
		operation.put("id", "urn:ngsi-ld:MachineOperation:27577638-bd8a-4732-b418-fc8b949a0b0f");
		operation.put("type", "ManufacturingMachineOperation");
		operation.put("machine", "urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e");

	}

	@Test
	void testGetAllElement() throws JsonProcessingException {
		when(machineService.getAllMachine()).thenReturn(List.of(machine));
		given().when().get("/machine/").then().statusCode(200)
				.body(is(equalTo(new ObjectMapper().writeValueAsString(List.of(machine)))));
	}

	@Test
	void testPostNewEntity() throws URISyntaxException, JsonProcessingException {

		mapCaptor = ArgumentCaptor.forClass(machine.getClass());
		when(machineService.saveMachine(machine)).thenReturn(Response.status(201).build());

		given().header("Content-type", "application/json").and().body(jsonMachineInput).when().post("/machine").then()
				.statusCode(201);
		verify(machineRest).postOneSingleMachine(mapCaptor.capture());
		assertThat(mapCaptor.getValue()).containsAllEntriesOf(machine);
		verify(machineService).saveMachine(machine);
	}

	@Test
	void testPostNewEntityComplex() throws URISyntaxException, JsonProcessingException {
		machine.put("componentMachine", List.of("urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7b9",
				"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d4f3"));
		String jsonInputComplex = "{\"description\":\"Industrial machine to create plastic bottles\",\"id\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0\",\"type\":\"ManufacturingMachine\",\"installationNotes\": {\"docUri\": \"http://example.com/sample/machine-instructions.pdf\",\"value\": \"Installed according to manufacturer instructions.\"},\"installedAt\":\"2017-05-04T10:18:16Z\",\"location\":{\"coordinates\": \"[-104.99404, 39.75621]\", \"type\":\"Point\"},\"componentMachine\": [\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7b9\",\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d4f3\"]}";
		when(machineService.saveMachine(machine)).thenReturn(Response.status(201).build());
		mapCaptor = ArgumentCaptor.forClass(machine.getClass());
		given().header("Content-type", "application/json").and().body(jsonInputComplex).when().post("/machine").then()
				.statusCode(201);
		verify(machineRest).postOneSingleMachine(mapCaptor.capture());
		assertThat(mapCaptor.getValue()).containsAllEntriesOf(machine);
		verify(machineService).saveMachine(machine);
	}

	@Test
	void testPostNotAMachine() throws URISyntaxException, JsonProcessingException {
		mapCaptor = ArgumentCaptor.forClass(operation.getClass());
		when(machineService.saveMachine(operation)).thenReturn(Response.status(422).build());
		given().header("Content-type", "application/json").and().body(jsonOperationInput).when().post("/machine").then()
				.statusCode(422);
		verify(machineRest).postOneSingleMachine(mapCaptor.capture());
		assertThat(mapCaptor.getValue()).containsAllEntriesOf(operation);
		verify(machineService).saveMachine(operation);
	}

	@Test
	void testGetPossibileOperation() throws JsonProcessingException {
		when(machineService.findOperationByMachineId(any(String.class))).thenReturn(Set.of("Run","Excave","Stop"));
		given().when().get("/machine/urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e/operation").then().statusCode(200)
				.body(is(equalTo(new ObjectMapper().writeValueAsString(Set.of("Run","Excave","Stop")))));
	}

	@Test
	void testPostExecuteOperation() throws Exception {
		when(machineService.executeOperation("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e", "activate",
				new HashMap<>())).thenReturn(Response.status(204).build());
		given().header("Content-type", "application/json").and().body(jsonOperationInput).when()
				.post("/machine/urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e/operation/activate").then()
				.statusCode(204);
		verify(machineRest).postOperationOnAMachine(eq("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e"),
				eq("activate"), eq(operation));
		verify(machineService).executeOperation("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e", "activate",
				operation);
	}

	@Test
	void testPostExecuteOperation_EmptyBody() throws Exception {
		when(machineService.executeOperation("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e", "activate",
				new HashMap<>())).thenReturn(Response.status(204).build());
		given().header("Content-type", "application/json").and().when()
				.post("/machine/urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e/operation/activate").then()
				.statusCode(204);
		verify(machineRest).postOperationOnAMachine(eq("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e"),
				eq("activate"), eq(null));
		verify(machineService).executeOperation("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e", "activate",
				new HashMap<>());
	}

	// manca get singola machine
	@Test
	void testDeleteMachineByUri() {
		when(machineService.deleteEntity(any(String.class))).thenReturn(Response.status(204).build());
		given().header("Content-type", "application/json").and().when()
				.delete("/machine/urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e").then().statusCode(204);
		verify(machineRest).deleteMachine(eq("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e"));
		verify(machineService).deleteEntity("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e");
	}

	@Test
	void testDeleteNotAMachineByUri() {
		when(machineService.deleteEntity(any(String.class))).thenReturn(Response.status(400).build());
		given().header("Content-type", "application/json").and().when()
				.delete("/machine/urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e").then().statusCode(400);
		verify(machineRest).deleteMachine(eq("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e"));
		verify(machineService).deleteEntity("urn:ngsi-ld:Machine:2033a7c7-d31b-48e7-91c2-014dc426c29e");
	}
}
