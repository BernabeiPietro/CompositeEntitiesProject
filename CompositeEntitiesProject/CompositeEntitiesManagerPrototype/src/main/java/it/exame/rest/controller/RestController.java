package it.exame.rest.controller;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.exame.service.Service;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/machine")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestController {

	@Inject
	private Service machineService;

	@GET
	public List<Map<String, Object>> getAllMachineAvailable() {
		return machineService.getAllMachine();
	}

	@GET
	@Path("/{id}/operation")
	public Set<String> getOperationOfMachine(@PathParam("id") String uri) throws URISyntaxException {
		return machineService.findOperationByMachineId(uri);
	}

	@POST
	public Response postOneSingleMachine(HashMap<String, Object> json) throws URISyntaxException {
		return machineService.saveMachine(json);

	}

	@POST
	@Path("/{idMachine}/operation/{action}")
	public Response postOperationOnAMachine(@PathParam("idMachine") String uri, @PathParam("action") String action,
			HashMap<String, Object> details) throws Exception {
		if (details == null)
			details = new HashMap<>();
		return machineService.executeOperation(uri, action, details);
	}

	@DELETE
	@Path("/{idMachine}")
	public Response deleteMachine(@PathParam("idMachine") String uri) {
		return machineService.deleteEntity(uri);
	}
}
