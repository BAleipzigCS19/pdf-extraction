package de.baleipzig.pdfextraction.client.connector.api;

import de.baleipzig.pdfextraction.api.dto.TemplateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
     * Delete the Template
     *
     * @param name name of the Template
     * @return Empty if successfully, else an error Mono
     */
    Mono<Void> delete(String name);
}
