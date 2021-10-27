package de.baleipzig.pdfextraction.client.connector.api;

import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

public interface ExtractionConnector {

    Mono<Map<String, String>> runJob(final String templateName, final Path pathToFile);

    Mono<byte[]> createTestImage(final String templateName, final Path pathToFile);
}
