package de.baleipzig.pdfextraction.backend.repositories;

import de.baleipzig.pdfextraction.backend.entities.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Integer> {

    boolean existsTemplateByName(String name);

    Template findTemplateByName(String name);
}
