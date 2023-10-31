package it.exame.service;

import java.util.ArrayList;
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
		return restClient.findAll("ManufacturingMachine");
	}

	public Response saveMachine(HashMap<String, Object> entity) {
		if (entity.get("type").equals("ManufacturingMachine"))
			return restClient.postEntity(entity);
		else
			return Response.status(422, "It is not a ManufactoringMachine").build();
	}

	public Set<String> findOperationByMachineId(String uri) {
		HashMap<String, Object> machine = restClient.findAttributeOfEntity(uri, "machineModel");
		HashMap<String, Object> machineModel = restClient
				.findEntity(getMap(machine, "machineModel").get("value").toString());
		// return Set.of(machineModel.toString());
		return getValueOfPropertyField(machineModel, "operationModel").keySet();
	}

	public Response executeOperation(String machineUri, String action, HashMap<String, Object> details)
			throws Exception {
		HashMap<String, Object> machine = restClient.findEntity(machineUri);
		if (machine.get("type").toString().compareTo("ManufacturingMachine") != 0) {
			throw (new Exception("Entity retrieved is not a ManufacturingMachine"));
		}
		HashMap<String, Object> operationalModel = restClient.findEntityByFilter(
				Map.of("type", "ManufacturingMachineOperationModel"), createFilter("description", action),
				createFilter("machineModel", getMap(machine, "machineModel").get("value").toString())).get(0);
		if (operationalModel.containsKey("operationComposite")) {
			Map<String, Object> elements = getValueOfPropertyField(operationalModel, "operationComposite");
			Map<String, Object> machineComponent = getValueOfPropertyField(machine, "machineComponent");
			List<HashMap<String, Object>> operationList = elements.entrySet().stream()
					.map((Entry<String, Object> x) -> {
						return createOperation(machineComponent.get(x.getValue()).toString(), x.getKey(), details);
					}).collect(Collectors.toList());
			operationList.add(
					addListOfId(createOperation(machineUri, action, details), operationList, "operationComposite"));
			return restClient.postEntity(operationList);
		} else {
			HashMap<String, Object> operation = createOperation(machineUri, action, details);
			return restClient.postEntity(operation);
		}
	}

	public Response deleteEntity(String id) {
		HashMap<String, Object> entity = restClient.findAttributeOfEntity(id, "type");
		if (entity.get("type").toString().compareTo("ManufacturingMachine") == 0) {

			return restClient.delete(id);
		}
		return Response.status(400, "It is not a ManufactoringMachine").build();
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

	private HashMap<String, Object> addListOfId(HashMap<String, Object> composite,
			List<HashMap<String, Object>> component, String key) {

		composite.put(key, Map.of("type", "Relationship", "object",
				component.stream().map((x) -> x.get("id")).collect(Collectors.toList())));
		return composite;
	}

	private String createFilter(String key, String value) {
		return key + "==" + value;
	}

	private HashMap<String, Object> createOperation(String uri, String action, HashMap<String, Object> details) {
		// "@Context",
		// "https://raw.githubusercontent.com/smart-data-models/dataModel.ManufacturingMachine/master/context.jsonld"
		// if Link is applied on request, che field @context is just inserted.

		HashMap<String, Object> operation = new HashMap<>(
				Map.of("id", uriGen.generateURI("MachineOperation"), "machine", expandedPropertyElement(uri), "type",
						"ManufacturingMachineOperation", "description", expandedPropertyElement(action)));
		details.forEach((key, value) -> operation.putIfAbsent(key, value));
		return operation;
	}

	private Map<String, String> expandedPropertyElement(String value) {
		return Map.of("value", value, "type", "Property");
	}

	// devo fare il modello delle operazioni,per mantenere le info sulle operazioni

}
