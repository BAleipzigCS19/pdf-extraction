package de.baleipzig.pdfextraction.client.util;

import de.baleipzig.pdfextraction.api.dto.FieldDTO;
import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import de.baleipzig.pdfextraction.api.fields.FieldType;
import de.baleipzig.pdfextraction.client.connector.api.TemplateConnector;
import de.baleipzig.pdfextraction.client.utils.injector.ImplementationOrder;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@ImplementationOrder(order = Integer.MAX_VALUE)
public class TestTemplateConnector implements TemplateConnector {
    @Override
    public Flux<String> getAllNames() {
        return Flux.just("Template A", "Template B", "Template C");
    }

    @Override
    public Mono<TemplateDTO> getForName(String name) {
        return Mono.just(new TemplateDTO("Template A", "TestConsumer", getFields()));
    }

    @Override
    public Mono<Void> save(TemplateDTO dto) {
        return Mono.empty();
    }

    @NotNull
    private List<FieldDTO> getFields() {
        return List.of(new FieldDTO(FieldType.ADDRESS_RECEIVER, 0, 0.1, 0.1, 0.2, 0.2),
                new FieldDTO(FieldType.ADDRESS_SENDER, 0, 0.3, 0.3, 0.2, 0.2),
                new FieldDTO(FieldType.EXPIRATION, 0, 0.4, 0.4, 0.2, 0.2),
                new FieldDTO(FieldType.INSURANCE_NUMBER, 0, 0.5, 0.5, 0.2, 0.2));
    }
}
