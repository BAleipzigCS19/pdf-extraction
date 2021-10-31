package de.baleipzig.pdfextraction.backend.repositories;

import de.baleipzig.pdfextraction.backend.entities.ExtractionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<ExtractionTemplate, Integer> {

    /**
     * Checks whether a template with the given name exists
     *
     * @param name Name of the Template to check
     * @return {@code true} if the Template exists, else false
     */
    boolean existsTemplateByName(String name);

    /**
     * Actually executes an extraction of the PDF with the given template
     *
     * @param templateName Name of the Template to use
     * @param pathToFile   Path to the PDF to use
     * @return Map of fieldtype-names to their extracted Result
     */
    ExtractionTemplate findTemplateByName(String name);
}
