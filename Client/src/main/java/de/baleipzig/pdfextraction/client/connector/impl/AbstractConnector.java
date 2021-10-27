package de.baleipzig.pdfextraction.client.connector.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

abstract class AbstractConnector {

    protected final WebClient webClient;

    protected AbstractConnector(final String baseURl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseURl)
                .defaultHeaders(header -> {
                    header.set(HttpHeaders.ACCEPT_CHARSET, "utf-8");
                    header.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    header.set(HttpHeaders.CONTENT_ENCODING, MediaType.APPLICATION_JSON_VALUE);
                }).build();
    }


    protected <T> Mono<T> createRequest(String templateName, Path pathToFile, String urlPath) {
        if (!StringUtils.hasText(templateName)) {
            return Mono.error(new IllegalArgumentException("Invalid Name \"%s\"".formatted(templateName)));
        }

        if (pathToFile == null) {
            return Mono.error(new IllegalArgumentException("The Path to the File cannot be null."));
        }

        final MultiValueMap<String, Object> toSend = CollectionUtils.toMultiValueMap(Map.of("name", List.of(templateName), "content", List.of(new FileSystemResource(pathToFile))));

        return this.webClient
                .post()
                .uri(urlPath)
                .body(BodyInserters.fromMultipartData(toSend))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(new ParameterizedTypeReference<>() {
                        });
                    } else {
                        return Mono.error(new IllegalStateException(response.statusCode().name()));
                    }
                });
    }
}
