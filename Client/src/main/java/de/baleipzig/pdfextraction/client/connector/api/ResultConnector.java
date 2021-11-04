package de.baleipzig.pdfextraction.client.connector.api;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public interface ResultConnector {

    Flux<String> getAllNames();

    Mono<Void> saveResultTemplate(final String templateName, final Path pathToFile);
}
