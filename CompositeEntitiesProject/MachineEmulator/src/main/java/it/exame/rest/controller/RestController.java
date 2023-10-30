package it.exame.rest.controller;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

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

@Path("/notification")
public class RestController {

	@Inject
	private static final Logger LOGGER = Logger.getLogger("MockConsumer");
	
	@POST
	public RestResponse<Object> postOneSingleMachine(String json) throws URISyntaxException {
		LOGGER.info(json.toString()); 
		return ResponseBuilder.create(201,json).build();
	}

}
