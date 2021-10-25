package de.baleipzig.pdfextraction.client.connector;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

public interface TemplateConnector {

    Flux<String> getAllNames();

    Mono<TemplateDTO> getForName(String name);

    Mono<Void> save(TemplateDTO dto);

    Mono<Map<String, String>> runJob(final String templateName, final Path pathToFile);

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

    public Mono<Map<String, String>> tesseract(final String templateName, final Path pathToFile) {

        final MultiValueMap<String, Object> map = CollectionUtils.toMultiValueMap(Map.of("name", List.of(templateName), "content", List.of(new FileSystemResource(pathToFile))));

        return this.webClient
                .post()
                .uri("/tesseract")
                .body(BodyInserters.fromMultipartData(map))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(new ParameterizedTypeReference<>() {
                        });
                    } else {
                        return Mono.error(new IllegalStateException(response.statusCode().name()));
                    }
                });
    }
    Mono<byte[]> createTestImage(final String templateName, final Path pathToFile);
}
