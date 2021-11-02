package de.baleipzig.pdfextraction.backend.repositories;

import de.baleipzig.pdfextraction.backend.entities.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Integer> {

    /**
     * Checks whether a template with the given name exists
     *
     * @param name Name of the Template to check
     * @return {@code true} if the Template exists, else false
     */
    boolean existsTemplateByName(String name);

    /**
     * Loads the template with the given name from the DB
     *
     * @param name Name of the Template to load
     * @return The Template if it exists.
     */
    Template findTemplateByName(String name);
}
