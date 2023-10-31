package it.exame.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import it.exame.scorpiobroker.ScorpioBrokerServer;
import jakarta.inject.Inject;

@QuarkusTest
@QuarkusTestResource(value = ScorpioBrokerServer.class, restrictToAnnotatedClass = true)
class ServiceClientIT {

	@Inject
	Service service;
	String url = "http://localhost:9090/ngsi-ld/v1/entities/";
	String idMachine = "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d890";
	String idModel = "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9";
	private Request request;
	private Response response;

	@BeforeEach
	void setup() throws ClientProtocolException, IOException {
		String bodyMachine = "{\"@context\": [\"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\"], \"description\": \"Auger - Trivella\",\"id\":\""
				+ idMachine
				+ "\",\"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\"type\": \"ManufacturingMachine\"}";
		String bodyModel = "{\n" + "    \"@context\": [\n"
				+ "        \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\"\n"
				+ "    ]," + "    \"id\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
				+ "    \"type\": \"ManufacturingMachineModel\",\n" + "    \"name\": \"Auger Machine\",\n"
				+ "    \"description\": \"Machine to digger\",\n" + "    \"brandName\": \"QuickT\",\n"
				+ "    \"version\": \"v1\",\n" + "    \"root\": \"false\",\n"
				+ "    \"machineModelParent\": \"urn:ngsi-ld:MachineModel:4146335f-839f-4ff9-a575-6b4e6232b734\",\n"
				+ "    \"machineModelChildren\":\n"
				+ "        \"urn:ngsi-ld:MachineModel:a74fcf24-58fa-11e8-ae3e-df1abd78f83f\",\n"
				+ "    \"processDescription\": \"Industrial Drilling\",\n" + "    \"operationModel\":{\n"
				+ "        \"type\": \"Property\",\n" + "        \"value\": \n"
				+ "            {\"Drill\": \"urn:ngsi-ld:MachineOperationModel:27577638-bd8a-4732-b418-fc8b949a0t90\"}"
				+ "        \n" + "    }}";

		postEntity(bodyMachine);
		postEntity(bodyModel);

	}

	@AfterEach
	void teardown() throws ClientProtocolException, IOException {
		Request.Delete(url + idMachine).execute();
		Request.Delete(url + idModel).execute();

	}

	@Test
	void getAllMachineIT() {
		assertThat(service.getAllMachine()).isEqualTo(List.of(Map.of("description",
				Map.of("type", "Property", "value", "Auger - Trivella"), "id", idMachine, "machineModel",
				Map.of("type", "Property", "value", "urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9"),
				"type", "ManufacturingMachine")));
	}

	@Test
	void findOperationByMachineIdIT() {
		assertThat(service.findOperationByMachineId(idMachine)).isEqualTo(Set.of("Drill"));
	}

	@Test
	void saveIT() throws StreamReadException, DatabindException, IOException {
		HashMap<String, Object> operationalModel = new ObjectMapper().readValue(
				new File("src/it/resources/exampleOfContextInformation/MachineManufactoring_auger.jsonld"),
				HashMap.class);
		assertThat(service.saveMachine(operationalModel).getStatus()).isEqualTo(201);
		assertThat(getEntity(operationalModel.get("id").toString()).returnContent().toString()).isEqualTo("{\n"
				+ "  \"id\" : \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\n"
				+ "  \"type\" : \"ManufacturingMachine\",\n" + "  \"description\" : {\n"
				+ "    \"type\" : \"Property\",\n" + "    \"value\" : \"Auger - Trivella\"\n" + "  },\n"
				+ "  \"assetIdentifier\" : {\n" + "    \"type\" : \"Property\",\n" + "    \"value\" : \"ID12345\"\n"
				+ "  },\n" + "  \"countryOfManufacture\" : {\n" + "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"UK\"\n" + "  },\n" + "  \"firmwareVersion\" : {\n"
				+ "    \"type\" : \"Property\",\n" + "    \"value\" : \"A.10\"\n" + "  },\n" + "  \"firstUsedAt\" : {\n"
				+ "    \"type\" : \"Property\",\n" + "    \"value\" : \"2017-05-04T10:18:16Z\"\n" + "  },\n"
				+ "  \"hardwareVersion\" : {\n" + "    \"type\" : \"Property\",\n" + "    \"value\" : \"2.1\"\n"
				+ "  },\n" + "  \"installedAt\" : {\n" + "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"2017-05-04T10:18:16Z\"\n" + "  },\n" + "  \"machineModel\" : {\n"
				+ "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\"\n" + "  },\n"
				+ "  \"manufacturedAt\" : {\n" + "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"2017-05-04T10:18:16Z\"\n" + "  },\n" + "  \"power\" : {\n"
				+ "    \"type\" : \"Property\",\n" + "    \"value\" : 4.4\n" + "  },\n" + "  \"rotationalSpeed\" : {\n"
				+ "    \"type\" : \"Property\",\n" + "    \"value\" : 100\n" + "  },\n" + "  \"supplierName\" : {\n"
				+ "    \"type\" : \"Property\",\n" + "    \"value\" : \"ACME NorthEast Inc.\"\n" + "  },\n"
				+ "  \"voltage\" : {\n" + "    \"type\" : \"Property\",\n" + "    \"value\" : 220\n" + "  },\n"
				+ "  \"dataProvider\" : {\n" + "    \"type\" : \"Property\",\n"
				+ "    \"value\" : \"https://provider.example.com\"\n" + "  },\n" + "  \"location\" : {\n"
				+ "    \"type\" : \"GeoProperty\",\n" + "    \"value\" : {\n" + "      \"type\" : \"Point\",\n"
				+ "      \"coordinates\" : [ -104.99404, 39.75621 ]\n" + "    }\n" + "  },\n"
				+ "  \"@context\" : [ \"https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld\", \"https://uri.etsi.org/ngsi-ld/v1/ngsi-ld-core-context-v1.7.jsonld\" ]\n"
				+ "}");
		Request.Delete(url + "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0").execute();
	}

	@Test
	void executeOperationIT() throws Exception {
		postEntity(IOUtils.toString(
				new FileInputStream(
						"src/it/resources/exampleOfContextInformation/MachineManufactoringOPerationalModel.jsonld"),
				"UTF-8"));
		postEntity(
				IOUtils.toString(
						new FileInputStream(
								"src/it/resources/exampleOfContextInformation/MachineManufactoringComposite.jsonld"),
						"UTF-8"));
		List<HashMap<String, Object>> operation = new ObjectMapper().readValue(
				getEntityOfType("ManufacturingMachineOperation").returnContent().toString(), ArrayList.class);
		assertThat(operation).isEmpty();
		assertThat(service.executeOperation("urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0", "Forward",
				new HashMap<>()).getStatus()).isEqualTo(201);
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
		Request.Delete(url + "urn:ngsi-ld:MachineOperationModel:27577638-bd8a-4732-b418-fc8b949a0t90").execute();
		Request.Delete(url + "urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d9b0").execute();
	}

	@Test
	void deleteMachineIT() {
		assertThat(service.deleteEntity(idMachine).getStatus()).isEqualTo(204);
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
