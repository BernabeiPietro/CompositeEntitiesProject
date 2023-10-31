package it.exame.service;

import java.util.UUID;

import jakarta.inject.Singleton;

@Singleton
public class URIGenerator {
	String prefix = "urn:ngsi-ld:";

	public String generateURI(String type) {

		return prefix + type + ":" + UUID.randomUUID();

	}
}
