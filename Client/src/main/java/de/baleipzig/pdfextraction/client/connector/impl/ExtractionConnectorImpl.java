package de.baleipzig.pdfextraction.client.connector.impl;

import de.baleipzig.pdfextraction.api.config.Config;
import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

@ImplementationOrder(order = 1)
public final class ExtractionConnectorImpl extends AbstractConnector implements ExtractionConnector {

    public ExtractionConnectorImpl(Config config) {
        this(config.getServerURL());
    }

    ExtractionConnectorImpl(String url) {
        super(url);
    }

    @Override
    public Mono<Map<String, String>> runJob(final String templateName, final Path pathToFile) {
        return createRequest(templateName, pathToFile, "/extraction/run");
    }

    @Override
    public Mono<byte[]> createTestImage(final String templateName, final Path pathToFile) {
        return createRequest(templateName, pathToFile, "/extraction/test");
    }

}
