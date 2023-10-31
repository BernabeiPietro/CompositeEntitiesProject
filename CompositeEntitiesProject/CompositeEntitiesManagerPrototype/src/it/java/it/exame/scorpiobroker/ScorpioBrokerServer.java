package it.exame.scorpiobroker;

import java.io.File;
import java.time.Duration;
import java.util.Map;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ScorpioBrokerServer implements QuarkusTestResourceLifecycleManager {
	private DockerComposeContainer<?> scorpio;
//	final static Logger logger = LoggerFactory.getLogger(DockerComposeContainer.class);

	@Override
	public Map<String, String> start() {

		scorpio = new DockerComposeContainer(new File("src/it/resources/scorpio-compose-it.yml")).withExposedService(
				"scorpio", 9090, Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));
		// scorpio.withLogConsumer("postgres", new Slf4jLogConsumer(logger));
		scorpio.start();

		return Map.of("quarkus.rest-client.\"it.exame.rest.client.ClientRest\".url",
				"http://localhost:9090/ngsi-ld/v1");
	}

	@Override
	public void stop() {
		scorpio.stop();
	}

}
