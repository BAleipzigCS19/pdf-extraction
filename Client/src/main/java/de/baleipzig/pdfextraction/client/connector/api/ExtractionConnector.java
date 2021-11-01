package de.baleipzig.pdfextraction.client.connector.api;

import javafx.scene.image.Image;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

public interface ExtractionConnector {

    /**
     * Actually executes an extraction of the PDF with the given template
     *
     * @param templateName Name of the Template to use
     * @param pathToFile   Path to the PDF to use
     * @return Map of fieldtype-names to their extracted Result
     */
    Mono<Map<String, String>> extractOnly(final String templateName, final Path pathToFile);

    /**
     * Actually executes an extraction of the PDF with the given template
     *
     * @param templateName Name of the Template to use
     * @param pathToFile   Path to the PDF to use
     * @return PDF as byte-array
     */
    Mono<byte[]> runJob(final String templateName, final Path pathToFile, final String resultName);

    /**
     * Creates an Image of the first Page of the given PDF with the Boxes of the given Template drawn to check if the Template matches the PDF
     *
     * @param templateName Name of the template to draw
     * @param pathToFile   Path to the PDF to use
     * @return Image
     */
    Mono<Image> createTestImage(final String templateName, final Path pathToFile);
}
