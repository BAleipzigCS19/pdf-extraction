package de.baleipzig.pdfextraction.client.connector.impl;

import de.baleipzig.pdfextraction.client.config.Config;
import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import javafx.scene.image.Image;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
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
    public Mono<Map<String, String>> extractOnly(final String templateName, final Path pathToFile) {
        return createRequest(templateName, pathToFile, "/extraction/run");
    }

    @Override
    public Mono<byte[]> runJob(final String templateName, final Path pathToFile, final String resultName) {
        Mono<byte[]> errorResponse = checkArgs(templateName, pathToFile);
        if (errorResponse != null) {
            return errorResponse;
        }

        final Base64.Decoder decoder = Base64.getDecoder();
        return this.webClient
                .post()
                .uri("/extraction")
                .body(BodyInserters.fromMultipartData(CollectionUtils.toMultiValueMap(
                        Map.of("templateName", List.of(templateName),
                                "content", List.of(new FileSystemResource(pathToFile)),
                                "resultName", List.of(resultName)))))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(String.class);
                    } else {
                        return Mono.error(new IllegalStateException(response.statusCode().name()));
                    }
                })
                .map(decoder::decode);
    }

    @Override
    public Mono<Image> createTestImage(final String templateName, final Path pathToFile) {
        final Base64.Decoder decoder = Base64.getDecoder();
        return this.createRequest(templateName, pathToFile, "/extraction/test", String.class)
                .map(decoder::decode)
                .map(ByteArrayInputStream::new)
                .map(Image::new);
    }

}
