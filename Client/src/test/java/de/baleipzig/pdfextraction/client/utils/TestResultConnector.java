package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.client.connector.api.ResultConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@ImplementationOrder(order = Integer.MAX_VALUE)
public class TestResultConnector implements ResultConnector {
    @Override
    public Flux<String> getAllNames() {
        return Flux.just("Result A", "Result B", "Result C");
    }

    @Override
    public Mono<Void> saveResultTemplate(String templateName, Path pathToFile) {
        return Mono.empty();
    }
}
