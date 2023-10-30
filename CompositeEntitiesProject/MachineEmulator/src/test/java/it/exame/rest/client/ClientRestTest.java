package it.exame.rest.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(value = WireMockTestServer.class, restrictToAnnotatedClass = true)
class ClientRestTest {

	@Inject
	@RestClient
	ClientRest client;

	@Test
	void testFindAll() {
		assertThat(client.findAll("ManufacturingMachine")).isEqualTo(List.of(Map.of("description", "Auger - Trivella",
				"id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0", "type", "ManufacturingMachine",
				"machineModel", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9")));
	}

	@Test
	void testFindEntity() {
		assertThat(client.findEntity("urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0"))
				.isEqualTo(Map.of("description", "Auger - Trivella", "id",
						"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0", "type", "ManufacturingMachine",
						"machineModel", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"));
	}

	@Test
	void testFindAttributeOfEntity() {
		assertThat(client.findAttributeOfEntity("urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0",
				"machineModel"))
				.isEqualTo(Map.of("description", "Auger - Trivella", "id",
						"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0", "type", "ManufacturingMachine",
						"machineModel", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"));
	}

	@Test
	void testFindEntityByFilter() {
		assertThat(client.findEntityByFilter(Map.of("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0",
				"machineModel", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9")))
				.isEqualTo(List.of(Map.of("description", "Auger - Trivella", "id",
						"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0", "type", "ManufacturingMachine",
						"machineModel", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9")));
	}

	@Test
	void testPostEntityMap() {
		assertThat(client.postEntity(
				new HashMap<>(Map.of("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0",
						"machineModel", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9", "description",
						"Auger - Trivella", "type", "ManufactoringMachine")))
				.getStatus()).isEqualTo(201);
	}

	@Test
	void testPostEntityList() {
		assertThat(client.postEntity(List
				.of(new HashMap<>(Map.of("id", "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0",
						"machineModel", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9", "description",
						"Auger - Trivella", "type", "ManufactoringMachine"))))
				.getStatus()).isEqualTo(201);
	}

	@Test
	void testDeleteEntity() {
		assertThat(client.delete("urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0").getStatus())
				.isEqualTo(204);
	}
}