package it.exame.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.MockitoConfig;
import it.exame.rest.client.ClientRest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@QuarkusTest
class ServiceTest {

	@RestClient
	@InjectMock
	ClientRest restClient;
	@InjectMock
	@MockitoConfig(convertScopes = true)
	URIGenerator uriGen;
	@Inject
	Service service;

	private HashMap<String, Object> machine;
	private HashMap<String, Object> machineModel;

	@BeforeEach
	void setup() throws StreamReadException, DatabindException, IOException {
		machine = new HashMap<>();
		machine.put("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0");
		machine.put("type", "ManufacturingMachine");
		machine.put("description", "Industrial machine to create plastic bottles");
		machine.put("installationNotes", Map.of("docUri", "http://example.com/sample/machine-instructions.pdf", "value",
				"Installed according to manufacturer instructions."));
		machine.put("installedAt", "2017-05-04T10:18:16Z");
		machine.put("location", Map.of("coordinates", "[-104.99404, 39.75621]", "type", "Point"));
		machine.put("machineModel",
				Map.of("type", "Property", "value", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"));
		machineModel = new HashMap<>();
		machineModel.put("id", "urn:ngsi-ld:MachineModels:9166c528-9c98-4579-a5d3-8068aea5d6c0");
		machineModel.put("type", "ManufacturingMachineModel");
		machineModel.put("description", "Industrial machine to create plastic bottles");
		machineModel.put("installationNotes", Map.of("docUri", "http://example.com/sample/machine-instructions.pdf",
				"value", "Installed according to manufacturer instructions."));
		machineModel.put("installedAt", "2017-05-04T10:18:16Z");
		machineModel.put("location", Map.of("coordinates", "[-104.99404, 39.75621]", "type", "Point"));
		machineModel.put("operationModel", Map.of("type", "Property", "value",
				Map.of("Forward", "urn:ngsi-ld:MachineOperationModel:27577638-bd8a-4732-b418-fc8b949a0t90")));
	}

	@Test
	void testGetAll() {
		HashMap<String, Object> machine2 = new HashMap<>();
		machine2.put("id", "urn:ngsi-ld:Machine:27577638-bd8a-4732-b418-fc8b949a0b0f");
		machine2.put("type", "ManufacturingMachine");
		machine2.put("description", "Industrial machine to create plastic bottles");
		machine2.put("installationNotes", Map.of("docUri", "http://example.com/sample/machine-instructions.pdf",
				"value", "Installed according to manufacturer instructions."));
		machine2.put("installedAt", "2017-05-04T10:18:16Z");
		machine2.put("location", Map.of("coordinates", "[-104.99404, 39.75621]", "type", "Point"));
		when(restClient.findAll("ManufacturingMachine")).thenReturn(List.of(machine, machine2));
		assertThat(service.getAllMachine()).isEqualTo(List.of(machine, machine2));
	}

	@Test
	void testSaveMachine() {
		when(restClient.postEntity(any(HashMap.class))).thenReturn(Response.status(201).build());
		assertThat(service.saveMachine(machine).getStatus()).isEqualTo(201);
		verify(restClient).postEntity(machine);
	}

	@Test
	void testSaveNotMachine() {
		Response response = service.saveMachine(machineModel);
		assertThat(response.getStatusInfo().getReasonPhrase()).isEqualTo("It is not a ManufactoringMachine");
		assertThat(response.getStatus()).isEqualTo(422);
		verifyNoInteractions(restClient);
	}

	@Test
	void testFindAvailableOperationByMachineId() {
		when(restClient.findAttributeOfEntity("urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0","machineModel")).thenReturn(machine);
		when(restClient.findEntity("urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9")).thenReturn(machineModel);
		assertThat(service.findOperationByMachineId("urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0"))
				.isEqualTo(Set.of("Forward"));
		verify(restClient).findEntity(any(String.class));
		verify(restClient).findAttributeOfEntity(any(String.class), any(String.class));
	}

	@Test
	void testExecuteSingleOperation_detailsNotPresent() throws StreamReadException, DatabindException, IOException {
		String id = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0";
		String action = "Forward";
		HashMap<String, Object> operationalModel = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:MachineModelOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490", "type",
						"ManufacturingMachineOperationModel", "description", "Forward"));
		HashMap<String, Object> manufactoringMachine = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490"),
						"type", "ManufacturingMachine"));
		HashMap<String, Object> operation = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machine",
						Map.of("type", "Property", "value", id), "type", "ManufacturingMachineOperation", "description",
						Map.of("value", action, "type", "Property"), "plannedStartAt", "2016-08-22T10:18:16Z"));
		HashMap<String, Object> details = new HashMap<>(Map.of("plannedStartAt", "2016-08-22T10:18:16Z"));
		when(restClient.findEntity(anyString())).thenReturn(manufactoringMachine);
		when(restClient.findEntityByFilter(anyMap(), anyString(), anyString())).thenReturn(List.of(operationalModel));
		when(uriGen.generateURI(anyString()))
				.thenReturn("urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0");
		assertThatNoException().isThrownBy(() -> service.executeOperation(id, action, details));
		verify(restClient).findEntity(id);
		verify(restClient).findEntityByFilter(Map.of("type", "ManufacturingMachineOperationModel"),
				"description==" + action,
				"machineModel==urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490");
		verify(restClient).postEntity(operation);
	}

	@Test
	void testExecuteSingleOperation_detailsJustPresent() throws Exception {

		String id = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0";
		String action = "Forward";
		HashMap<String, Object> operationalModel = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:MachineModelOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490", "type",
						"ManufacturingMachineOperationModel", "description", "Forward"));
		HashMap<String, Object> manufactoringMachine = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490"),
						"type", "ManufacturingMachine"));
		HashMap<String, Object> operation = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machine",
						Map.of("type", "Property", "value", id), "type", "ManufacturingMachineOperation", "description",
						Map.of("value", action, "type", "Property")));
		HashMap<String, Object> details = new HashMap<>(Map.of("machine", Map.of("type", "Property", "value", id)));
		when(restClient.findEntity(anyString())).thenReturn(manufactoringMachine);
		when(restClient.findEntityByFilter(anyMap(), anyString(), anyString())).thenReturn(List.of(operationalModel));
		when(uriGen.generateURI(anyString()))
				.thenReturn("urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0");
		when(restClient.postEntity(any(HashMap.class))).thenReturn(Response.status(204).build());
		assertThat(service.executeOperation(id, action, details).getStatus()).isEqualTo(204);
		verify(restClient).findEntity(id);
		verify(restClient).findEntityByFilter(Map.of("type", "ManufacturingMachineOperationModel"),
				"description==" + action,
				"machineModel==urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490");
		verify(restClient).postEntity(operation);
	}

	@Test
	void testExecuteMultipleOperation() throws Exception {

		String id = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0";
		String action = "Forward";
		HashMap<String, Object> manufactoringMachine = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490"),
						"type", "ManufacturingMachine", "machineComponent",
						Map.of("type", "Property", "value",
								Map.of("urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9",
										"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0"))));
		HashMap<String, Object> operationalModel = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:MachineModelOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490", "type",
						"ManufacturingMachineOperationModel", "description", "Forward", "operationComposite",
						Map.of("type", "property", "value",
								Map.of("Drill", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"))));
		List<HashMap<String, Object>> operation = List.of(
				new HashMap<>(Map.of("id", "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d908",
						"machine",
						Map.of("type", "Property", "value", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0"),
						"type", "ManufacturingMachineOperation", "description",
						Map.of("value", "Drill", "type", "Property"), "plannedStartAt", "2016-08-22T10:18:16Z")),
				new HashMap<>(Map.of("id", "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0",
						"machine", Map.of("type", "Property", "value", id), "type", "ManufacturingMachineOperation",
						"description", Map.of("value", action, "type", "Property"), "plannedStartAt",
						"2016-08-22T10:18:16Z", "operationComposite", Map.of("type", "Relationship", "object",
								List.of("urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d908"))

				)));
		HashMap<String, Object> details = new HashMap<>(Map.of("plannedStartAt", "2016-08-22T10:18:16Z"));
		when(restClient.findEntity(anyString())).thenReturn(manufactoringMachine);
		when(restClient.findEntityByFilter(Map.of("type", "ManufacturingMachineOperationModel"),
				"description==" + action,
				"machineModel==urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490"))
				.thenReturn(List.of(operationalModel));
		when(uriGen.generateURI(anyString())).thenReturn(
				"urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d908",
				"urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0");
		when(restClient.postEntity(operation)).thenReturn(Response.status(201).build());
		assertThat(service.executeOperation(id, action, details).getStatus()).isEqualTo(201);
		verify(restClient).findEntity(id);
		verify(restClient).findEntityByFilter(Map.of("type", "ManufacturingMachineOperationModel"),
				"description==" + action,
				"machineModel==urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490");
		verify(restClient).postEntity(operation);
	}

	@Test
	void testExecuteMultipleOperation_badFormed() throws StreamReadException, DatabindException, IOException {

		String id = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0";
		String action = "Forward";
		HashMap<String, Object> manufactoringMachine = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490"),
						"type", "ManufacturingMachine", "machineComponent",
						Map.of("type", "Property", "value",
								Map.of("urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9",
										"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0"))));
		HashMap<String, Object> operationalModel = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:MachineModelOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490", "type",
						"ManufacturingMachineOperationModel", "description", "Forward", "operationComposite",
						Map.of("type", "property", "value",
								Map.of("Drill", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"))));
		List<HashMap<String, Object>> operation = List.of(
				new HashMap<>(Map.of("id", "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d908",
						"machine",
						Map.of("type", "Property", "value", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0"),
						"type", "ManufacturingMachineOperation", "description",
						Map.of("value", "Drill", "type", "Property"), "plannedStartAt", "2016-08-22T10:18:16Z")),
				new HashMap<>(Map.of("id", "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0",
						"machine", Map.of("type", "Property", "value", id), "type", "ManufacturingMachineOperation",
						"description", Map.of("value", action, "type", "Property"), "plannedStartAt",
						"2016-08-22T10:18:16Z", "operationComposite",
						Map.of("object", List.of("urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d908"),
								"type", "Relationship"))));
		HashMap<String, Object> details = new HashMap<>(Map.of("plannedStartAt", "2016-08-22T10:18:16Z"));
		when(restClient.findEntity(anyString())).thenReturn(manufactoringMachine);
		when(restClient.findEntityByFilter(Map.of("type", "ManufacturingMachineOperationModel"),
				"description==" + action,
				"machineModel==urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490"))
				.thenReturn(List.of(operationalModel));
		when(uriGen.generateURI(anyString())).thenReturn(
				"urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d908",
				"urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0");

		assertThatNoException().isThrownBy(() -> service.executeOperation(id, action, details));
		verify(restClient).findEntity(id);
		verify(restClient).findEntityByFilter(Map.of("type", "ManufacturingMachineOperationModel"),
				"description==" + action,
				"machineModel==urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490");
		verify(restClient).postEntity(operation);
	}

	@Test
	void test_GetMap_checkValueMap() {
		assertThat(service.getMap(Map.of("key", Map.of("key2", "value")), "key")).isEqualTo(Map.of("key2", "value"));
	}

	@Test
	void test_GetMap_valueMapNotPresent() {
		assertThat(service.getMap(Map.of("key", "value"), "key")).isEqualTo(null);
	}

	@Test
	void test_GetMap_keyNotPresent() {
		assertThat(service.getMap(Map.of("key", "value"), "key2")).isEqualTo(null);
	}

	@Test
	void test_getValueOfPropertyField() {
		assertThat(
				service.getValueOfPropertyField(Map.of("key", Map.of("value", Map.of("goalKey", "goalValue"))), "key"))
				.isEqualTo(Map.of("goalKey", "goalValue"));
	}

	@Test
	void test_getValueOfPropertyField_notPresentValueKey() {
		assertThat(service.getValueOfPropertyField(Map.of("key", Map.of("goalKey", "goalValue")), "key"))
				.isEqualTo(Map.of("goalKey", "goalValue"));
	}

	@Test
	void testExecuteOperation_NotAMachineId() {
		String id = "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0";
		String action = "Forward";
		HashMap<String, Object> machineOperation = new HashMap<>(
				Map.of("id", "urn:ngsi-ld:MachineOperation:9166c528-9c98-4579-a5d3-8068aea5d9b0", "machineModel",
						"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd6490", "type",
						"ManufacturingMachineOperation"));
		HashMap<String, Object> details = new HashMap<>();
		when(restClient.findEntity(id)).thenReturn(machineOperation);
		assertThatException().isThrownBy(() -> service.executeOperation(id, action, details))
				.hasFieldOrPropertyWithValue("message", "Entity retrieved is not a ManufacturingMachine");
		verify(restClient).findEntity(id);
		verifyNoMoreInteractions(restClient);
	}

	@Test
	void testDeleteEntity() {
		String id = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0";
		when(restClient.findAttributeOfEntity(id, "type")).thenReturn(machine);
		when(restClient.delete(id)).thenReturn(Response.status(204).build());
		assertThat(service.deleteEntity(id).getStatus()).isEqualTo(204);
		verify(restClient).delete(id);
	}

	@Test
	void testDeleteEntity_notAMachine() {
		String id = "urn:ngsi-ld:MachineModel:9166c528-9c98-4579-a5d3-8068aea5d6c0";
		when(restClient.findAttributeOfEntity(id, "type")).thenReturn(machineModel);
		assertThat(service.deleteEntity(id).getStatus()).isEqualTo(400);
		verify(restClient).findAttributeOfEntity(id, "type");
		verifyNoMoreInteractions(restClient);
	}
}
