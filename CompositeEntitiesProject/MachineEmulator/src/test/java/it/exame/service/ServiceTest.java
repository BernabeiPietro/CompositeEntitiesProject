package it.exame.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
import org.mockito.ArgumentCaptor;

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
	void testCreateSubscription() {
		HashMap<String, Object> subscription = new HashMap<>(Map.of( "type", "Subscription",
				"entities", List.of(Map.of("type", "ManufacturingMachineOperation")), "notification", Map.of("endpoint",
						Map.of("uri", "http://172.17.0.1:8090/notification", "accept", "application/json"))));
		ArgumentCaptor<HashMap<String,Object>> argument=ArgumentCaptor.forClass(HashMap.class);
		when(restClient.postSubscription(any(HashMap.class))).thenReturn(Response.status(201).build());
		assertThat(service.createSubscription().getStatus()).isEqualTo(201);
		verify(restClient).postSubscription(argument.capture());
		assertThat(argument.getValue()).containsAllEntriesOf(subscription);
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
	void testSave() {
		when(restClient.postEntity(any(HashMap.class))).thenReturn(Response.status(201).build());
		assertThat(service.save(machine).getStatus()).isEqualTo(201);
		verify(restClient).postEntity(machine);
	}
	@Test
	void testSaveList() {
		when(restClient.postEntity(any(List.class))).thenReturn(Response.status(201).build());
		assertThat(service.saveListOfElement(List.of(machine)).getStatus()).isEqualTo(201);
		verify(restClient).postEntity(List.of(machine));
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
	void test_deleteEntities()
	{
		when(restClient.delete(anyString())).thenReturn(Response.status(204).build());
		assertThat(service.deleteEntity("idtoremove").getStatus()).isEqualTo(204);
	}
}
