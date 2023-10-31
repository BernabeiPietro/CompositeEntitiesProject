package it.exame;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.arc.profile.UnlessBuildProfile;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import it.exame.service.Service;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
@UnlessBuildProfile("test")
public class AppLifeCycleBean {

	@ConfigProperty(name = "machine.json.path")
	String pathMachine;
	@ConfigProperty(name = "model.json.path")
	String pathModel;
	@ConfigProperty(name = "operationalmodel.json.path")
	String pathOperationalModel;
	ArrayList<String> element = new ArrayList<String>(
			List.of("auger", "compositeauger", "frameinstaller", "locomotive"));
	@Inject
	Service service;
	private static final Logger LOGGER = Logger.getLogger("MockConsumer");

	// @Observes
	void onStart(@Observes StartupEvent ev) {
		ObjectMapper objectMapper = new ObjectMapper();
		Response response;
		LOGGER.info("The application is starting...");

		LOGGER.info("Load entities...");
		LOGGER.info("Load Machine...");
		element.forEach(x -> {
			try {

				LOGGER.info("Result of Operation="
						+ service.save(objectMapper.readValue(new File(pathMachine + x + ".jsonld"), HashMap.class))
								.getStatusInfo().getStatusCode());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		LOGGER.info("Load Model...");
		element.forEach(x -> {
			try {
				LOGGER.info("Result of Operation="
						+ service.save(objectMapper.readValue(new File(pathModel + x + ".jsonld"), HashMap.class))
								.getStatusInfo().getStatusCode());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		LOGGER.info("Load OperationModel...");
		element.forEach(x -> {
			try {
				LOGGER.info("Result of Operation=" + service
						.save(objectMapper.readValue(new File(pathOperationalModel + x + ".jsonld"), HashMap.class))
						.getStatusInfo().getStatusCode());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		LOGGER.info("Load Subscription...");
		response = service.createSubscription();
		LOGGER.info("Result of Operation=" + response.getStatusInfo().getStatusCode());

	}

//@Observes
	void onStop(@Observes ShutdownEvent ev) {
		ObjectMapper objectMapper = new ObjectMapper();

		LOGGER.info("The application is stopping...");
		LOGGER.info("Delete all previous entities...");
		element.forEach(x -> {
			try {
				service.deleteEntity(objectMapper.readValue(new File(pathMachine + x + ".jsonld"), HashMap.class)
						.get("id").toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		element.forEach(x -> {
			try {
				service.deleteEntity(objectMapper.readValue(new File(pathModel + x + ".jsonld"), HashMap.class)
						.get("id").toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		element.forEach(x -> {
			try {
				service.deleteEntity(
						objectMapper.readValue(new File(pathOperationalModel+x+".jsonld"), HashMap.class).get("id").toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

}