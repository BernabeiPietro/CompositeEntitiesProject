package it.exame.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import it.exame.rest.client.ClientRest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class Service {
	@Inject
	@RestClient
	ClientRest restClient;

	@Inject
	URIGenerator uriGen;

	public List<Map<String, Object>> getAllMachine() {
		// TODO Auto-generated method stub
		return restClient.findAll("ManufacturingMachine");
	}

	public Response save(HashMap<String, Object> entity) {
		// TODO Auto-generated method stub
		return restClient.postEntity(entity);
	}

	public Response saveListOfElement(List<HashMap<String, Object>> entity) {
		// TODO Auto-generated method stub
		return restClient.postEntity(entity);
	}

	public Set<String> findOperationByMachineId(String uri) {
		// TODO Auto-generated method stub
		HashMap<String, Object> machine = restClient.findAttributeOfEntity(uri, "machineModel");
		HashMap<String, Object> machineModel = restClient
				.findEntity(getMap(machine, "machineModel").get("value").toString());
		// return Set.of(machineModel.toString());
		return getValueOfPropertyField(machineModel, "operationModel").keySet();
	}

	protected Map<String, Object> getValueOfPropertyField(Map<String, Object> operationalModel, String key) {
		Map<String, Object> property = getMap(operationalModel, key);
		return getMap(property, "value") != null ? getMap(property, "value") : property;

	}

	protected Map<String, Object> getMap(Map<String, Object> operationalModel, String key) {
		if (operationalModel.get(key) instanceof Map) {
			return (Map<String, Object>) operationalModel.get(key);
		}
		return null;
	}

	public Response deleteEntity(String id) {
				return restClient.delete(id);
		}

	public Response createSubscription() {
		HashMap<String, Object> subscription = new HashMap<String,Object>(Map.of("type", "Subscription",
				"entities", List.of(Map.of("type", "ManufacturingMachineOperation")), "notification",
				Map.of("endpoint", Map.of("uri", "http://172.17.0.1:8090/notification", "accept", "application/json"))));
		return restClient.postSubscription(subscription);
		// TODO Auto-generated method stub

	}

	// devo fare il modello delle operazioni,per mantenere le info sulle operazioni

}
