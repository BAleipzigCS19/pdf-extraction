package de.baleipzig.pdfextraction.client.connector;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

public interface TemplateConnector {

    /**
     * Provides the Names of all saved Templates in the Backend
     *
     * @return Flux of TemplateNames or error Flux
     */
    Flux<String> getAllNames();

    /**
     * Loads the Template Object with the given Name.
     *
     * @param templateName Name of the template to load.
     * @return Mono of the loaded Template or error Mono
     */
    Mono<TemplateDTO> getForName(String templateName);

    /**
     * Saves the given Template in the Backend
     *
     * @param dto Template to save
     * @return empty Mono or error Mono
     */
    Mono<Void> save(TemplateDTO dto);

    /**
     * Creates an Image of the first Page of the given PDF with the Boxes of the given Template drawn to check if the Template matches the PDF
     *
     * @param templateName Name of the template to draw
     * @param pathToFile   Path to the PDF to use
     * @return Image as byte-Array
     */
    Mono<byte[]> createTestImage(final String templateName, final Path pathToFile);

    /**
     * Actually executes an extraction of the PDF with the given template
     *
     * @param templateName Name of the Template to use
     * @param pathToFile   Path to the PDF to use
     * @return Map of fieldtype-names to their extracted Result
     */
    Mono<Map<String, String>> runJob(final String templateName, final Path pathToFile);
}
