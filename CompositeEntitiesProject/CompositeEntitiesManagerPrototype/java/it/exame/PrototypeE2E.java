package it.exame;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import it.exame.rest.controller.RestControllerServiceIT;
import it.exame.scorpiobroker.ScorpioBrokerServer;

@QuarkusIntegrationTest
@QuarkusTestResource(value = ScorpioBrokerServer.class, restrictToAnnotatedClass = true)
class PrototypeE2E extends RestControllerServiceIT {

}
