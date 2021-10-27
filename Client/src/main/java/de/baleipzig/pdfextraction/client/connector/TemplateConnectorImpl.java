package de.baleipzig.pdfextraction.client.connector;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.config.Config;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import jakarta.inject.Singleton;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Singleton
@ImplementationOrder(order = 1)
public final class TemplateConnectorImpl implements TemplateConnector {

    private final WebClient webClient;

    public TemplateConnectorImpl(Config config) {
        this(config.getServerURL() + "/rest");
    }

    TemplateConnectorImpl(final String baseURl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseURl)
                .defaultHeaders(header -> {
                    header.set(HttpHeaders.ACCEPT_CHARSET, "utf-8");
                    header.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    header.set(HttpHeaders.CONTENT_ENCODING, MediaType.APPLICATION_JSON_VALUE);
                }).build();
    }

    @Override
    public Flux<String> getAllNames() {
        return this.webClient.method(HttpMethod.GET)
                .uri("/template/names")
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                        });
                    } else {
                        return Mono.error(new IllegalStateException(response.statusCode().name()));
                    }
                })
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<TemplateDTO> getForName(final String name) {
        return this.webClient.method(HttpMethod.GET)
                .uri("/template?name={name}", Map.of("name", name))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(new ParameterizedTypeReference<>() {
                        });
                    } else {
                        return Mono.error(new IllegalStateException(response.statusCode().name()));
                    }
                });
    }

    @Override
    public Mono<Void> save(final TemplateDTO dto) {
        return this.webClient
                .method(HttpMethod.PUT)
                .uri("/template")
                .body(BodyInserters.fromValue(dto))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.CREATED)) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new IllegalStateException(response.statusCode().name()));
                    }
                });
    }


    public Mono<Map<String, String>> runJob(final String templateName, final Path pathToFile) {
        return createRequest(templateName, pathToFile, "/runAnalysis");
    }

    public Mono<byte[]> createTestImage(final String templateName, final Path pathToFile) {
        return createRequest(templateName, pathToFile, "/test");
    }

    private <T> Mono<T> createRequest(String templateName, Path pathToFile, String urlPath) {
        if (!StringUtils.hasText(templateName)) {
            return Mono.error(new IllegalArgumentException("Invalid Template Name \"%s\"".formatted(templateName)));
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
