package de.baleipzig.pdfextraction.client.connector.impl;

import de.baleipzig.pdfextraction.client.config.Config;
import de.baleipzig.pdfextraction.client.connector.api.ResultConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;

@ImplementationOrder(order = 1)
public final class ResultConnectorImpl extends AbstractConnector implements ResultConnector {

    public ResultConnectorImpl(Config config) {
        this(config.getServerURL());
    }

    ResultConnectorImpl(String url) {
        super(url);
    }

    @Override
    public Flux<String> getAllNames() {
        return this.webClient
                .get()
                .uri("result/names")
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
    public Mono<Void> saveResultTemplate(String templateName, Path pathToFile) {
        return createRequest(templateName, pathToFile, "/results");
    }
}
