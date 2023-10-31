package it.exame.rest.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WireMockTestServer implements QuarkusTestResourceLifecycleManager {

	private WireMockServer wireMockServer;

	@Override
	public Map<String, String> start() {
		wireMockServer = new WireMockServer(WireMockConfiguration.options().notifier(new ConsoleNotifier(true)));
		wireMockServer.start();

		wireMockServer.stubFor(get(urlEqualTo("/entities/?type=ManufacturingMachine")).withHeader("Link", equalTo(
				"<https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\""))
				.willReturn(okJson("[\n" + "	{\n" + "		\"description\": \"Auger - Trivella\",\n"
						+ "	    \"id\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\n"
						+ "	    \"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
						+ "	    \"type\": \"ManufacturingMachine\"\n" + "	}\n" + "]")));
		wireMockServer.stubFor(get(urlEqualTo("/entities/urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0"))
				.withHeader("Link", equalTo(
						"<https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\""))
				.willReturn(okJson("{\n" + "		\"description\": \"Auger - Trivella\",\n"
						+ "	    \"id\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\n"
						+ "	    \"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
						+ "	    \"type\": \"ManufacturingMachine\"\n" + "	}")));
		wireMockServer.stubFor(get(
				urlEqualTo("/entities/urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0?attrs=machineModel"))
				.withHeader("Link", equalTo(
						"<https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\""))
				.willReturn(okJson("{\n" + "		\"description\": \"Auger - Trivella\",\n"
						+ "	    \"id\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\n"
						+ "	    \"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
						+ "	    \"type\": \"ManufacturingMachine\"\n" + "	}")));
		wireMockServer.stubFor(get(urlEqualTo(
				"/entities/?id=urn%3Angsi-ld%3AMachine%3A9166c528-9c98-4579-a5d3-8068aea5d7k0&machineModel=urn%3Angsi-ld%3AMachineModel%3A00b42701-43e1-482d-aa7a-e2956cfd65k9"))
				.withHeader("Link", equalTo(
						"<https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json\""))
				.willReturn(okJson("[{\n" + "		\"description\": \"Auger - Trivella\",\n"
						+ "	    \"id\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\n"
						+ "	    \"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
						+ "	    \"type\": \"ManufacturingMachine\"\n" + "	}]")));
		wireMockServer.stubFor(get(urlEqualTo(
				"/entities/?machineModel=urn%3Angsi-ld%3AMachineModel%3A00b42701-43e1-482d-aa7a-e2956cfd65k9&id=urn%3Angsi-ld%3AMachine%3A9166c528-9c98-4579-a5d3-8068aea5d7k0"))
				.willReturn(okJson("[{\n" + "		\"description\": \"Auger - Trivella\",\n"
						+ "	    \"id\": \"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\n"
						+ "	    \"machineModel\": \"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\n"
						+ "	    \"type\": \"ManufacturingMachine\"\n" + "	}]")));
		wireMockServer.stubFor(post(urlEqualTo("/entities/"))
				.withRequestBody(containing(
						"\"machineModel\":\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\""))
				.withRequestBody(containing("\"description\":\"Auger - Trivella\""))
				.withRequestBody(containing("\"id\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\""))
				.withRequestBody(containing("\"type\":\"ManufactoringMachine\""))
				.withRequestBody(containing(
						"{\"machineModel\":\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\"description\":\"Auger - Trivella\",\"id\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\"type\":\"ManufactoringMachine\"}"))
				.willReturn(aResponse().withStatus(201)));
		wireMockServer.stubFor(post(urlEqualTo("/entityOperations/create"))
				.withRequestBody(containing(
						"\"machineModel\":\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\""))
				.withRequestBody(containing("\"description\":\"Auger - Trivella\""))
				.withRequestBody(containing("\"id\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\""))
				.withRequestBody(containing("\"type\":\"ManufactoringMachine\""))
				.withRequestBody(containing(
						"[{\"machineModel\":\"urn:ngsi-ld:MachineModel:00b42701-43e1-482d-aa7a-e2956cfd65k9\",\"description\":\"Auger - Trivella\",\"id\":\"urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d7k0\",\"type\":\"ManufactoringMachine\"}]"))
				.willReturn(aResponse().withStatus(201)));
		wireMockServer.stubFor(delete(urlEqualTo("/entities/urn:ngsi-ld:Machine:9166c528-9c98-4579-a5d3-8068aea5d6c0"))
				.willReturn(aResponse().withStatus(204)));
		return Map.of("quarkus.rest-client.\"it.exame.rest.client.ClientRest\".url", wireMockServer.baseUrl());
	}

	@Override
	public void stop() {
		if (null != wireMockServer) {
			wireMockServer.stop();
		}
	}
}