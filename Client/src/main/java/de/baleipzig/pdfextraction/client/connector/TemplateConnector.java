package de.baleipzig.pdfextraction.client.connector;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import jakarta.inject.Singleton;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Singleton
public class TemplateConnector {

    private final WebClient webClient;

    public TemplateConnector() {
        this("http://localhost:5050/rest");
    }

    protected TemplateConnector(final String baseURl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseURl)
                .defaultHeaders(header -> {
                    header.set(HttpHeaders.ACCEPT_CHARSET, "utf-8");
                    header.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    header.set(HttpHeaders.CONTENT_ENCODING, MediaType.APPLICATION_JSON_VALUE);
                }).build();
    }

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
}
