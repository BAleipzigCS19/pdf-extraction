package de.baleipzig.pdfextraction.backend.repositories;

import de.baleipzig.pdfextraction.backend.entities.ResultTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<ResultTemplate, Integer> {

    boolean existsTemplateByName(String name);

    ResultTemplate findResultByName(String name);
}
