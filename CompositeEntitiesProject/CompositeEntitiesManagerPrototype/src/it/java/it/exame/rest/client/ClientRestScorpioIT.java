package it.exame.rest.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import it.exame.scorpiobroker.ScorpioBrokerServer;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(value = ScorpioBrokerServer.class, restrictToAnnotatedClass = true)
class ClientRestScorpioIT {

	@Inject
	@RestClient
	ClientRest client;
	String url = "http://localhost:9090/ngsi-ld/v1/entities/";
	String id = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d890";
	private Request request;
	private Response response;
	private String body;

	@BeforeEach
	void setup() throws ClientProtocolException, IOException {
		body = "{\"@context\": [\"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\"], \"description\": \"drill\",\"id\":\""
				+ id
				+ "\",\"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\"type\": \"ManufacturingMachine\"}";
		request = Request.Post(url).addHeader("Accept", "application/ld+json")
				.addHeader("Content-type", "application/ld+json")
				.bodyString(body, ContentType.parse("application/ld+json"));
		response = request.execute();
	}

	@AfterEach
	void teardown() throws ClientProtocolException, IOException {
		Request.Delete(url + id).execute();
	}

	@Test
	void findAllIT() throws ClientProtocolException, IOException {
		assertThat(client.findAll("ManufacturingMachine")).isEqualTo(List.of(
				Map.of("description", Map.of("type", "Property", "value", "drill"), "id", id, "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"),
						"type", "ManufacturingMachine")));

	}

	@Test
	void findEntityIT() throws ClientProtocolException, IOException {

		assertThat(client.findEntity(id)).isEqualTo(
				Map.of("description", Map.of("type", "Property", "value", "drill"), "id", id, "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"),
						"type", "ManufacturingMachine"));

	}

	@Test
	void findAttributeOfEntityIT() throws ClientProtocolException, IOException {

		assertThat(client.findAttributeOfEntity(id, "machineModel")).isEqualTo(Map.of("id", id, "machineModel",
				Map.of("type", "Property", "value", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"),
				"type", "ManufacturingMachine"));

	}

	@Test
	void findEntityByFilterIT() throws ClientProtocolException, IOException {

		assertThat(client.findEntityByFilter(
				Map.of("id", id), "machineModel==urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9","description==drill"))
				.isEqualTo(List.of(Map.of("description", Map.of("type", "Property", "value", "drill"), "id",
						id, "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"),
						"type", "ManufacturingMachine")));

	}

	@Test
	void postEntityIT() throws ClientProtocolException, IOException {

		String idPost = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d780";

		assertThat(client.postEntity(new HashMap<>(Map.of("id", idPost, "machineModel",
				Map.of("type", "Property", "value", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"),
				"type", "ManufacturingMachine"))).getStatus()).isEqualTo(201);
		response = getEntity(idPost);
		assertThat(response.returnContent().toString()).isEqualTo("{\n"
				+ "  \"id\" : \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d780\",\n"
				+ "  \"type\" : \"ManufacturingMachine\",\n" + "  \"machineModel\" : {\n"
				+ "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\"\n" + "  },\n"
				+ "  \"@context\" : [ \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\", \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context-v1.7.jsonld\" ]\n"
				+ "}");
		Request.Delete(url + idPost).execute();
	}

	@Test
	void postEntityBatchIT() throws ClientProtocolException, IOException {
		String idPost1 = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d780";
		String idPost2 = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d970";
		assertThat(getEntity(idPost1).returnResponse().getStatusLine().getStatusCode()).isEqualTo(404);
		assertThat(getEntity(idPost2).returnResponse().getStatusLine().getStatusCode()).isEqualTo(404);
		assertThat(client.postEntity(List.of(
				new HashMap<>(Map.of("id", idPost1, "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"),
						"type", "ManufacturingMachine")),
				new HashMap<>(Map.of("id", idPost2, "machineModel",
						Map.of("type", "Property", "value",
								"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd67j9"),
						"type", "ManufacturingMachine"))))
				.getStatus()).isEqualTo(201);

		assertThat(getEntity(idPost1).returnContent().toString()).isEqualTo("{\n"
				+ "  \"id\" : \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d780\",\n"
				+ "  \"type\" : \"ManufacturingMachine\",\n" + "  \"machineModel\" : {\n"
				+ "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\"\n" + "  },\n"
				+ "  \"@context\" : [ \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\", \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context-v1.7.jsonld\" ]\n"
				+ "}");
		assertThat(getEntity(idPost2).returnContent().toString()).isEqualTo("{\n"
				+ "  \"id\" : \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d970\",\n"
				+ "  \"type\" : \"ManufacturingMachine\",\n" + "  \"machineModel\" : {\n"
				+ "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd67j9\"\n" + "  },\n"
				+ "  \"@context\" : [ \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\", \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context-v1.7.jsonld\" ]\n"
				+ "}");
		Request.Delete(url + idPost1).execute();
		Request.Delete(url + idPost2).execute();
	}

	@Test
	void deleteEntityIT() {
		assertThat(client.delete(id).getStatus()).isEqualTo(204);
	}

	private Response getEntity(String idEntity) throws ClientProtocolException, IOException {
		request = Request.Get(url + idEntity).addHeader("Accept", "application/ld+json")
				.addHeader("Content-type", "application/ld+json").addHeader("Link",
						"<https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\"");
		return request.execute();
	}

}
