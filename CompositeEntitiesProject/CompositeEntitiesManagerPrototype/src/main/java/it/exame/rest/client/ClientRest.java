package it.exame.rest.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@ApplicationScoped
@RegisterRestClient
//baseUri ="http://localhost:9090/ngsi-ld/v1"
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ClientHeaderParam(name = "Link", value = "${rest-client.linkheader}")
public interface ClientRest {

	@GET
	@Path("/entities/")
	public List<Map<String, Object>> findAll(@QueryParam("type") String type);

	@POST
	@Path("/entities/")
	public Response postEntity(HashMap<String, Object> entity);

	@GET
	@Path("/entities/{id}")
	public HashMap<String, Object> findEntity(@PathParam("id") String id);

	@GET
	@Path("/entities/{id}")
	public HashMap<String, Object> findAttributeOfEntity(@PathParam("id") String id,
			@QueryParam("attrs") String attribute);

	@GET
	@Path("/entities/")

	public List<HashMap<String, Object>> findEntityByFilter(@RestQuery Map<String, String> map, @QueryParam("q")String action, @QueryParam("q") String idModel );

	@POST
	@Path("/entityOperations/create")
	public Response postEntity(List<HashMap<String, Object>> operation);

	@DELETE
	@Path("/entities/{id}")
	public Response delete(String id);

}
