package de.baleipzig.pdfextraction.backend.repositories;

import de.baleipzig.pdfextraction.backend.entities.ExtractionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<ExtractionTemplate, Integer> {

    boolean existsTemplateByName(String name);

    ExtractionTemplate findTemplateByName(String name);
}
