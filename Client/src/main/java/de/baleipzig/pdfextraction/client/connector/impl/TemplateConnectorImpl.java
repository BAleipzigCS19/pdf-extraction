package de.baleipzig.pdfextraction.client.connector.impl;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.client.config.Config;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@ImplementationOrder(order = 1)
public final class TemplateConnectorImpl extends AbstractConnector implements TemplateConnector {

    public TemplateConnectorImpl(Config config) {
        this(config.getServerURL());
    }

    TemplateConnectorImpl(String url) {
        super(url);
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


}
