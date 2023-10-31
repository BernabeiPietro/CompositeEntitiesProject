package it.exame.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class URIGeneratorTest {

	@Inject
	URIGenerator uriGen;

	@Test
	void testContainType() {
		assertThat("urn:ngsi-ld:Test:").isSubstringOf(uriGen.generateURI("Test"));
	}

	@Test
	void testTwoDifferentTypeValue() {
		assertThat(uriGen.generateURI("NotTest")).isNotEqualTo(uriGen.generateURI("Test"));
	}

	@Test
	void testTwoSameTypeValue() {
		assertThat(uriGen.generateURI("Test")).isNotEqualTo(uriGen.generateURI("Test"));
	}

}
