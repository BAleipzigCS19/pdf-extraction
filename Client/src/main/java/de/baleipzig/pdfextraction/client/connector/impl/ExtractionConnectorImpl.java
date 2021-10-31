package de.baleipzig.pdfextraction.client.connector.impl;

import de.baleipzig.pdfextraction.client.config.Config;
import de.baleipzig.pdfextraction.client.connector.api.ExtractionConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import javafx.scene.image.Image;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
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
    public Mono<Map<String, String>> runJob(final String templateName, final Path pathToFile) {
        return createRequest(templateName, pathToFile, "/extraction/run");
    }

    @Override
    public Mono<Image> createTestImage(final String templateName, final Path pathToFile) {
        if (!StringUtils.hasText(templateName)) {
            return Mono.error(new IllegalArgumentException("Invalid Template Name \"%s\"".formatted(templateName)));
        }

        if (pathToFile == null) {
            return Mono.error(new IllegalArgumentException("The Path to the File cannot be null."));
        }

        final MultiValueMap<String, Object> toSend = CollectionUtils.toMultiValueMap(Map.of("name", List.of(templateName), "content", List.of(new FileSystemResource(pathToFile))));

        final Base64.Decoder decoder = Base64.getDecoder();
        return this.webClient
                .post()
                .uri("/test")
                .body(BodyInserters.fromMultipartData(toSend))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(String.class);
                    } else {
                        return Mono.error(new IllegalStateException(response.statusCode().name()));
                    }
                })
                .map(decoder::decode)
                .map(ByteArrayInputStream::new)
                .map(Image::new);
    }

}
