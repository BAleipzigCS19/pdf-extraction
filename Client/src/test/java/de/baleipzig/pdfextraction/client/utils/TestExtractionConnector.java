package de.baleipzig.pdfextraction.client.utils;

import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import javafx.scene.image.Image;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

@ImplementationOrder(order = Integer.MAX_VALUE)
public class TestExtractionConnector implements ExtractionConnector {
    @Override
    public Mono<Map<String, String>> runJob(String templateName, Path pathToFile) {
        return Mono.just(Map.of("Test A", "Content A"));
    }

    @Override
    public Mono<Image> createTestImage(String templateName, Path pathToFile) {
        return Mono.error(new UnsupportedOperationException("Not implemented."));
    }
}
