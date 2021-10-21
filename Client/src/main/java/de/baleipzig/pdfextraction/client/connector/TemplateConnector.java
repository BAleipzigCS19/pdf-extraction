package de.baleipzig.pdfextraction.client.connector;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TemplateConnector {

    Flux<String> getAllNames();

    Mono<TemplateDTO> getForName(String name);

    Mono<Void> save(TemplateDTO dto);
}
