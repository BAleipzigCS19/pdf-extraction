package de.baleipzig.pdfextraction.client.connector;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import javafx.scene.image.Image;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

public interface TemplateConnector {

    Flux<String> getAllNames();

    Mono<TemplateDTO> getForName(String name);

    Mono<Void> save(TemplateDTO dto);

    Mono<Map<String, String>> runJob(final String templateName, final Path pathToFile);

    Mono<Image> createTestImage(final String templateName, final Path pathToFile);
}
